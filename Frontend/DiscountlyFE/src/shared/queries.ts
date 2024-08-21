import { useQuery, useSuspenseQuery } from "@tanstack/react-query";

// UTILS
import { apiCalls } from "./apiCalls";

// TYPES & CONSTANTS
import { USE_PRODUCTS_QUERY_KEY } from "./queryKeys";

// PRODUCTS
export const useProducts = () =>
  useQuery({
    queryKey: [USE_PRODUCTS_QUERY_KEY],
    queryFn: () => apiCalls.getProducts(),
  });
