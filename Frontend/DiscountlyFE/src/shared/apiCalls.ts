// UTILS
import { call } from "./axiosConfig";
import { createQueryString } from "./apiCallsUtils";

// TYPES
import { ProductResponse } from "../products/types";
import { SearchProductParams } from "../products/types";
import {
  RegisterRequest,
  AuthenticationResponse,
  AuthenticationRequest,
} from "../components/auth/types";

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
  return response.data;
};

const authenticate = async (request: AuthenticationRequest) => {
  const response = await call<AuthenticationResponse>({
    url: `/auth/authenticate`,
    method: "POST",
    data: request,
  });
  return response.data;
};

export const apiCalls = {
  getProducts,
  register,
  authenticate,
};
