import React, { useState, useEffect } from "react";
import {
  LineChart,
  Line,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import {
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Box,
  ButtonGroup,
  Button,
} from "@mui/material";
import { motion } from "framer-motion";
import { useSelector, useDispatch } from "react-redux";
import { fetchMedia } from "../../redux/slices/mediaSlice";
import { fetchLoans } from "../../redux/slices/loanSlice";
import dayjs from "dayjs";
import isoWeek from "dayjs/plugin/isoWeek";
import isBetween from "dayjs/plugin/isBetween";

dayjs.extend(isoWeek);
dayjs.extend(isBetween);

export default function LoansPerMedia() {
  const dispatch = useDispatch();

  const { media } = useSelector((state) => state.media);
  const { loans } = useSelector((state) => state.loans);

  const [selectedMediaId, setSelectedMediaId] = useState("");
  const [filteredData, setFilteredData] = useState([]);
  const [timeInterval, setTimeInterval] = useState("monthly");

  useEffect(() => {
    dispatch(fetchMedia());
    dispatch(fetchLoans());
  }, [dispatch]);

  useEffect(() => {
    if (selectedMediaId) {
      const filteredLoans = loans.filter(
        (loan) => loan.media?.mediaId === selectedMediaId
      );
      const groupedData = groupLoansByInterval(filteredLoans, timeInterval);
      setFilteredData(groupedData);
    } else {
      setFilteredData([]);
    }
  }, [selectedMediaId, timeInterval, loans]);

  const groupLoansByInterval = (loans, interval) => {
    const now = dayjs();
    const grouped = {};

    loans.forEach((loan) => {
      const date = dayjs(loan.borrowedAt);
      let key;

      switch (interval) {
        case "yearly":
          key = date.format("YYYY");
          break;
        case "monthly":
          key = date.format("YYYY-MM");
          break;
        case "weekly":
          key = `Week ${date.isoWeek()} ${date.year()}`;
          break;
        case "daily":
          key = date.format("YYYY-MM-DD");
          break;
        default:
          key = date.format("YYYY-MM");
      }

      if (!grouped[key]) {
        grouped[key] = 0;
      }
      grouped[key]++;
    });

    return Object.entries(grouped)
      .map(([key, count]) => {
        if (interval === "weekly") {
          const [year, week] = key.split("-W").map(Number);
          return { name: key, loans: count, year, week };
        }
        return { name: key, loans: count };
      })
      .sort((a, b) => {
        if (interval === "weekly") {
          return a.year === b.year ? a.week - b.week : a.year - b.year;
        }
        return dayjs(a.name).toDate() - dayjs(b.name).toDate();
      });
  };

  return (
    <motion.div
      className="bg-white dark:bg-gray-800 shadow-lg rounded-xl p-6 border border-gray-200 dark:border-gray-700"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: 0.4 }}
    >
      <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-4">
        Loans Per Media Timeline
      </h2>

      {/* Media Selection */}
      <Box sx={{ mb: 4 }}>
        <FormControl fullWidth>
          <InputLabel id="media-select-label">Select Media</InputLabel>
          <Select
            labelId="media-select-label"
            value={selectedMediaId}
            onChange={(e) => setSelectedMediaId(e.target.value)}
          >
            {media.map((item) => (
              <MenuItem key={item.mediaId} value={item.mediaId}>
                {item.title}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>

      {/* Time Interval Selection */}
      <Box sx={{ mb: 4 }}>
        <ButtonGroup fullWidth>
          <Button
            onClick={() => setTimeInterval("yearly")}
            variant={timeInterval === "yearly" ? "contained" : "outlined"}
          >
            Yearly
          </Button>
          <Button
            onClick={() => setTimeInterval("monthly")}
            variant={timeInterval === "monthly" ? "contained" : "outlined"}
          >
            Monthly
          </Button>
          <Button
            onClick={() => setTimeInterval("weekly")}
            variant={timeInterval === "weekly" ? "contained" : "outlined"}
          >
            Weekly
          </Button>
          <Button
            onClick={() => setTimeInterval("daily")}
            variant={timeInterval === "daily" ? "contained" : "outlined"}
          >
            Daily
          </Button>
        </ButtonGroup>
      </Box>

      {/* Timeline Chart */}
      {selectedMediaId && (
        <div style={{ width: "100%", height: 300 }}>
          <ResponsiveContainer>
            <LineChart data={filteredData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
              <XAxis dataKey="name" stroke="#9CA3AF" />
              <YAxis stroke="#9CA3AF" />
              <Tooltip
                contentStyle={{
                  backgroundColor: "rgba(31, 41, 55, 0.8)",
                  borderColor: "#4B5563",
                }}
                itemStyle={{ color: "#E5E7EB" }}
              />
              <Line
                type="monotone"
                dataKey="loans"
                stroke="#6366F1"
                strokeWidth={3}
                dot={{ fill: "#6366F1", strokeWidth: 2, r: 6 }}
                activeDot={{ r: 8, strokeWidth: 2 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      )}
    </motion.div>
  );
}
