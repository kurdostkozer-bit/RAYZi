import {createSlice} from '@reduxjs/toolkit';

const initialState = {
  connected: false,
  connecting: false,
  error: null,
  // تعديل حاسم: تتبع حالة اتصال خادم الألعاب بنص أو قيمة منطقية عوضاً عن كائن السوكيت المعقد
  gameSocketConnected: false, 
};

const socketSlice = createSlice({
  name: 'socket',
  initialState,
  reducers: {
    connectStart: state => {
      state.connecting = true;
      state.error = null;
    },
    connectSuccess: state => {
      state.connecting = false;
      state.connected = true;
      state.error = null;
    },
    connectFailure: (state, action) => {
      state.connecting = false;
      state.connected = false;
      state.error = action.payload;
    },
    disconnect: state => {
      state.connected = false;
      state.gameSocketConnected = false;
      state.error = null;
    },
    setGameSocketStatus: (state, action) => {
      state.gameSocketConnected = action.payload; // استقبال true أو false فقط
    },
    clearError: state => {
      state.error = null;
    },
  },
});

export const {
  connectStart,
  connectSuccess,
  connectFailure,
  disconnect,
  setGameSocketStatus,
  clearError,
} = socketSlice.actions;

export default socketSlice.reducer;
