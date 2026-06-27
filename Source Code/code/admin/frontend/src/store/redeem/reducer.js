import { GET_REDEEM, ACCEPT_REDEEM, NEW_REDEEM_CREATE, OPEN_NEW_REDEEM_DIALOG, CLOSE_NEW_REDEEM_DIALOG } from "./types";

const initialState = {
  redeem: [],
  totalRedeem: 0,
  totalRevenue: 0,
  myRedeem: [],
  totalMyRedeem: 0,
  dialog: false,
  dialogData: null,
};

const redeemReducer = (state = initialState, action) => {
  switch (action.type) {
    case GET_REDEEM:
      return {
        ...state,
        redeem: action.payload,
      };

    case ACCEPT_REDEEM:
      return {
        ...state,
        redeem: state.redeem.filter(
          (redeem) => redeem._id !== action.payload
        ),
      };
    case NEW_REDEEM_CREATE:
      const data = [...state.myRedeem];
      data.unshift(action.payload);
      return {
        ...state,
        myRedeem: data,
      };
      case OPEN_NEW_REDEEM_DIALOG:
      return {
        ...state,
        dialog: true,
        dialogData: action.payload || null,
      };
    case CLOSE_NEW_REDEEM_DIALOG:
      return {
        ...state,
        dialog: false,
        dialogData: null,
      };

    default:
      return state;
  }
};

export default redeemReducer;
