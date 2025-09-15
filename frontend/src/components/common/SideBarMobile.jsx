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
import { useMediaQuery } from "@mui/material";

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

export default function SideBarMobile() {
  const user = useSelector((state) => state.auth.user);
  const isMobile = useMediaQuery("(max-width: 768px)");
  const [isSidebarVisible, setIsSidebarVisible] = useState(true);

  return (
    <>
      {isMobile && (
        <motion.button
          onClick={() => setIsSidebarVisible(!isSidebarVisible)}
          className="fixed top-4 left-4 z-20 p-2 rounded-full bg-gray-900 text-white dark:bg-gray-800 dark:text-gray-100"
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.9 }}
        >
          <Menu size={24} />
        </motion.button>
      )}

      <AnimatePresence>
        {isSidebarVisible && (
          <motion.div
            className={`relative z-10 transition-all duration-300 ease-in-out ${
              isMobile ? "fixed top-0 left-0 h-full w-20" : "flex-shrink-0 w-64"
            }`}
            initial={{ x: isMobile ? -80 : 0 }}
            animate={{ x: 0 }}
            exit={{ x: isMobile ? -80 : 0 }}
            transition={{ duration: 0.3 }}
          >
            <div className="h-full bg-white dark:bg-gray-800 p-4 flex flex-col border-r border-gray-200 dark:border-gray-700">
              {/* Profile Icon */}
              <div className="flex items-center justify-center mb-4">
                <img
                  src="../../AdamPos.png"
                  alt="User"
                  className="w-10 h-10 rounded-full"
                />
              </div>

              {/* Divider Line */}
              <div className="border-b border-gray-200 dark:border-gray-700 mb-4"></div>

              {/* Navigation Items */}
              <nav className="flex-grow">
                {SIDEBAR_ITEMS.map((item) => (
                  <Link key={item.href} to={item.href}>
                    <motion.div
                      className={`flex items-center justify-center p-4 text-base font-medium text-gray-900 dark:text-gray-100 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors mb-2`}
                      whileHover={{ scale: 1.02 }}
                    >
                      <item.icon
                        size={20}
                        style={{ color: item.color, minWidth: "20px" }}
                      />
                      {!isMobile && (
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
                    </motion.div>{" "}
                  </Link>
                ))}
              </nav>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
}
