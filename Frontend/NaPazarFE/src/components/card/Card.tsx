import React, { useState } from "react";
import { apiCalls } from "../../shared/apiCalls";
import "./Card.css";

interface CardProps {
  id: number;
  title: string;
  oldPrice: number;
  newPrice: number;
  discountPhrase: string;
  onAddToCart?: () => void;
}

const Card: React.FC<CardProps> = ({
  id,
  title,
  oldPrice,
  newPrice,
  discountPhrase,
  onAddToCart,
}) => {
  const [isFavorited, setIsFavorited] = useState(false);

  const handleFavoriteClick = async () => {
    try {
      if (isFavorited) {
        await apiCalls.removeFavoriteItem(id);
      } else {
        await apiCalls.addFavoriteItem(id);
      }
      setIsFavorited(!isFavorited);
    } catch (error) {
      console.error("Error updating favorite status:", error);
    }
  };

  return (
    <div className="card">
      <h2 className="card-title">{title}</h2>
      <p className="card-old-price">Стара цена: {oldPrice}лв</p>
      <p className="card-new-price">Нова цена: {newPrice}лв</p>
      <p className="card-discount-phrase">Намаление: {discountPhrase}</p>
      <div className="card-footer">
        <button className="add-to-cart-button" onClick={onAddToCart}>
          Добави в количка
        </button>
        <span
          className={`favorite-icon ${isFavorited ? "star-filled" : ""}`}
          onClick={handleFavoriteClick}
        >
          ★
        </span>
      </div>
    </div>
  );
};

export default Card;
