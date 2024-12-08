import React, { useState } from "react";

// UTILS
import { useProducts } from "../../shared/queries";
import { apiCalls } from "../../shared/apiCalls";

// COMPONENTS
import Card from "../card/Card";
import { Checkbox, FormControlLabel, FormGroup } from "@mui/material";
import { Flexbox } from "../../shared/components/Flexbox";
import { SearchField } from "../../shared/components/SearchField";
import PaginationComponent from "../pagination/PaginationComponent";

// TYPES
import { SearchProductParams } from "../../products/types";

// HOOKS
import { useDebounce } from "../../hooks/useDebounce";

const STORE_IDS = {
  Billa: 1,
  Lidl: 2,
  Kaufland: 3,
};

const MainPage: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedStores, setSelectedStores] = useState<number[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 8;

  const debouncedSearchTerm = useDebounce(searchTerm, 300);

  const searchParams: SearchProductParams & { page: number; size: number } = {
    searchTerm: debouncedSearchTerm,
    storeIds: selectedStores.length ? selectedStores.join(",") : undefined,
    page: currentPage - 1,
    size: pageSize,
  };

  const { data, error, isLoading } = useProducts(searchParams);

  const productsData = data?.products || [];
  const totalPages = data?.totalPages || 1;

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
    setCurrentPage(1);
  };

  const handleStoreChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const storeId = parseInt(event.target.value, 10);
    setSelectedStores((prevSelected) =>
      event.target.checked
        ? [...prevSelected, storeId]
        : prevSelected.filter((id) => id !== storeId)
    );
    setCurrentPage(1);
  };

  const handlePageChange = (
    event: React.ChangeEvent<unknown>,
    page: number
  ) => {
    setCurrentPage(page);
  };

  const handleAddToCart = async (productId: number) => {
    try {
      await apiCalls.addItemToCart(productId);
      alert("Продуктът беше добавен в количката.");
    } catch (error) {
      alert("Възникна грешка при добавяне на продукта в количката.");
    }
  };

  return (
    <Flexbox direction="column" sx={{ padding: "16px", height: "100vh" }}>
      <Flexbox
        justifyContent="flex-start"
        alignItems="center"
        sx={{ marginBottom: "16px", width: "100%" }}
      >
        <SearchField value={searchTerm} onChange={handleSearchChange} />
        <FormGroup
          row
          sx={{
            marginLeft: "16px",
            backgroundColor: "rgba(255, 255, 255, 0.8)",
            padding: "8px",
            borderRadius: "4px",
            boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.2)",
          }}
        >
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
      </Flexbox>

      {isLoading && <p>Зареждане...</p>}
      {error && <p>Грешка при зареждане на продукти.</p>}
      {productsData.length === 0 && <p>Няма намерени продукти.</p>}
      <Flexbox justifyContent="center" flexWrap="wrap">
        {productsData.map((product, index) => (
          <Card
            id={product.id}
            title={product.title}
            oldPrice={product.oldPrice}
            newPrice={product.newPrice}
            discountPhrase={product.discountPhrase}
            onAddToCart={() => handleAddToCart(product.id)}
          />
        ))}
      </Flexbox>

      <Flexbox justifyContent="center" sx={{ marginTop: "16px" }}>
        <PaginationComponent
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </Flexbox>
    </Flexbox>
  );
};

export default MainPage;
