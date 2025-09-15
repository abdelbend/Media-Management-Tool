import React, { useEffect, useMemo, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import { fetchLoans, fetchOverdueLoans } from "../../redux/slices/loanSlice";
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
} from "recharts";
import {
  Box,
  Typography,
  FormControl,
  useTheme,
  useMediaQuery,
  Autocomplete,
  TextField,
} from "@mui/material";

export default function LoanStatistics() {
  const dispatch = useDispatch();
  const theme = useTheme();
  const textColor = theme.palette.mode === "dark" ? "white" : "black";
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  const loansData = useSelector((state) => state.loans);
  const { loans, overdueLoans: overdueLoansData } = loansData;

  const [selectedPersons, setSelectedPersons] = useState([]);

  useEffect(() => {
    dispatch(fetchLoans());
    dispatch(fetchOverdueLoans());
  }, [dispatch]);

  const allPersons = useMemo(() => {
    const persons = loans.map((loan) => loan.person);
    const uniquePersons = Array.from(
      new Set(persons.map((person) => person.personId))
    ).map((personId) => persons.find((person) => person.personId === personId));
    return uniquePersons;
  }, [loans]);

  const overduePersons = useMemo(() => {
    const personsWithOverdue = overdueLoansData.map((loan) => loan.person);
    const uniqueOverduePersons = Array.from(
      new Set(personsWithOverdue.map((person) => person.personId))
    ).map((personId) =>
      personsWithOverdue.find((person) => person.personId === personId)
    );
    return uniqueOverduePersons;
  }, [overdueLoansData]);

  // Initialize default selection with overdue persons
  useEffect(() => {
    setSelectedPersons(overduePersons.map((person) => person.personId));
  }, [overduePersons]);

  // Filter loans by selected persons
  const filteredLoans = useMemo(() => {
    if (selectedPersons.length === 0) return loans;

    return loans.filter((loan) =>
      selectedPersons.includes(loan.person?.personId)
    );
  }, [selectedPersons, loans]);

  // Compute statistics
  const statistics = useMemo(() => {
    if (!filteredLoans || filteredLoans.length === 0) return null;

    const totalLoans = filteredLoans.length;

    // Average loan duration
    const totalDuration = filteredLoans.reduce((acc, loan) => {
      const borrowedAt = new Date(loan.borrowedAt);
      const returnedAt = loan.returnedAt
        ? new Date(loan.returnedAt)
        : new Date();
      const duration = (returnedAt - borrowedAt) / (1000 * 60 * 60 * 24);
      return acc + duration;
    }, 0);

    const averageDurationInDays =
      totalLoans > 0 ? totalDuration / totalLoans : 0;
    const fullDays = Math.floor(averageDurationInDays);
    const remainingHours = Math.round((averageDurationInDays - fullDays) * 24);

    const punctuality = filteredLoans.reduce((acc, loan) => {
      const { person, dueDate, returnedAt } = loan;
      const personName =
        person?.firstName + " " + person?.lastName || "Unknown";

      const isLate = returnedAt
        ? new Date(returnedAt) > new Date(dueDate)
        : new Date() > new Date(dueDate);

      if (!acc[personName]) {
        acc[personName] = { late: 0, onTime: 0 };
      }

      if (isLate) {
        acc[personName].late += 1;
      } else {
        acc[personName].onTime += 1;
      }

      return acc;
    }, {});

    const punctualityRanking = Object.entries(punctuality).map(
      ([name, stats]) => ({
        name,
        onTime: stats.onTime,
        late: stats.late,
      })
    );

    return { totalLoans, fullDays, remainingHours, punctualityRanking };
  }, [filteredLoans]);

  if (!statistics) {
    return <Typography>No Loans Found</Typography>;
  }

  const { totalLoans, fullDays, remainingHours, punctualityRanking } =
    statistics;

  // Determine minimum width of the chart
  const minWidth =
    punctualityRanking.length > (isMobile ? 1 : 4)
      ? `${punctualityRanking.length * 100}px`
      : "100%";

  return (
    <Box
      className="bg-white dark:bg-gray-800 shadow-lg p-6 rounded-xl border border-gray-200 dark:border-gray-700"
      sx={{
        maxWidth: "100%",
      }}
    >
      <Typography variant="h4" gutterBottom sx={{ color: textColor }}>
        Loan Statistics
      </Typography>

      {/* Person Selection */}
      <FormControl fullWidth sx={{ mb: 4 }}>
        <Autocomplete
          multiple
          options={allPersons}
          getOptionLabel={(option) =>
            `${option.firstName} ${option.lastName}` ||
            `Unknown (ID: ${option.personId})`
          }
          value={allPersons.filter((person) =>
            selectedPersons.includes(person.personId)
          )}
          onChange={(event, value) =>
            setSelectedPersons(value.map((person) => person.personId))
          }
          renderInput={(params) => (
            <TextField
              {...params}
              label="Select Persons"
              variant="outlined"
              placeholder="Search or select persons"
            />
          )}
          ListboxProps={{
            style: { maxHeight: "300px", overflow: "auto" },
          }}
        />
      </FormControl>

      {/* Total loans */}
      <Typography variant="h6" gutterBottom sx={{ color: textColor }}>
        Total Loans: {totalLoans}
      </Typography>
      <Typography variant="h6" gutterBottom sx={{ color: textColor }}>
        Average Loan Duration: {fullDays} days and {remainingHours} hours
      </Typography>

      {/* Punctuality: Ranking as BarChart */}
      <Box
        height={300}
        mt={4}
        sx={{
          overflowX: "auto", // Horizontale Scrollbar
          overflowY: "hidden",
        }}
      >
        <Box
          sx={{
            minWidth, // Dynamische Mindestbreite
            display: "inline-block",
          }}
        >
          <ResponsiveContainer width="100%" height={300}>
            <BarChart
              data={punctualityRanking}
              margin={{ top: 5, right: 30, left: 10, bottom: 5 }}
            >
              <CartesianGrid strokeDasharray="3 3" stroke="#4B5563" />
              <XAxis dataKey="name" stroke="#9ca3af" />
              <YAxis stroke="#9ca3af" />
              <Tooltip
                contentStyle={{
                  backgroundColor: "rgba(31, 41, 55, 0.8)",
                  borderColor: "#4B5563",
                }}
                itemStyle={{ color: "#E5E7EB" }}
              />
              <Bar
                dataKey="onTime"
                fill="#4CAF50"
                name="On Time"
                barSize={50}
              />
              <Bar dataKey="late" fill="#F44336" name="Late" barSize={50} />
            </BarChart>
          </ResponsiveContainer>
        </Box>
      </Box>
    </Box>
  );
}
