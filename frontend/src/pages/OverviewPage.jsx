// src/pages/OverviewPage.jsx

import React, { useCallback } from "react";
import { useEffect } from "react";
import { StepForward, ShoppingBag, UsersIcon } from "lucide-react";
import { motion } from "framer-motion";

import Header from "../components/header/Header";
import StatCard from "../components/common/StatCard";
import BorrowedMediaCalendar from "../components/overview/BorrowedMediaCalendar";
import BorrowedMediaCard from "../components/overview/BorrowedMediaCard";
import { fetchMedia } from "../redux/slices/mediaSlice";
import { fetchPersonsByUsername } from "../redux/slices/personSlice";
import { fetchActiveLoans } from "../redux/slices/loanSlice";

import { useDispatch, useSelector } from "react-redux";

export default function OverviewPage() {
  const dispatch = useDispatch();

  const { activeLoans } = useSelector((state) => state.loans);
  const persons = useSelector((state) => state.persons?.persons || []);
  const media = useSelector((state) => state.media?.media || []);

  const fetchMediaData = useCallback(async () => {
    dispatch(fetchMedia());
  }, [dispatch]);

  const fetchPersonData = useCallback(async () => {
    dispatch(fetchPersonsByUsername());
  }, [dispatch]);

  const fetchActiveLoansData = useCallback(async () => {
    dispatch(fetchActiveLoans());
  }, [dispatch]);

  useEffect(() => {
    fetchMediaData();
    fetchPersonData();
    fetchActiveLoansData();
  }, [fetchMediaData, fetchPersonData, fetchActiveLoansData]);

  return (
    <div className="bg-gray-100 flex-1 overflow-auto relative z-10 dark:bg-gray-900">
      <Header title="Media Overview" />

      <main className="max-w-7xl mx-auto py-6 px-4 lg:px-8">
        {/* STATS */}
        <motion.div
          className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4 mb-8"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1 }}
        >
          <StatCard
            name="Total Media"
            icon={StepForward}
            value={media.length.toLocaleString()}
            color="#6366F1"
          />
          <StatCard
            name="Total Members"
            icon={UsersIcon}
            value={persons.length.toLocaleString()}
            color="#6366F1"
          />
          <StatCard
            name="Loaned Media"
            icon={ShoppingBag}
            value={activeLoans.length.toLocaleString()}
            color="#EC4899"
          />
        </motion.div>

        <div className="mb-8">
          <BorrowedMediaCard />
        </div>

        <BorrowedMediaCalendar />
      </main>
    </div>
  );
}
