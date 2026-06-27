import React, { useEffect, useState } from "react";

// antd
import { Popconfirm } from "antd";
// import "antd/dist/antd.css";

// base url
import { baseURL } from "../util/Config";
// dayjs
import dayjs from "dayjs";
// routing
import { Link, useNavigate , useLocation } from "react-router-dom";
// action
import {
  getComment,
  getLike,
  allowDisallowComment,
  deleteComment,
  videoDetails,
} from "../store/video/action";
// redux
import { connect, useDispatch, useSelector } from "react-redux";
//image
import Male from "../assets/images/male.png";

const VideoDetail = (props) => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const location = useLocation();

  const { videoDetail } = useSelector((state) => state.video);
  let now = dayjs();

  const [allowComment, setAllowComment] = useState(false);
  const [commentCount, setCommentCount] = useState(0);



  useEffect(() => {
    dispatch(videoDetails(location?.state));
  }, [dispatch, location]);

  useEffect(() => {
    setAllowComment(videoDetail?.allowComment);
    setCommentCount(videoDetail?.comment);
  }, [videoDetail?.allowComment, videoDetail?.comment]);

  useEffect(() => {
    dispatch(getComment(location?.state));
    dispatch(getLike(location?.state));
  }, [dispatch, location]);

  const comment = useSelector((state) => state.video.comment);
  const like = useSelector((state) => state.video.like);

  const handleSwitch = (videoId) => {

    setAllowComment(!allowComment);
    props.allowDisallowComment(videoId);
  };

  function handleDelete(commentId) {

    props.deleteComment(commentId);
    setCommentCount(commentCount - 1);
    // message.success('Click on Yes');
  }

  const handleUserInfo = (user) => {
    sessionStorage.setItem("user", JSON.stringify(user));
    navigate("/admin/user/detail");
  };

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
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
                  <Link to="/admin/video" className="text-danger">
                    Relite
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

      <div className="card card-bg">
        <div className="card-body">
          <div className="row">
            <div className="col-md-12 col-lg-5">
              <div className="post">
                <div
                  className="post-header pointer-cursor"
                  onClick={() => handleUserInfo(videoDetail?.userId)}
                >
                  <img
                    src={
                      videoDetail?.userId?.image
                        ? videoDetail?.userId?.image
                        : Male
                    }
                    alt=""
                  />
                  <div className="post-info">
                    <span className="post-author">
                      {videoDetail?.userId?.username}
                    </span>
                    <br />
                    <span className="post-date">
                      {now.diff(videoDetail?.date, "minute") <= 60 &&
                      now.diff(videoDetail?.date, "minute") >= 0
                        ? now.diff(videoDetail?.date, "minute") + " minutes ago"
                        : now.diff(videoDetail?.date, "hour") >= 24
                        ? dayjs(videoDetail?.date).format("DD MMM, YYYY")
                        : now.diff(videoDetail?.date, "hour") + " hour ago"}
                    </span>
                  </div>
                </div>
                <div className="post-body">
                  <p>{videoDetail?.userId?.caption}</p>
                  <video
                    controls
                    src={videoDetail?.video}
                    className="post-image"
                    alt=""
                  />
                </div>
                <div id="myGroup">
                  <div className="post-actions">
                    <ul className="list-unstyled">
                      <li>
                        <a
                          className="like-btn"
                          data-toggle="collapse"
                          href="#collapseExample"
                          role="button"
                          aria-expanded="false"
                          aria-controls="collapseExample"
                        >
                          {videoDetail?.like} Likes
                        </a>
                      </li>
                      <li>
                        <a
                          className="like-btn"
                          data-toggle="collapse"
                          href="#collapseComment"
                          role="button"
                          aria-expanded="false"
                          aria-controls="collapseComment"
                        >
                          {commentCount} Comments
                        </a>
                      </li>
                    </ul>
                  </div>
                  <div className="post-comments">
                    <div
                      className="collapse"
                      id="collapseExample"
                      data-parent="#myGroup"
                      style={{ maxHeight: 250, overflow: "auto" }}
                    >
                      <div className="row">
                        <div className="col-12">
                          {like?.length > 0 ? (
                            like.map((like) => {
                              return (
                                <div className="post-comm post-padding">
                                  <img
                                    src={like.image ? like.image : Male}
                                    className="comment-img"
                                    alt=""
                                    onClick={() => handleUserInfo(like.user)}
                                  />
                                  <div className="comment-container">
                                    <span className="comment-author pointer-cursor">
                                      <span
                                        onClick={() =>
                                          handleUserInfo(like.user)
                                        }
                                      >
                                        {like.name}
                                      </span>
                                      <small className="comment-date">
                                        {like.time}
                                      </small>
                                    </span>
                                  </div>
                                </div>
                              );
                            })
                          ) : (
                            <p className="text-center">No data found</p>
                          )}
                        </div>
                      </div>
                    </div>
                    <div
                      className="collapse"
                      id="collapseComment"
                      data-parent="#myGroup"
                      style={{ maxHeight: 250, overflow: "auto" }}
                    >
                      <div className="row">
                        <div className="col-12">
                          {comment?.length > 0 ? (
                            comment.map((comment, index) => {
                              return (
                                <div className="post-comm post-padding">
                                  <img
                                    src={comment.image ? comment.image : Male}
                                    className="comment-img commentImg"
                                    alt=""
                                    onClick={() => handleUserInfo(comment.user)}
                                  />
                                  <div className="comment-container">
                                    <span className="comment-author pointer-cursor">
                                      <span
                                        onClick={() =>
                                          handleUserInfo(comment.user)
                                        }
                                      >
                                        {comment.name}
                                      </span>
                                      <small className="comment-date">
                                        {comment.time}
                                        <Popconfirm
                                          title="Are you sure to delete this comment?"
                                          onConfirm={() =>
                                            handleDelete(comment._id)
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
                                      {comment.comment}
                                    </span>
                                  </div>
                                </div>
                              );
                            })
                          ) : (
                            <p className="text-center">No data found</p>
                          )}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div className="col-md-1"></div>
            <div className="col-md-12 col-lg-6" style={{ overflowX: "auto" }}>
              <h5 className="card-title">About</h5>
              <hr />
              <table className="table">
                <tbody>
                  <tr className="border-bottom">
                    <td className="align-top">Caption</td>
                    <td className="align-top">:</td>
                    <td>{videoDetail?.caption}</td>
                  </tr>
                  <tr className="border-bottom">
                    <td>Location</td>
                    <td>:</td>
                    <td>
                      {videoDetail?.location ? videoDetail?.location : "-"}
                    </td>
                  </tr>
                  <tr className="border-bottom">
                    <td>Thumbnail</td>
                    <td>:</td>
                    <td>
                      <a
                        href={ videoDetail?.thumbnail}
                        target="_blank"
                        rel="noreferrer"
                      >
                        {videoDetail?.thumbnail}
                      </a>
                    </td>
                  </tr>
                  <tr className="border-bottom">
                    <td>Screenshot</td>
                    <td>:</td>
                    <td>
                      <a
                        href={videoDetail?.screenshot}
                        target="_blank"
                        rel="noreferrer"
                      >
                        {videoDetail?.screenshot}
                      </a>
                    </td>
                  </tr>
                  <tr className="border-bottom">
                    <td>Song</td>
                    <td>:</td>
                    <td>
                      {videoDetail?.isOriginalAudio ? (
                        "-"
                      ) : (
                        <a
                          href={baseURL + videoDetail?.song}
                          target="_blank"
                          rel="noreferrer"
                        >
                          {videoDetail?.song?.title}
                        </a>
                      )}
                    </td>
                  </tr>
                  <tr className="border-bottom">
                    <td>Original Audio?</td>
                    <td>:</td>
                    <td>{videoDetail?.isOriginalAudio ? "true" : "false"}</td>
                  </tr>
                  <tr className="border-bottom">
                    <td>Duration</td>
                    <td>:</td>
                    <td>{videoDetail?.duration}</td>
                  </tr>
                  <tr className="border-bottom">
                    <td>Video Size</td>
                    <td>:</td>
                    <td>{videoDetail?.size}</td>
                  </tr>
                  <tr className="border-bottom">
                    <td>Hashtag</td>
                    <td>:</td>
                    <td>
                      {videoDetail?.hashtag
                        ? videoDetail?.hashtag?.join(",").replace(/['"]+/g, "")
                        : "-"}{" "}
                    </td>
                  </tr>
                  <tr className="border-bottom">
                    <td>Mention People</td>
                    <td>:</td>
                    <td>
                      {videoDetail?.mentionPeople?.length === 0
                        ? "-"
                        : videoDetail?.mentionPeople?.map((data) => {
                            return data.name;
                          })}
                    </td>
                  </tr>
                  
                  <tr>
                    <td>Allow Comment On Video?</td>
                    <td>:</td>
                    <td>
                      <label className="switch">
                        <input
                          type="checkbox"
                          checked={allowComment}
                          onChange={() => handleSwitch(videoDetail?._id)}
                        />
                        <span className="slider">
                          <p
                            style={{
                              fontSize: 12,
                              marginLeft: `${allowComment ? "-25px" : "35px"}`,
                              color: "#000",
                              marginTop: "6px",
                            }}
                          >
                            {allowComment ? "Yes" : "No"}
                          </p>
                        </span>
                      </label>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default connect(null, {
  getComment,
  getLike,
  allowDisallowComment,
  deleteComment,
})(VideoDetail);
