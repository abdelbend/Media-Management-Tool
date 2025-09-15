import React, { useState } from "react";
import {
  Box,
  Tabs,
  Tab,
  Typography,
  Paper,
  Button,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import TimelineIcon from "@mui/icons-material/Timeline";
import { ViewList, GridView } from "@mui/icons-material";
import { BarChart2, Film, PieChart, Disc3 } from "lucide-react";
import Header from "../components/header/Header";
import LoansPerGenre from "../components/statistics/MediaTimeline";
import LoansPerMonth from "../components/statistics/LoanStatistics";
import LoansPerType from "../components/statistics/LoansPerMedia";
import CategoryDistributionChart from "../components/statistics/CategoryDistributionChart";
import GenreDistributionChart from "../components/statistics/GenreDistributionChart";

export default function StatisticsPage() {
  const [activeTab, setActiveTab] = useState(0);
  const [viewMode, setViewMode] = useState("tabs");

  const theme = useTheme();
  const isMobile = useMediaQuery("(max-width: 768px)");
  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  const toggleViewMode = () => {
    setViewMode((prevMode) => (prevMode === "tabs" ? "grid" : "tabs"));
  };

  return (
    <div className="bg-gray-100 dark:bg-gray-900 flex-1 overflow-auto relative z-10">
      <Header title={"Statistics Page"} />

      <main className="max-w-7xl mx-auto py-6 px-4 sm:px-1 lg:px-8">
        <Box
          display="flex"
          justifyContent="space-between"
          alignItems="center"
          className="bg-gray-100 dark:bg-gray-900"
          sx={{
            display: "flex",
            alignItems: "center",
            p: 3,
            width: "100%",
            maxWidth: 1200,
            mx: "auto",
            backgroundColor: "bg-gray-100",
          }}
        >
          <Button
            variant="contained"
            color="primary"
            size="small"
            onClick={toggleViewMode}
            sx={{ ml: "auto" }} //
            startIcon={
              isMobile ? (
                viewMode === "tabs" ? (
                  <GridView />
                ) : (
                  <ViewList />
                )
              ) : null
            }
          >
            {!isMobile &&
              (viewMode === "tabs"
                ? "Switch to Grid View"
                : "Switch to Tabs View")}
          </Button>
        </Box>

        {viewMode === "tabs" ? (
          <Box
            className="flex flex-col items-center rounded-xl"
            sx={{
              backgroundColor:
                theme.palette.mode === "dark" ? "#1F2937" : "#FFFFFF",
              color: theme.palette.mode === "dark" ? "#FFFFFF" : "#000000",
              boxShadow: 3,
              p: 3,
              width: "100%",
              maxWidth: "1200px",
              margin: "0 auto",
            }}
          >
            <Typography
              variant="h5"
              component="h2"
              align="center"
              sx={{ mb: 2, color: theme.palette.text.primary }}
            >
              Statistics Overview
            </Typography>

            <Tabs
              value={activeTab}
              onChange={handleTabChange}
              centered={isMobile}
              indicatorColor="primary"
              textColor="primary"
              variant="scrollable"
              scrollButtons="auto"
              sx={{
                mb: 3,
                "& .MuiTab-root": {
                  minWidth: "100px",
                  "@media (max-width: 1200px)": {
                    minWidth: "60px",
                    padding: "0 8px",
                  },
                },
              }}
            >
              <Tab
                icon={
                  isMobile ? (
                    <TimelineIcon sx={{ color: theme.palette.text.primary }} />
                  ) : null
                }
                label={isMobile ? null : "Media-Timeline"}
              />
              <Tab
                icon={
                  isMobile ? (
                    <BarChart2 sx={{ color: theme.palette.text.primary }} />
                  ) : null
                }
                label={isMobile ? "" : "Loan Statistics"}
              />
              <Tab
                icon={
                  isMobile ? (
                    <Film sx={{ color: theme.palette.text.primary }} />
                  ) : null
                }
                label={isMobile ? "" : "Loans per Media"}
              />
              <Tab
                icon={
                  isMobile ? (
                    <PieChart sx={{ color: theme.palette.text.primary }} />
                  ) : null
                }
                label={isMobile ? "" : "Category Distribution"}
              />
              <Tab
                icon={
                  isMobile ? (
                    <Disc3 sx={{ color: theme.palette.text.primary }} />
                  ) : null
                }
                label={isMobile ? "" : "Type Distribution"}
              />
            </Tabs>

            <Paper
              sx={{
                p: 0,
                backgroundColor: "primary",
                dark: { backgroundColor: "#1E293B" },
                borderRadius: 0,
                width: "100%",
              }}
            >
              {activeTab === 0 && <LoansPerGenre />}
              {activeTab === 1 && <LoansPerMonth />}
              {activeTab === 2 && <LoansPerType />}
              {activeTab === 3 && <CategoryDistributionChart />}
              {activeTab === 4 && <GenreDistributionChart />}
            </Paper>
          </Box>
        ) : (
          <Box
            className="grid grid-cols-1 lg:grid-cols-2 gap-8 overflow-x-auto bg-white dark:bg-gray-900"
            sx={{
              backgroundColor: theme.palette.background.default,
              color: theme.palette.text.primary,
            }}
          >
            <LoansPerGenre />
            <LoansPerMonth />
            <LoansPerType />
            <CategoryDistributionChart />
            <GenreDistributionChart />
          </Box>
        )}
      </main>
    </div>
  );
}
