import "./Card.css";

interface CardProps {
  title: string;
  oldPrice: number;
  newPrice: number;
  discountPhrase: string;
}

const Card: React.FC<CardProps> = ({
  title,
  oldPrice,
  newPrice,
  discountPhrase,
}) => {
  return (
    <div className="card">
      <h2 className="card-title">{title}</h2>
      <p className="card-old-price">Стара цена: {oldPrice}лв</p>
      <p className="card-new-price">Нова цена: {newPrice}лв</p>
      <p className="card-discount-phrase">Намаление: {discountPhrase}</p>
    </div>
  );
};

export default Card;
