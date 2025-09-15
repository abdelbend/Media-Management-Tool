import React, { useEffect, useMemo } from "react";
import { useSelector, useDispatch } from "react-redux";
import { motion } from "framer-motion";
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from "recharts";
import { Box, Typography } from "@mui/material";

import { fetchMedia } from "../../redux/slices/mediaSlice";

/**
 * Displays a pie chart showing how many media items belong to each genre (type).
 * e.g., { type: "BOOK" }, { type: "FILM" }, { type: "CD" }, etc.
 */
const COLORS = [
    "#FF6B6B", // Vivid Coral
    "#39FF14", // Neon Green
    "#FF00FF", // Bold Magenta
    "#FFD700", // Bright Gold
    "#00B7EB", // Sparkling Blue
    "#FF007F", // Hot Pink
];
export default function GenreDistributionChart() {
  const dispatch = useDispatch();
  const { media, loading, error } = useSelector((state) => state.media);

  useEffect(() => {
    dispatch(fetchMedia());
  }, [dispatch]);

  /**
   * Build the data array for Recharts:
   * [
   *   { name: "BOOK", value: 10 },
   *   { name: "FILM", value: 5 },
   *   ...
   * ]
   */
  const chartData = useMemo(() => {
    if (!media || media.length === 0) return [];

    // type -> { name: <type>, value: <count> }
    const typeCountMap = {};

    media.forEach((mediaItem) => {
      const { type } = mediaItem;
      if (type) {
        if (!typeCountMap[type]) {
          typeCountMap[type] = { name: type, value: 0 };
        }
        typeCountMap[type].value += 1;
      }
    });

    return Object.values(typeCountMap);
  }, [media]);

  if (error) {
    return (
      <Typography color="error">
        Failed to load genre distribution: {error}
      </Typography>
    );
  }

  if (loading && (!media || media.length === 0)) {
    return <Typography>Loading genre distribution...</Typography>;
  }

  if (chartData.length === 0) {
    return <Typography>No media or genres found.</Typography>;
  }

  return (
    <motion.div
      className="bg-white dark:bg-gray-800 shadow-lg rounded-xl p-6 border border-gray-200 dark:border-gray-700"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: 0.3 }}
    >
      <Typography
        variant="h6"
        component="h2"
        className="text-gray-900 dark:text-gray-100 mb-4"
      >
        Type Distribution
      </Typography>

      <Box className="h-80">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={chartData}
              cx="50%"
              cy="50%"
              labelLine={false}
              outerRadius={80}
              dataKey="value"
              nameKey="name"
              label={({ name, percent }) =>
                `${name} ${(percent * 100).toFixed(0)}%`
              }
            >
              {chartData.map((entry, index) => (
                <Cell
                  key={`cell-${index}`}
                  fill={COLORS[index % COLORS.length]}
                />
              ))}
            </Pie>

            <Tooltip
              contentStyle={{
                backgroundColor: "rgba(31, 41, 55, 0.8)",
                borderColor: "#4B5563",
              }}
              itemStyle={{ color: "#E5E7EB" }}
            />

            <Legend />
          </PieChart>
        </ResponsiveContainer>
      </Box>
    </motion.div>
  );
}
