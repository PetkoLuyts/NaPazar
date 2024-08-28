import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import "./index.css";
import { loadStripe } from "@stripe/stripe-js";
import { Elements } from "@stripe/react-stripe-js";

const stripePromise = loadStripe(
  "pk_test_51OiFMxDwkSJeeW0ievApPuSoWtxfNiR4rwWFKIC4ddwxCRqQiscoO9ltc1yyspic9GLzZbmC9fs8sat0qIoljodj00yna29rNR"
);

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <Elements stripe={stripePromise}>
      <App />
    </Elements>
  </React.StrictMode>
);
