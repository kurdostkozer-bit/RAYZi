import { combineReducers } from "redux";
import adminReducer from "./admin/reducer";
import spinnerReducer from "./spinner/reducer";
import redeemReducer from "./redeem/reducer";
import userReducer from "./user/reducer";
import { sellerReducer } from "./seller/seller.reducer";
import { historyReducer } from "./history/history.reducer";
import hostRequestReducer from "./hostRequest/reducer";
import myRedeemReducer from "./myRedeem/reducer";
import liveRoomReducer from "./liveRoom/reducer";
// import chatRoomReducer from "./chatRoom/reducer";
import videoCallRoomReducer from "./videoCall/reducer";

export default combineReducers({
  admin: adminReducer,
  user: userReducer,
  history: historyReducer,
  liveRoomHistory: liveRoomReducer,
  // chatRoomHistory: chatRoomReducer,
  redeem: redeemReducer,
  sellerCoin: sellerReducer,
  spinner: spinnerReducer,
  hostRequest: hostRequestReducer,
  videoCallHistory: videoCallRoomReducer,
  myRedeem: myRedeemReducer,
});
