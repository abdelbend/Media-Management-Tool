import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { LogOut } from "lucide-react";
import { useDispatch, useSelector } from "react-redux";
import { logout } from "../../redux/slices/authSlice";

export default function ProfileMenu() {
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [isPersistent, setIsPersistent] = useState(false);

  const user = useSelector((state) => state.auth.user);

  const handleLogout = () => {
    localStorage.removeItem("token");
    sessionStorage.removeItem("token");

    dispatch(logout());
    console.log("Logout action dispatched user is", user);
    setIsOpen(false);
    navigate("/landing");
  };

  const handleStayLoggedIn = (e) => {
    const checked = e.target.checked;
    setIsPersistent(checked);

    const token =
      localStorage.getItem("token") || sessionStorage.getItem("token");
    if (token) {
      if (checked) {
        localStorage.setItem("token", token);
        sessionStorage.removeItem("token");
      } else {
        sessionStorage.setItem("token", token);
        localStorage.removeItem("token");
      }
    }
  };

  useEffect(() => {
    const isTokenInLocalStorage = !!localStorage.getItem("token");
    setIsPersistent(isTokenInLocalStorage);
  }, []);

  return (
    <div className="relative">
      <button onClick={() => setIsOpen(!isOpen)} className="focus:outline-none">
        {/* Display profile image dynamically if available */}
        <img
          src={user?.profileImage || "../../AdamPos.png"}
          alt="Profile"
          className="w-8 h-8 rounded-full"
        />
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-48 bg-white dark:bg-gray-800 rounded-md shadow-lg py-1 z-20">
          {/* Persistent Token Checkbox */}
          <div className="px-4 py-2">
            <label className="flex items-center text-sm text-gray-700 dark:text-gray-200">
              <input
                type="checkbox"
                checked={isPersistent}
                onChange={handleStayLoggedIn}
                className="mr-2"
              />
              Stay Logged In
            </label>
          </div>

          <button
            className="w-full text-left px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700"
            onClick={handleLogout}
          >
            <div className="flex items-center">
              <LogOut size={16} className="mr-2" />
              Log Out
            </div>
          </button>
        </div>
      )}
    </div>
  );
}
