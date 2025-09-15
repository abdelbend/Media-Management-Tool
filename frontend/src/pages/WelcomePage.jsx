import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { useSelector } from "react-redux";

export default function WelcomePage() {
  const user = useSelector((state) => state.auth.user);
  const mode = useSelector((state) => state.theme?.mode || "light");

  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      navigate("/login");
    } else {
      const timer = setTimeout(() => {
        navigate("/");
      }, 1500);

      return () => clearTimeout(timer);
    }
  }, [user, navigate]);

  return (
    <div
      className={`min-h-screen flex items-center justify-center ${
        mode === "dark" ? "bg-gray-900" : "bg-gray-100"
      }`}
    >
      {user ? (
        <motion.div
          className="text-center"
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.8 }}
        >
          <h1 className="text-4xl font-bold mb-6 text-gray-800 dark:text-gray-100">
            Welcome, {user?.username || "Guest"}!
          </h1>
          <p className="text-lg text-gray-600 dark:text-gray-300">
            We're glad to have you back.
          </p>
        </motion.div>
      ) : (
        <motion.div
          className="text-center"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.8 }}
        >
          <p className="text-lg text-gray-600 dark:text-gray-300">
            Redirecting to login...
          </p>
        </motion.div>
      )}
    </div>
  );
}
