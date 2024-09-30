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
  return (
    <div className="card">
      <h2 className="card-title">{title}</h2>
      <p className="card-old-price">Стара цена: {oldPrice}лв</p>
      <p className="card-new-price">Нова цена: {newPrice}лв</p>
      <p className="card-discount-phrase">Намаление: {discountPhrase}</p>
      <button className="add-to-cart-button" onClick={onAddToCart}>
        Добави в количка
      </button>
    </div>
  );
};

export default Card;
