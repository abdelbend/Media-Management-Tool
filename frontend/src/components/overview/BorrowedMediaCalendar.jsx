import React, { useState, useContext, useEffect } from "react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import { motion } from "framer-motion";
import { Book, Film, Music, Gamepad2 } from "lucide-react";
import { ThemeContext } from "../../ThemeContext";
import { useDispatch, useSelector } from "react-redux";
import { fetchActiveLoans } from "../../redux/slices/loanSlice";
import "./BorrowedMediaCalendar.css";

const getIcon = (type) => {
  switch (type) {
    case "Book":
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

const mediaColors = {
  Book: "#6366F1",
  Film: "#10B981",
  CD: "#F59E0B",
  Image: "#EF4444",
};

export default function BorrowedMediaCalendar() {
  const [value, setValue] = useState(new Date());
  const { mode } = useContext(ThemeContext);
  const dispatch = useDispatch();
  const { activeLoans } = useSelector((state) => state.loans);

  useEffect(() => {
    dispatch(fetchActiveLoans());
  }, [dispatch]);

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

  const getMediaForDate = (date) => {
    return activeLoans.filter(
      (loan) =>
        new Date(loan.dueDate).getDate() === date.getDate() &&
        new Date(loan.dueDate).getMonth() === date.getMonth() &&
        new Date(loan.dueDate).getFullYear() === date.getFullYear()
    );
  };

  return (
    <motion.div
      className="bg-white dark:bg-gray-800 shadow-lg rounded-xl p-6 border border-gray-200 dark:border-gray-700 mb-8"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 1 }}
    >
      <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-4">
        Borrowed Media Calendar
      </h2>
      <Calendar
        onChange={setValue}
        value={value}
        tileContent={({ date, view }) =>
          view === "month" ? (
            <div
              className="mt-1 space-y-1 overflow-hidden"
              style={{
                maxHeight: "50px",
                overflowY: "auto",
              }}
            >
              {getMediaForDate(date).map((loan) => {
                const Icon = getIcon(loan.media.type);
                const isNearDueDate = isDueDateLessThanThreeDays(loan.dueDate);
                const isOverdueDate = isOverdue(loan.dueDate);
                return (
                  <div
                    key={loan.loanId}
                    className="flex items-center text-xs rounded px-1 py-0.5 text-white"
                    style={{
                      backgroundColor: isOverdueDate
                        ? "#F87171"
                        : isNearDueDate
                        ? "#F81"
                        : mediaColors[loan.media.type] || "#6366F1",
                    }}
                    title={`${loan.media.type}: ${
                      loan.media.title
                    } (Due: ${new Date(loan.dueDate).toLocaleDateString()})`}
                  >
                    <Icon size={22} />
                    <span>{loan.media.title}</span>
                  </div>
                );
              })}
            </div>
          ) : null
        }
        className={`react-calendar w-full rounded-lg ${
          mode === "dark"
            ? "bg-gray-800 text-gray-100"
            : "bg-white text-gray-900"
        }`}
      />
    </motion.div>
  );
}
