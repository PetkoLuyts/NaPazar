import React, { useState } from "react";
import { apiCalls } from "../../shared/apiCalls";
import "./ForgotPassword.css";

const ForgotPassword: React.FC = () => {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    try {
      await apiCalls.forgotPassword({ email });
      setMessage("Инструкции за възстановяване са изпратени на мейла ви.");
    } catch (error) {
      setMessage("Възникна грешка. Опитайте отново.");
    }
  };

  return (
    <div className="forgot-password-container">
      <form onSubmit={handleSubmit}>
        <h1>Забравена парола</h1>
        <span>Въведете мейла си за възстановяване на паролата</span>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <button type="submit">Изпрати</button>
        {message && <p>{message}</p>}
      </form>
    </div>
  );
};

export default ForgotPassword;
