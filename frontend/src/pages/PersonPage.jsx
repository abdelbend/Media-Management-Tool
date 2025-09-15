import { UsersIcon } from "lucide-react";
import { motion } from "framer-motion";
import { useSelector, useDispatch } from "react-redux";
import { fetchActiveLoans } from "/src/redux/slices/loanSlice";
import { useEffect } from "react";

import Header from "../components/header/Header";
import StatCard from "../components/common/StatCard";
import PersonTable from "../components/person/PersonTable";

export default function PersonPage() {
  const dispatch = useDispatch();

  const persons = useSelector((state) => state.persons?.persons || []);
  const loans = useSelector((state) => state.loans?.activeLoans || []);

  useEffect(() => {
    dispatch(fetchActiveLoans());
  }, [dispatch]);

  const activeMembers = persons.filter((person) =>
    loans.some((loan) => loan.person.personId === person.personId)
  ).length;

  return (
    <div className="bg-gray-100 dark:bg-gray-900 flex-1 overflow-auto relative z-10">
      <Header title="Person Page" />

      <main className="max-w-7xl mx-auto py-6 px-4 lg:px-8">
        <motion.div
          className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4 mb-8"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1 }}
        >
          <StatCard
            name="Total Members"
            icon={UsersIcon}
            value={persons.length.toLocaleString()}
            color="#6366F1"
          />
          <StatCard
            name="Active Members"
            icon={UsersIcon}
            value={activeMembers}
            color="#F59E0B"
          />
        </motion.div>

        <PersonTable />
      </main>
    </div>
  );
}
