import {createSlice} from '@reduxjs/toolkit';

const initialState = {
  balance: 0,
  coins: 0,
  transactions: [],
  loading: false,
  error: null,
};

const walletSlice = createSlice({
  name: 'wallet',
  initialState,
  reducers: {
    fetchWalletStart: state => {
      state.loading = true;
      state.error = null;
    },
    fetchWalletSuccess: (state, action) => {
      state.loading = false;
      state.balance = action.payload.balance;
      state.coins = action.payload.coins;
      state.transactions = action.payload.transactions || [];
      state.error = null;
    },
    fetchWalletFailure: (state, action) => {
      state.loading = false;
      state.error = action.payload;
    },
    addCoins: (state, action) => {
      state.coins += action.payload;
    },
    deductCoins: (state, action) => {
      // تعديل أمني: منع رصيد العملات من الهبوط تحت الصفر مطلقاً لحماية حسابات اللعبة
      const nextCoins = state.coins - action.payload;
      state.coins = nextCoins < 0 ? 0 : nextCoins;
    },
    addTransaction: (state, action) => {
      state.transactions = [action.payload, ...state.transactions].slice(0, 100);
    },
    updateBalance: (state, action) => {
      state.balance = action.payload;
    },
    clearError: state => {
      state.error = null;
    },
  },
});

export const {
  fetchWalletStart,
  fetchWalletSuccess,
  fetchWalletFailure,
  addCoins,
  deductCoins,
  addTransaction,
  updateBalance,
  clearError,
} = walletSlice.actions;

export default walletSlice.reducer;
