import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axios from "../../utils/api";

export const fetchPersonsByUsername = createAsyncThunk(
  "persons/fetchByUsername",
  async (username, { rejectWithValue }) => {
    try {
      const response = await axios.get(
        `/persons/by-username?username=${username}`
      );
      return response.data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message ||
          "An error occurred while fetching persons"
      );
    }
  }
);

export const fetchPersons = createAsyncThunk(
  "persons/fetchAllPersons",
  async () => {
    try {
      const response = await axios.get("/persons");
      return response.data;
    } catch (error) {
      throw Error(error.response?.data?.message || error.message);
    }
  }
);

export const addPerson = createAsyncThunk(
  "persons/add",
  async (person, { rejectWithValue }) => {
    try {
      const response = await axios.post("/persons", person); // Token will automatically be added
      return response.data;
    } catch (error) {
      if (error.response?.status === 401) {
        // Optionally, dispatch a logout action or handle unauthorized access
        return rejectWithValue("Unauthorized. Please log in again.");
      }
      // Handle other errors
      return rejectWithValue(
        error.response?.data?.message || "An unexpected error occurred"
      );
    }
  }
);

export const updatePerson = createAsyncThunk(
  "persons/update",
  async ({ id, person }) => {
    const response = await axios.put(`/persons/${id}`, person);
    return response.data;
  }
);

export const deletePerson = createAsyncThunk(
  "persons/delete",
  async (id, { rejectWithValue }) => {
    try {
      await axios.delete(`/persons/${id}`);
      return id;
    } catch (error) {
      if (error.response?.status === 401) {
        console.warn("Unauthorized access while deleting person.");
        return rejectWithValue("Unauthorized access. Please log in again.");
      }
      return rejectWithValue(
        error.response?.data?.message || "Failed to delete person"
      );
    }
  }
);

const personSlice = createSlice({
  name: "persons",
  initialState: {
    persons: [],
    loading: false,
    status: "idle",
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase("auth/logout", () => ({
        persons: [],
        loading: false,
        status: "idle",
        error: null,
      }))
      .addCase(fetchPersonsByUsername.pending, (state) => {
        state.status = "loading";
        state.loading = true;
      })
      .addCase(fetchPersonsByUsername.fulfilled, (state, action) => {
        state.status = "succeeded";
        state.persons = action.payload;
        state.loading = false;
      })
      .addCase(fetchPersonsByUsername.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.payload || action.error.message;
        state.loading = false;
      })
      .addCase(fetchPersons.pending, (state) => {
        state.status = "loading";
        state.loading = true;
      })
      .addCase(fetchPersons.fulfilled, (state, action) => {
        state.status = "succeeded";
        state.persons = action.payload;
        state.loading = false;
      })
      .addCase(fetchPersons.rejected, (state, action) => {
        state.status = "failed"; // Set failed state if there's an error
        state.error = action.error.message; // Store error message
        state.loading = false;
      })
      .addCase(addPerson.fulfilled, (state, action) => {
        state.persons.push(action.payload);
        state.loading = false;
      })
      .addCase(addPerson.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.error.message;
        state.loading = false;
      })
      .addCase(addPerson.pending, (state) => {
        state.loading = true;
      })
      .addCase(updatePerson.fulfilled, (state, action) => {
        state.persons = state.persons.map((p) =>
          p.personId === action.payload.personId ? action.payload : p
        );
      })
      .addCase(deletePerson.fulfilled, (state, action) => {
        state.persons = state.persons.filter((p) => p.id !== action.payload);
        state.loading = false;
      })
      .addCase(deletePerson.rejected, (state, action) => {
        state.error = action.payload || "Failed to delete person";
        state.loading = false;
      })
      .addCase(deletePerson.pending, (state) => {
        state.loading = true;
      });
  },
});

export default personSlice.reducer;
