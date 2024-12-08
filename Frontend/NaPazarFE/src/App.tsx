import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import Navbar from "./components/navbar/Navbar";
import MainPage from "./components/pages/MainPage";
import Checkout from "./components/checkout/Checkout";
import "./App.css";
import AuthForm from "./components/auth/AuthForm";
import Footer from "./components/footer/Footer";
import PaymentPage from "./components/payment/PaymentPage";
import ForgotPassword from "./components/auth/ForgotPassword";
import ResetPassword from "./components/auth/ResetPassword";
import FavoritesPage from "./components/favourites/FavouritesPage";

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <div className="App">
          <Navbar />
          <Routes>
            <Route path="/" element={<MainPage />} />
            <Route path="/checkout" element={<Checkout />} />
            <Route path="/favourites" element={<FavoritesPage />} />
            <Route path="/auth" element={<AuthForm />} />
            <Route path="/payment" element={<PaymentPage />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />
          </Routes>
          <Footer />
        </div>
      </Router>
    </QueryClientProvider>
  );
}

export default App;
