import * as ActionType from "./history.type";

const initialState = {
  history: [],
  hostHistory:[],
  totalCoin: 0,
  total: 0,
};

export const historyReducer = (state = initialState, action) => {
  switch (action.type) {
    case ActionType.GET_ADMIN_HISTORY:
      return {
        ...state,
        hostHistory: action.payload.history,
        total: action.payload.total,
      };

    case ActionType.GET_ADMIN_CASHOUT:
      return {
        ...state,
        historyCashOut: action.payload.history,
        total: action.payload.total,
      };

    case ActionType.GET_HOST_HISTORY:
      return {
        ...state,
        hostHitory: action.payload.history,
        total: action.payload.total,
      };

    case ActionType.GET_HOST_HISTORY_INFO:
      return {
        ...state,
        infoHistory: action.payload.history,
      };

   

    case ActionType.GET_ADMIN_EARNING:
      return {
        ...state,
        earning: action.payload.history,
      };
    default:
      return state;
  }
};
