import {
  GET_REDEEM,
  ACCEPT_REDEEM,
  NEW_REDEEM_CREATE,
  OPEN_NEW_REDEEM_DIALOG,
  CLOSE_NEW_REDEEM_DIALOG,
  DECLINE_REDEEM,
} from "./types";

const initialState = {
  agencyRedeem: [],
  totalRedeem: 0,
  totalRevenue: 0,
  myAgencyRedeem: [],
  totalMyRedeem: 0,
  dialog: false,
  dialogData: null,
};

const agencyRedeemReducer = (state = initialState, action) => {
  switch (action.type) {
    case GET_REDEEM:
      return {
        ...state,
        agencyRedeem: action.payload,
      };

    case ACCEPT_REDEEM:
      return {
        ...state,
        agencyRedeem: state.agencyRedeem.filter(
          (agencyRedeem) => agencyRedeem._id !== action.payload
        ),
      };

      case DECLINE_REDEEM:
        return {
          ...state,
          agencyRedeem: state.agencyRedeem.filter(
            (agencyRedeem) => agencyRedeem._id !== action.payload
          ),
        };
    case NEW_REDEEM_CREATE:
      const data = [...state.myAgencyRedeem];
      data.unshift(action.payload);
      return {
        ...state,
        myAgencyRedeem: data,
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

export default agencyRedeemReducer;
