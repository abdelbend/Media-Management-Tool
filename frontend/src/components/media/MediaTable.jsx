import React, { useState, useCallback, useEffect } from "react";
import {
  DataGrid,
  GridToolbarContainer,
  GridToolbarColumnsButton,
  GridToolbarFilterButton,
  GridToolbarDensitySelector,
  GridToolbarExport,
} from "@mui/x-data-grid";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  MenuItem,
  Snackbar,
  IconButton,
  Alert as MuiAlert,
  FormControl,
  InputLabel,
  Select,
  Chip,
  CircularProgress,
  Typography,
} from "@mui/material";
import { LocalizationProvider, DatePicker } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";

import StarIcon from "@mui/icons-material/Star";
import StarOutlineIcon from "@mui/icons-material/StarOutline";
import DeleteIcon from "@mui/icons-material/Delete";
import EditNoteTwoToneIcon from "@mui/icons-material/EditNoteTwoTone";
import CameraAltIcon from "@mui/icons-material/CameraAlt";
import CloseIcon from "@mui/icons-material/Close";

import { useDispatch, useSelector } from "react-redux";
import BarcodeScannerComponent from "react-qr-barcode-scanner";
import toast, { Toaster } from "react-hot-toast";
import axios from "axios";

import CategoryChips from "./CategoryChips";
import {
  fetchMedia,
  addMedia,
  updateMedia,
  deleteMedia,
  toggleFavorite,
} from "../../redux/slices/mediaSlice";
import { fetchCategories } from "../../redux/slices/categorySlice";
import { createLoan } from "../../redux/slices/loanSlice";
import { fetchPersonsByUsername } from "../../redux/slices/personSlice";


const googleApiKey =
  import.meta.env.VITE_GOOGLE_API_KEY || "YOUR_GOOGLE_API_KEY_HERE";

function CustomToolbar() {
  return (
    <GridToolbarContainer>
      <GridToolbarColumnsButton />
      <GridToolbarFilterButton />
      <GridToolbarDensitySelector />
      <GridToolbarExport />
    </GridToolbarContainer>
  );
}

