import React from "react";
import {
  Book,
  Film,
  Music,
  Gamepad2,
  AlertTriangle,
  AlertOctagon,
} from "lucide-react";
import { motion } from "framer-motion";
import { useSelector } from "react-redux";

const getIcon = (type) => {
  switch (type) {
    case "BOOK":
      return Book;
    case "FILM":
      return Film;
    case "CD":
      return Music;
    case "GAME":
      return Gamepad2;
    default:
      return Book;
  }
};

export default function BorrowedMediaCard() {
  const { activeLoans } = useSelector((state) => state.loans);

  const today = new Date().toLocaleDateString("en-US", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });

  const isDueDateLessThanThreeDays = (dueDate) => {
    const today = new Date();
    const due = new Date(dueDate);
    const timeDifference = due - today;
    const daysDifference = Math.ceil(timeDifference / (1000 * 60 * 60 * 24));
    return daysDifference <= 3;
  };

  const isOverdue = (dueDate) => {
    const today = new Date();
    const due = new Date(dueDate);
    return due < today;
  };

  const sortedLoans = [...activeLoans].sort((a, b) => {
    const dateA = isDueDateLessThanThreeDays(a.dueDate);
    const dateB = isDueDateLessThanThreeDays(b.dueDate);

    if (dateA === dateB) {
      return 0;
    }
    return dateA ? -1 : 1; // Place loans due soon first
  });

  return (
    <motion.div
      className="bg-white dark:bg-gray-800 shadow-lg rounded-xl border border-gray-200 dark:border-gray-700 max-h-60 overflow-y-scroll"
      whileHover={{ y: -5, boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.5)" }}
    >
      <div className="px-4 py-5 sm:p-6">
        <div className="flex items-center text-sm font-medium text-gray-500 dark:text-gray-400">
          <span className="mr-2">Borrowed Media</span>
          <span className="flex ml-8 items-center text-sm font-medium text-gray-500 dark:text-gray-400">
            Today: {today}
          </span>
        </div>
        <p className="mt-1 text-3xl font-semibold text-gray-900 dark:text-gray-100">
          {activeLoans.length.toLocaleString()} Items
        </p>

        <ul className="mt-4 space-y-2" role="list">
          {sortedLoans.map((loan) => {
            const Icon = getIcon(loan.media.type);
            const WarningIcon = isOverdue(loan.dueDate)
              ? AlertOctagon
              : isDueDateLessThanThreeDays(loan.dueDate)
              ? AlertTriangle
              : null;
            return (
              <li
                key={loan.loanId}
                className="flex items-center text-gray-700 dark:text-gray-300"
              >
                <Icon className="mr-2" size={16} />
                <span className="flex-1">{loan.media.title}</span>
                {WarningIcon && (
                  <motion.div
                    className="ml-2"
                    animate={{ x: [0, -9, -3, 0] }}
                    transition={{
                      duration: 0.5,
                      repeat: Infinity,
                      ease: "easeInOut",
                    }}
                  >
                    <WarningIcon
                      size={16}
                      className={
                        isOverdue(loan.dueDate)
                          ? "text-red-500 dark:text-red-400"
                          : "text-orange-500 dark:text-orange-400"
                      }
                    />
                  </motion.div>
                )}
                <span
                  className={`text-sm ${
                    isOverdue(loan.dueDate)
                      ? "text-red-500 dark:text-red-400 font-bold"
                      : isDueDateLessThanThreeDays(loan.dueDate)
                      ? "text-orange-500 dark:text-orange-400 font-bold"
                      : "text-gray-500 dark:text-gray-400"
                  }`}
                >
                  Due: {loan.dueDate}
                </span>
              </li>
            );
          })}
        </ul>
      </div>
    </motion.div>
  );
}
