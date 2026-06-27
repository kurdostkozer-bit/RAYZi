import { combineReducers } from 'redux';

import adminReducer from './admin/reducer';
import coinPlanReducer from './coinPlan/reducer';
import vipPlanReducer from './vipPlan/reducer';
import giftCategoryReducer from './giftCategory/reducer';
import spinnerReducer from './spinner/reducer';
import giftReducer from './gift/reducer';
import songReducer from './song/reducer';
import hashtagReducer from './hashtag/reducer';
import hostReducer from './host/reducer';
import levelReducer from './level/reducer';
import redeemOptReducer from './redeemOptions/reducer';
import userReducer from './user/reducer';
import postReducer from './post/reducer';
import videoReducer from './video/reducer';
import followerReducer from './follower/reducer';
import settingReducer from './setting/reducer';
import advertisementReducer from './advertisement/reducer';
import complainReducer from './complain/reducer';
import redeemReducer from './redeem/reducer';
import dashboardReducer from './dashboard/reducer';
import reportedUserReducer from './reportedUser/reducer';
import stickerReducer from './sticker/reducer';
import themeReducer from './Theme/theme.reducer';
import fakeUserReducer from './FakeUser/Reducer';
import fakeCommentReducer from './fakeComment/reducer';
import { gameHistoryReducer } from './GameHistory/reducer';
import { admissionSVGAReducer } from './AdmissionCar/reducer';
import { avatarFrameReducer } from './AvatarFrame/reducer';
import gameReducer from './game/reducer';
import agencyReducer from './agency/reducer';
import hostRequestReducer from './hostRequest/reducer';
import commissionReducer from './commision/reducer';
import hostCommissionReducer from './hostCommision/reducer';
import { coinSellerReducer } from './coinSeller/reducer';
import { settlementReducer } from './settlement/settlement.reducer';
import bannerReducer from './banner/reducer';
import reactionReducer from './reaction/reducer';
import agencyRedeemReducer from './agenyRedeem/reducer';

export default combineReducers({
  admin: adminReducer,
  user: userReducer,
  post: postReducer,
  song: songReducer,
  gift: giftReducer,
  host: hostReducer,
  banner: bannerReducer,
  game: gameReducer,
  video: videoReducer,
  level: levelReducer,
  redeemOption: redeemOptReducer,
  sticker: stickerReducer,
  reaction: reactionReducer,
  complain: complainReducer,
  gameHistory: gameHistoryReducer,
  redeem: redeemReducer,
  report: reportedUserReducer,
  dashboard: dashboardReducer,
  hostRequest: hostRequestReducer,
  hashtag: hashtagReducer,
  followersFollowing: followerReducer,
  giftCategory: giftCategoryReducer,
  vipPlan: vipPlanReducer,
  coinPlan: coinPlanReducer,
  setting: settingReducer,
  advertisement: advertisementReducer,
  spinner: spinnerReducer,
  fakeUser: fakeUserReducer,
  Comment: fakeCommentReducer,
  theme: themeReducer,
  admissionSVGA: admissionSVGAReducer,
  avatarFrame: avatarFrameReducer,
  agency: agencyReducer,
  commision: commissionReducer,
  hostCommision: hostCommissionReducer,
  coinSeller: coinSellerReducer,
  settlement: settlementReducer,
  agencyRedeem: agencyRedeemReducer,
});