const MediaTable = () => {
  const dispatch = useDispatch();

  const { media = [], loading: mediaLoading } = useSelector(
    (state) => state.media
  );
  const { items: allCategories = [], loading: categoriesLoading } = useSelector(
    (state) => state.categories
  );
  const { persons = [], loading: personsLoading } = useSelector(
    (state) => state.persons
  );
  const username = useSelector((state) => state.auth.user);

  const fetchMediaData = useCallback(() => {
    dispatch(fetchMedia());
  }, [dispatch]);

  const fetchCategoryData = useCallback(() => {
    dispatch(fetchCategories());
  }, [dispatch]);

  const fetchPersonsData = useCallback(() => {
    dispatch(fetchPersonsByUsername(username));
  }, [dispatch, username]);

  useEffect(() => {
    fetchMediaData();
    fetchCategoryData();
    fetchPersonsData();
  }, [fetchMediaData, fetchCategoryData, fetchPersonsData]);

  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success",
  });
  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const Alert = React.forwardRef(function Alert(props, ref) {
    return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
  });

  const [openDialog, setOpenDialog] = useState(false);
  const [editingMediaId, setEditingMediaId] = useState(null);

  const [newMedia, setNewMedia] = useState({
    title: "",
    type: "",
    producer: "",
    releaseYear: "",
    isbn: "",
    isFavorite: false,
    mediaState: "AVAILABLE",
    notes: "",
    categories: [],
  });

  const [isScannerOpen, setIsScannerOpen] = useState(false);
  const [isFetching, setIsFetching] = useState(false);

  const handleOpenScanner = () => setIsScannerOpen(true);
  const handleCloseScanner = () => setIsScannerOpen(false);

  const handleScan = (err, result) => {
    if (result) {
      const scannedText = result.text.trim();
      setNewMedia((prev) => ({ ...prev, isbn: scannedText }));
      setIsScannerOpen(false);
      toast.success(`ISBN Scanned: ${scannedText}`);
      fetchBookDetails(scannedText);
    } else if (err) {
      console.error("Scanner Error:", err);
      toast.error("Scanning failed. Please try again.");
    }
  };

  const handleFetchByISBN = () => {
    if (!newMedia.isbn) {
      alert("Please enter an ISBN.");
      return;
    }
    fetchBookDetails(newMedia.isbn);
  };

  const fetchBookDetails = async (isbn) => {
    setIsFetching(true);
    try {
      const response = await axios.get(
        `https://www.googleapis.com/books/v1/volumes?q=isbn:${isbn}&key=${googleApiKey}`
      );
      if (response.data.items && response.data.items.length > 0) {
        const book = response.data.items[0].volumeInfo || {};
        setNewMedia((prev) => ({
          ...prev,
          title: book.title || prev.title,
          producer: book.authors ? book.authors.join(", ") : prev.producer,
          type: book.printType || prev.type,
          releaseYear: book.publishedDate
            ? parseInt(book.publishedDate.split("-")[0])
            : prev.releaseYear,
        }));
        setSnackbar({
          open: true,
          message: "Data fetched successfully.",
          severity: "success",
        });
      } else {
        toast.error("No results found for this ISBN.");
      }
    } catch (error) {
      console.error("Error fetching data:", error);
      setSnackbar({
        open: true,
        message: "Failed to fetch data. Please try again.",
        severity: "error",
      });
    } finally {
      setIsFetching(false);
    }
  };

  const handleAddNew = () => {
    setEditingMediaId(null);
    setNewMedia({
      title: "",
      type: "",
      producer: "",
      releaseYear: "",
      isbn: "",
      isFavorite: false,
      mediaState: "AVAILABLE",
      notes: "",
      categories: [],
    });
    setOpenDialog(true);
  };

  const handleCancelNewMedia = () => {
    setOpenDialog(false);
  };

  const handleEditMedia = (id) => {
    const mediaToUpdate = media.find((m) => m.mediaId === id);
    if (mediaToUpdate) {
      const catIds = mediaToUpdate.categories?.map((c) => c.categoryId) || [];
      setNewMedia({
        title: mediaToUpdate.title || "",
        type: mediaToUpdate.type || "",
        producer: mediaToUpdate.producer || "",
        releaseYear: mediaToUpdate.releaseYear || "",
        isbn: mediaToUpdate.isbn || "",
        isFavorite: mediaToUpdate.isFavorite || false,
        mediaState: mediaToUpdate.mediaState || "AVAILABLE",
        notes: mediaToUpdate.notes || "",
        categories: catIds,
      });
      setEditingMediaId(id);
      setOpenDialog(true);
    }
  };

  const handleDeleteMedia = async (id) => {
    try {
      await dispatch(deleteMedia(id)).unwrap();
      setSnackbar({
        open: true,
        message: "Deleted successfully",
        severity: "warning",
      });
      dispatch(fetchMedia());
    } catch (error) {
      setSnackbar({
        open: true,
        message: `Delete failed: ${error}`,
        severity: "error",
      });
    }
  };

  const handleSaveNewMedia = async () => {
    if (
      !newMedia.title ||
      !newMedia.type ||
      !newMedia.releaseYear ||
      !newMedia.mediaState
    ) {
      alert("Please fill in all required fields.");
      return;
    }
    try {
      if (editingMediaId) {
        await dispatch(
          updateMedia({ id: editingMediaId, media: newMedia })
        ).unwrap();
        setSnackbar({
          open: true,
          message: `Updated "${newMedia.title}".`,
          severity: "success",
        });
      } else {
        await dispatch(addMedia(newMedia)).unwrap();
        setSnackbar({
          open: true,
          message: `Added "${newMedia.title}".`,
          severity: "success",
        });
      }
      setEditingMediaId(null);
      setNewMedia({
        title: "",
        type: "",
        producer: "",
        releaseYear: "",
        isbn: "",
        isFavorite: false,
        mediaState: "AVAILABLE",
        notes: "",
        categories: [],
      });
      setOpenDialog(false);
      dispatch(fetchMedia());
    } catch (error) {
      setSnackbar({
        open: true,
        message: `Save failed: ${error}`,
        severity: "error",
      });
    }
  };

  const handleToggleFavorite = async (mediaId, currentFavorite) => {
    try {
      await dispatch(
        toggleFavorite({ id: mediaId, isFavorite: !currentFavorite })
      ).unwrap();
    } catch (error) {
      console.error("Failed to toggle favorite:", error);
      setSnackbar({
        open: true,
        message: "Failed to toggle favorite",
        severity: "error",
      });
    }
  };

  // ----------------------------
  // Loan Creation
  // ----------------------------
  const [openLoanDialog, setOpenLoanDialog] = useState(false);
  const [newLoan, setNewLoan] = useState({
    mediaId: null,
    mediaTitle: "",
    personId: "",
    dueDate: dayjs().add(7, "day").format("YYYY-MM-DD"),
    borrowedAt: dayjs().format("YYYY-MM-DDTHH:mm:ss"),
  });

  const handleLoanMedia = (mediaId) => {
    const mediaObj = media.find((m) => m.mediaId === mediaId);
    if (!mediaObj) {
      console.error("Could not find media with ID:", mediaId);
      return;
    }
    setNewLoan({
      mediaId,
      mediaTitle: mediaObj.title,
      personId: "",
      dueDate: dayjs().add(7, "day").format("YYYY-MM-DD"),
      borrowedAt: dayjs().format("YYYY-MM-DDTHH:mm:ss"),
    });
    setOpenLoanDialog(true);
  };

  const handleSaveLoan = async () => {
    if (!newLoan.personId || !newLoan.mediaId) {
      alert("Please select a person and ensure the media is valid.");
      return;
    }
    const borrowedAtDate = dayjs(newLoan.borrowedAt);
    const dueDateDate = dayjs(newLoan.dueDate);
    if (dueDateDate.isBefore(borrowedAtDate, "day")) {
      alert("Due date cannot be before the borrowed date!");
      return;
    }
    try {
      const payload = {
        mediaId: newLoan.mediaId,
        personId: newLoan.personId,
        dueDate: newLoan.dueDate,
        borrowedAt: newLoan.borrowedAt,
      };
      await dispatch(createLoan(payload)).unwrap();
      setSnackbar({
        open: true,
        message: `Media '${newLoan.mediaTitle}' loaned successfully`,
        severity: "success",
      });
      setOpenLoanDialog(false);
      setNewLoan({
        mediaId: null,
        mediaTitle: "",
        personId: "",
        dueDate: dayjs().add(7, "day").format("YYYY-MM-DD"),
        borrowedAt: dayjs().format("YYYY-MM-DDTHH:mm:ss"),
      });
      dispatch(fetchMedia());
    } catch (error) {
      console.error("Loan creation failed:", error);
      setSnackbar({
        open: true,
        message: `Loan creation failed: ${error}`,
        severity: "error",
      });
    }
  };

  const rows = media.map((m) => ({
    // All row data for the DataGrid
    id: m.mediaId,
    title: m.title,
    type: m.type,
    producer: m.producer,
    releaseYear: m.releaseYear,
    isbn: m.isbn,
    isFavorite: m.isFavorite,
    mediaState: m.mediaState,
    notes: m.notes,
    categories: m.categories || [],
  }));

  const columns = [
    { field: "title", headerName: "Title", width: 150 },
    { field: "type", headerName: "Type", width: 80 },
    { field: "producer", headerName: "Producer", width: 140 },
    { field: "releaseYear", headerName: "Year", width: 70 },
    { field: "isbn", headerName: "ISBN", width: 120 },
    {
      field: "isFavorite",
      headerName: "Favorite",
      width: 90,
      renderCell: (params) => {
        const row = params.row || {};
        return (
          <IconButton
            onClick={() => handleToggleFavorite(row.id, row.isFavorite)}
            aria-label="toggle favorite"
          >
            {row.isFavorite ? (
              <StarIcon sx={{ color: "orange" }} />
            ) : (
              <StarOutlineIcon />
            )}
          </IconButton>
        );
      },
    },
    { field: "mediaState", headerName: "State", width: 120 },
    { field: "notes", headerName: "Notes", width: 150 },
    {
      field: "categories",
      headerName: "Categories",
      width: 200,
      renderCell: (params) =>
        params.row ? (
          <CategoryChips
            categoryObjects={params.row.categories}
            mediaId={params.row.id}
          />
        ) : null,
      valueGetter: (params) => {
        const catArray = params.value || [];
        return catArray.length
          ? catArray.map((cat) => cat.categoryName).join(", ")
          : "None";
      },
      exportValue: (params) => {
        const catArray = params.value || [];
        return catArray.length
          ? catArray.map((cat) => cat.categoryName).join(", ")
          : "None";
      },
    },
    {
      field: "actions",
      headerName: "Actions",
      width: 190,
      sortable: false,
      renderCell: (params) => {
        if (!params.row) return null;
        const isDisabled = params.row.mediaState !== "AVAILABLE"; // Disable if the media is not available
        return (
          <>
            <IconButton
              color="secondary"
              onClick={() => handleEditMedia(params.row.id)}
              aria-label="Edit Media"
            >
              <EditNoteTwoToneIcon />
            </IconButton>

            <IconButton
              color="error"
              onClick={() => handleDeleteMedia(params.row.id)}
              aria-label="Delete Media"
            >
              <DeleteIcon />
            </IconButton>

            <Button
              variant="outlined"
              color="primary"
              size="small"
              onClick={() => handleLoanMedia(params.row.id)}
              sx={{ ml: 1 }}
              disabled={isDisabled}
            >
              Loan
            </Button>
          </>
        );
      },
    },
  ];

  return (
    <Box className="p-6 bg-gray-100 dark:bg-gray-900 min-h-screen">
      <Toaster position="top-right" reverseOrder={false} />
      <Box
        sx={{
          backgroundColor: "rgba(59,130,246,0.1)",
          borderRadius: "8px",
          padding: "1rem",
          marginBottom: "1rem",
        }}
      >
        <Typography variant="body1" color="text.secondary">
          <strong>Tip:</strong> You can drag categories from the categories
          panel to the “Categories” column in this table to assign them to a
          media.
        </Typography>
      </Box>

      {/* Header Section */}
      <Box className="flex justify-between items-center mb-6">
        <Button
          variant="contained"
          color="primary"
          onClick={handleAddNew}
          className="bg-blue-500 hover:bg-blue-600 dark:bg-blue-700 dark:hover:bg-blue-800"
        >
          Add New Media
        </Button>
      </Box>

      <Box className="bg-white dark:bg-gray-800 rounded-lg p-2">
        <DataGrid
          rows={rows}
          columns={columns}
          loading={mediaLoading || categoriesLoading || personsLoading}
          autoHeight
          disableRowSelectionOnClick
          pageSizeOptions={[5, 10, 20, 50]}
          initialState={{
            pagination: {
              paginationModel: { pageSize: 10 },
            },
          }}
          slots={{
            toolbar: CustomToolbar,
          }}
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
      </Box>

      <Dialog
        open={openDialog}
        onClose={() => setOpenDialog(false)}
        fullWidth
        maxWidth="md"
      >
        <DialogTitle>
          {editingMediaId ? "Edit Media" : "Add New Media"}
        </DialogTitle>
        <DialogContent className="bg-white dark:bg-gray-800">
          <TextField
            autoFocus
            margin="dense"
            label="Title"
            fullWidth
            variant="outlined"
            value={newMedia.title || ""}
            onChange={(e) =>
              setNewMedia({ ...newMedia, title: e.target.value })
            }
          />

          {/* Media Type */}
          <FormControl fullWidth margin="dense">
            <InputLabel>Media Type</InputLabel>
            <Select
              value={newMedia.type || ""}
              onChange={(e) =>
                setNewMedia({ ...newMedia, type: e.target.value })
              }
              label="Media Type"
            >
              <MenuItem value="BOOK">Book</MenuItem>
              <MenuItem value="CD">CD</MenuItem>
              <MenuItem value="FILM">Film</MenuItem>
              <MenuItem value="GAME">Game</MenuItem>
            </Select>
          </FormControl>

          {/* Producer */}
          <TextField
            margin="dense"
            label="Producer"
            fullWidth
            variant="outlined"
            value={newMedia.producer || ""}
            onChange={(e) =>
              setNewMedia({ ...newMedia, producer: e.target.value })
            }
          />

          {/* Release Year */}
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DatePicker
              views={["year"]}
              label="Release Year"
              value={
                newMedia.releaseYear
                  ? dayjs(newMedia.releaseYear.toString())
                  : null
              }
              onChange={(date) =>
                setNewMedia((prev) => ({
                  ...prev,
                  releaseYear: date ? date.year() : "",
                }))
              }
              maxDate={dayjs()}
              renderInput={(params) => (
                <TextField
                  {...params}
                  margin="dense"
                  fullWidth
                  variant="outlined"
                />
              )}
            />
          </LocalizationProvider>

          {/* ISBN + Camera */}
          <Box display="flex" alignItems="center" marginTop={2}>
            <TextField
              margin="dense"
              label="ISBN"
              fullWidth
              variant="outlined"
              value={newMedia.isbn || ""}
              onChange={(e) =>
                setNewMedia({ ...newMedia, isbn: e.target.value })
              }
            />
            <IconButton
              color="primary"
              onClick={handleOpenScanner}
              aria-label="Scan ISBN"
              sx={{ marginLeft: 1 }}
            >
              <CameraAltIcon />
            </IconButton>
          </Box>

          {/* Fetch from Google Books by ISBN */}
          <Button
            variant="contained"
            color="primary"
            onClick={handleFetchByISBN}
            className="mt-2"
            disabled={isFetching}
            startIcon={isFetching && <CircularProgress size={20} />}
          >
            {isFetching ? "Fetching..." : "Fetch by ISBN"}
          </Button>

          {/* Categories */}
          <FormControl fullWidth margin="dense">
            <InputLabel>Categories</InputLabel>
            <Select
              multiple
              value={newMedia.categories}
              onChange={(e) =>
                setNewMedia({ ...newMedia, categories: e.target.value })
              }
              label="Categories"
              renderValue={(selected) => (
                <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.5 }}>
                  {selected.map((catId) => {
                    const cat = allCategories.find(
                      (c) => c.categoryId === catId
                    );
                    return (
                      <Chip
                        key={catId}
                        label={cat?.categoryName || `ID ${catId}`}
                      />
                    );
                  })}
                </Box>
              )}
            >
              {allCategories.map((cat) => (
                <MenuItem key={cat.categoryId} value={cat.categoryId}>
                  {cat.categoryName}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          {/* Media State */}
          <FormControl fullWidth margin="dense">
            <InputLabel>Media State</InputLabel>
            <Select
              value={newMedia.mediaState || "AVAILABLE"}
              onChange={(e) =>
                setNewMedia({ ...newMedia, mediaState: e.target.value })
              }
              label="Media State"
            >
              <MenuItem value="AVAILABLE">Available</MenuItem>
              <MenuItem value="BORROWED">Borrowed</MenuItem>
              <MenuItem value="UNAVAILABLE">Unavailable</MenuItem>
            </Select>
          </FormControl>

          {/* Favorite Toggle */}
          <Box display="flex" alignItems="center" marginTop={2}>
            <Box component="span" mr={1}>
              Favorite:
            </Box>
            <IconButton
              onClick={() =>
                setNewMedia((prev) => ({
                  ...prev,
                  isFavorite: !prev.isFavorite,
                }))
              }
              color="primary"
            >
              {newMedia.isFavorite ? (
                <StarIcon sx={{ color: "gold" }} />
              ) : (
                <StarOutlineIcon />
              )}
            </IconButton>
          </Box>

          {/* Notes */}
          <TextField
            margin="dense"
            label="Notes"
            fullWidth
            variant="outlined"
            multiline
            rows={3}
            value={newMedia.notes || ""}
            onChange={(e) =>
              setNewMedia({ ...newMedia, notes: e.target.value })
            }
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCancelNewMedia} color="secondary">
            Cancel
          </Button>
          <Button
            onClick={handleSaveNewMedia}
            variant="contained"
            color="primary"
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>

      {/* Barcode Scanner Dialog */}
      <Dialog
        open={isScannerOpen}
        onClose={handleCloseScanner}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>
          Scan ISBN
          <IconButton
            aria-label="close"
            onClick={handleCloseScanner}
            sx={{ position: "absolute", right: 8, top: 8 }}
          >
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent dividers>
          <Box display="flex" justifyContent="center" alignItems="center">
            <BarcodeScannerComponent
              width={500}
              height={500}
              onUpdate={handleScan}
              delay={1500}
            />
          </Box>
        </DialogContent>
      </Dialog>

      {/* Create Loan Dialog */}
      <Dialog open={openLoanDialog} onClose={() => setOpenLoanDialog(false)}>
        <DialogTitle>Create Loan</DialogTitle>
        <DialogContent>
          <TextField
            label="Media"
            value={newLoan.mediaTitle}
            fullWidth
            margin="dense"
            variant="outlined"
            disabled
          />

          <FormControl fullWidth margin="dense">
            <InputLabel>Person</InputLabel>
            <Select
              value={newLoan.personId}
              onChange={(e) =>
                setNewLoan({ ...newLoan, personId: e.target.value })
              }
              label="Person"
            >
              {persons.map((person) => (
                <MenuItem key={person.personId} value={person.personId}>
                  {person.firstName} {person.lastName}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <Box mt={2}>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DatePicker
                label="Borrowed At"
                value={dayjs(newLoan.borrowedAt)}
                maxDate={dayjs()}
                onChange={(date) =>
                  setNewLoan((prev) => ({
                    ...prev,
                    borrowedAt: date
                      ? date.format("YYYY-MM-DDTHH:mm:ss")
                      : prev.borrowedAt,
                  }))
                }
                renderInput={(params) => (
                  <TextField {...params} fullWidth margin="dense" />
                )}
              />
            </LocalizationProvider>
          </Box>

          <Box mt={2}>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DatePicker
                label="Due Date"
                type="date"
                value={dayjs(newLoan.dueDate)}
                onChange={(date) =>
                  setNewLoan((prev) => ({
                    ...prev,
                    dueDate: date ? date.format("YYYY-MM-DD") : prev.dueDate,
                  }))
                }
                renderInput={(params) => (
                  <TextField {...params} fullWidth margin="dense" />
                )}
              />
            </LocalizationProvider>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenLoanDialog(false)}>Cancel</Button>
          <Button onClick={handleSaveLoan} variant="contained" color="primary">
            Save Loan
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar Feedback */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={handleCloseSnackbar}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity={snackbar.severity}
          sx={{ width: "100%" }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default MediaTable;
