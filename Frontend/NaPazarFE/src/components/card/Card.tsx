import React, { useState } from "react";
import "./Card.css";

interface CardProps {
  title: string;
  oldPrice: number;
  newPrice: number;
  discountPhrase: string;
  onAddToCart?: () => void;
}

const Card: React.FC<CardProps> = ({
  title,
  oldPrice,
  newPrice,
  discountPhrase,
  onAddToCart,
}) => {
  const [isFavorited, setIsFavorited] = useState(false);

  const handleFavoriteClick = () => {
    setIsFavorited(!isFavorited);
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
