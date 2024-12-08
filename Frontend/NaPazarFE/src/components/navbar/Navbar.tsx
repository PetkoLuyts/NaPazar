import React from "react";
import { Link } from "react-router-dom";
import "./Navbar.css";

// ICONS
import ShoppingBasketIcon from "@mui/icons-material/ShoppingBasket";
import StarIcon from "@mui/icons-material/Star";

const Navbar: React.FC = () => {
  return (
    <nav className="navbar">
      <div className="navbar-logo">
        <Link style={{ textDecoration: "none" }} to="/">
          NaPazar
        </Link>
      </div>
      <div className="navbar-icons">
        <div className="navbar-favourites">
          <Link to="/favourites">
            <StarIcon fontSize="large" />
          </Link>
        </div>
        <div className="navbar-cart">
          <Link to="/checkout">
            <ShoppingBasketIcon fontSize="large" />
          </Link>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
