export interface ProductResponse {
  title: string;
  oldPrice: number;
  newPrice: number;
  discountPhrase: string;
}

export interface SearchProductParams {
  searchTerm?: string;
  storeIds?: string;
}
