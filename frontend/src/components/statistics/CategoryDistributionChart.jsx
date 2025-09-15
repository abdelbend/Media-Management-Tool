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
 * Displays a pie chart of how many media items there are per category.
 * It aggregates the Redux 'media' slice to count the total number
 * of media items for each category and renders a Recharts PieChart.
 */

const COLORS = [
  "#EC4899",
  "#10B981",
  "#F59E0B",
  "#3B82F6",
  "#EF4444",
];

export default function CategoryDistributionChart() {
  const dispatch = useDispatch();
  const { media, loading, error } = useSelector((state) => state.media);

  useEffect(() => {
    dispatch(fetchMedia());
  }, [dispatch]);

  /**
   * Build the data array for Recharts:
   *  [
   *    { name: "CategoryName1", value: 10 },
   *    { name: "CategoryName2", value: 5 },
   *    ...
   *  ]
   */
  const chartData = useMemo(() => {
    if (!media || media.length === 0) return [];

    // categoryId -> { categoryName, count }
    const categoryCountMap = {};

    media.forEach((mediaItem) => {
      // Each mediaItem has an array of categories, e.g. mediaItem.categories = [ {categoryId, categoryName}, ... ]
      if (mediaItem.categories) {
        mediaItem.categories.forEach((catObj) => {
          const { categoryId, categoryName } = catObj;
          if (!categoryCountMap[categoryId]) {
            categoryCountMap[categoryId] = {
              name: categoryName,
              value: 0,
            };
          }
          categoryCountMap[categoryId].value += 1;
        });
      }
    });

    // Convert the map to an array that Recharts expects
    return Object.values(categoryCountMap);
  }, [media]);

  if (error) {
    return (
      <Typography color="error">
        Failed to load category distribution: {error}
      </Typography>
    );
  }

  if (loading && (!media || media.length === 0)) {
    return <Typography>Loading category distribution...</Typography>;
  }

  if (chartData.length === 0) {
    return <Typography>No media or categories found.</Typography>;
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
        Category Distribution
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
