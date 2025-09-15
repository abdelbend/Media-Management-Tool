import { motion } from "framer-motion";

export default function StatCard({ name, icon: Icon, value, color, children }) {
  return (
    <motion.div
      className="bg-white dark:bg-gray-800 shadow-lg rounded-xl border border-gray-200 dark:border-gray-700"
      whileHover={{ y: -5, boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.5)" }}
    >
      <div className="px-4 py-5 sm:p-6">
        <span className="flex items-center text-sm font-medium text-gray-500 dark:text-gray-400">
          <Icon size={20} className="mr-2" style={{ color }} />
          {name}
        </span>
        <p className="mt-1 text-3xl font-semibold text-gray-900 dark:text-gray-100">
          {value}
        </p>
        {children}
      </div>
    </motion.div>
  );
}
