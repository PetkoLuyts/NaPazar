import React from "react";
import { Link } from "react-router-dom";
import "./Navbar.css";

// ICONS
import ShoppingBasketIcon from "@mui/icons-material/ShoppingBasket";

const Navbar: React.FC = () => {
  return (
    <nav className="navbar">
      <div className="navbar-logo">
        <Link style={{ textDecoration: "none" }} to="/">
          NaPazar
        </Link>
      </div>
      <div className="navbar-cart">
        <Link to="/checkout">
          <ShoppingBasketIcon fontSize="large" />
        </Link>
      </div>
    </nav>
  );
};

export default Navbar;
