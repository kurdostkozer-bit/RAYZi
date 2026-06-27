import {
  GET_REDEEM,
  ACCEPT_REDEEM,
  GET_SETTING,
  GET_MY_REDEEM,
  OPEN_NEW_REDEEM_DIALOG,
  CLOSE_NEW_REDEEM_DIALOG,
  NEW_REDEEM_CREATE,
  UPDATE_REDEEM_AGENCY,
} from "./types";

const initialState = {
  redeem: [],
  totalRedeem: 0,
  totalRevenue: 0,
  myRedeem: [],
  totalMyRedeem:0,
  setting: {},
  dialog: false,
  dialogData: null,
};

const redeemReducer = (state = initialState, action) => {
  switch (action.type) {
    case GET_REDEEM:
      return {
        ...state,
        redeem: action.payload.data,
        totalRedeem: action.payload.total,
      };
    case GET_MY_REDEEM:
      return {
        ...state,
        myRedeem: action.payload.data,
        totalMyRedeem:action.payload.total,
        totalRevenue: action.payload.totalRevenue
      };
    case ACCEPT_REDEEM:
      return {
        ...state,
        redeem: state.redeem.filter(
          (redeem) => redeem?._id !== action.payload?.id
        ),
      };
    case GET_SETTING:
      return {
        ...state,
        setting: action.payload,
      };
    case NEW_REDEEM_CREATE:
      const data = [...state.myRedeem];
      data.unshift(action.payload);
      return {
        ...state,
        myRedeem: data,
      };
    case UPDATE_REDEEM_AGENCY:
      return {
        ...state,
        myRedeem: state.myRedeem?.map((redeem) => {
          if (redeem?._id === action.payload?.id) return action.payload?.data;
          else return redeem;
        }),
      };
    case OPEN_NEW_REDEEM_DIALOG:
      return {
        ...state,
        dialog: true,
        dialogData: action.payload,
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
