import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./slices/authSlice";
import categoryReducer from "./slices/categorySlice";
import personReducer from "./slices/personSlice";
import mediaReducer from "./slices/mediaSlice";
import userReducer from "./slices/userSlice";
import loanReducer from "./slices/loanSlice";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    persons: personReducer,
    media: mediaReducer,
    loans: loanReducer,
    users: userReducer,
    categories: categoryReducer,
  },
});

export default store;
