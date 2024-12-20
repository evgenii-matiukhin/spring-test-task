import { config } from '@/lib/config'

export const API_CONFIG = {
  BASE_URL: config.apiBaseUrl,
  getAuthHeaders: (username: string, password: string) => ({
    'Authorization': 'Basic ' + btoa(`${username}:${password}`),
    'Content-Type': 'application/json',
  }),
};