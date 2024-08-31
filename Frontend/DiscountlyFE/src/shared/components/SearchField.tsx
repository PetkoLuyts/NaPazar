import * as React from "react";

// COMPONENTS
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import SearchIcon from "@mui/icons-material/Search";

interface SearchFieldProps {
  value: string;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

export const SearchField: React.FC<SearchFieldProps> = ({
  value,
  onChange,
}) => {
  return (
    <Box
      sx={{
        display: "flex",
        alignItems: "flex-end",
        backgroundColor: "rgba(255, 255, 255, 0.8)",
        padding: "8px",
        borderRadius: "4px",
        boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.2)",
      }}
    >
      <SearchIcon sx={{ color: "action.active", mr: 1, my: 0.5 }} />
      <TextField
        id="input-with-search-icon"
        label="Търси продукт"
        variant="standard"
        value={value}
        onChange={onChange}
      />
    </Box>
  );
};
