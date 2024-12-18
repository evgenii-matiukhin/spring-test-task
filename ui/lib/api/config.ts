export const API_CONFIG = {
  BASE_URL: process.env.NEXT_PUBLIC_API_URL,
  getAuthHeaders: (username: string, password: string) => ({
    'Authorization': 'Basic ' + btoa(`${username}:${password}`),
    'Content-Type': 'application/json',
  }),
};