import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axios from "../../utils/api";

export const fetchMedia = createAsyncThunk("media/fetchAllMedia", async () => {
  try {
    //  returns something like:
    // [
    //   { mediaId: 1, title: "Book1", ..., categories: [ {categoryId:12, categoryName:"Fiction"} ] },
    //   { mediaId: 2, ... },
    //   ...
    // ]
    const response = await axios.get("/media/by-username");
    return response.data;
  } catch (error) {
    throw Error(error.response?.data?.message || error.message);
  }
});

export const assignCategoryToMedia = createAsyncThunk(
  "media/assignCategoryToMedia",
  async ({ mediaId, categoryId }, { rejectWithValue }) => {
    try {
      // Suppose the server returns the updated media object
      // after assigning the category
      const response = await axios.post(
        `/media/${mediaId}/assign-category/${categoryId}`
      );
      return response.data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || "Failed to assign category."
      );
    }
  }
);

export const removeCategoryFromMedia = createAsyncThunk(
  "media/removeCategoryFromMedia",
  async ({ mediaId, categoryId }, { rejectWithValue }) => {
    try {
      const response = await axios.delete(
        `/media/${mediaId}/remove-category/${categoryId}`
      );
      return response.data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || "Failed to remove category."
      );
    }
  }
);

export const addMedia = createAsyncThunk(
  "media/add",
  async (media, { rejectWithValue }) => {
    try {
      // Expect {title, type, ..., categories: [12, 13]}
      const response = await axios.post("/media", media);
      return response.data; // newly created media object
    } catch (error) {
      if (error.response?.status === 401) {
        return rejectWithValue("Unauthorized. Please log in again.");
      }
      return rejectWithValue(
        error.response?.data?.message || "An unexpected error occurred"
      );
    }
  }
);

export const updateMedia = createAsyncThunk(
  "media/update",
  async ({ id, media }, { rejectWithValue }) => {
    try {
      // media = { title, type, categories: [12, 13], ... }
      const response = await axios.put(`/media/${id}`, media);
      return response.data; // updated media object
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || "Failed to update media."
      );
    }
  }
);

export const toggleFavorite = createAsyncThunk(
  "media/toggleFavorite",
  async ({ id, isFavorite }, { rejectWithValue }) => {
    try {
      const response = await axios.put(`/media/${id}/favorite`, { isFavorite });
      return response.data; // updated media
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || "Failed to toggle favorite."
      );
    }
  }
);

export const deleteMedia = createAsyncThunk(
  "media/delete",
  async (id, { rejectWithValue }) => {
    try {
      await axios.delete(`/media/${id}`);
      return id;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || "Failed to delete media."
      );
    }
  }
);

const mediaSlice = createSlice({
  name: "media",
  initialState: {
    media: [],
    loading: false,
    status: "idle",
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      // Clear on logout
      .addCase("auth/logout", () => ({
        media: [],
        loading: false,
        status: "idle",
        error: null,
      }))

      // fetchMedia
      .addCase(fetchMedia.pending, (state) => {
        state.status = "loading";
        state.loading = true;
      })
      .addCase(fetchMedia.fulfilled, (state, action) => {
        state.status = "succeeded";
        state.media = action.payload;
        state.loading = false;
      })
      .addCase(fetchMedia.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.error.message;
        state.loading = false;
      })

      // addMedia
      .addCase(addMedia.pending, (state) => {
        state.loading = true;
      })
      .addCase(addMedia.fulfilled, (state, action) => {
        state.media.push(action.payload);
        state.loading = false;
      })
      .addCase(addMedia.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.error.message;
        state.loading = false;
      })

      // updateMedia
      .addCase(updateMedia.fulfilled, (state, action) => {
        const updatedMedia = action.payload;
        const index = state.media.findIndex(
          (m) => m.mediaId === updatedMedia.mediaId
        );
        if (index !== -1) {
          state.media[index] = updatedMedia;
        }
      })
      .addCase(updateMedia.rejected, (state, action) => {
        state.error = action.payload;
      })

      // deleteMedia
      .addCase(deleteMedia.fulfilled, (state, action) => {
        const deletedId = action.payload;
        state.media = state.media.filter((m) => m.mediaId !== deletedId);
      })
      .addCase(deleteMedia.rejected, (state, action) => {
        state.error = action.payload;
      })

      // toggleFavorite
      .addCase(toggleFavorite.fulfilled, (state, action) => {
        const updatedMedia = action.payload;
        const index = state.media.findIndex(
          (m) => m.mediaId === updatedMedia.mediaId
        );
        if (index !== -1) {
          state.media[index] = updatedMedia;
        }
      })
      .addCase(toggleFavorite.rejected, (state, action) => {
        state.error = action.payload;
      })

      // Assign Category
      .addCase(assignCategoryToMedia.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(assignCategoryToMedia.fulfilled, (state, action) => {
        // If server returns updated media in action.payload
        const updatedMedia = action.payload;
        const index = state.media.findIndex(
          (m) => m.mediaId === updatedMedia.mediaId
        );
        if (index !== -1) {
          state.media[index] = updatedMedia;
        }
        state.loading = false;
      })
      .addCase(assignCategoryToMedia.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Remove Category
      .addCase(removeCategoryFromMedia.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(removeCategoryFromMedia.fulfilled, (state, action) => {
        // If server returns updated media in action.payload
        const updatedMedia = action.payload;
        const index = state.media.findIndex(
          (m) => m.mediaId === updatedMedia.mediaId
        );
        if (index !== -1) {
          state.media[index] = updatedMedia;
        }
        state.loading = false;
      })
      .addCase(removeCategoryFromMedia.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export default mediaSlice.reducer;
