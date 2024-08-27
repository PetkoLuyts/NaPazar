import React, { useState } from "react";

// UTILS
import { useProducts } from "../../shared/queries";

// COMPONENTS
import Card from "../card/Card";
import {
  TextField,
  InputAdornment,
  Checkbox,
  FormControlLabel,
  FormGroup,
} from "@mui/material";

// TYPES
import { SearchProductParams } from "../../products/types";

// HOOKS
import { useDebounce } from "../../hooks/useDebounce";

// ICONS
import SearchIcon from "@mui/icons-material/Search";

// Store IDs
const STORE_IDS = {
  Billa: 1,
  Lidl: 2,
  Kaufland: 3,
};

const MainPage: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedStores, setSelectedStores] = useState<number[]>([]);

  const debouncedSearchTerm = useDebounce(searchTerm, 300);

  const searchParams: SearchProductParams = {
    searchTerm: debouncedSearchTerm,
    storeIds: selectedStores.length ? selectedStores.join(",") : undefined,
  };

  const { data: productsData, error, isLoading } = useProducts(searchParams);

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
  };

  const handleStoreChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const storeId = parseInt(event.target.value, 10);
    setSelectedStores((prevSelected) =>
      event.target.checked
        ? [...prevSelected, storeId]
        : prevSelected.filter((id) => id !== storeId)
    );
  };

  return (
    <div style={{ padding: "16px" }}>
      <TextField
        label="Search Products"
        variant="outlined"
        fullWidth
        value={searchTerm}
        onChange={handleSearchChange}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon />
            </InputAdornment>
          ),
        }}
        style={{ marginBottom: "16px" }}
      />

      <FormGroup row style={{ marginBottom: "16px" }}>
        {Object.entries(STORE_IDS).map(([storeName, storeId]) => (
          <FormControlLabel
            key={storeId}
            control={
              <Checkbox
                value={storeId}
                onChange={handleStoreChange}
                checked={selectedStores.includes(storeId)}
              />
            }
            label={storeName}
          />
        ))}
      </FormGroup>

      {isLoading && <p>Loading...</p>}
      {error && <p>Error fetching products</p>}
      <div
        style={{ display: "flex", flexWrap: "wrap", justifyContent: "center" }}
      >
        {productsData?.map((product, index) => (
          <Card
            key={index}
            title={product.title}
            oldPrice={product.oldPrice}
            newPrice={product.newPrice}
            discountPhrase={product.discountPhrase}
          />
        ))}
      </div>
    </div>
  );
};

export default MainPage;
