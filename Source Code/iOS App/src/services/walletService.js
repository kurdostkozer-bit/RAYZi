import api from './api';

export const walletService = {
  // Get Wallet Balance
  getBalance: async () => {
    try {
      const response = await api.get('/wallet/balance');
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Get Transaction History
  getTransactions: async (page = 1, limit = 20) => {
    try {
      const response = await api.get(`/wallet/transactions?page=${page}&limit=${limit}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Add Coins (Purchase)
  purchaseCoins: async (amount, paymentMethod) => {
    try {
      const response = await api.post('/wallet/purchase', {
        amount,
        paymentMethod,
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Get Coin Plans
  getCoinPlans: async () => {
    try {
      const response = await api.get('/wallet/coin-plans');
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Redeem Promo Code
  redeemPromoCode: async code => {
    try {
      const response = await api.post('/wallet/redeem-code', {code});
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Withdraw
  withdraw: async (amount, accountDetails) => {
    try {
      const response = await api.post('/wallet/withdraw', {
        amount,
        accountDetails,
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Get Withdraw History
  getWithdrawHistory: async (page = 1, limit = 20) => {
    try {
      const response = await api.get(`/wallet/withdraw-history?page=${page}&limit=${limit}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Get Daily Bonus
  claimDailyBonus: async () => {
    try {
      const response = await api.post('/wallet/daily-bonus');
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Get Referral Bonus
  getReferralBonus: async () => {
    try {
      const response = await api.get('/wallet/referral-bonus');
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Invite Friend
  inviteFriend: async email => {
    try {
      const response = await api.post('/wallet/invite', {email});
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },
};

export default walletService;