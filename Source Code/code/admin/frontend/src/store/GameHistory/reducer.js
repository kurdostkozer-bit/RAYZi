import { GET_GAME_HISTORY, RESET_COIN } from "./type";

const initialState = {
  gameHistory: [],
  total: 0,
  adminCoin: 0,
};

export const gameHistoryReducer = (state = initialState, action) => {
  switch (action.type) {
    case GET_GAME_HISTORY:
      return {
        ...state,
        gameHistory: action.payload.gameHistories,
        total: action.payload.totalPage,
        adminCoin: action.payload.adminCoin,
      };
    case RESET_COIN:
      return {
        ...state,
        adminCoin: action.payload,
      };
    default:
      return state;
  }
};
