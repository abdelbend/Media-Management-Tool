import React, { useEffect, useState } from "react";
import { Box, Chip } from "@mui/material";
import { useDispatch } from "react-redux";
import toast from "react-hot-toast";
import { useDrop } from "react-dnd";
import RemoveIcon from "@mui/icons-material/Remove";
import {
  assignCategoryToMedia,
  removeCategoryFromMedia,
  fetchMedia,
} from "../../redux/slices/mediaSlice";

const ItemTypes = {
  CATEGORY: "category",
};

const CategoryChips = ({ categoryObjects, mediaId }) => {
  const dispatch = useDispatch();
  const [isOver, setIsOver] = useState(false);

  // DnD drop target
  const [{ canDrop, isOverCurrent }, drop] = useDrop({
    accept: ItemTypes.CATEGORY,
    drop: async (item) => {
      // item is: { categoryId, categoryName }
      const alreadyHas = categoryObjects.some(
        (c) => c.categoryId === item.categoryId
      );
      if (!alreadyHas) {
        try {
          await dispatch(
            assignCategoryToMedia({
              mediaId,
              categoryId: item.categoryId,
            })
          ).unwrap();
          toast.success(`Assigned "${item.categoryName}" to media.`);
          // Force re-fetch unless your server returns the updated media
          dispatch(fetchMedia());
        } catch (error) {
          toast.error(`Failed to assign category: ${error}`);
        }
      } else {
        toast.error(`"${item.categoryName}" is already assigned.`);
      }
    },
    collect: (monitor) => ({
      isOverCurrent: monitor.isOver({ shallow: true }),
      canDrop: monitor.canDrop(),
    }),
  });

  useEffect(() => {
    setIsOver(isOverCurrent && canDrop);
  }, [isOverCurrent, canDrop]);

  // Remove category from the current media
  const handleRemoveCategory = async (catId) => {
    try {
      await dispatch(
        removeCategoryFromMedia({
          mediaId,
          categoryId: catId,
        })
      ).unwrap();
      toast.success("Category removed.");
      // Force re-fetch or update store with returned data
      dispatch(fetchMedia());
    } catch (error) {
      toast.error(`Failed to remove category: ${error}`);
    }
  };

  return (
    <Box
      ref={drop}
      sx={{
        display: "flex",
        flexWrap: "wrap",
        gap: 0.5,
        minHeight: "40px",
        padding: "4px",
        border: isOver ? "2px solid green" : "2px dashed #ccc",
        borderRadius: "4px",
        backgroundColor: isOver ? "#e0ffe0" : "transparent",
        transition: "all 0.2s ease",
      }}
    >
      {categoryObjects.map((catObj, index) => {
        return (
          <Chip
            key={catObj.categoryId}
            label={catObj.categoryName}
            size="small"
            onDelete={() => handleRemoveCategory(catObj.categoryId)}
            deleteIcon={<RemoveIcon style={{ color: "#fff" }} />}
            sx={{
              backgroundColor: colorPalette(index),
              color: "#fff",
              fontWeight: 600,
            }}
          />
        );
      })}
    </Box>
  );
};

const palette = [
  "#EC4899",
  "#6366F1",
  "#10B981",
  "#F59E0B",
  "#3B82F6",
  "#8B5CF6",
];
const colorPalette = (index) => palette[index % palette.length];

export default CategoryChips;
