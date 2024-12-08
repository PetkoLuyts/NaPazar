import React, { useState, useEffect } from "react";
import { apiCalls } from "../../shared/apiCalls";
import Card from "../card/Card";
import { ProductResponse } from "../../products/types";
import { Flexbox } from "../../shared/components/Flexbox";

const FavoritesPage: React.FC = () => {
  const [favoriteItems, setFavoriteItems] = useState<ProductResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchFavorites = async () => {
      try {
        setLoading(true);
        const response = await apiCalls.getAllFavoriteItems();
        setFavoriteItems(response.products);
      } catch (error) {
        setError("Failed to fetch favorite items.");
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetchFavorites();
  }, []);

  if (loading) return <p>Loading favorites...</p>;
  if (error) return <p>{error}</p>;
  if (favoriteItems.length === 0) return <p>No favorite items found.</p>;

  return (
    <Flexbox direction="column" sx={{ padding: "16px" }}>
      <Flexbox justifyContent="center" flexWrap="wrap">
        {favoriteItems.map((item) => (
          <Card
            key={item.id}
            id={item.id}
            title={item.title}
            oldPrice={item.oldPrice}
            newPrice={item.newPrice}
            discountPhrase={item.discountPhrase}
          />
        ))}
      </Flexbox>
    </Flexbox>
  );
};

export default FavoritesPage;
