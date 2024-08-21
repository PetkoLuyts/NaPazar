import React from "react";

// UTILS
import { useProducts } from "../../shared/queries";

// COMPONENTS
import Card from "../card/Card";

const products = [
  { name: "Product 1", oldPrice: 100, newPrice: 80 },
  { name: "Product 2", oldPrice: 200, newPrice: 150 },
  { name: "Product 3", oldPrice: 50, newPrice: 30 },
];

const MainPage: React.FC = () => {
  const { data: productsData } = useProducts();

  return (
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
  );
};

export default MainPage;
