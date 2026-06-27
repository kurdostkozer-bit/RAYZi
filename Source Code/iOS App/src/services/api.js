import axios from 'axios';
import {CONFIG} from '../utils/config';

// Create axios instance
const api = axios.create({
  baseURL: CONFIG.API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
api.interceptors.request.use(
  async config => {
    // Add auth token if available
    const token = await getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  response => {
    return response;
  },
  async error => {
    const originalRequest = error.config;

    // Handle 401 Unauthorized
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Try to refresh token
        const refreshToken = await getRefreshToken();
        if (refreshToken) {
          const response = await axios.post(`${CONFIG.API_BASE_URL}/auth/refresh`, {
            refreshToken,
          });
          
          const {token} = response.data;
          await saveToken(token);
          
          // Retry original request
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return api(originalRequest);
        }
      } catch (refreshError) {
        // Refresh failed, logout user
        await clearTokens();
        // Navigate to login (this should be handled by the calling code)
      }
    }

    return Promise.reject(error);
  }
);

// Token management helpers
const getToken = async () => {
  try {
    const AsyncStorage = require('@react-native-async-storage/async-storage').default;
    return await AsyncStorage.getItem('authToken');
  } catch (error) {
    return null;
  }
};

const getRefreshToken = async () => {
  try {
    const AsyncStorage = require('@react-native-async-storage/async-storage').default;
    return await AsyncStorage.getItem('refreshToken');
  } catch (error) {
    return null;
  }
};

const saveToken = async token => {
  try {
    const AsyncStorage = require('@react-native-async-storage/async-storage').default;
    await AsyncStorage.setItem('authToken', token);
  } catch (error) {
    console.error('Error saving token:', error);
  }
};

const clearTokens = async () => {
  try {
    const AsyncStorage = require('@react-native-async-storage/async-storage').default;
    await AsyncStorage.multiRemove(['authToken', 'refreshToken']);
  } catch (error) {
    console.error('Error clearing tokens:', error);
  }
};

export default api;
export {getToken, saveToken, clearTokens};