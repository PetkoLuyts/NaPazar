import React from "react";
import { Link } from "react-router-dom";
import "./Navbar.css";

const Navbar: React.FC = () => {
  return (
    <nav className="navbar">
      <div className="navbar-logo">
        <Link to="/">Home</Link>
      </div>
      <div className="navbar-cart">
        <Link to="/checkout">Cart</Link>
      </div>
    </nav>
  );
};

export default Navbar;
