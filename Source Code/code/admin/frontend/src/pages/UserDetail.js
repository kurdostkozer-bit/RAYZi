import React, { useEffect, useState } from "react";

// antd
import { Popconfirm } from "antd";

//react-router
import { connect, useDispatch, useSelector } from "react-redux";

import male from "../assets/images/male.png";

import $ from "jquery";
import { Link, useLocation, useNavigate } from "react-router-dom";

//action
import {
  getPost,
  getComment,
  getLike,
  deleteComment,
} from "../store/post/action";
import { getFollowersFollowing } from "../store/follower/action";
import { getVideo } from "../store/video/action";
import { editCoin } from "../store/user/action";

//serverPath
import { baseURL } from "../util/Config";



// dayjs
import dayjs from "dayjs";

//image
import Male from "../assets/images/male.png";

//inline edit
import EdiText from "react-editext";

//toast
import { Toast } from "../util/Toast";

const UserDetail = (props) => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const location = useLocation();
  const [userInfoData, setUserInfoData] = useState({});
  const user = JSON.parse(sessionStorage.getItem("user"));

  const userFollow = JSON.parse(sessionStorage.getItem("userFollow"));
  const post = useSelector((state) => state.post.post);
  const video = useSelector((state) => state.video.video);
  const followersFollowingList = useSelector(
    (state) => state.followersFollowing.followersFollowing
  );
  const comment = useSelector((state) => state.post.comment);
  const like = useSelector((state) => state.post.like);



  const [showFeed, setShowFeed] = useState(true);
  const [followersFollowing, setFollowersFollowing] = useState(false);
  const [postVideo, setPostVideo] = useState([]);
  const [coin, setCoin] = useState(0);
  const [isCoin, setIsCoin] = useState(false);
  const [diamond, setDiamond] = useState(0);
  const [isDiamond, setIsDiamond] = useState(false);
  const [post_, setPost] = useState("post");


  const id = location?.state?.id ? location?.state?.id : user?._id 

  useEffect(() => {
    userFollow
      ? userFollow?.fromUserId?._id
        ? setUserInfoData(userFollow?.fromUserId)
        : setUserInfoData(userFollow?.toUserId)
      : setUserInfoData(user);
  }, []);

  $(document).ready(() => {
    $("#manageVideoFeed").on("click", "a", function () {
      // remove className 'active' from all li who already has className 'active'
      $("#manageVideoFeed a.active").removeClass("active");
      // adding className 'active' to current click li
      $(this).addClass("active");
      if ($(this).attr("at") === "Videos") {
        setShowFeed(false);
        setFollowersFollowing(false);
      } else {
        if ($(this).attr("at") === "Feed") {
          setShowFeed(true);
          setFollowersFollowing(false);
        } else setFollowersFollowing(true);
      }
    });
  });
  // set default Image

  $(document).ready(function () {
    $("img").bind("error", function () {
      // Set the default image
      $(this).attr("src", male);
    });
  });

  document.addEventListener(
    "play",
    function (e) {
      var videos = document.getElementsByTagName("video");
      for (var i = 0, len = videos.length; i < len; i++) {
        if (videos[i] != e.target) {
          videos[i].pause();
        }
      }
    },
    true
  );

  useEffect(() => {
    $("#manageVideoFeed li a").first().addClass("active");
  }, []);

  useEffect(() => {
    dispatch(
      getPost(
        userFollow
          ? userFollow?.fromUserId?._id
            ? userFollow?.fromUserId?._id
            : userFollow?.toUserId?._id
          : id
      )
    );
  }, [dispatch, user?._id]);

  useEffect(() => {
    setPostVideo(post);
  }, [post]);
  useEffect(() => {
    setPostVideo(video);
  }, [video]);

  let now = dayjs();

  const handleLike = (id, index, type) => {
    $(`#commentWrap${index}`).slideUp();
    $(`#likeWrap${index}`).slideToggle("slow");
    dispatch(getLike(id, type));
  };
  const handleComment = (id, index, type) => {
    $(`#likeWrap${index}`).slideUp();
    $(`#commentWrap${index}`).slideToggle("slow");
    dispatch(getComment(id, type));
  };

  const handleFollowersFollowing = (type, id) => {
    dispatch(getFollowersFollowing(type, id));
  };

  const handleVideo = (id) => {
    setPost("video");
    dispatch(getVideo(id));
  };
  const handlePost = (id) => {
    setPost("post");
    dispatch(getPost(id));
  };

  function handleDelete(commentId, index) {

    const commentCount = $(`#comment${index}`).text();
    parseInt(commentCount) > 0 &&
      $(`#comment${index}`).text(parseInt(commentCount) - 1);
    props.deleteComment(commentId);
  }

  const handleUserInfo = (user, type) => {
    if (type === "Like") {
      sessionStorage.removeItem("userFollow");
      sessionStorage.setItem("user", JSON.stringify(user));
    } else {
      sessionStorage.setItem("userFollow", JSON.stringify(user));
      sessionStorage.removeItem("user");
    }

    window.location.href = "/admin/user/detail";
  };

  const handleSave = (coin, id, type) => {

    const validNumber = /^\d+$/.test(coin);
    if (!validNumber) {
      return Toast("error", "Invalid Value");
    }
    let data;
    if (type === "rCoin") {
      const newObj = {
        ...user,
        rCoin: coin,
      };

      sessionStorage.setItem("user", JSON.stringify(newObj));
      setIsCoin(true);
      setCoin(coin);
      data = {
        userId: id,
        rCoin: coin,
      };
    } else {
      const newObj = {
        ...user,
        diamond: coin,
      };
      sessionStorage.setItem("user", JSON.stringify(newObj));
      setIsDiamond(true);
      setDiamond(coin);
      data = {
        userId: id,
        diamond: coin,
      };
    }
    props.editCoin(data);
  };

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last mb-1">
            <button
              className="btn btn-danger custom-btn"
              onClick={() => navigate(-1)}
            >
              <i className="fas fa-chevron-left"></i> Go Back
            </button>
          </div>
          <div className="col-12 col-md-6 order-md-2 order-first">
            <nav
              aria-label="breadcrumb"
              className="breadcrumb-header float-start float-lg-end"
            >
              <ol className="breadcrumb">
                <li className="breadcrumb-item">
                  <Link to="/admin/dashboard" className="text-danger">
                    Dashboard
                  </Link>
                </li>
                <li className="breadcrumb-item">
                  <Link to="/admin/user" className="text-danger">
                    User
                  </Link>
                </li>
                <li className="breadcrumb-item active" aria-current="page">
                  Detail
                </li>
              </ol>
            </nav>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col-xl-12">
          <div className="profile-cover"></div>
          <div className="profile-header ">
            <div className="profile-img">
              {/* <img src={user.image ? user.image : Male} alt="" /> */}
              <img src={userInfoData?.image} alt="" />
            </div>
            <div className="profile-name">
              {/* <h3>{user?.name}</h3> */}
              <h3>{userInfoData?.name}</h3>
            </div>
            <div className="profile-header-menu">
              <ul className="list-unstyled" id="manageVideoFeed">
                <li>
                  <a
                    href={() => false}
                    className="pointer-cursor"
                    at="Feed"
                    onClick={() =>
                      handlePost(
                        userFollow
                          ? userFollow?.fromUserId?._id
                            ? userFollow?.fromUserId?._id
                            : userFollow?.toUserId?._id
                          : user._id
                      )
                    }
                  >
                    Feed ({userInfoData?.post})
                  </a>
                </li>

                <li>
                  <a
                    href={() => false}
                    className="pointer-cursor"
                    at="Videos"
                    onClick={() =>
                      handleVideo(
                        userFollow
                          ? userFollow?.fromUserId?._id
                            ? userFollow?.fromUserId?._id
                            : userFollow?.toUserId?._id
                          : user._id
                      )
                    }
                  >
                    Videos ({userInfoData?.video})
                  </a>
                </li>
                <li>
                  <a
                    href={() => false}
                    className="pointer-cursor"
                    onClick={() =>
                      handleFollowersFollowing(
                        "follower",
                        userFollow
                          ? userFollow?.fromUserId?._id
                            ? userFollow?.fromUserId?._id
                            : userFollow?.toUserId?._id
                          : user._id
                      )
                    }
                  >
                    Followers ({userInfoData?.followers})
                  </a>
                </li>
                <li>
                  <a
                    href={() => false}
                    className="pointer-cursor"
                    onClick={() =>
                      handleFollowersFollowing(
                        "following",
                        userFollow
                          ? userFollow?.fromUserId?._id
                            ? userFollow?.fromUserId?._id
                            : userFollow?.toUserId
                          : user._id
                      )
                    }
                  >
                    Following ({userInfoData?.following})
                  </a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col-md-12 col-lg-4">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title">About</h5>
              <span className="text-danger">
                {userInfoData?.bio ? "Bio : " : ""}
              </span>
              <span>{userInfoData?.bio}</span>
              {userInfoData?.bio && <br />}
              {userInfoData?.bio && <br />}
              <ul className="list-unstyled profile-about-list">
                <li>
                  <i className="fal fa-user m-r-xxs"></i>
                  <span>
                    Username &nbsp;
                    <span className="text-danger">
                      {userInfoData?.username}
                    </span>
                  </span>
                </li>
                <li>
                  <i
                    className="fas fa-id-card-alt "
                    style={{ marginRight: "5px" }}
                  ></i>
                  <span>
                    Unique Id &nbsp;
                    <span className="text-danger">
                      {userInfoData?.uniqueId}
                    </span>
                  </span>
                </li>
                <li>
                  <span className="d-flex" style={{ alignItems: "baseline" }}>
                    <i className="far fa-gem m-r-xxs"></i>
                    Diamond &nbsp;
                    <EdiText
                      type="text"
                      value={isDiamond ? diamond : userInfoData?.diamond}
                      onSave={(val) =>
                        handleSave(
                          val,
                          userFollow
                            ? userFollow?.fromUserId?._id
                            : userFollow?.toUserId?._id
                            ? user
                            : user?._id,
                          "diamond"
                        )
                      }
                      className="editClass"
                    />
                  </span>
                </li>
                <li>
                  <span className="d-flex" style={{ alignItems: "baseline" }}>
                    <i className="far fa-registered m-r-xxs "></i>
                    RCoin &nbsp;{" "}
                    <EdiText
                      type="text"
                      value={isCoin ? coin : userInfoData?.rCoin}
                      onSave={(val) =>
                        handleSave(
                          val,
                          userFollow
                            ? userFollow?.fromUserId
                            : userFollow?.toUserId
                            ? user
                            : user?._id,
                          "rCoin"
                        )
                      }
                      className="editClass"
                    />
                  </span>
                </li>
                <li>
                  <i className="fas fa-venus-mars m-r-xxs"></i>
                  <span>
                    Gender &nbsp;{" "}
                    <span className="text-danger">{userInfoData?.gender}</span>
                  </span>
                </li>
                <li>
                  <i className="fas fa-child m-r-xxs"></i>
                  <span>
                    Age &nbsp;
                    <span className="text-danger">{userInfoData?.age}</span>
                  </span>
                </li>

                <li>
                  <i className="fas fa-sign-in-alt m-r-xxs"></i>
                  <span>
                    Login Type &nbsp;
                    <span className="text-danger">
                      {userInfoData?.loginType === 0
                        ? "Google"
                        : userInfoData?.loginType === 1
                        ? "Facebook"
                        : userInfoData?.loginType === 2
                        ? "Quick"
                        : "IOS"}
                    </span>
                  </span>
                </li>
                <li>
                  <i className="fas fa-crown m-r-xxs"></i>
                  <span>
                    isVIP &nbsp;
                    <span
                      className={`${
                        userInfoData?.isVIP ? "text-success" : "text-primary"
                      }`}
                    >
                      {userInfoData?.isVIP ? "Yes" : "No"}
                    </span>
                  </span>
                </li>
                <li>
                  <i className="fas fa-map-marker m-r-xxs"></i>
                  <span>
                    Ip &nbsp;
                    <span className="text-danger">{userInfoData?.ip}</span>
                  </span>
                </li>
                <li>
                  <i className="fas fa-clock m-r-xxs"></i>
                  <span>
                    Last login date &nbsp;
                    <span className="text-danger">
                      {userInfoData?.lastLogin}
                    </span>
                  </span>
                </li>
              </ul>
            </div>
          </div>
          <div className="card">
            <div className="card-body">
              <h5 className="card-title">Contact Info</h5>
              <ul className="list-unstyled profile-about-list">
                <li>
                  <i className="far fa-envelope m-r-xxs"></i>
                  <span>{userInfoData?.email}</span>
                </li>
                <li>
                  <i className="far fa-compass m-r-xxs"></i>
                  <span>
                    Lives in <span>{userInfoData?.country}</span>
                  </span>
                </li>
              </ul>
            </div>
          </div>
        </div>
        <div className="col-md-12 col-lg-8">
          <div className="card card-bg">
            <div className="card-body ">
              {followersFollowing ? (
                <div
                  className="container followDiv"
                  style={{ maxHeight: 548, overflow: "auto" }}
                >
                  <div className="row post-comments d-flex flex-wrap justify-content-between">
                    {followersFollowingList?.length > 0 ? (
                      followersFollowingList.map((data, index) => {
                        return (
                          <>
                            <div
                              className="col-md-6"
                              style={{
                                paddingRight: "10px",
                                borderRight: `${
                                  followersFollowingList?.length > 1 &&
                                  index % 2 === 0
                                    ? "1px solid #7d7d83"
                                    : "transparent"
                                }`,
                              }}
                            >
                              <div className="post-comm post-padding">
                                <img
                                  src={
                                    data?.toUserId?.image
                                      ? data?.toUserId?.image
                                      : data?.fromUserId?.image
                                      ? data?.fromUserId?.image
                                      : Male
                                  }
                                  className="comment-img commentImg"
                                  alt=""
                                />
                                <div
                                  className="comment-container pointer-cursor"
                                  onClick={() => handleUserInfo(data)}
                                >
                                  <span className="comment-author">
                                    {data?.toUserId?.name
                                      ? data?.toUserId?.name
                                      : data?.fromUserId?.name}
                                    <small className="comment-date">
                                      {now.diff(data?.createdAt, "minute") <=
                                        60 &&
                                      now.diff(data?.createdAt, "minute") >= 0
                                        ? now.diff(data?.createdAt, "minute") +
                                          " minutes ago"
                                        : now.diff(data?.createdAt, "hour") >=
                                          24
                                        ? dayjs(data?.createdAt).format(
                                            "DD MMM, YYYY"
                                          )
                                        : now.diff(data?.createdAt, "hour") +
                                          " hour ago"}
                                    </small>
                                  </span>
                                  <span>
                                    {data?.toUserId?.username
                                      ? data?.toUserId?.username
                                      : data?.fromUserId?.username}
                                  </span>
                                </div>
                              </div>
                            </div>
                          </>
                        );
                      })
                    ) : (
                      <p className="text-center">Nothing to Show!!</p>
                    )}
                  </div>
                </div>
              ) : postVideo?.length > 0 ? (
                postVideo
                  .slice(0)
                  .reverse()
                  .map((data, index) => {
                    return (
                      <>
                        <div className="post">
                          <div className="post-header">
                            <img src={userInfoData?.image} alt="" />
                            <div className="post-info">
                              <span className="post-author">
                                {userInfoData?.name}
                              </span>
                              <br />
                              <span className="post-date">
                                {now.diff(data?.date, "minute") <= 60 &&
                                now.diff(data?.date, "minute") >= 0
                                  ? now.diff(data?.date, "minute") +
                                    " minutes ago"
                                  : now.diff(data?.date, "hour") >= 24
                                  ? dayjs(data?.date).format("DD MMM, YYYY")
                                  : now.diff(data?.date, "hour") + " hour ago"}
                              </span>
                            </div>
                          </div>
                          {data.isDelete ? (
                            <p className="text-center">
                              This Post is delete by Admin
                            </p>
                          ) : (
                            <>
                              <div className="post-body">
                                {post_ == "post" ? (
                                  <img
                                    src={baseURL + data?.post}
                                    className="post-image m-auto py-3"
                                  />
                                ) : (
                                  <video
                                    src={data?.video}
                                    className="post-image m-auto py-3"
                                    controls
                                    style={{ maxHeight: 400, width: "40%" }}
                                  />
                                )}
                              </div>
                              <div id="myGroup">
                                <div className="post-actions">
                                  <ul className="list-unstyled">
                                    <li>
                                      <a
                                        href={() => false}
                                        className="like-btn"
                                        onClick={() =>
                                          handleLike(
                                            data?._id,
                                            index,
                                            data?.post ? "post" : "video"
                                          )
                                        }
                                        id="likeToggle"
                                      >
                                        {data?.like} Likes
                                      </a>
                                    </li>
                                    <li>
                                      <a
                                        href={() => false}
                                        className="like-btn"
                                        role="button"
                                        onClick={() =>
                                          handleComment(
                                            data?._id,
                                            index,
                                            data?.post ? "post" : "video"
                                          )
                                        }
                                        id="commentToggle"
                                      >
                                        <span id={`comment${index}`}>
                                          {data?.comment}
                                        </span>{" "}
                                        &nbsp; Comments
                                      </a>
                                    </li>
                                  </ul>
                                </div>
                                <div className="post-comments">
                                  <div
                                    className="collapse"
                                    id={`likeWrap${index}`}
                                    style={{
                                      maxHeight: 250,
                                      overflow: "auto",
                                      display: "none",
                                    }}
                                  >
                                    <div className="row">
                                      <div className="col-12">
                                        {like?.length > 0 ? (
                                          like?.map((like) => {
                                            return (
                                              <>
                                                <div className="post-comm post-padding">
                                                  <img
                                                    src={
                                                      like?.image
                                                        ? like?.image
                                                        : Male
                                                    }
                                                    className="comment-img"
                                                    alt=""
                                                    onClick={() =>
                                                      handleUserInfo(
                                                        like.user,
                                                        "Like"
                                                      )
                                                    }
                                                  />
                                                  <div className="comment-container pointer-cursor">
                                                    <span className="comment-author">
                                                      <span
                                                        onClick={() =>
                                                          handleUserInfo(
                                                            like?.user,
                                                            "Like"
                                                          )
                                                        }
                                                      >
                                                        {like?.name}
                                                      </span>
                                                      <small className="comment-date">
                                                        {like?.time}
                                                      </small>
                                                    </span>
                                                  </div>
                                                </div>
                                              </>
                                            );
                                          })
                                        ) : (
                                          <p className="text-center">
                                            No data found
                                          </p>
                                        )}
                                      </div>
                                    </div>
                                  </div>
                                  <div
                                    className="collapse"
                                    id={`commentWrap${index}`}
                                    style={{
                                      maxHeight: 250,
                                      overflow: "auto",
                                    }}
                                  >
                                    <div className="row">
                                      <div className="col-12">
                                        {comment?.length > 0 ? (
                                          comment.map((comment) => {
                                            return (
                                              <>
                                                <div className="post-comm post-padding">
                                                  <img
                                                    src={
                                                      comment?.image
                                                        ? comment?.image
                                                        : Male
                                                    }
                                                    className="comment-img commentImg"
                                                    alt=""
                                                    onClick={() =>
                                                      handleUserInfo(
                                                        comment?.user,
                                                        "Like"
                                                      )
                                                    }
                                                  />
                                                  <div className="comment-container">
                                                    <span className="comment-author pointer-cursor">
                                                      <span
                                                        onClick={() =>
                                                          handleUserInfo(
                                                            comment?.user,
                                                            "Like"
                                                          )
                                                        }
                                                      >
                                                        {comment?.name}
                                                      </span>
                                                      <small className="comment-date">
                                                        {comment?.time}
                                                        <Popconfirm
                                                          title="Are you sure to delete this comment?"
                                                          onConfirm={() =>
                                                            handleDelete(
                                                              comment?._id,
                                                              index
                                                            )
                                                          }
                                                          okText="Yes"
                                                          cancelText="No"
                                                          placement="top"
                                                        >
                                                          <i className="fas fa-trash-alt text-danger comment-delete pointer-cursor"></i>
                                                        </Popconfirm>
                                                      </small>
                                                    </span>
                                                    <span className="pointer-cursor">
                                                      {comment?.comment}
                                                    </span>
                                                  </div>
                                                </div>
                                              </>
                                            );
                                          })
                                        ) : (
                                          <p className="text-center">
                                            No data found
                                          </p>
                                        )}
                                      </div>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </>
                          )}
                        </div>
                      </>
                    );
                  })
              ) : (
                <p className="text-center">Nothing to show!!</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default connect(null, {
  getPost,
  getLike,
  getComment,
  getFollowersFollowing,
  getVideo,
  deleteComment,
  editCoin,
})(UserDetail);
