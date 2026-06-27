import React, { useEffect } from 'react';

// js
import '../assets/js/main.min.js';

// router
import { Routes, Route, useNavigate, useLocation } from 'react-router-dom';

// css
import '../assets/css/main.min.css';
import '../assets/css/custom.css';

// components
import Navbar from '../component/navbar/Navbar';
import Topnav from '../component/navbar/Topnav';
import CoinPlanTable from '../component/table/CoinPlan';
import PurchaseCoinPlanHistoryTable from '../component/table/PurchaseCoinPlanHistory';
import VIPPlanTable from '../component/table/VIPPlan';
import PurchaseVIPPlanTable from '../component/table/PurchaseVipPlanHistory';
import GiftCategoryTable from '../component/table/GiftCategory';
import GiftTable from '../component/table/Gift';
import SongTable from '../component/table/Song';
import SongDialog from '../component/dialog/Song';
import GameTable from '../component/table/Game';
import GiftDialog from '../component/dialog/Gift/Add';
import HashtagTable from '../component/table/Hashtag';
import LevelTable from '../component/table/Level';
import UserTable from '../component/table/User';
import PostTable from '../component/table/Post';
import VideoTable from '../component/table/Video';
import UserDetail from './UserDetail';
import UserHistory from './UserHistory';
import PostDetail from './PostDetail';
import VideoDetail from './VideoDetail';
import Dashboard from './Dashboard';
import Setting from './Settings';
import ThemeTable from '../component/table/Theme';
import Advertisement from '../component/table/Advertisement';
import PendingComplainTable from '../component/table/PendingComplain';
import SolvedComplainTable from '../component/table/SolvedComplain';
import ReportedUserTable from '../component/table/ReportedUser';
import StickerTable from '../component/table/Sticker';
import FakeUser from '../component/table/FakeUser';
import FakeUserPage from '../component/dialog/FakeUserPage';
import Banner from '../component/table/Banner';
import Reaction from '../component/table/Reaction';
import Profile from './Profile';
import GameHistory from '../component/table/GameHistory';
import Avatar from '../component/table/Avatar';
import AdmissionCar from '../component/table/AdmissionCar';
import UserRedeemRequest from '../component/table/userRedeem/UserRedeemRequest';
import HostRequest from '../component/table/hostRequest/HostRequest';
import CoinSeller from './CoinSeller';
import CoinSellerHistory from '../component/table/CoinSellerHistory';
import AllSettlement from '../component/table/settlement/AllSettlement';
import SettlementPending from '../component/table/settlement/SettlementPending';
import SettlementSolved from '../component/table/settlement/SettlementSolved';
import HostSettlementPending from '../component/table/hostSettlement/SettlementPending.js';
import HostSettlementSolved from '../component/table/hostSettlement/SettlementSolved.js';
import FakePost from '../component/table/FakePost.js';
import FakeComment from '../component/table/FakeComment.js';
import FakeVideo from '../component/table/FakeVideo.js';
import Agency from './Agency';
import AgencyWiseHost from './AgencyWiseHost';
import AgencyRedeemRequest from '../component/table/agencyRedeem/AgencyRedeemRequest';
import AgencyHistory from './AgencyHistory.js';
import ComplainRequest from '../component/table/complain/ComplainRequest';
import FakeCommentDialog from '../component/dialog/FakeCommentDialog.js';
import FakeNormalUser from '../component/dialog/FakeLiveStreamingPage.js';
import FakePkUserPage from '../component/dialog/FakePkUserPage.js';
import FakeAudioUserPage from '../component/dialog/FakeAudioUserPage.js';
import FakePostPage from '../component/dialog/FakePostPage.js';
import FakeVideoPage from '../component/dialog/FakeVideoPage.js';
import MainPost from './MainPost.js';
import MainVideo from './MainVideo.js';
import MainPlan from './MainPlan.js';
import PlanHistory from './PlanHistory.js';
import Host from '../component/table/Host.js';

