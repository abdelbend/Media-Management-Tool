import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { login } from "../redux/slices/authSlice";
import { fetch5Users } from "../redux/slices/userSlice";
import { User, Lock } from "lucide-react";
import { Link } from "react-router-dom";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  const icon = <User size={26} />;

  const user = useSelector((state) => state.auth.user);

  useEffect(() => {
    if (user) {
      dispatch(fetch5Users());
    }
  }, [user, dispatch]);

  const handleLogin = async (e) => {
    e.preventDefault();
    const action = await dispatch(login({ username, password }));
    if (action.meta.requestStatus === "fulfilled") {
      navigate("/");
    } else {
      setError("Invalid username or password");
      setTimeout(() => setError(""), 4000);
    }
  };

  const users = useSelector((state) => state.users.users);
  const countUsers = users.length.toLocaleString();

  useEffect(() => {
    dispatch(fetch5Users());
  }, [dispatch]);

  return (
    <div
      className={` ${
        users.length > 0
          ? "max-w-4xl w-full mx-auto px-4 pt-28 overflow-auto"
          : "max-w-md w-full mx-auto px-4 pt-20 overflow-auto"
      }`}
    >
      <div className="flex flex-col md:flex-row bg-white dark:bg-gray-800 shadow-lg rounded-lg overflow-hidden">
        {/* Login Form */}
        <div
          className={`w-full  ${
            users.length > 0 && users.length <= 5 ? "md:w-1/2" : "w-3/5 mx-auto"
          } p-8`}
        >
          <h2 className="text-2xl font-semibold text-gray-800 dark:text-gray-200 mb-6 text-center">
            Login to Your Account
          </h2>
          <form onSubmit={handleLogin}>
            {error && <div className="mb-4 text-red-500">{error}</div>}
            <div className="mb-4">
              <label className="block mb-2 text-gray-600 dark:text-gray-300">
                Username
              </label>
              <div className="flex items-center border rounded-md bg-white dark:bg-gray-700">
                <User className="ml-2 text-gray-400" />
                <input
                  type="text"
                  className="w-full px-3 py-2 focus:outline-none bg-transparent text-gray-800 dark:text-gray-200"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>
            </div>
            <div className="mb-4">
              <label className="block mb-2 text-gray-600 dark:text-gray-300">
                Password
              </label>
              <div className="flex items-center border rounded-md bg-white dark:bg-gray-700">
                <Lock className="ml-2 text-gray-400" />
                <input
                  type="password"
                  className="w-full px-3 py-2 focus:outline-none bg-transparent text-gray-800 dark:text-gray-200"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="mb-4 flex items-center"></div>

            <button
              type="submit"
              className="w-full bg-indigo-600 text-white py-2 rounded-md hover:bg-indigo-700 transition duration-300 shadow"
            >
              Login
            </button>

            <div className="mt-4 text-center">
              <Link to="/signup" className="text-indigo-600 hover:underline">
                Don't have an account? Sign up
              </Link>
            </div>
          </form>
        </div>
        {/* User Icons Section */}
        {users.length > 0 && users.length <= 5 ? (
          <div className="w-full md:w-1/2 p-4 bg-gray-50 dark:bg-gray-900 align-center justify-center">
            <h2 className="text-xl font-semibold text-gray-800 dark:text-gray-200 mb-2 text-center">
              Recent Users
            </h2>
            <div
              className={`grid grid-cols-2 gap-4 ${
                users.length === 5 ? "grid-rows-3" : "" // Adjust rows for 5 users
              }`}
            >
              {Array.isArray(users) &&
                users.map((user, index) => (
                  <div
                    key={index}
                    className={`flex flex-col items-center bg-white dark:bg-gray-800 p-4 rounded-lg shadow cursor-pointer ${
                      users.length === 5 && index === 4
                        ? "col-span-2 flex justify-center"
                        : ""
                    }`}
                    onClick={() => setUsername(user.username)} // Set username on click
                  >
                    <div className="p-4 bg-indigo-600 text-white rounded-full mb-2 ">
                      {icon}
                    </div>
                    <span className="text-gray-800 dark:text-gray-200">
                      {user.username}
                    </span>
                  </div>
                ))}
            </div>
          </div>
        ) : null}
      </div>
    </div>
  );
}
