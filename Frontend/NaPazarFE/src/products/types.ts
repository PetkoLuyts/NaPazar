export interface ProductResponse {
  id: number;
  title: string;
  oldPrice: number;
  newPrice: number;
  discountPhrase: string;
}

export interface SearchProductParams {
  searchTerm?: string;
  storeIds?: string;
}

export interface PaymentInfoDTO {
  amount: number;
  currency: string;
  receiptEmail: string | undefined;
}
