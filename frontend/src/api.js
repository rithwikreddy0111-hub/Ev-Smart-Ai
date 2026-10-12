import axios from 'axios';

// Set VITE_API_BASE_URL (including /api) in Vercel. In a production build without it we fall back to the
// deployed Render backend, otherwise local development uses http://localhost:8080/api.
const PROD_DEFAULT = 'https://ev-smart-ai.onrender.com/api';
const baseURL = (import.meta.env.VITE_API_BASE_URL || (import.meta.env.PROD ? PROD_DEFAULT : 'http://localhost:8080/api')).replace(/\/+$/, '');

// 60s: free Render instances can take ~50s to wake up, and a 10s timeout made the first login after idle fail.
export const api = axios.create({ baseURL, timeout: 60000 });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('ev_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// An expired or invalid token should send the user back to the sign-in screen instead of showing endless errors.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const url = error?.config?.url || '';
    if (error?.response?.status === 401 && !url.startsWith('/auth/') && localStorage.getItem('ev_token')) {
      window.dispatchEvent(new Event('ev-auth-expired'));
    }
    return Promise.reject(error);
  }
);

// Optional separate battery-health AI service. Empty means "not configured": the app uses its built-in estimate
// instead of calling localhost:8000 from every visitor's browser.
export const aiBase = (import.meta.env.VITE_AI_BASE_URL || '').replace(/\/+$/, '');
