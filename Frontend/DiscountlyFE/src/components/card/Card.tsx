import "./Card.css";

interface CardProps {
  name: string;
  oldPrice: number;
  newPrice: number;
}

const Card: React.FC<CardProps> = ({ name, oldPrice, newPrice }) => {
  return (
    <div className="card">
      <h2 className="card-title">{name}</h2>
      <p className="card-old-price">Old Price: ${oldPrice}</p>
      <p className="card-new-price">New Price: ${newPrice}</p>
    </div>
  );
};

export default Card;
