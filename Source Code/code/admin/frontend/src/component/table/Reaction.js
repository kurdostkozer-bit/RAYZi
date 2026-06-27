/* eslint-disable jsx-a11y/anchor-is-valid */
import React, { useEffect, useState } from "react";

//redux
import { connect, useDispatch, useSelector } from "react-redux";

//action
import { getReaction, deleteReaction } from "../../store/reaction/action";

//config
//routing
import { Link, useNavigate  } from "react-router-dom";

import {  warning } from "../../util/Alert";

//image
import noImage from "../../assets/images/noImage.png";

import { OPEN_REACTION_DIALOG } from "../../store/reaction/type";
import ReactionDialog from "../dialog/ReactionDialog";
import male from "../../assets/images/avatar4.jpeg"

const ReactionTable = (props) => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [data, setData] = useState([]);

useEffect(() => {
    dispatch(getReaction());
  }, [dispatch]);

  const { reaction } = useSelector((state) => state.reaction);



  useEffect(() => {
    setData(reaction);
  }, [reaction]);

  const handleDelete = (reactionId) => {


    const data = warning();
    data
      .then((isDeleted) => {
        if (isDeleted) {
          props.deleteReaction(reactionId);
        }
      })
      .catch((err) => console.log(err));
  };

  const handleEdit = (data) => {
    dispatch({ type: OPEN_REACTION_DIALOG, payload: data });
  };

  const handleOpen = () => {


    dispatch({ type: OPEN_REACTION_DIALOG });
  };

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-white">Reactions</h3>
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

                <li className="breadcrumb-item active" aria-current="page">
                  Reaction
                </li>
              </ol>
            </nav>
          </div>
        </div>
      </div>
      <div className="main-wrapper">
        <div className="row">
          <div className="col-xs-12 col-sm-12 col-md-6 col-lg-8 float-left">
            <div className="mb-5">
              <button
                className="btn btn-danger btn-sm float-left "
                type="button"
                onClick={handleOpen}
              >
                New
              </button>
            </div>
          </div>
        </div>
        <div className="row">
          {data?.length > 0 ? (
            data?.map((data, index) => {
              return (
                <>
                  <div
                    className="col-12 col-sm-6 col-md-4 col-lg-3 col-xl-3 col-xxl-3"
                    key={index}
                  >
                    <div className="card contact-card card-bg">
                      <div className="card-body p-1">
                        <div className="row px-3 py-2">
                          <div className="col-4 ps-4">
                            <img
                              src={!data?.image.includes('\\') || data.image === "" ? data?.image : noImage}
                              style={{
                                width: "135px",
                                height: "135px",
                                objectFit: "cover",
                              }}
                              alt=""
                              className="rounded-circle my-auto"
                              height={80}
                            />
                          </div>
                          <div
                            className="col-8 text-end"
                            style={{
                              padding: 0,
                              paddingLeft: 5,
                            }}
                          >
                            <div className="px-3">
                              {/* <i
                                className="fas fa-edit text-white p-2 bg-primary rounded-circle"
                                style={{ marginRight: 10, fontSize: 25 }}
                                onClick={() => handleEdit(data)}
                              ></i> */}

                              <i
                                className="fas fa-trash text-white p-2 mt-2 bg-danger rounded-circle mt-5"
                                style={{ marginRight: 10, fontSize: 25,cursor:"pointer" }}
                                onClick={() => handleDelete(data._id)}
                              ></i>
                            </div>
                          </div>
                          <div className="fs-2 text-capitalize">{data?.name}</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </>
              );
            })
          ) : (
            <tr>
              <td colSpan="6" align="center">
                Nothing to show!!
              </td>
            </tr>
          )}
        </div>
      </div>
      <ReactionDialog />
    </>
  );
};

export default connect(null, { getReaction, deleteReaction })(ReactionTable);
