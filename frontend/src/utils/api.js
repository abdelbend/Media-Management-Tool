import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 4000,
});

api.interceptors.request.use(
  (config) => {
    const token =
      localStorage.getItem("token") || sessionStorage.getItem("token");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error); // Reject if there's an error with the request
  }
);

// Response Interceptor to handle token expiration (401 errors)
api.interceptors.response.use(
  (response) => {
    return response; // Pass the response through if it's successful
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      console.error("Unauthorized access. Please log in again.");
    }

    return Promise.reject(error);
  }
);

export default api;
