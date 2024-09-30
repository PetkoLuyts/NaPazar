import qs from "qs";

export const createQueryString = (obj: unknown) =>
  qs.stringify(obj, {
    indices: false,
    addQueryPrefix: true,
    filter: (_, value: unknown) =>
      value instanceof Date ? value.toISOString() : value || undefined,
  });
