import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiCalls } from "../../shared/apiCalls";
import { CartItem } from "../../components/cart/types";
import {
  Typography,
  CircularProgress,
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  Grid,
  IconButton,
} from "@mui/material";
import { styled } from "@mui/material/styles";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";

const CheckoutContainer = styled(Box)(({ theme }) => ({
  padding: theme.spacing(3),
  maxWidth: 1000,
  margin: "0 auto",
}));

const TableHeaderCell = styled(TableCell)(({ theme }) => ({
  fontWeight: "bold",
  backgroundColor: theme.palette.primary.light,
  color: theme.palette.primary.contrastText,
}));

const TotalBox = styled(Box)(({ theme }) => ({
  marginTop: theme.spacing(3),
  padding: theme.spacing(2),
  backgroundColor: theme.palette.background.paper,
  borderRadius: theme.shape.borderRadius,
  boxShadow: theme.shadows[2],
}));

const BackgroundTypography = styled(Typography)(({ theme }) => ({
  backgroundColor: "rgba(255, 255, 255, 0.8)",
  padding: theme.spacing(1),
  borderRadius: theme.shape.borderRadius,
}));

const Checkout: React.FC = () => {
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCartItems = async () => {
      try {
        const items = await apiCalls.getCartItems();
        setCartItems(items);
      } catch (err) {
        setError("Failed to load cart items.");
      } finally {
        setLoading(false);
      }
    };

    fetchCartItems();
  }, []);

  const handleDelete = async (itemId: number) => {
    try {
      await apiCalls.removeItemFromCart(itemId);
      setCartItems(cartItems.filter((item) => item.id !== itemId));
    } catch (err) {
      setError("Failed to delete item.");
    }
  };

  const handleEdit = async (itemId: number, quantity: number) => {
    if (!isNaN(quantity) && quantity > 0) {
      try {
        await apiCalls.updateItemQuantity(itemId, quantity);
        setCartItems(
          cartItems.map((item) =>
            item.id === itemId ? { ...item, quantity } : item
          )
        );
      } catch (err) {
        setError("Failed to update the item quantity.");
      }
    } else {
      alert("Please enter a valid quantity.");
    }
  };

  if (loading) {
    return (
      <CheckoutContainer textAlign="center">
        <CircularProgress />
        <BackgroundTypography variant="h6" color="textSecondary" mt={2}>
          Loading...
        </BackgroundTypography>
      </CheckoutContainer>
    );
  }

  if (error) {
    return (
      <CheckoutContainer textAlign="center">
        <BackgroundTypography variant="h6" color="error">
          {error}
        </BackgroundTypography>
      </CheckoutContainer>
    );
  }

  const totalAmount = cartItems.reduce(
    (total, item) => total + item.price * item.quantity,
    0
  );

  const handleProceedToPayment = () => {
    navigate("/payment", { state: { totalAmount } });
  };

  return (
    <CheckoutContainer>
      <BackgroundTypography
        variant="h5"
        gutterBottom
        align="center"
        color="textSecondary"
      >
        Продукти в количката:
      </BackgroundTypography>
      {cartItems.length === 0 ? (
        <BackgroundTypography variant="h6" color="textSecondary" align="center">
          Количката е празна.
        </BackgroundTypography>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableHeaderCell>Продукт</TableHeaderCell>
                <TableHeaderCell>Бр.</TableHeaderCell>
                <TableHeaderCell>Цена</TableHeaderCell>
                <TableHeaderCell>Общо</TableHeaderCell>
                <TableHeaderCell>Действия</TableHeaderCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {cartItems.map((item) => (
                <TableRow key={item.id}>
                  <TableCell>
                    <Typography variant="body1">{item.productTitle}</Typography>
                  </TableCell>
                  <TableCell>
                    <input
                      type="number"
                      value={item.quantity}
                      onChange={(e) =>
                        handleEdit(item.id, parseInt(e.target.value))
                      }
                      min="1"
                      style={{ width: "50px" }}
                    />
                  </TableCell>
                  <TableCell>
                    <Typography variant="body1">
                      {item.price.toFixed(2)} лв
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Typography variant="body1">
                      {(item.price * item.quantity).toFixed(2)} лв
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <IconButton
                      aria-label="edit"
                      color="primary"
                      onClick={() => handleEdit(item.id, item.quantity)}
                    >
                      <EditIcon />
                    </IconButton>
                    <IconButton
                      aria-label="delete"
                      color="secondary"
                      onClick={() => handleDelete(item.id)}
                    >
                      <DeleteIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
      <TotalBox>
        <Grid container justifyContent="space-between" alignItems="center">
          <BackgroundTypography variant="h5" color="primary">
            Общо: {totalAmount.toFixed(2)} лв
          </BackgroundTypography>
          <Button
            variant="contained"
            color="primary"
            onClick={handleProceedToPayment}
          >
            Продължи към плащане
          </Button>
        </Grid>
      </TotalBox>
    </CheckoutContainer>
  );
};

export default Checkout;
