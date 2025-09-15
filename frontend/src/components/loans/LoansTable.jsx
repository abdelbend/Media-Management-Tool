import React, { useEffect, useState } from "react";
import {
  Box,
  Button,
  CircularProgress,
  Typography,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Snackbar,
  Alert as MuiAlert,
  useMediaQuery,
  Autocomplete,
} from "@mui/material";
import {
  DataGrid,
  GridToolbarContainer,
  GridToolbarExport,
} from "@mui/x-data-grid"; 
import { useDispatch, useSelector } from "react-redux";
import {
  fetchLoans,
  fetchOverdueLoans,
  createLoan,
  fetchActiveLoans,
  returnLoan,
} from "../../redux/slices/loanSlice";
import { fetchPersonsByUsername } from "../../redux/slices/personSlice";
import { fetchMedia } from "../../redux/slices/mediaSlice";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider, DatePicker } from "@mui/x-date-pickers";
import dayjs from "dayjs";

const LoansTable = () => {
  const dispatch = useDispatch();
  const isMobile = useMediaQuery("(max-width:600px)");
  const { persons, loading: personsLoading } = useSelector(
    (state) => state.persons
  );
  const { media, loading: mediaLoading } = useSelector((state) => state.media);
  const { loans, loading: loansLoading } = useSelector((state) => state.loans);
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success",
  });

  const [filter, setFilter] = useState("all");
  const [openDialog, setOpenDialog] = useState(false);
  const [newLoan, setNewLoan] = useState({
    personId: "",
    mediaId: "",
    dueDate: dayjs().add(7, "day").format("YYYY-MM-DD"),
    borrowedAt: dayjs().format("YYYY-MM-DDTHH:mm:ss"),
  });
  const [openReturnDialog, setOpenReturnDialog] = useState(false);
  const [selectedLoanId, setSelectedLoanId] = useState(null);
  const [returnDate, setReturnDate] = useState(
    dayjs().format("YYYY-MM-DD HH:mm:ss")
  );
  const availableMedia = media.filter(
    (item) => item.mediaState === "AVAILABLE"
  );
  const [returnDateError, setReturnDateError] = useState("");

  const username = useSelector((state) => state.auth.username);

  useEffect(() => {
    if (filter === "all") {
      dispatch(fetchLoans());
    } else if (filter === "overdue") {
      dispatch(fetchOverdueLoans(dayjs().format("YYYY-MM-DD")));
    } else if (filter === "active") {
      dispatch(fetchActiveLoans());
    }
  }, [dispatch, filter]);

  useEffect(() => {
    dispatch(fetchActiveLoans());
    dispatch(fetchPersonsByUsername(username));
    dispatch(fetchMedia(username));
  }, [dispatch, username]);

  const handleCloseSnackbar = () => {
    setSnackbar({ open: false, message: "", severity: "success" });
  };

  const handleSaveNewLoan = async () => {
    if (!newLoan.personId || !newLoan.mediaId) {
      alert("Please select a person and ensure the media is valid.");
      return;
    }
    if(dayjs(newLoan.dueDate).isBefore(dayjs(newLoan.borrowedAt))) {
     alert("Due date cannot be before the borrowed date!");
      return;
    }
    const loanToSave = {
      personId: newLoan.personId,
      mediaId: newLoan.mediaId,
      dueDate: newLoan.dueDate,
      borrowedAt: newLoan.borrowedAt,
    };
    try {
      await dispatch(createLoan(loanToSave)); 
      await dispatch(fetchMedia(username)); 
      setOpenDialog(false); 
      setNewLoan({
        personId: "",
        mediaId: "",
        dueDate: dayjs().add(7, "day").format("YYYY-MM-DD"),
        borrowedAt: dayjs().format("YYYY-MM-DDTHH:mm:ss"),
      });

      dispatch(fetchLoans());
      dispatch(fetchActiveLoans());
      dispatch(fetchOverdueLoans());

      setSnackbar({
        open: true,
        message: `Loan created successfully!`,
        severity: "success",
      });
    } catch (error) {
      setSnackbar({
        open: true,
        message: "Failed to create loan.",
        severity: "error",
      });
    }
  };

  const handleReturnLoan = (loanId) => {
    setSelectedLoanId(loanId);
    setReturnDate(dayjs().format("YYYY-MM-DD HH:mm:ss"));
    setReturnDateError("");
    setOpenReturnDialog(true);
  };


  const handleConfirmReturn = async () => {
    const selectedLoan = loans.find((loan) => loan.loanId === selectedLoanId);
    console.log("Selected Loan:", selectedLoan);
    if (!selectedLoanId) return;

    if (dayjs(returnDate).isBefore(dayjs(selectedLoan.borrowedAt))) {
      setReturnDateError(
        "Return date cannot be earlier than the borrowed date."
      );
      return; 
    }
    try {
      await dispatch(
        returnLoan({
          loanId: selectedLoanId,
          returnedAt: returnDate,
        })
      );

      setOpenReturnDialog(false);
      setSnackbar({
        open: true,
        message: `Successfully returned the loan for "${selectedLoan.mediaTitle}"!`,
        severity: "success",
      });
      setSelectedLoanId(null);
      setReturnDate(dayjs().format("YYYY-MM-DD HH:mm:ss"));
      dispatch(fetchLoans());
      dispatch(fetchActiveLoans());
      dispatch(fetchOverdueLoans());
    } catch (error) {
      const errorMessage =
        error.response?.data?.message || "Failed to mark loan as returned.";
      setSnackbar({
        open: true,
        message: errorMessage,
        severity: "error",
      });
    }
  };

  const filteredLoans = (() => {
    if (filter === "overdue") {
      return loans.filter(
        (loan) => dayjs(loan.dueDate).isBefore(dayjs()) && !loan.returnedAt
      );
    }
    if (filter === "active") {
      return loans.filter((loan) => !loan.returnedAt);
    }
    return loans;
  })();

  const columns = [
    //{ field: "loanId", headerName: isMobile ? "ID" : "Loan ID", width: isMobile ? 90 : 120 },
    { field: "personName", headerName: "Person", width: isMobile ? 150 : 200 },
    {
      field: "mediaTitle",
      headerName: "Media Title",
      width: isMobile ? 150 : 280,
    },
    {
      field: "borrowedAt",
      headerName: isMobile ? "Borrowed" : "Borrowed At",
      width: isMobile ? 160 : 220,
    },
    {
      field: "dueDate",
      headerName: isMobile ? "Due" : "Due Date",
      width: isMobile ? 100 : 160,
    },
    {
      field: "ReturnedAt",
      headerName: "Status",
      width: isMobile ? 140 : 180,
      renderCell: (params) => {
        const isReturned = !!params.row.returnedAt;

        if (isReturned) {
          return (
            <Typography variant="body2" color="textSecondary">
              Returned at:
              <br />
              {dayjs(params.row.returnedAt).format("YYYY-MM-DD, HH:mm:ss")}
            </Typography>
          );
        }

        return (
          <Button
            variant="contained"
            color="secondary"
            onClick={() => handleReturnLoan(params.row.loanId)}
          >
            Return
          </Button>
        );
      },
    },
  ];

  const rows = filteredLoans.map((loan) => ({
    id: loan.loanId,
    loanId: loan.loanId,
    personName:
      loan.person?.firstName + " " + loan.person?.lastName ||
      `Unknown (ID: ${loan.person?.personId})`,

    mediaTitle: loan.media?.title || `Unknown (ID: ${loan.media?.mediaId})`,
    borrowedAt: dayjs(loan.borrowedAt).format("YYYY-MM-DD HH:mm:ss"),
    dueDate: dayjs(loan.dueDate).format("YYYY-MM-DD"),
    returnedAt: loan.returnedAt,
  }));

  function CustomToolbar() {
    return (
      <GridToolbarContainer>
        <GridToolbarExport />
      </GridToolbarContainer>
    );
  }
  return (
    <Box className="p-6 bg-gray-100 dark:bg-gray-900 min-h-screen">
      <Box
        mb={2}
        display="flex"
        flexDirection={{ xs: "column", sm: "row" }}
        gap={1.9}
      >
        {/* Add New Loan Button */}
        <Button
          variant="contained"
          color="primary"
          onClick={() => setOpenDialog(true)}
          size="small"
          className="bg-blue-500 hover:bg-blue-600 dark:bg-blue-700 dark:hover:bg-blue-800"
        >
          Add New Loan
        </Button>
        {/* Filter Buttons */}
        <Box className="ml-4 flex space-x-2">
          <Button
            variant={filter === "all" ? "contained" : "outlined"}
            color="primary"
            onClick={() => setFilter("all")}
            size="small"
          >
            All Loans
          </Button>
          <Button
            variant={filter === "overdue" ? "contained" : "outlined"}
            onClick={() => setFilter("overdue")}
            size="small"
          >
            Overdue Loans
          </Button>
          <Button
            variant={filter === "active" ? "contained" : "outlined"}
            onClick={() => setFilter("active")}
            size="small"
          >
            Active Loans
          </Button>
        </Box>
      </Box>

      {/* DataGrid Container */}
      <Box className="bg-white dark:bg-gray-800 rounded-lg p-2 overflow-x-auto">
        <div>
          {loansLoading ? (
            <CircularProgress />
          ) : (
            <DataGrid
              rows={rows}
              columns={columns}
              pageSizeOptions={[5, 10, 20, 50]}
              initialState={{
                pagination: {
                  paginationModel: {
                    pageSize: 10,
                  },
                },
              }}
              disableRowSelectionOnClick
              autoHeight
              slots={{ toolbar: CustomToolbar }}
              sx={{
                "& .MuiDataGrid-root": {
                  color: "inherit",
                },
                "& .MuiDataGrid-cell": {
                  borderBottom: "1px solid",
                  borderColor: "inherit",
                },
                "& .MuiDataGrid-columnHeaders": {
                  backgroundColor: "transparent",
                  borderBottom: "1px solid",
                  borderColor: "inherit",
                  color: "inherit",
                  textTransform: "uppercase",
                  fontSize: "0.75rem",
                  fontWeight: "500",
                },
                "& .MuiDataGrid-footerContainer": {
                  backgroundColor: "transparent",
                  borderTop: "1px solid",
                  borderColor: "inherit",
                  color: "inherit",
                },
                "& .MuiDataGrid-row:nth-of-type(odd)": {
                  backgroundColor: "transparent",
                },
                "& .MuiDataGrid-virtualScroller": {
                  backgroundColor: "transparent",
                },
                "& .MuiDataGrid-virtualScroller::-webkit-scrollbar": {
                  width: "8px",
                },
                "& .MuiDataGrid-virtualScroller::-webkit-scrollbar-thumb": {
                  backgroundColor: "rgba(107, 114, 128, 0.5)",
                },
                "& .MuiDataGrid-toolbarContainer": {
                  backgroundColor: "transparent",
                },
              }}
            />
          )}
        </div>
      </Box>

      {/* Return Loan Dialog */}
      <Dialog
        open={openReturnDialog}
        onClose={() => setOpenReturnDialog(false)}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>Return Loan</DialogTitle>
        <DialogContent dividers>
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DatePicker
              label="Return Date"
              value={dayjs(returnDate)}
              onChange={(date) =>
                setReturnDate(date.format("YYYY-MM-DD HH:mm:ss"))
              }
              maxDate={dayjs()} // Restrict to today's date or earlier
              renderInput={(params) => (
                <TextField {...params} fullWidth margin="dense" />
              )}
            />
          </LocalizationProvider>
          {returnDateError && (
            <MuiAlert severity="error">{returnDateError}</MuiAlert>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenReturnDialog(false)}>Cancel</Button>
          <Button onClick={handleConfirmReturn} color="primary">
            Confirm
          </Button>
        </DialogActions>
      </Dialog>

      {/* Dialog for Adding Loan */}
      <Dialog
        open={openDialog}
        onClose={() => setOpenDialog(false)}
        fullWidth
        maxWidth="sm" 
      >
        <DialogTitle>Add New Loan</DialogTitle>
        <DialogContent dividers>
          <FormControl
            fullWidth
            margin="dense"
            sx={{ mt: 2 }}
            disabled={personsLoading}
          >
            <Autocomplete
              options={persons}
              getOptionLabel={(option) =>
                option.firstName || `Unknown (ID: ${option.personId})`
              }
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Person"
                  variant="outlined"
                  fullWidth
                  placeholder="Search or select person"
                />
              )}
              onChange={(event, value) => {
                setNewLoan({
                  ...newLoan,
                  personId: value ? value.personId : "",
                });
              }}
              ListboxProps={{
                style: { maxHeight: "200px", overflow: "auto" },
              }}
            />
          </FormControl>

          <FormControl
            fullWidth
            margin="dense"
            sx={{ mb: 2 }}
            disabled={mediaLoading}
          >
            <Autocomplete
              options={availableMedia}
              getOptionLabel={(option) =>
                option.title || `Unknown (ID: ${option.mediaId})`
              }
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Media"
                  variant="outlined"
                  fullWidth
                  placeholder="Search or select media"
                />
              )}
              onChange={(event, value) => {
                setNewLoan({ ...newLoan, mediaId: value ? value.mediaId : "" });
              }}
              ListboxProps={{
                style: { maxHeight: "200px", overflow: "auto" }, 
              }}
            />
          </FormControl>
          <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DatePicker
                label="Borrowed At"
                value={newLoan.borrowedAt ? dayjs(newLoan.borrowedAt) : null}
                maxDate={dayjs()}
                onChange={(date) =>
                  setNewLoan({
                    ...newLoan,
                    borrowedAt: date
                      ? date.format("YYYY-MM-DDTHH:mm:ss")
                      : null,
                  })
                }
                renderInput={(params) => (
                  <TextField {...params} fullWidth margin="dense" />
                )}
              />
            </LocalizationProvider>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DatePicker
                label="Due Date"
                type="date"
                value={newLoan.dueDate ? dayjs(newLoan.dueDate) : null}
                onChange={(date) =>
                  setNewLoan({
                    ...newLoan,
                    dueDate: date ? date.format("YYYY-MM-DD") : null,
                  })
                }
                renderInput={(params) => (
                  <TextField {...params} fullWidth margin="dense" />
                )}
              />
            </LocalizationProvider>
          </Box>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={handleSaveNewLoan} color="primary">
            Save
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: "top", horizontal: "center" }}
      >
        <MuiAlert
          onClose={handleCloseSnackbar}
          severity={snackbar.severity}
          sx={{ width: "100%", maxWidth: "600px", margin: "0 auto" }}
        >
          {snackbar.message}
        </MuiAlert>
      </Snackbar>
    </Box>
  );
};

export default LoansTable;
