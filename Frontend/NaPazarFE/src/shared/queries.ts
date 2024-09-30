import { useQuery } from "@tanstack/react-query";

// UTILS
import { apiCalls } from "./apiCalls";

// TYPES & CONSTANTS
import { USE_PRODUCTS_QUERY_KEY } from "./queryKeys";
import { SearchProductParams } from "../products/types";

export const useProducts = (
  params: SearchProductParams & { page: number; size: number }
) =>
  useQuery({
    queryKey: [USE_PRODUCTS_QUERY_KEY, params],
    queryFn: () => apiCalls.getProducts(params),
  });
