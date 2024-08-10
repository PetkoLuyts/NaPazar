import React from "react";
import Card from "../card/Card";

const products = [
  { name: "Product 1", oldPrice: 100, newPrice: 80 },
  { name: "Product 2", oldPrice: 200, newPrice: 150 },
  { name: "Product 3", oldPrice: 50, newPrice: 30 },
];

const MainPage: React.FC = () => {
  return (
    <div
      style={{ display: "flex", flexWrap: "wrap", justifyContent: "center" }}
    >
      {products.map((product, index) => (
        <Card
          key={index}
          name={product.name}
          oldPrice={product.oldPrice}
          newPrice={product.newPrice}
        />
      ))}
    </div>
  );
};

export default MainPage;
