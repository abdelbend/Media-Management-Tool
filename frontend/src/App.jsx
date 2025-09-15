import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { jwtDecode } from "jwt-decode";
import { restoreAuth, logout } from "./redux/slices/authSlice";
import { Route, Routes, Navigate, useLocation } from "react-router-dom";

import { useMediaQuery } from "@mui/material";

import Sidebar from "./components/common/Sidebar";
import SideBarMobile from "./components/common/SideBarMobile";
import OverviewPage from "./pages/OverviewPage";
import PersonPage from "./pages/PersonPage";
import LoansPage from "./pages/LoansPage";
import StatisticsPage from "./pages/StatisticsPage";
import LoginPage from "./pages/LoginPage";
import SignUpPage from "./pages/SignUpPage";
import WelcomePage from "./pages/WelcomePage";
import MediaPage from "./pages/MediaPage";
import LandingPage from "./pages/LandingPage";

function App() {
  const user = useSelector((state) => state.auth.user);
  const dispatch = useDispatch();
  const location = useLocation();
  const [showSplash, setShowSplash] = useState(true);
  const isMobile = useMediaQuery("(max-width: 768px)");

  useEffect(() => {
    const token =
      localStorage.getItem("token") || sessionStorage.getItem("token");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        if (decoded.exp * 1000 > Date.now()) {
          dispatch(restoreAuth({ user: decoded.sub, token }));
        } else {
          dispatch(logout());
        }
      } catch (err) {
        console.error("Failed to decode token:", err);
        dispatch(logout());
      }
    }

    // Show splash screen for 1.5 seconds
    const timer = setTimeout(() => setShowSplash(false), 1000);
    return () => clearTimeout(timer);
  }, [dispatch]);

  if (showSplash) {
    return (
      <div className="flex h-screen justify-center items-center bg-gradient-to-r from-yellow-400 via-orange-300 to-yellow-200 text-gray-800">
        <div className="text-center">
          <div className="text-5xl mb-4">🎉</div>
          <p>Getting things ready...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex h-screen bg-gray-900 text-gray-100 overflow-hidden">
      {user && (isMobile ? <SideBarMobile /> : <Sidebar />)}

      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignUpPage />} />
        <Route path="/landing" element={<LandingPage />} />

        {/* Private Routes */}
        {user ? (
          <>
            <Route path="/" element={<OverviewPage />} />
            <Route path="/welcome" element={<WelcomePage />} />
            <Route path="/person" element={<PersonPage />} />
            <Route path="/media" element={<MediaPage />} />
            <Route path="/loans" element={<LoansPage />} />
            <Route path="/statistics" element={<StatisticsPage />} />
          </>
        ) : (
          <Route path="*" element={<Navigate to="/landing" />} />
        )}
      </Routes>
    </div>
  );
}

export default App;
