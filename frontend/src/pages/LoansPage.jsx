import { motion } from "framer-motion";

import Header from "../components/header/Header";
import StatCard from "../components/common/StatCard";

import {
  AlertTriangle,
  CheckIcon,
  DollarSign,
  Package,
  TrendingUp,
} from "lucide-react";
import LoansTable from "../components/loans/LoansTable";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchLoans,
  fetchOverdueLoans,
  fetchActiveLoans,
} from "../redux/slices/loanSlice";
import { useEffect } from "react";

const LoansPage = () => {
  const dispatch = useDispatch();
  const { loans, overdueLoans, activeLoans, loading } = useSelector(
    (state) => state.loans
  );

  useEffect(() => {
    dispatch(fetchLoans());
    dispatch(fetchOverdueLoans(new Date().toISOString().split("T")[0]));
    dispatch(fetchActiveLoans());
  }, [dispatch]);

  return (
    <div className="bg-gray-100 dark:bg-gray-900 flex-1 overflow-auto relative z-10">
      <Header title="Loan Page" />

      <main className="max-w-7xl mx-auto py-6 px-4 lg:px-8">
        <motion.div
          className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4 mb-8"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1 }}
        >
          <StatCard
            name="Total Loans"
            icon={Package}
            value={loans.length.toLocaleString()}
            color="#6366F1"
          />
          <StatCard
            name="Overdue Loans"
            icon={AlertTriangle}
            value={overdueLoans.length.toLocaleString()}
            color="#EF4444"
          />
          <StatCard
            name="Active Loans"
            icon={CheckIcon}
            value={activeLoans.length.toLocaleString()}
            color="#10B981"
          />
        </motion.div>

        <LoansTable />
      </main>
    </div>
  );
};

export default LoansPage;
