import api from './api';

export const userService = {
  // Get User Profile
  getProfile: async () => {
    try {
      const response = await api.get('/user/profile');
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Update Profile
  updateProfile: async profileData => {
    try {
      const response = await api.put('/user/profile', profileData);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Upload Profile Picture
  uploadProfilePicture: async imageUri => {
    try {
      const formData = new FormData();
      formData.append('profilePicture', {
        uri: imageUri,
        type: 'image/jpeg',
        name: 'profile.jpg',
      });

      const response = await api.post('/user/profile-picture', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Get User Statistics
  getStatistics: async () => {
    try {
      const response = await api.get('/user/statistics');
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Get Game History
  getGameHistory: async (page = 1, limit = 20) => {
    try {
      const response = await api.get(`/user/game-history?page=${page}&limit=${limit}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Change Password
  changePassword: async (currentPassword, newPassword) => {
    try {
      const response = await api.post('/user/change-password', {
        currentPassword,
        newPassword,
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Delete Account
  deleteAccount: async () => {
    try {
      const response = await api.delete('/user/account');
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Get Achievements
  getAchievements: async () => {
    try {
      const response = await api.get('/user/achievements');
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Get Leaderboard
  getLeaderboard: async (gameType = 'all', limit = 10) => {
    try {
      const response = await api.get(`/user/leaderboard?gameType=${gameType}&limit=${limit}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },
};

export default userService;