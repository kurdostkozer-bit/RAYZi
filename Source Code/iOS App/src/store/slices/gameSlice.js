import {createSlice} from '@reduxjs/toolkit';

const initialState = {
  currentGame: null,
  gameType: null,
  table: null,
  players: [],
  gameData: null,
  loading: false,
  error: null,
  isPlaying: false,
  gameHistory: [],
};

const gameSlice = createSlice({
  name: 'game',
  initialState,
  reducers: {
    setGameType: (state, action) => {
      state.gameType = action.payload;
    },
    joinTableStart: state => {
      state.loading = true;
      state.error = null;
    },
    joinTableSuccess: (state, action) => {
      state.loading = false;
      state.table = action.payload.table;
      state.players = action.payload.players;
      state.isPlaying = true;
      state.error = null;
    },
    joinTableFailure: (state, action) => {
      state.loading = false;
      state.error = action.payload;
    },
    leaveTable: state => {
      state.table = null;
      state.players = [];
      state.isPlaying = false;
      state.gameData = null;
    },
    updateGameData: (state, action) => {
      state.gameData = {...state.gameData, ...action.payload};
    },
    updatePlayers: (state, action) => {
      state.players = action.payload;
    },
    // تعديل أمني: التحقق أولاً من عدم وجود اللاعب في المصفوفة لمنع مشكلة التكرار الرسومي (Duplicate Keys Error)
    playerJoined: (state, action) => {
      const exists = state.players.some(p => p.id === action.payload.id);
      if (!exists) {
        state.players = [...state.players, action.payload];
      }
    },
    // تعديل تقني: التحقق من معرّف اللاعب سواء كان كائن كامل أو مجرد نص (ID string) لضمان مرونة استلام الأحداث من السوكيت
    playerLeft: (state, action) => {
      const targetId = typeof action.payload === 'object' ? action.payload.id : action.payload;
      state.players = state.players.filter(p => p.id !== targetId);
    },
    setGameResult: (state, action) => {
      state.gameData = {...state.gameData, result: action.payload};
    },
    addToHistory: (state, action) => {
      state.gameHistory = [action.payload, ...state.gameHistory].slice(0, 50);
    },
    clearError: state => {
      state.error = null;
    },
  },
});

export const {
  setGameType,
  joinTableStart,
  joinTableSuccess,
  joinTableFailure,
  leaveTable,
  updateGameData,
  updatePlayers,
  playerJoined,
  playerLeft,
  setGameResult,
  addToHistory,
  clearError,
} = gameSlice.actions;

export default gameSlice.reducer;
