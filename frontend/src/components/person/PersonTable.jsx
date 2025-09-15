import React, { useCallback, useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchPersonsByUsername,
  addPerson,
  updatePerson,
  deletePerson,
} from "../../redux/slices/personSlice";
import {
  DataGrid,
  GridToolbarContainer,
  GridToolbarExport,
} from "@mui/x-data-grid";
import {
  Box,
  Button,
  Dialog,
  DialogTitle,
  TextField,
  DialogActions,
  IconButton,
  DialogContent,
  Alert as MuiAlert,
  Snackbar,
} from "@mui/material";
import validator from "validator";
import DeleteIcon from "@mui/icons-material/Delete";
import EditNoteTwoToneIcon from "@mui/icons-material/EditNoteTwoTone";
export default function PersonTable() {
  const dispatch = useDispatch();
  const { persons, loading, error } = useSelector((state) => state.persons);

  const [emailError, setEmailError] = useState("");
  const [phoneError, setPhoneError] = useState("");
  const [editingPersonId, setEditingPersonId] = useState(null);

  const [openDialog, setOpenDialog] = useState(false);
  const [newPerson, setNewPerson] = useState({
    firstName: "",
    lastName: "",
    address: "",
    email: "",
    phone: "",
  });

  const username = useSelector((state) => state.auth.user);

  const fetchPersonsData = useCallback(() => {
    dispatch(fetchPersonsByUsername(username));
  }, [dispatch, username]);

  useEffect(() => {
    fetchPersonsData();
  }, [fetchPersonsData]);

  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success",
  });

  const Alert = React.forwardRef(function Alert(props, ref) {
    return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
  });

  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const handleSaveNewPerson = async () => {
    setEmailError("");
    setPhoneError("");

    if (!validator.isEmail(newPerson.email)) {
      setEmailError("Please enter a valid email address.");

      return;
    }
    if (!validator.isMobilePhone(newPerson.phone)) {
      setPhoneError("Please enter a valid phone number.");

      return;
    }

    if (
      !newPerson.firstName ||
      !newPerson.lastName ||
      !newPerson.email ||
      !newPerson.phone ||
      !newPerson.address
    ) {
      alert("Please fill in all required fields with valid data.");
      return;
    }

    if (editingPersonId) {
      await dispatch(updatePerson({ id: editingPersonId, person: newPerson }));
      setSnackbar({
        open: true,
        message: `Updated "${newPerson.firstName}".`,
        severity: "success",
      });
    } else {
      await dispatch(addPerson(newPerson));
      setSnackbar({
        open: true,
        message: `Added "${newPerson.firstName}".`,
        severity: "success",
      });
    }

    setNewPerson({
      firstName: "",
      lastName: "",
      address: "",
      email: "",
      phone: "",
    });

    setEditingPersonId(null);
    setOpenDialog(false);
  };

  const columns = [
    { field: "firstName", headerName: "First Name", width: 120 },
    { field: "lastName", headerName: "Last Name", width: 120 },
    { field: "address", headerName: "Address", width: 200 },
    { field: "email", headerName: "Email", width: 200 },
    { field: "phone", headerName: "Phone", width: 120 },
    {
      field: "actions",
      headerName: "Actions",
      width: 100,
      renderCell: (params) => (
        <div>
          <IconButton
            color="secondary"
            onClick={() => handleEditPerson(params.row.id)}
            aria-label="edit"
            size="large"
          >
            <EditNoteTwoToneIcon />
          </IconButton>

          <IconButton
            onClick={() => handleDeletePerson(params.row.id)}
            color="error"
            aria-label="delete"
            size="large"
          >
            <DeleteIcon fontSize="inherit" />
          </IconButton>
        </div>
      ),
    },
  ];

  const rows =
    (Array.isArray(persons) &&
      persons.map((person) => ({
        id: person.personId,
        firstName: person.firstName,
        lastName: person.lastName,
        address: person.address,
        email: person.email,
        phone: person.phone,
      }))) ||
    [];

  const handleEditPerson = (id) => {
    const personToEdit = persons.find((person) => person.personId === id);
    setNewPerson(personToEdit);
    setEditingPersonId(id);
    setOpenDialog(true);
  };

  const handleDeletePerson = async (id) => {
    try {
      await dispatch(deletePerson(id));
      setSnackbar({
        open: true,
        message: "Deleted successfully",
        severity: "warning",
      });

      await dispatch(fetchPersonsByUsername(username));
    } catch (error) {
      if (error.message === "Unauthorized access. Please log in again.") {
        console.error("Unauthorized access while deleting person:", error);
      } else {
        console.error("An error occurred while deleting person:", error);
        setSnackbar({
          open: true,
          message: "Failed to delete person",
          severity: "error",
        });
      }
    }
  };

  function CustomToolbar() {
    return (
      <GridToolbarContainer>
        <GridToolbarExport />
      </GridToolbarContainer>
    );
  }

  return (
    <Box className="p-6 bg-gray-100 dark:bg-gray-900 min-h-screen">
      {/* Header Section */}
      <Box className="flex justify-between items-center mb-6">
        {/* Add New Person Button */}
        <Button
          variant="contained"
          color="primary"
          onClick={() => setOpenDialog(true)}
          className="bg-blue-500 hover:bg-blue-600 dark:bg-blue-700 dark:hover:bg-blue-800 transition-colors duration-300"
        >
          Add New Person
        </Button>
      </Box>

      {/* DataGrid Container */}
      <Box className="bg-white dark:bg-gray-800 rounded-lg p-2">
        <DataGrid
          rows={rows}
          columns={columns}
          pageSizeOptions={[5, 10, 20, 50]}
          initialState={{
            pagination: {
              paginationModel: { pageSize: 10 },
            },
          }}
          disableRowSelectionOnClick
          autoHeight
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

      {/* Add New Person Dialog */}

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
        <DialogTitle className="text-gray-900 dark:text-gray-100">
          Add New Person
        </DialogTitle>
        <DialogContent className="bg-white dark:bg-gray-800">
          <TextField
            autoFocus
            margin="dense"
            label="FirstName"
            fullWidth
            variant="outlined"
            value={newPerson.firstName}
            onChange={(e) =>
              setNewPerson({
                ...newPerson,
                firstName: e.target.value,
              })
            }
            className="text-gray-900 dark:text-white"
          />

          <TextField
            autoFocus
            margin="dense"
            label="LastName"
            fullWidth
            variant="outlined"
            value={newPerson.lastName}
            onChange={(e) =>
              setNewPerson({
                ...newPerson,
                lastName: e.target.value,
              })
            }
            className="text-gray-900 dark:text-white"
          />
          <TextField
            autoFocus
            margin="dense"
            label="Address"
            fullWidth
            variant="outlined"
            value={newPerson.address}
            onChange={(e) =>
              setNewPerson({
                ...newPerson,
                address: e.target.value,
              })
            }
            className="text-gray-900 dark:text-white"
          />
          <TextField
            autoFocus
            margin="dense"
            label="email"
            fullWidth
            variant="outlined"
            value={newPerson.email}
            onChange={(e) =>
              setNewPerson({
                ...newPerson,
                email: e.target.value,
              })
            }
            className="text-gray-900 dark:text-white"
          />
          {emailError && (
            <Alert variant="filled" severity="error">
              {emailError}
            </Alert>
          )}
          <TextField
            autoFocus
            margin="dense"
            label="phone"
            fullWidth
            variant="outlined"
            value={newPerson.phone}
            onChange={(e) =>
              setNewPerson({
                ...newPerson,
                phone: e.target.value,
              })
            }
            className="text-gray-900 dark:text-white"
          />
          {phoneError && (
            <Alert variant="filled" severity="error">
              {phoneError}
            </Alert>
          )}
        </DialogContent>
        <DialogActions className="bg-white dark:bg-gray-800">
          <Button
            onClick={() => setOpenDialog(false)}
            color="secondary"
            className="text-gray-700 dark:text-gray-300"
          >
            Cancel
          </Button>
          <Button
            onClick={handleSaveNewPerson}
            variant="contained"
            color="primary"
            className="bg-blue-500 hover:bg-blue-600 dark:bg-blue-700 dark:hover:bg-blue-800"
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar for User Feedback */}
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
}
