import {
  CLOSE_NEW_REDEEM_DIALOG,
  GET_MY_REDEEM_AGENCY_PANEL,
  GET_PERTICULAR_AGENCY_USER_REDEEM_REQUEST,
  NEW_REDEEM_CREATE,
  OPEN_NEW_REDEEM_DIALOG,
} from "./type";

const initialState = {
  totalRedeem: 0,
  totalRevenue: 0,
  myRedeem: [],
  totalMyRedeem: 0,
  dialog: false,
  dialogData: null,
};

const myRedeemReducer = (state = initialState, action) => {
  switch (action.type) {
    case GET_MY_REDEEM_AGENCY_PANEL:
      return {
        ...state,
        myRedeem: action.payload.data,
        totalMyRedeem: action.payload.total,
        totalRevenue: action.payload.totalRevenue,
      };
    case GET_PERTICULAR_AGENCY_USER_REDEEM_REQUEST:
      return {
        ...state,
        userRedeem: action.payload.data,
        totalUserRedeem: action.payload.total,
        totalUserRevenue: action.payload.totalRevenue,
      };
      return {
        ...state,
        myRedeem: action.payload.data,
        totalMyRedeem: action.payload.total,
        totalRevenue: action.payload.totalRevenue,
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

export default myRedeemReducer;
