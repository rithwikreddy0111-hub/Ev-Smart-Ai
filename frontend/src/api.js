import axios from 'axios';
export const api = axios.create({ baseURL: import.meta.env.VITE_API_BASE_URL || 'https://ev-smart-ai.onrender.com/api', timeout: 10000 });
api.interceptors.request.use((config) => { const token = localStorage.getItem('ev_token'); if (token) config.headers.Authorization = `Bearer ${token}`; return config; });
export const aiBase = import.meta.env.VITE_AI_BASE_URL || 'https://ev-smart-ai.onrender.com/api';
