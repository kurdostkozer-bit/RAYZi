import React, { useEffect } from 'react';

// routing
import { NavLink as Link } from 'react-router-dom';

// alert
import { warning } from '../../util/Alert';

// redux
import { useDispatch } from 'react-redux';

// types
import { UNSET_ADMIN } from '../../store/admin/types';
import { ReactComponent as Settlement } from '../../assets/images/settlement.svg';
import { ReactComponent as HostSettleMent } from '../../assets/images/hostSettlement.svg';

//MUI

// jquery
import $ from "jquery";
import { useNavigate  } from "react-router-dom";
import { projectName } from '../../util/Config';




const Navbar = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const handleLogout = () => {
    const data = warning();
    data.then((isLogout) => {
      if (isLogout) {
        dispatch({ type: UNSET_ADMIN });
        navigate('/');
      }
    });
  };

  useEffect(() => {
    $('').addClass('submenu-margin');
  }, []);
  return (
    <>
      <div className="page-sidebar">
        <Link to="/admin/dashboard">
          <span className="logo text-danger text-capitalize">
            {projectName}
          </span>
        </Link>
        <ul className="list-unstyled accordion-menu">
          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Dashboard"
          >
            <Link to="/admin/dashboard" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="activity"></i>
              </span>
              Dashboard
            </Link>
          </li>

          <li data-bs-toggle="tooltip" data-bs-placement="top" title="Banner">
            <Link to="/admin/banner" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="image"></i>
              </span>
              Banner
            </Link>
          </li>
          {/* <li data-bs-toggle="tooltip" data-bs-placement="top" title="User">
     <Link to="/admin/user" className="nav-link">
       <span className="sidenav__icon">
         <i data-feather="users"></i>
       </span>
       User
     </Link>
   </li> */}
          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="User"
            className="pointer-cursor"
          >
            <a
              href={() => false}
              className="add-collapse-margin"
              style={{ marginLeft: 0 }}
            >
              <span className="sidenav__icon">
                <i data-feather="users"></i>
              </span>
              User
              <i className="fas fa-chevron-right dropdown-icon"></i>
            </a>
            <ul className="">
              <li>
                <Link to="/admin/user" className="nav-link">
                  <i className="far fa-circle"></i>Real
                </Link>
              </li>
              <li>
                <Link to="/admin/fakeUser" className="nav-link">
                  <i className="far fa-circle"></i>Fake
                </Link>
              </li>
            </ul>
          </li>

          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="User"
            className="pointer-cursor"
          >
            <a
              href={() => false}
              className="add-collapse-margin"
              style={{ marginLeft: 0 }}
            >
              <span className="sidenav__icon">
                <i data-feather="user-check"></i>
              </span>
              Host
              <i className="fas fa-chevron-right dropdown-icon"></i>
            </a>
            <ul className="">
              <li>
                <Link to="/admin/host" className="nav-link">
                  <i className="far fa-circle"></i>Host 
                </Link>
              </li>
              <li>
                <Link to="/admin/hostRequest" className="nav-link">
                  <i className="far fa-circle"></i>Host Request
                </Link>
              </li>
            </ul>
          </li>


          

          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Theme"
            className="pointer-cursor"
          >
            {/* <Link to="/admin/agency" className="nav-link"> */}
            <a
              href={() => false}
              className="add-collapse-margin"
              style={{ marginLeft: 0 }}
            >
              <span className="sidenav__icon">
                <i data-feather="image"></i>
              </span>
              Agency
              <i className="fas fa-chevron-right dropdown-icon"></i>
            </a>
            {/* </Link> */}
            <ul className="">


              <li>
                <Link to="/admin/agency" className="nav-link">
                  <i className="far fa-circle"></i>Agency
                </Link>
              </li>
              <li>
                <Link to="/admin/agencyHistory" className="nav-link">
                  <i className="far fa-circle"></i>Agency History
                </Link>
              </li>
              <li>
                <Link to="/admin/agencyRedeemRequest" className="nav-link">
                  <i className="far fa-circle"></i> Agency Redeem
                </Link>
              </li>
            </ul>
          </li>


          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Coin Seller"
          >
            <Link to="/admin/coinSeller" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="dollar-sign"></i>
              </span>
              Coin Seller
            </Link>
          </li>

          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Redeem"
            className="pointer-cursor"
          >
            <Link to="/admin/userRedeemRequest" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="key"></i>
              </span>
              User Redeem
            </Link>
          </li>

          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Plan"
            className="pointer-cursor"
          >
            <Link to="/admin/mainPlan" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="layout"></i>
              </span>
              Plan
            </Link>

          </li>
          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Purchase Plan History"
            className="pointer-cursor"
          >


            <Link to="/admin/planHistory" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="clock"></i>
              </span>
              Plan History
            </Link>


          </li>
          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Game History"
          >
            <Link
              to="/admin/game"
              className="nav-link"
              style={{ display: 'flex' }}
            >
              <span
                className="sidenav__icon"
                style={{ display: 'flex', alignItems: 'center' }}
              >
                <i className="far fa-gamepad" style={{ fontSize: '23px' }}></i>
              </span>
              <span> Game</span>
            </Link>
          </li>
          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Game History"
          >
            <Link to="/admin/gameHistory" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="hash"></i>
              </span>
              Game History
            </Link>
          </li>
          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Gift"
            className="pointer-cursor"
          >
            <a
              href={() => false}
              className="add-collapse-margin"
              style={{ marginLeft: 0 }}
            >
              <span className="sidenav__icon">
                <i data-feather="gift"></i>
              </span>
              Gift
              <i className="fas fa-chevron-right dropdown-icon"></i>
            </a>
            <ul className="">
              <li>
                <Link
                  to="/admin/giftCategory"
                  className="nav-link"
                  onClick={() => sessionStorage.removeItem('GiftClick')}
                >
                  <i className="far fa-circle"></i>Category
                </Link>
              </li>
              <li>
                <Link
                  to="/admin/gift"
                  className="nav-link"
                  onClick={() => sessionStorage.setItem('GiftClick', true)}
                >
                  <i className="far fa-circle"></i>Gift
                </Link>
              </li>
            </ul>
          </li>

          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Game History"
          >
            <Link
              to="/admin/reaction"
              className="nav-link"
              style={{ display: 'flex' }}
            >
              <span
                className="sidenav__icon"
                style={{ display: 'flex', alignItems: 'center' }}
              >
                <i className="far fa-smile-wink" style={{ fontSize: '23px' }} />
              </span>
              Reaction
            </Link>
          </li>

          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Gift"
            className="pointer-cursor"
          >
            <a
              href={() => false}
              className="add-collapse-margin"
              style={{ marginLeft: 0 }}
            >
              <span className="sidenav__icon">
                <i data-feather="loader"></i>
              </span>
              Store
              <i className="fas fa-chevron-right dropdown-icon"></i>
            </a>
            <ul className="">
              <li>
                <Link
                  to="/admin/entryEffect"
                  className="nav-link"
                  onClick={() => sessionStorage.removeItem('GiftClick')}
                >
                  <i className="far fa-circle"></i>Entry Effect
                </Link>
              </li>
              <li>
                <Link
                  to="/admin/avatarFrame"
                  className="nav-link"
                  onClick={() => sessionStorage.setItem('GiftClick', true)}
                >
                  <i className="far fa-circle"></i>Avatar Frame
                </Link>
              </li>
            </ul>
          </li>


          <li data-bs-toggle="tooltip" data-bs-placement="top" title="Theme">
            <Link to="/admin/theme" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="image"></i>
              </span>
              Theme
            </Link>
          </li>
          <li data-bs-toggle="tooltip" data-bs-placement="top" title="Song">
            <Link to="/admin/song" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="music"></i>
              </span>
              Song
            </Link>
          </li>
          <li data-bs-toggle="tooltip" data-bs-placement="top" title="Hashtag">
            <Link to="/admin/hashtag" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="hash"></i>
              </span>
              Hashtag
            </Link>
          </li>
          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Dashboard"
          >
            <Link to="/admin/comment" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="message-circle"></i>
              </span>
              Fake Comment
            </Link>
          </li>
          <li data-bs-toggle="tooltip" data-bs-placement="top" title="Level">
            <Link to="/admin/level" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="bar-chart"></i>
              </span>
              Level
            </Link>
          </li>

          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Post"
            className="pointer-cursor"
          >
            <Link
              to={'/admin/mainPost'}
              className="nav-link"
              style={{ marginLeft: 0 }}
            >
              <span className="sidenav__icon">
                <i data-feather="maximize"></i>
              </span>
              Post
            </Link>

          </li>

          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Video"
            className="pointer-cursor"
          >

            <Link
              to={'/admin/mainVideo'}
              className="nav-link"
              style={{ marginLeft: 0 }}
            >
              <span className="sidenav__icon">
                <i data-feather="film"></i>
              </span>
              Video
            </Link>

          </li>
          <li data-bs-toggle="tooltip" data-bs-placement="top" title="Video">
            <Link to="/admin/reportedUser" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="flag"></i>
              </span>
              Reported User
            </Link>
          </li>
          <li data-bs-toggle="tooltip" data-bs-placement="top" title="Complain">
            <Link to="/admin/complainRequest" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="help-circle"></i>
              </span>
              Complain Request
            </Link>
          </li>

          <li
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            title="Google Ad"
          >
            <Link to="/admin/advertisement" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="book"></i>
              </span>
              Google Ad
            </Link>
          </li>
          <li data-bs-toggle="tooltip" data-bs-placement="top" title="Setting">
            <Link to="/admin/Setting" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="settings"></i>
              </span>
              Setting
            </Link>
          </li>
          <li data-bs-toggle="tooltip" data-bs-placement="top" title="Profile">
            <Link to="/admin/adminProfile" className="nav-link">
              <span className="sidenav__icon">
                <i data-feather="user"></i>
              </span>
              Profile
            </Link>
          </li>
          <li data-bs-toggle="tooltip" data-bs-placement="top" title="Logout">
            <a
              href={() => false}
              onClick={handleLogout}
              className="add-collapse-margin cursor-pointer"
              role="button"
              tabIndex={0}
            >
              <span className="sidenav__icon">
                <i data-feather="log-out"></i>
              </span>
              Logout
            </a>
          </li>
        </ul>
        <a
          href={() => false}
          id="sidebar-collapsed-toggle"
          style={{ opacity: 0, pointerEvents: 'none' }}
        >
          <i data-feather="arrow-right"></i>
        </a>
      </div>
    </>
  );
};
export default Navbar;
