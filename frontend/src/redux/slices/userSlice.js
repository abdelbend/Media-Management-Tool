import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";

import axios from "../../utils/api";

export const fetch5Users = createAsyncThunk("users/fetch5Users", async () => {
  try {
    const response = await axios.get("/users/returnUsers");
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || error.message);
  }
});

const userSlice = createSlice({
  name: "users",
  initialState: {
    users: [],
    loading: false,
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetch5Users.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetch5Users.fulfilled, (state, action) => {
        state.loading = false;
        state.users = action.payload;
      })
      .addCase(fetch5Users.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      });
  },
});

export default userSlice.reducer;
