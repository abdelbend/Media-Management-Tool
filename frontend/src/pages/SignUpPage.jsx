// src/pages/SignUpPage.jsx

import React, { useState, useContext, useEffect } from "react";
import { ThemeContext } from "../ThemeContext";
import { useNavigate, Link } from "react-router-dom";
import { User, Lock, Mail } from "lucide-react";
import { motion } from "framer-motion";
import { Snackbar, Alert as MuiAlert } from "@mui/material";
import { fetch5Users } from "../redux/slices/userSlice";

import { useDispatch } from "react-redux";
import { register } from "../redux/slices/authSlice";

const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

export default function SignUpPage() {
  const { mode } = useContext(ThemeContext);
  const navigate = useNavigate();

  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success",
  });

  const [formData, setFormData] = useState({
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
  });
  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const [success, setSuccess] = useState(false);

  const { email, username, password, confirmPassword } = formData;

  const [errors, setErrors] = useState({});

  const dispatch = useDispatch();

  const onChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  useEffect(() => {
    if (success) {
      dispatch(fetch5Users());
    }
  }, [success, dispatch]);

  const handleSignUp = async (e) => {
    e.preventDefault();
    if (password !== confirmPassword) {
      setErrors({ confirmPassword: "Passwords do not match" });
      return;
    }

    const action = await dispatch(register({ email, username, password }));

    if (register.rejected.match(action)) {
      const errorMessage = action.payload;

      if (errorMessage === "Username is taken!") {
        setErrors({ username: errorMessage });
      } else if (errorMessage === "Email is taken!") {
        setErrors({ email: errorMessage });
      } else if (
        errorMessage === "Please provide username, email and password"
      ) {
        setErrors({ general: errorMessage });
      } else {
        setErrors({ general: "An error occurred during registration." });
      }
    } else {
      setSnackbar({
        open: true,
        message: "Account created successfully!",
        severity: "success",
      });
      setSuccess(true);

      setTimeout(() => {
        window.location.href = "/login";
      }, 1500);
    }
  };

  return (
    <div className="max-w-md w-full mx-auto px-4 pt-20">
      <div className="bg-white dark:bg-gray-800 shadow-lg rounded-lg p-8">
        <h1 className="text-2xl font-bold mb-6 text-center text-gray-800 dark:text-gray-200">
          Create an Account
        </h1>

        {success && (
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="mb-4 p-4 text-green-800 bg-green-100 border border-green-200 rounded-md"
          >
            Account created successfully! Redirecting to login...
          </motion.div>
        )}

        <form onSubmit={handleSignUp}>
          {errors.general && (
            <div className="mb-4 text-red-500">{errors.general}</div>
          )}
          <div className="mb-4">
            <label className="block mb-2 text-gray-600 dark:text-gray-300">
              Email
            </label>
            <div className="flex items-center border rounded-md bg-white dark:bg-gray-700">
              <Mail className="ml-2 text-gray-400" />
              <input
                type="email"
                name="email"
                placeholder="enter your email"
                className="w-full px-3 py-2 focus:outline-none bg-transparent text-gray-800 dark:text-gray-200"
                value={email}
                onChange={onChange}
                required
              />
            </div>
            {errors.email && (
              <div className="text-red-500 text-sm mt-1">{errors.email}</div>
            )}
          </div>
          <div className="mb-4">
            <label className="block mb-2 text-gray-600 dark:text-gray-300">
              Username
            </label>
            <div className="flex items-center border rounded-md bg-white dark:bg-gray-700">
              <User className="ml-2 text-gray-400" />
              <input
                type="text"
                name="username"
                placeholder="enter your username"
                className="w-full px-3 py-2 focus:outline-none bg-transparent text-gray-800 dark:text-gray-200"
                value={username}
                onChange={onChange}
                required
              />
            </div>
            {errors.username && (
              <div className="text-red-500 text-sm mt-1">{errors.username}</div>
            )}
          </div>
          <div className="mb-4">
            <label className="block mb-2 text-gray-600 dark:text-gray-300">
              Password
            </label>
            <div className="flex items-center border rounded-md bg-white dark:bg-gray-700">
              <Lock className="ml-2 text-gray-400" />
              <input
                type="password"
                name="password"
                placeholder="Password"
                className="w-full px-3 py-2 focus:outline-none bg-transparent text-gray-800 dark:text-gray-200"
                value={password}
                onChange={onChange}
                required
              />
            </div>
            {errors.password && (
              <div className="text-red-500 text-sm mt-1">{errors.password}</div>
            )}
          </div>
          <div className="mb-6">
            <label className="block mb-2 text-gray-600 dark:text-gray-300">
              Confirm Password
            </label>
            <div className="flex items-center border rounded-md bg-white dark:bg-gray-700">
              <Lock className="ml-2 text-gray-400" />
              <input
                type="password"
                name="confirmPassword"
                placeholder="Confirm Password"
                className="w-full px-3 py-2 focus:outline-none bg-transparent text-gray-800 dark:text-gray-200"
                value={confirmPassword}
                onChange={onChange}
                required
              />
            </div>
            {errors.confirmPassword && (
              <div className="text-red-500 text-sm mt-1">
                {errors.confirmPassword}
              </div>
            )}
          </div>
          <button
            type="submit"
            className="w-full bg-indigo-600 text-white py-2 rounded-md hover:bg-indigo-700 transition duration-300 shadow"
          >
            Sign Up
          </button>
          <div className="mt-4 text-center">
            <Link to="/login" className="text-indigo-600 hover:underline">
              Already have an account? Login
            </Link>
          </div>
        </form>
      </div>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={handleCloseSnackbar}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity={snackbar.severity}
          sx={{ width: "100%" }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </div>
  );
}
