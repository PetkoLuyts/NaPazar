import React, { useState } from "react";
import { apiCalls } from "../../shared/apiCalls";
import "./ForgotPassword.css";

const ResetPassword: React.FC = () => {
  const [email, setEmail] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    if (newPassword !== confirmPassword) {
      setError("Паролите не съвпадат.");
      return;
    }

    try {
      await apiCalls.resetPassword({ email, password: newPassword });
      setMessage("Паролата е променена успешно!");
    } catch (error) {
      setMessage("");
      setError("Възникна грешка. Опитайте отново.");
    }
  };

  return (
    <div className="forgot-password-container">
      <form onSubmit={handleSubmit}>
        <h1>Нова парола</h1>
        <span>Въведете новата си парола и потвърдете</span>

        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          type="password"
          placeholder="Нова парола"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
        />
        <input
          type="password"
          placeholder="Потвърдете паролата"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />

        <button type="submit">Запази новата парола</button>

        {message && <p className="success">{message}</p>}
        {error && <p className="error">{error}</p>}
      </form>
    </div>
  );
};

export default ResetPassword;
