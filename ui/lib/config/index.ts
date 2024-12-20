export const config = {
  apiMocking: process.env.NEXT_PUBLIC_API_MOCKING === 'enabled',
  apiBaseUrl: process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api/v1',
};