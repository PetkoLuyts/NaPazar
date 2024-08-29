import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import Navbar from "./components/navbar/Navbar";
import MainPage from "./components/pages/MainPage";
import Checkout from "./components/checkout/Checkout";
import "./App.css";
import AuthForm from "./components/auth/AuthForm";

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
            <Route path="/auth" element={<AuthForm />} />
          </Routes>
        </div>
      </Router>
    </QueryClientProvider>
  );
}

export default App;
