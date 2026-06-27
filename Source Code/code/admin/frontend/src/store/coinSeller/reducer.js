import * as ActionType from "./type";
const initialState = {
  coinSeller: [],
  total: 0,
  totalHistory: 0,
  coinSellerId: [],
  coinSellerHistory: [],
  dialogOpen: false,
  dialogData: null,
  monyDialogOpen: false,
  moneyDialogData: null,
  lessMonyDialogOpen: false,
  lessMoneyDialogData: null,
  totalCoin: 0,
  mobileDialogOpen: false,
  mobileDialogData: null,
};

export const coinSellerReducer = (state = initialState, action) => {
  switch (action.type) {
    case ActionType.GET_COINSELLER:
      return {
        ...state,
        coinSeller: action.payload.coinSeller,
        total: action.payload.total,
      };
    case ActionType.GET_COINSELLER_UNIQUEID:
      return {
        ...state,
        coinSellerId: action.payload,
      };
    case ActionType.OPEN_DIALOGUE:
      return {
        ...state,
        dialogOpen: true,
        dialogData: action.payload || null,
      };

    case ActionType.OPEN_COINSELLER_DIALOGUE:
      return {
        ...state,
        dialogOpen: true,
        dialogData: action.payload || null,
      };

    case ActionType.CLOSE_DIALOGUE:
      return {
        ...state,
        dialogOpen: false,
        dialogData: null,
      };
    case ActionType.ADD_COINSELLER:
      let data = [...state.coinSeller];
      data?.unshift(action.payload);

      return {
        ...state,
        coinSeller: data,
      };

    case ActionType.EDIT_COINSELLER:
      return {
        ...state,
        coinSeller: state.coinSeller.map((coinSellerId) =>
          coinSellerId._id === action.payload.id
            ? action.payload.coinSeller
            : coinSellerId
        ),
      };

    case ActionType.ADD_MONEY_BY_ADMIN:
      return {
        ...state,
        coinSeller: state.coinSeller.map((coinSellerId) =>
          coinSellerId?._id == action.payload.id
            ? action.payload.coinSeller
            : coinSellerId
        ),
      };
    case ActionType.LESS_MONEY_BY_ADMIN:
      return {
        ...state,
        coinSeller: state.coinSeller.map((coinSellerId) =>
          coinSellerId?._id == action.payload.id
            ? action.payload.coinSeller
            : coinSellerId
        ),
      };

    case ActionType.DELETE_COINSELLER:
      return {
        ...state,
        coinSeller: state.coinSeller.map((coinSellerId) =>
          coinSellerId?._id == action?.payload?.id
            ? action?.payload?.data
            : coinSellerId
        ),
      };

    case ActionType.ADD_MONEY_OPEN_DIALOGUE:
      return {
        ...state,
        monyDialogOpen: true,
        moneyDialogData: action.payload || null,
      };
    case ActionType.ADD_MONEY_CLOSE_DIALOGUE:
      return {
        ...state,
        monyDialogOpen: false,
        moneyDialogData: null,
      };
    case ActionType.GET_COINSELLER_HISTORY:
      return {
        ...state,
        coinSellerHistory: action.payload.history,
        totalCoin: action.payload.totalCoin,
        totalHistory: action.payload.total,
      };

    case ActionType.MOBILE_NUMBER_BY_ADMIN:
      return {
        ...state,
        coinSeller: state.coinSeller.map((coinSellerId) =>
          coinSellerId?._id == action.payload.id
            ? action.payload.coinSeller
            : coinSellerId
        ),
      };

    case ActionType.SHOW_COINSELLER:
      return {
        ...state,
        coinSeller: state.coinSeller.map((coinSellerData) => {
          if (coinSellerData._id === action.payload.id)
            return action.payload.data;
          else return coinSellerData;
        }),
      };

    case ActionType.ADD_MOBILE_OPEN_DIALOGUE:
      return {
        ...state,
        mobileDialogOpen: true,
        mobileDialogData: action.payload || null,
      };
    case ActionType.ADD_MOBILE_CLOSE_DIALOGUE:
      return {
        ...state,
        mobileDialogOpen: false,
        mobileDialogData: null,
      };

    case ActionType.LESS_MONEY_OPEN_DIALOGUE:
      return {
        ...state,
        lessMonyDialogOpen: true,
        lessMoneyDialogData: action.payload || null,
      };
    case ActionType.LESS_MONEY_CLOSE_DIALOGUE:
      return {
        ...state,
        lessMonyDialogOpen: false,
        lessMoneyDialogData: null,
      };
    default:
      return state;
  }
};
