import React, { useEffect, useMemo } from "react";
import { useSelector, useDispatch } from "react-redux";
import { fetchLoans } from "../../redux/slices/loanSlice";
import {
  ResponsiveContainer,
  Tooltip,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
} from "recharts";
import { Box, Typography } from "@mui/material";

export default function LoanStatistics() {
  const dispatch = useDispatch();

  const { loans } = useSelector((state) => state.loans);

  useEffect(() => {
    dispatch(fetchLoans());
  }, [dispatch]);

  const statistics = useMemo(() => {
    if (!loans || loans.length === 0) return null;

    const totalLoans = loans.length;

    const totalDuration = loans.reduce((acc, loan) => {
      const borrowedAt = new Date(loan.borrowedAt);
      const returnedAt = loan.returnedAt
        ? new Date(loan.returnedAt)
        : new Date();

      if (returnedAt < borrowedAt) {
        console.warn(
          "Inconsistent data: returnedAt is earlier than borrowedAt",
          loan
        );
        return acc;
      }

      const duration = (returnedAt - borrowedAt) / (1000 * 60 * 60 * 24);
      return acc + duration;
    }, 0);

    const averageDurationInDays =
      totalLoans > 0 ? totalDuration / totalLoans : 0;
    const fullDays = Math.floor(averageDurationInDays); // Ganze Tage
    const remainingHours = Math.round((averageDurationInDays - fullDays) * 24);

    const punctuality = loans.reduce((acc, loan) => {
      const { person, dueDate, returnedAt } = loan;
      const personName = person?.firstName || "Unknown";
      const isLate = returnedAt && new Date(returnedAt) > new Date(dueDate);

      if (!acc[personName]) {
        acc[personName] = { onTime: 0, late: 0 };
      }

      if (isLate) {
        acc[personName].late += 1;
      } else if (returnedAt) {
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
  }, [loans]);

  if (!statistics) {
    return <Typography>Loading statistics...</Typography>;
  }

  const { totalLoans, fullDays, remainingHours, punctualityRanking } =
    statistics;

  return (
    <Box className="bg-white dark:bg-gray-800 shadow-lg p-6 border border-gray-200 dark:border-gray-700">
      <Typography variant="h4" gutterBottom>
        Loan Statistics
      </Typography>

      <Typography variant="h6" gutterBottom>
        Total Loans: {totalLoans}
      </Typography>

      <Typography variant="h6" gutterBottom>
        Average Loan Duration: {fullDays} days and {remainingHours} hours
      </Typography>

      <Box height={300} mt={4}>
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={punctualityRanking}>
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
            <Bar dataKey="onTime" fill="#4CAF50" name="On Time" />
            <Bar dataKey="late" fill="#F44336" name="Late" />
          </BarChart>
        </ResponsiveContainer>
      </Box>
    </Box>
  );
}
