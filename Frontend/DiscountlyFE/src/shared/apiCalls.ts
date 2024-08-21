//UTILS
import { call } from "./axiosConfig";
import { createQueryString } from "./apiCallsUtils";

// TYPES
import { ProductResponse } from "../products/types";

// PRODUCTS
const getProducts = () =>
  call<ProductResponse[]>({
    url: `/product/all`,
    method: "GET",
  }).then((response) => response.data);

export const apiCalls = {
  getProducts,
};
