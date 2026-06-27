import * as ActionType from "./seller.type";

const initialState = {
  sellerCoin: [],
  dialog: false,
  dialogData: null,
  history: [],
  total: 0,
  totalCoin:0,
};
export const sellerReducer = (state = initialState, action) => {
  switch (action.type) {
    case ActionType.GET_HISTORY:
      return {
        ...state,
        sellerCoin: action.payload.data,
        total: action.payload.total,
      };
    //   open dialogue
    case ActionType.OPEN_COIN_SELLER_DIALOGUE:
      return {
        ...state,
        dialog: true,
        dialogData: action.payload || null,
      };
    //   close dialogue
    case ActionType.CLOSE_COIN_SELLER_DIALOGUE:
      return {
        ...state,
        dialog: false,
        dialogData: null,
      };

    case ActionType.COIN_DATA_CREATE:
    

      return {
        ...state,
        sellerCoin: action.payload,
      };
    
    case ActionType.HISTORY_TABLE:
      return {
        ...state,
        history: action.payload.coinSeller,
        total:action.payload.total,
        totalCoin:action.payload.totalCoin
      };
    default:
      return state;
  }
};
