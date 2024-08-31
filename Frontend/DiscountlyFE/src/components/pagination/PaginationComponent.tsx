// COMPONENTS
import React from "react";
import Pagination from "@mui/material/Pagination";
import Stack from "@mui/material/Stack";

interface PaginationComponentProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (event: React.ChangeEvent<unknown>, page: number) => void;
}

const PaginationComponent: React.FC<PaginationComponentProps> = ({
  currentPage,
  totalPages,
  onPageChange,
}) => {
  return (
    <Stack spacing={2} sx={{ alignItems: "center", marginTop: "16px" }}>
      <Pagination
        count={totalPages}
        page={currentPage}
        onChange={onPageChange}
        shape="rounded"
      />
    </Stack>
  );
};

export default PaginationComponent;