const Admin = () => {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    if (
      location.pathname === '/admin' ||
      location.pathname === '/admin/dashboard'
    ) {
      navigate('/admin/dashboard');
    }
  }, [location.pathname, navigate]);

  return (
    <>
      <div className="page-container">
        <Navbar />
        <div className="page-content">
          <Topnav />
          <div className="main-wrapper">
            <Routes>
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/banner" element={<Banner />} />
              <Route path="/adminProfile" element={<Profile />} />
              {/* Plan Module  */}
              <Route path="/mainPlan" element={<MainPlan />} />
              <Route path="/coinplan" element={<CoinPlanTable />} />
              <Route path="/vipplan" element={<VIPPlanTable />} />

              {/* Plan History Module  */}
              <Route path="/planHistory" element={<PlanHistory />} />
              <Route
                path="/coinplan/history"
                element={<PurchaseCoinPlanHistoryTable />}
              />
              <Route
                path="/vipplan/history"
                element={<PurchaseVIPPlanTable />}
              />

              <Route path="/giftCategory" element={<GiftCategoryTable />} />
              <Route path="/gameHistory" element={<GameHistory />} />
              <Route path="/reaction" element={<Reaction />} />
              <Route path="/allSettlement" element={<AllSettlement />} />
              <Route path="/pending" element={<SettlementPending />} />
              <Route path="/solved" element={<SettlementSolved />} />
              <Route path="/hostpending" element={<HostSettlementPending />} />
              <Route path={`/hostsolved`} element={<HostSettlementSolved />} />
              <Route path={`/comment`} element={<FakeComment />} />

              <Route path="/agency" element={<Agency />} />
              <Route path="/agencyHistory" element={<AgencyHistory />} />
              <Route
                path="/agencyRedeemRequest"
                element={<AgencyRedeemRequest />}
              />
              <Route
                path="/agency/agencyWiseHost"
                element={<AgencyWiseHost />}
              />
              <Route
                path="/agency/agencyWiseHost"
                element={<AgencyWiseHost />}
              />
              <Route path="/theme" element={<ThemeTable />} />
              <Route path="/fake/fakeUserdialog" element={<FakeUserPage />} />
              <Route
                path="/fake/fakePkUserdialog"
                element={<FakePkUserPage />}
              />
              <Route path="/fake/fakeUserdialog" element={<FakeUserPage />} />
              <Route
                path="/fake/fakeAudioUserdialog"
                element={<FakeAudioUserPage />}
              />

              <Route path="/gift" element={<GiftTable />} />
              <Route path="/gift/dialog" element={<GiftDialog />} />
              <Route path="/song" element={<SongTable />} />
              <Route path="/song/dialog" element={<SongDialog />} />
              <Route path="/hashtag" element={<HashtagTable />} />
              <Route path="/level" element={<LevelTable />} />
              <Route path="/user" element={<UserTable />} />
              <Route path={`/fakeUser`} element={<FakeUser />} />
              <Route path="/user/detail" element={<UserDetail />} />
              <Route path="/user/history" element={<UserHistory />} />

              {/* Post module  */}
              <Route path="/mainPost" element={<MainPost />} />
              <Route path="/post" element={<PostTable />} />
              <Route path="/post/fake" element={<FakePost />} />
              <Route path="/post/detail" element={<PostDetail />} />
              <Route path="/post/dialog" element={<FakePostPage />} />

              {/* Video Module  */}
              <Route path="/mainVideo" element={<MainVideo />} />
              <Route path="/video" element={<VideoTable />} />
              <Route path="/video/fake" element={<FakeVideo />} />
              <Route path="/video/detail" element={<VideoDetail />} />
              <Route path="/video/dialog" element={<FakeVideoPage />} />

              <Route path="/setting" element={<Setting />} />
              <Route path="/reportedUser" element={<ReportedUserTable />} />
              <Route path="/advertisement" element={<Advertisement />} />
              <Route path="/coinSeller" element={<CoinSeller />} />
              <Route
                path="/coinSeller/history"
                element={<CoinSellerHistory />}
              />
              <Route
                path="/userRedeemRequest"
                element={<UserRedeemRequest />}
              />
              <Route
                path="/coinSeller/history"
                element={<CoinSellerHistory />}
              />
              <Route
                path="/userRedeemRequest"
                element={<UserRedeemRequest />}
              />
              <Route path="/hostRequest" element={<HostRequest />} />
              <Route path="/host" element={<Host />} />
              <Route path="/sticker" element={<StickerTable />} />
              <Route path="/avatarFrame" element={<Avatar />} />
              <Route path="/entryEffect" element={<AdmissionCar />} />
              <Route path="/game" element={<GameTable />} />
              <Route path="/complainRequest" element={<ComplainRequest />} />
              <Route path="*" element={<Dashboard />} />
            </Routes>
          </div>
        </div>
      </div>
    </>
  );
};

export default Admin;
