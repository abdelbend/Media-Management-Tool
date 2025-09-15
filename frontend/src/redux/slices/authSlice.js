import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";

import axios from "../../utils/api";
import { jwtDecode } from "jwt-decode";

export const login = createAsyncThunk("auth/login", async (userCredentials) => {
  const response = await axios.post("/auth/login", userCredentials);
  const { accessToken } = response.data;
  const user = jwtDecode(accessToken);
  const userName = user.sub;
  return { user: userName, accessToken };
});

export const register = createAsyncThunk(
  "auth/register",
  async (userCredentials, { rejectWithValue }) => {
    try {
      const response = await axios.post("/auth/register", userCredentials);
      return response.data;
    } catch (error) {
      if (error.response) {
        const errorMessage =
          error.response.data.message ||
          error.response.data ||
          "Registration failed.";
        return rejectWithValue(errorMessage);
      }
      return rejectWithValue("An unexpected error occurred.");
    }
  }
);

const authSlice = createSlice({
  name: "auth",
  initialState: { user: null, token: null, status: "idle", error: null },
  reducers: {
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.status = "idle";
      state.error = null;

      // Remove token from local storage
      localStorage.removeItem("token");

      // delete all states
      state.media = [];
      state.persons = [];
      state.users = [];
      state.loans = [];
    },
    restoreAuth: (state, action) => {
      const { user, token } = action.payload;
      state.user = user;
      state.token = token;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.status = "loading";
      })
      .addCase(login.fulfilled, (state, action) => {
        state.status = "succeeded";
        state.user = action.payload.user;
        // Store accessToken from the response as token
        state.token = action.payload.accessToken; // This is where you extract accessToken
        localStorage.setItem("token", action.payload.accessToken); // Store the token in local storage
      })
      .addCase(login.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.error.message;
      })
      .addCase(register.pending, (state) => {
        state.status = "loading";
      })
      .addCase(register.fulfilled, (state, action) => {
        state.status = "succeeded";
        state.user = action.payload.user;
        state.token = action.payload.accessToken;
        localStorage.setItem("token", action.payload.accessToken);
      })
      .addCase(register.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.error.message;
      });
  },
});

export const { logout, restoreAuth } = authSlice.actions;
export default authSlice.reducer;
