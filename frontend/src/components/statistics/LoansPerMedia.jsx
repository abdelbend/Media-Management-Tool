import React, { useState, useEffect } from "react";
import { useTheme } from "@mui/material/styles";
import {
  Box,
  FormControl,
  Typography,
  List,
  ListItem,
  ListItemText,
  IconButton,
  Autocomplete,
  TextField,
} from "@mui/material";
import { motion } from "framer-motion";
import { useSelector, useDispatch } from "react-redux";
import { fetchMedia } from "../../redux/slices/mediaSlice";
import { fetchLoans } from "../../redux/slices/loanSlice";
import PrintIcon from "@mui/icons-material/Print";
import dayjs from "dayjs";

export default function LoansPerMedia() {
  const dispatch = useDispatch();
  const theme = useTheme();
  const primaryTextColor = theme.palette.mode === "dark" ? "white" : "black";
  const secondaryTextColor =
    theme.palette.mode === "dark" ? "#BBBBBB" : "#666666";

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

  const handlePrint = () => {
    const content = document.getElementById("loans-timeline");
    const printWindow = window.open("", "Print", "width=600,height=800");
    printWindow.document.write(content.outerHTML);
    printWindow.document.close();
    printWindow.focus();
    printWindow.print();
    printWindow.close();
  };

  return (
    <motion.div
      className="bg-white dark:bg-gray-800 shadow-lg p-6 rounded-xl border border-gray-200 dark:border-gray-700"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: 0.4 }}
      sx={{
        maxWidth: "1200px",
        width: "100%",
      }}
    >
      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        sx={{ maxWidth: "1200px", width: "100%", margin: "0 auto" }}
      >
        <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100">
          Loans Per Media
        </h2>
        {/* Print Button */}
        <IconButton
          color="primary"
          aria-label="print loans per media"
          onClick={handlePrint}
        >
          <PrintIcon />
        </IconButton>
      </Box>

      {/* Media Selection */}
      <Box sx={{ mb: 2 }}>
        <FormControl fullWidth>
          <Autocomplete
            options={media}
            getOptionLabel={(option) =>
              option.title || `Unknown (ID: ${option.mediaId})`
            }
            renderInput={(params) => (
              <TextField
                {...params}
                label="Select Media"
                variant="outlined"
                placeholder="Search or select media"
              />
            )}
            onChange={(event, value) =>
              setSelectedMediaId(value ? value.mediaId : "")
            }
            ListboxProps={{
              style: { maxHeight: "200px", overflow: "auto" },
            }}
          />
        </FormControl>
      </Box>

      {/* Timeline */}
      {selectedMediaId && (
        <Box id="loans-timeline">
          <Typography variant="h6" sx={{ mb: 2, color: primaryTextColor }}>
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
                  primaryTypographyProps={{ sx: { color: primaryTextColor } }}
                  secondary={`Borrowed At: ${dayjs(loan.borrowedAt).format(
                    "YYYY-MM-DD HH:mm:ss"
                  )}`}
                  secondaryTypographyProps={{
                    sx: { color: secondaryTextColor },
                  }}
                />
                {loan.returnedAt ? (
                  <ListItemText
                    secondary={`Returned At: ${dayjs(loan.returnedAt).format(
                      "YYYY-MM-DD HH:mm:ss"
                    )}`}
                    secondaryTypographyProps={{
                      sx: { color: secondaryTextColor },
                    }}
                  />
                ) : (
                  <Typography
                    variant="body2"
                    sx={{
                      mt: 1,
                      color:
                        theme.palette.mode === "dark" ? "#FFB74D" : "orange",
                    }}
                  >
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
