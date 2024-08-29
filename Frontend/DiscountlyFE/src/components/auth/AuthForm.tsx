import React, { useState } from "react";
import "./AuthForm.css";

const AuthForm: React.FC = () => {
  const [isSignUpActive, setIsSignUpActive] = useState(false);

  const toggleForm = () => {
    setIsSignUpActive(!isSignUpActive);
  };

  return (
    <div
      className={`container ${isSignUpActive ? "active" : ""}`}
      id="container"
    >
      <div className="form-container sign-up">
        <form>
          <h1>Регистрация</h1>
          <span>използвайте мейл и парола</span>
          <input type="email" placeholder="Email" />
          <input type="password" placeholder="Password" />
          <button type="submit">Регистрирай се</button>
        </form>
      </div>
      <div className="form-container sign-in">
        <form>
          <h1>Влез</h1>
          <span>използвай мейл и парола</span>
          <input type="email" placeholder="Мейл" />
          <input type="password" placeholder="Парола" />
          <a href="#">Забравена парола?</a>
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
