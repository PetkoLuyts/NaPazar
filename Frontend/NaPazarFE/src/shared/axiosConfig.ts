import axios, { AxiosRequestConfig } from "axios";

const caller = axios.create({
  baseURL: import.meta.env.VITE_DISCOUNTLY_API_URL || "http://localhost:8080",
});

export const call = <ResponseType>(params: AxiosRequestConfig) =>
  caller.request<ResponseType>(params);
