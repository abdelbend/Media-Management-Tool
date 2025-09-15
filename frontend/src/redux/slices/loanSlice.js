import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axios from "../../utils/api";

export const fetchLoans = createAsyncThunk("loans/fetchLoans", async () => {
  try {
    const response = await axios.get("loans/all");
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || error.message);
  }
});

export const createLoan = createAsyncThunk(
  "loans/createLoan",
  async ({ mediaId, personId, dueDate, borrowedAt }, { rejectWithValue }) => {
    try {
      const response = await axios.post(`/loans/${mediaId}/${personId}`, null, {
        params: { dueDate, borrowedAt },
      });
      return response.data;
    } catch (error) {
      const message = error.response?.data?.message || error.message;
      return rejectWithValue(message);
    }
  }
);

export const returnLoan = createAsyncThunk(
  "loans/returnLoan",
  async ({ loanId, returnedAt }, { rejectWithValue }) => {
    try {
      const response = await axios.put(`/loans/${loanId}/return`, {
        returnedAt,
      });
      return response.data;
    } catch (error) {
      const message = error.response?.data?.message || error.message;
      return rejectWithValue(message);
    }
  }
);

export const fetchOverdueLoans = createAsyncThunk(
  "loans/fetchOverdueLoans",
  async (currentDate, { rejectWithValue }) => {
    try {
      const response = await axios.get("/loans/overdue", {
        params: { currentDate },
      });
      return response.data;
    } catch (error) {
      const message = error.response?.data?.message || error.message;
      console.error("Error fetching overdue loans:", message);
      return rejectWithValue(message);
    }
  }
);

export const fetchActiveLoans = createAsyncThunk(
  "loans/fetchActiveLoans",
  async (_, { rejectWithValue }) => {
    try {
      const response = await axios.get("/loans/active");
      return response.data;
    } catch (error) {
      const message = error.response?.data?.message || error.message;
      console.error("Error fetching active loans:", message);
      return rejectWithValue(message);
    }
  }
);

const initialState = {
  loans: [],
  overdueLoans: [],
  activeLoans: [],
  loading: false,
  error: null,
  success: false,
};

const loanSlice = createSlice({
  name: "loans",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase("auth/logout", () => ({
        loans: [],
        overdueLoans: [],
        activeLoans: [],
        loading: false,
        error: null,
        success: false,
      }))
      .addCase(fetchLoans.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchLoans.fulfilled, (state, action) => {
        state.loading = false;
        state.loans = action.payload;
      })
      .addCase(fetchLoans.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch loans.";
      })
      .addCase(fetchOverdueLoans.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchOverdueLoans.fulfilled, (state, action) => {
        state.loading = false;
        state.overdueLoans = action.payload;
      })
      .addCase(fetchOverdueLoans.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch overdue loans.";
      })
      .addCase(fetchActiveLoans.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchActiveLoans.fulfilled, (state, action) => {
        state.loading = false;
        state.activeLoans = action.payload;
      })
      .addCase(fetchActiveLoans.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch active loans.";
      })
      // createLoan
      .addCase(createLoan.pending, (state) => {
        state.loading = true;
      })
      .addCase(createLoan.fulfilled, (state, action) => {
        state.loading = false;
        state.success = true;

        state.loans.push(action.payload);

        const { mediaId } = action.payload;
      })
      .addCase(createLoan.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to create loan.";
      })
      .addCase(returnLoan.pending, (state) => {
        state.loading = true;
      })
      .addCase(returnLoan.fulfilled, (state, action) => {
        state.loading = false;
        state.success = true;
        const loanIndex = state.loans.findIndex(
          (loan) => loan.loanId === returnLoan
        );
        if (loanIndex !== -1) {
          state.loans[loanIndex].returned = new Date().toISOString();
          state.loans[loanIndex].media.mediaState = "AVAILABLE";
        }
      })
      .addCase(returnLoan.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to return loan.";
      });
  },
});

export default loanSlice.reducer;
