// src/components/media/CategoryStatCard.jsx

import React, { useEffect, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import { TextField, IconButton } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import AddIcon from "@mui/icons-material/Add";
import RemoveIcon from "@mui/icons-material/Remove";
import CategoryIcon from "@mui/icons-material/Category";
import { useDrag } from "react-dnd";

import StatCard from "../common/StatCard";
import {
  fetchCategories,
  createCategory,
  deleteCategory,
} from "../../redux/slices/categorySlice";
import { fetchMedia } from "../../redux/slices/mediaSlice";
import toast from "react-hot-toast";

const ItemTypes = {
  CATEGORY: "category",
};

const DraggableCategory = ({ category, onDelete }) => {
  const [{ isDragging }, drag] = useDrag(
    () => ({
      type: ItemTypes.CATEGORY,
      item: {
        categoryId: category.categoryId,
        categoryName: category.categoryName,
      },
      collect: (monitor) => ({
        isDragging: !!monitor.isDragging(),
      }),
    }),
    [category]
  );

  return (
    <div
      ref={drag}
      className="flex items-center bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm cursor-move"
      style={{ opacity: isDragging ? 0.5 : 1 }}
    >
      <span>{category.categoryName}</span>
      <button
        onClick={() => onDelete(category.categoryId)}
        className="ml-2 text-red-500 hover:text-red-700"
        aria-label={`Delete ${category.categoryName}`}
      >
        <RemoveIcon className="w-4 h-4" />
      </button>
    </div>
  );
};

const CategoryList = ({ categories, onDelete }) => {
  return (
    <div className="mt-4 flex flex-wrap gap-2">
      {categories.map((category) => (
        <DraggableCategory
          key={category.categoryId}
          category={category}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
};

const CategoryStatCard = () => {
  const dispatch = useDispatch();
  const {
    items: categories,
    loading,
    error,
  } = useSelector((state) => state.categories);

  const [showAddForm, setShowAddForm] = useState(false);
  const [newCategoryName, setNewCategoryName] = useState("");

  useEffect(() => {
    dispatch(fetchCategories());
  }, [dispatch]);

  const handleAddCategory = () => {
    if (newCategoryName.trim()) {
      dispatch(createCategory({ categoryName: newCategoryName.trim() }))
        .unwrap()
        .then(() => {
          setNewCategoryName("");
          setShowAddForm(false);
          toast.success("Category added successfully.");
          dispatch(fetchMedia()); // Refresh media list
        })
        .catch((err) => {
          console.error("Error adding category:", err);
          toast.error("Failed to add category.");
        });
    }
  };

  const handleDeleteCategory = (categoryId) => {
    dispatch(deleteCategory(categoryId))
      .unwrap()
      .then(() => {
        toast.success("Category deleted successfully.");
        dispatch(fetchMedia()); // Refresh media list to update category associations
      })
      .catch((err) => {
        console.error("Error deleting category:", err);
        toast.error("Failed to delete category.");
      });
  };

  return (
    <StatCard
      name="Categories"
      icon={CategoryIcon}
      value={categories.length}
      color="#3b82f6"
    >
      <div className="mt-4">
        {loading && <p className="text-gray-500">Loading categories...</p>}
        {error && <p className="text-red-500">Error: {error}</p>}
        {!loading && !error && (
          <>
            <CategoryList
              categories={categories}
              onDelete={handleDeleteCategory}
            />
            {showAddForm ? (
              <div className="mt-4 flex items-center">
                <TextField
                  value={newCategoryName}
                  onChange={(e) => setNewCategoryName(e.target.value)}
                  placeholder="New category"
                  variant="outlined"
                  size="small"
                  className="flex-grow mr-2"
                />
                <IconButton color="primary" onClick={handleAddCategory}>
                  <AddIcon />
                </IconButton>
                <IconButton
                  color="secondary"
                  onClick={() => setShowAddForm(false)}
                >
                  <CloseIcon />
                </IconButton>
              </div>
            ) : (
              <button
                onClick={() => setShowAddForm(true)}
                className="mt-4 flex items-center text-blue-500 hover:text-blue-700"
                aria-label="Add new category"
              >
                <AddIcon className="w-5 h-5 mr-1" />
                Add Category
              </button>
            )}
          </>
        )}
      </div>
    </StatCard>
  );
};

export default CategoryStatCard;
