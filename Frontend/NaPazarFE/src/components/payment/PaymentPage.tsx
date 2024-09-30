import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import { apiCalls } from "../../shared/apiCalls";
import { useStripe, useElements, CardElement } from "@stripe/react-stripe-js";
import {
  Typography,
  Box,
  Button,
  CircularProgress,
  TextField,
} from "@mui/material";
import { styled } from "@mui/material/styles";

// Styled components
const PaymentPageContainer = styled(Box)(({ theme }) => ({
  padding: theme.spacing(3),
  minWidth: 900,
  margin: "0 auto",
  backgroundColor: theme.palette.background.default,
  borderRadius: theme.shape.borderRadius,
  boxShadow: theme.shadows[3],
}));

const FormBox = styled(Box)(({ theme }) => ({
  padding: theme.spacing(3),
  backgroundColor: theme.palette.background.paper,
  borderRadius: theme.shape.borderRadius,
  boxShadow: theme.shadows[1],
}));

const HeaderTypography = styled(Typography)(({ theme }) => ({
  marginBottom: theme.spacing(2),
  textAlign: "center",
  color: theme.palette.primary.main,
}));

const InputField = styled(TextField)(({ theme }) => ({
  marginBottom: theme.spacing(2),
}));

const CustomCardElement = styled(CardElement)(({ theme }) => ({
  border: `1px solid ${theme.palette.divider}`,
  borderRadius: theme.shape.borderRadius,
  padding: theme.spacing(2),
  backgroundColor: theme.palette.background.paper,
  marginBottom: theme.spacing(2),
}));

const SubmitButton = styled(Button)(({ theme }) => ({
  width: "100%",
  padding: theme.spacing(1.5),
  textTransform: "none",
}));

export interface PaymentIntentRequest {
  amount: number;
  currency: string;
  receiptEmail: string;
}

const PaymentPage: React.FC = () => {
  const stripe = useStripe();
  const elements = useElements();
  const location = useLocation();
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [name, setName] = useState<string>("");
  const [clientSecret, setClientSecret] = useState<string | null>(null);

  const totalAmount = (location.state as { totalAmount: number }).totalAmount;

  useEffect(() => {
    const createPaymentIntent = async () => {
      try {
        setLoading(true);

        const receiptEmail = localStorage.getItem("username");

        const paymentIntentRequest: PaymentIntentRequest = {
          amount: totalAmount,
          currency: "bgn",
          receiptEmail: receiptEmail || "",
        };

        const response = await apiCalls.getPaymentIntent(paymentIntentRequest);

        setClientSecret(response);
      } catch (err) {
        setError("Failed to create payment intent");
      } finally {
        setLoading(false);
      }
    };

    createPaymentIntent();
  }, [totalAmount]);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!stripe || !elements || !clientSecret) {
      return;
    }

    setLoading(true);
    setError(null);

    const { error } = await stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: elements.getElement(CardElement)!,
        billing_details: {
          name,
        },
      },
    });

    if (error) {
      setError(error.message || "An unexpected error occurred.");
    } else {
      alert("Payment successful!");
    }

    setLoading(false);
  };

  return (
    <PaymentPageContainer>
      <HeaderTypography variant="h4">Детайли на плащане</HeaderTypography>
      <FormBox>
        <form onSubmit={handleSubmit}>
          <InputField
            fullWidth
            label="Име на картата"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
          <CustomCardElement
            options={{
              style: {
                base: {
                  fontSize: "16px",
                  color: "#424770",
                  "::placeholder": {
                    color: "#aab7c4",
                  },
                  ":-webkit-autofill": {
                    color: "#fce883",
                  },
                },
                invalid: {
                  color: "#9e2146",
                },
              },
            }}
          />
          <Box mt={2} textAlign="center">
            <SubmitButton
              type="submit"
              variant="contained"
              color="primary"
              disabled={loading || !clientSecret}
            >
              {loading ? (
                <CircularProgress size={24} color="inherit" />
              ) : (
                "Плати"
              )}
            </SubmitButton>
            {error && (
              <Typography color="error" mt={2}>
                {error}
              </Typography>
            )}
          </Box>
        </form>
      </FormBox>
    </PaymentPageContainer>
  );
};

export default PaymentPage;
