import React, { useState } from "react";
import { apiCalls } from "../../shared/apiCalls";
import "./AuthForm.css";
import { useNavigate } from "react-router-dom";
import GoogleLoginButton from "./GoogleLoginButton";

const AuthForm: React.FC = () => {
  const [isSignUpActive, setIsSignUpActive] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const toggleForm = () => {
    setIsSignUpActive(!isSignUpActive);
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    const request = { email, password };
    const endpoint = isSignUpActive ? apiCalls.register : apiCalls.authenticate;

    try {
      const response = await endpoint(request);
      console.log(response);
      navigate("/");
    } catch (error) {
      console.error("An error occurred:", error);
    }
  };

  return (
    <div
      className={`container ${isSignUpActive ? "active" : ""}`}
      id="container"
    >
      <div className="form-container sign-up">
        <form onSubmit={handleSubmit}>
          <h1>Регистрация</h1>
          <span>използвайте мейл и парола</span>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button type="submit">Регистрирай се</button>
          <GoogleLoginButton />
        </form>
      </div>
      <div className="form-container sign-in">
        <form onSubmit={handleSubmit}>
          <h1>Влез</h1>
          <span>използвай мейл и парола</span>
          <input
            type="email"
            placeholder="Мейл"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="password"
            placeholder="Парола"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <a
            href="#"
            onClick={(e) => {
              e.preventDefault();
              navigate("/forgot-password");
            }}
          >
            Забравена парола?
          </a>
          <button type="submit">Влез</button>
        </form>
      </div>
      <div className="toggle-container">
        <div className="toggle">
          <div className="toggle-panel toggle-left">
            <h1>Добре дошъл отново!</h1>
            <p>
              Влезте с вашите лични данни, за да използвате всички функции на
              сайта
            </p>
            <button className="hidden" id="login" onClick={toggleForm}>
              Влез
            </button>
            <GoogleLoginButton />
          </div>
          <div className="toggle-panel toggle-right">
            <h1>За първи път тук?</h1>
            <p>
              Регистрирайте се с вашите лични данни, за да използвате всички
              функции на сайта
            </p>
            <button className="hidden" id="register" onClick={toggleForm}>
              Регистрирай се
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AuthForm;
