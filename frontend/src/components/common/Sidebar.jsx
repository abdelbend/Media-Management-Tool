import {
  BarChart2,
  Clapperboard,
  Menu,
  ShoppingBag,
  TrendingUp,
  Users,
} from "lucide-react";
import { useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";

const SIDEBAR_ITEMS = [
  {
    name: "Overview",
    icon: BarChart2,
    color: "#6366f1",
    href: "/",
  },
  { name: "Media", icon: Clapperboard, color: "#8B5CF6", href: "/media" },
  { name: "Person", icon: Users, color: "#EC4899", href: "/person" },
  { name: "Loans", icon: ShoppingBag, color: "#EC4899", href: "/loans" },
  {
    name: "Statistics",
    icon: TrendingUp,
    color: "#3B82F6",
    href: "/statistics",
  },
];

export default function Sidebar() {
  const user = useSelector((state) => state.auth.user);
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  return (
    <motion.div
      className="relative z-10 transition-all duration-300 ease-in-out flex-shrink-0"
      animate={{
        width: isSidebarOpen ? (window.innerWidth < 768 ? "33.333%" : 256) : 80,
      }}
    >
      <div className="h-full bg-white dark:bg-gray-800 p-4 flex flex-col border-r border-gray-200 dark:border-gray-700">
        {/* Menu Toggle Button */}
        <motion.button
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.9 }}
          onClick={() => setIsSidebarOpen(!isSidebarOpen)}
          className="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors max-w-fit mb-4"
        >
          <Menu size={24} className="text-gray-900 dark:text-gray-100" />
        </motion.button>

        {/* Profile Icon */}
        <div className="flex items-center mb-4">
          <img
            src="../../AdamPos.png"
            alt="User"
            className="w-10 h-10 rounded-full"
          />
          <AnimatePresence>
            {isSidebarOpen && (
              <motion.span
                className="ml-3 text-gray-900 dark:text-gray-100 font-medium"
                initial={{ opacity: 0, width: 0 }}
                animate={{ opacity: 1, width: "auto" }}
                exit={{ opacity: 0, width: 0 }}
                transition={{ duration: 0.2 }}
              >
                {user}
              </motion.span>
            )}
          </AnimatePresence>
        </div>

        {/* Divider Line */}
        <div className="border-b border-gray-200 dark:border-gray-700 mb-4"></div>

        {/* Navigation Items */}
        <nav className="flex-grow">
          {SIDEBAR_ITEMS.map((item) => (
            <Link key={item.href} to={item.href}>
              <motion.div
                className="flex items-center p-4 text-base font-medium text-gray-900 dark:text-gray-100 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors mb-2"
                whileHover={{ scale: 1.02 }}
              >
                <item.icon
                  size={20}
                  style={{ color: item.color, minWidth: "20px" }}
                />
                <AnimatePresence>
                  {isSidebarOpen && (
                    <motion.span
                      className="ml-4 whitespace-nowrap"
                      initial={{ opacity: 0, width: 0 }}
                      animate={{ opacity: 1, width: "auto" }}
                      exit={{ opacity: 0, width: 0 }}
                      transition={{ duration: 0.2, delay: 0.1 }}
                    >
                      {item.name}
                    </motion.span>
                  )}
                </AnimatePresence>
              </motion.div>
            </Link>
          ))}
        </nav>
      </div>
    </motion.div>
  );
}
