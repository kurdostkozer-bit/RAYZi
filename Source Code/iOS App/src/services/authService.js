import api from './api';

export const authService = {
  // Login
  login: async (email, password) => {
    try {
      const response = await api.post('/auth/login', {email, password});
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Register
  register: async userData => {
    try {
      const response = await api.post('/auth/register', userData);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Logout
  logout: async () => {
    try {
      const response = await api.post('/auth/logout');
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Forgot Password
  forgotPassword: async email => {
    try {
      const response = await api.post('/auth/forgot-password', {email});
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Reset Password
  resetPassword: async (token, newPassword) => {
    try {
      const response = await api.post('/auth/reset-password', {token, newPassword});
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Verify Email
  verifyEmail: async token => {
    try {
      const response = await api.post('/auth/verify-email', {token});
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Social Login
  socialLogin: async (provider, token) => {
    try {
      const response = await api.post('/auth/social-login', {provider, token});
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Google Login
  googleLogin: async idToken => {
    return authService.socialLogin('google', idToken);
  },

  // Facebook Login
  facebookLogin: async accessToken => {
    return authService.socialLogin('facebook', accessToken);
  },

  // Apple Login
  appleLogin: async identityToken => {
    return authService.socialLogin('apple', identityToken);
  },
};

export default authService;