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
      <p className="card-old-price">Old Price: ${oldPrice}</p>
      <p className="card-new-price">New Price: ${newPrice}</p>
      <p className="card-discount-phrase">Discount Phrase: ${discountPhrase}</p>
    </div>
  );
};

export default Card;
