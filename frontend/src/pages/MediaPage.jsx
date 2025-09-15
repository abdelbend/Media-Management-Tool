import { motion } from "framer-motion";
import Header from "../components/header/Header";
import StatCard from "../components/common/StatCard";
import { Package, TrendingUp } from "lucide-react";

import MediaTable from "../components/media/MediaTable";
import CategoryStatCard from "../components/media/CategoryStatCard";

import { useSelector } from "react-redux";

export default function MediaPage() {
  const media = useSelector((state) => state.media?.media || []);
  const loans = useSelector((state) => state.loans?.activeLoans || []);

  return (
    <div className="bg-gray-100 dark:bg-gray-900 flex-1 overflow-auto relative z-10">
      <Header title="Media Page" />

      <main className="max-w-7xl mx-auto py-6 px-4 lg:px-8">
        <motion.div
          className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4 mb-8"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1 }}
        >
          <StatCard
            name="Total Media"
            icon={Package}
            value={media.length.toLocaleString()}
            color="#6366F1"
          />
          <StatCard
            name="Loaned Media"
            icon={TrendingUp}
            value={loans.length.toLocaleString()}
            color="#10B981"
          />
        </motion.div>
        <CategoryStatCard />

        <MediaTable />
      </main>
    </div>
  );
}
