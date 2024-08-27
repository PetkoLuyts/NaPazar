import * as React from "react";
import Box from "@mui/material/Box";
import Input from "@mui/material/Input";
import InputLabel from "@mui/material/InputLabel";
import InputAdornment from "@mui/material/InputAdornment";
import FormControl from "@mui/material/FormControl";
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
    <Box sx={{ display: "flex", alignItems: "flex-end" }}>
      <SearchIcon sx={{ color: "action.active", mr: 1, my: 0.5 }} />
      <TextField
        id="input-with-search-icon"
        label="Search Products"
        variant="standard"
        value={value}
        onChange={onChange}
      />
    </Box>
  );
};
