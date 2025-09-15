import React, { useState, useEffect } from "react";
import {
  Box,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Typography,
  List,
  ListItem,
  ListItemText,
} from "@mui/material";
import { motion } from "framer-motion";
import { useSelector, useDispatch } from "react-redux";
import { fetchMedia } from "../../redux/slices/mediaSlice";
import { fetchLoans } from "../../redux/slices/loanSlice";
import dayjs from "dayjs";

export default function MediaLoanTimeline() {
  const dispatch = useDispatch();

  const { media } = useSelector((state) => state.media);
  const { loans } = useSelector((state) => state.loans);

  const [selectedMediaId, setSelectedMediaId] = useState("");
  const [filteredLoans, setFilteredLoans] = useState([]);

  useEffect(() => {
    dispatch(fetchMedia());
    dispatch(fetchLoans());
  }, [dispatch]);

  useEffect(() => {
    if (selectedMediaId) {
      const filtered = loans.filter(
        (loan) => loan.media?.mediaId === selectedMediaId
      );
      setFilteredLoans(filtered);
    } else {
      setFilteredLoans([]);
    }
  }, [selectedMediaId, loans]);

  return (
    <motion.div
      className="bg-white dark:bg-gray-800 shadow-lg rounded-xl p-6 border border-gray-200 dark:border-gray-700"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: 0.4 }}
    >
      <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-4">
        Media Loan Timeline
      </h2>

      {/* Media Selection */}
      <Box sx={{ mb: 4 }}>
        <FormControl fullWidth>
          <InputLabel id="media-select-label">Select Media</InputLabel>
          <Select
            labelId="media-select-label"
            value={selectedMediaId}
            onChange={(e) => setSelectedMediaId(e.target.value)}
          >
            {media.map((item) => (
              <MenuItem key={item.mediaId} value={item.mediaId}>
                {item.title}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>

      {/* Timeline */}
      {selectedMediaId && (
        <Box>
          <Typography variant="h6" sx={{ mb: 2 }}>
            Timeline of Loans
          </Typography>
          <List>
            {filteredLoans.map((loan) => (
              <ListItem
                key={loan.loanId}
                sx={{
                  display: "flex",
                  flexDirection: "column",
                  alignItems: "flex-start",
                  mb: 2,
                  p: 2,
                  border: "1px solid",
                  borderColor: "rgba(107, 114, 128, 0.5)",
                  borderRadius: "8px",
                  backgroundColor: "rgba(255, 255, 255, 0.1)",
                }}
              >
                <ListItemText
                  primary={`Borrowed by: ${
                    loan.person?.firstName || "Unknown"
                  } ${loan.person?.lastName || ""}`}
                  secondary={`Borrowed At: ${dayjs(loan.borrowedAt).format(
                    "YYYY-MM-DD HH:mm:ss"
                  )}`}
                />
                {loan.returnedAt ? (
                  <ListItemText
                    secondary={`Returned At: ${dayjs(loan.returnedAt).format(
                      "YYYY-MM-DD HH:mm:ss"
                    )}`}
                  />
                ) : (
                  <Typography variant="body2" sx={{ mt: 1, color: "orange" }}>
                    Not yet returned
                  </Typography>
                )}
              </ListItem>
            ))}
          </List>
        </Box>
      )}
    </motion.div>
  );
}
