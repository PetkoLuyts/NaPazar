// utils/apiCalls.ts
import { call } from "./axiosConfig";
import { createQueryString } from "./apiCallsUtils";

// TYPES
import { ProductResponse } from "../products/types";
import { SearchProductParams } from "../products/types";

// PRODUCTS
const getProducts = (params: SearchProductParams) => {
  const queryString = createQueryString(params);
  return call<ProductResponse[]>({
    url: `/product/all${queryString}`,
    method: "GET",
  }).then((response) => response.data);
};

export const apiCalls = {
  getProducts,
};
