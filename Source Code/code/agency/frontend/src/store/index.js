import { combineReducers } from "redux";
import adminReducer from "./admin/reducer";
import spinnerReducer from "./spinner/reducer";
import redeemReducer from "./redeem/reducer";
import agencyCommissionReducer from "./agencyCommission/reducer";
import userReducer from "./user/reducer";
import { sellerReducer } from "./seller/seller.reducer";
import { historyReducer } from "./history/history.reducer";
import hostRequestReducer from "./hostRequest/reducer";
import myRedeemReducer from "./myRedeem/reducer";

export default combineReducers({
  admin: adminReducer,
  user:userReducer,
  history:historyReducer,
  agencyCommission:agencyCommissionReducer,
  redeem: redeemReducer,
  sellerCoin :sellerReducer,
  spinner: spinnerReducer,
  hostRequest: hostRequestReducer,
  myRedeem: myRedeemReducer,
});
