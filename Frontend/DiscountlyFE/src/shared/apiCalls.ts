// UTILS
import { call } from "./axiosConfig";
import { createQueryString } from "./apiCallsUtils";
import { CartItem } from "../components/cart/types";

// TYPES
import { ProductResponse } from "../products/types";
import { SearchProductParams } from "../products/types";
import {
  RegisterRequest,
  AuthenticationResponse,
  AuthenticationRequest,
} from "../components/auth/types";
import { PaymentIntentRequest } from "../components/payment/types";

// PRODUCTS
const getProducts = async (
  params: SearchProductParams & { page: number; size: number }
) => {
  const queryString = createQueryString(params);
  const response = await call<{
    products: ProductResponse[];
    totalPages: number;
    totalElements: number;
  }>({
    url: `/product/all${queryString}`,
    method: "GET",
  });
  return response.data;
};

// AUTH
const register = async (request: RegisterRequest) => {
  const response = await call<AuthenticationResponse>({
    url: `/auth/register`,
    method: "POST",
    data: request,
  });
  const token = response.data.access_token;
  const refreshToken = response.data.refresh_token;
  const username = response.data.username;

  localStorage.setItem("token", token);
  localStorage.setItem("refreshToken", refreshToken);
  localStorage.setItem("username", username);

  return response.data;
};

const authenticate = async (request: AuthenticationRequest) => {
  const response = await call<AuthenticationResponse>({
    url: `/auth/authenticate`,
    method: "POST",
    data: request,
  });
  const token = response.data.access_token;
  const refreshToken = response.data.refresh_token;
  const username = response.data.username;

  localStorage.setItem("token", token);
  localStorage.setItem("refreshToken", refreshToken);
  localStorage.setItem("username", username);

  return response.data;
};

// CART
const addItemToCart = async (productId: number) => {
  const token = localStorage.getItem("token");

  await call<void>({
    url: `/cart/add-item/${productId}`,
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
};

const getCartItems = async () => {
  const token = localStorage.getItem("token");

  const response = await call<CartItem[]>({
    url: `/cart/items`,
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  return response.data;
};

// PAYMENT
const getPaymentIntent = async (request: PaymentIntentRequest) => {
  const token = localStorage.getItem("token");

  const response = await call<string>({
    url: `/payment/payment-intent`,
    method: "POST",
    data: request,
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  return response.data;
};

export const apiCalls = {
  getProducts,
  register,
  authenticate,
  addItemToCart,
  getCartItems,
  getPaymentIntent,
};
