import * as ActionType from "./history.type";

const initialState = {
  history: [],
  totalCoin: 0,
  total: 0,
};

export const historyReducer = (state = initialState, action) => {
  switch (action.type) {
    case ActionType.GET_ADMIN_HISTORY:
      return {
        ...state,
        history: action.payload.history,
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

      case ActionType.GET_HOST_LIVE_HISTORY:
        return {
          ...state,
          hostHitory: action.payload.history,
          total: action.payload.total,
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
