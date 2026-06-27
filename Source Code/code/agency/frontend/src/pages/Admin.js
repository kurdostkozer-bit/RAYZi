import React, { forwardRef, useEffect, useState } from "react";

// js
import "../assets/js/main.min.js";

//router
import { NavLink as Link, useNavigate, useRouteMatch } from "react-router-dom";

// css
import "../assets/css/main.min.css";
import "../assets/css/custom.css";

import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

// component

import { useDispatch, useSelector } from "react-redux";
import { getProfile } from "../store/admin/action.js";
import male from "../assets/images/male.png";
import moment from "moment";
import { disableHost, getUser } from "../store/user/action.js";
import {
  getAgencyCommission,
  getAgencyTypeCommission,
} from "../store/agencyCommission/action.js";
import { getAgencyEarning } from "../store/history/history.action.js";
import CoinSeller from "../component/dialog/CoinSeller.js";
import { OPEN_COIN_SELLER_DIALOGUE } from "../store/seller/seller.type.js";
import { OPEN_NEW_REDEEM_DIALOG } from "../store/myRedeem/type.js";
import AgencyRedeemCreate from "../component/dialog/AgencyRedeemCreate.js";
import { permissionError } from "../util/Alert.js";
import rCoin from "../assets/images/r coin 2.png";
import leftArrow from "../assets/images/leftArrow.png";
import gredientImage from "../assets/images/gredientrectengle.png";
import grendientearning from "../assets/images/grendientearning.png";
import upArrow from "../assets/images/upArrow.png";
import rounded from "../assets/images/rounded-shape.png";
import { getSetting } from "../store/redeem/action.js";

const Admin = () => {
  // const location = useRouteMatch();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const admin = useSelector((state) => state.admin.seller);
  const { user } = useSelector((state) => state.user);
  const { agencyType } = useSelector((state) => state.agencyCommission);
  const { setting } = useSelector((state) => state.redeem);
  const agencyId = localStorage.getItem("agencyId");

  console.log(admin?.image)

  useEffect(() => {
    dispatch(getProfile(agencyId));
    dispatch(getAgencyTypeCommission());
    dispatch(getUser(agencyId));
    dispatch(getSetting())
  }, [dispatch, agencyId]);

  const handleOpenCreator = () => {
    if (admin?.isActive === false) return permissionError();
    dispatch({ type: OPEN_COIN_SELLER_DIALOGUE });
  };

  const handleBack = () => {
    window.showAndroidToast();
  };

  const handleOpenAgencyIncome = (id) => {
    navigate("/agencypanel/Income", { state: id });
  };

  const handleOpenAgencyRedeemDetails = (admin) => {
   navigate("/agencypanel/agencyredeem", { state: admin?._id });
  };

  const handleOpenCreatorDetails = (admin) => {
    navigate("/agencypanel/creatorRequest", { state: admin?._id });
   };
 

  const handleOpenRedeem = () => {
    if (admin?.isActive === false) return permissionError();
    dispatch({ type: OPEN_NEW_REDEEM_DIALOG });
  };

  const handleOpenHostHistory = (id) => {
    navigate("/agencypanel/hosthistory", { state: id });
  };

  const handleClick = (id) => {
    dispatch(disableHost(id));
  };


  return (
    <>
      <div
        class="page-container"
        style={{
          background: "#0F1219",
        }}
      >
        <div class="page-content">
          <div class="main-wrapper ps-0">
            <div
              className="row mb-2"  
              style={{
                zIndex: "9",
                position: "fixed",
                width: "100%",
                top: "0",
                background: "#231C2C",
                paddingTop: "15px",
                paddingBottom: "15px",
                paddingLeft: "11px",
                paddingRight: "11px",
                borderBottomLeftRadius: "20px",
                borderBottomRightRadius: "20px",
              }}
            >
              <div className="col-4 d-flex align-items-center">
                <svg
                  width="22"
                  height="22"
                  viewBox="0 0 22 22"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                  onClick={() => handleBack()}
                >
                  <path
                    d="M1.18529 11.648L7.60196 18.0647C7.77484 18.2317 8.0064 18.3241 8.24674 18.322C8.48709 18.3199 8.717 18.2235 8.88696 18.0535C9.05692 17.8836 9.15332 17.6537 9.15541 17.4133C9.1575 17.173 9.0651 16.9414 8.89812 16.7685L4.04621 11.9166H20.1667C20.4098 11.9166 20.643 11.82 20.8149 11.6481C20.9868 11.4762 21.0834 11.2431 21.0834 11C21.0834 10.7568 20.9868 10.5237 20.8149 10.3518C20.643 10.1799 20.4098 10.0833 20.1667 10.0833H4.04621L8.89812 5.23137C8.98568 5.14681 9.05551 5.04566 9.10355 4.93382C9.15159 4.82198 9.17688 4.7017 9.17794 4.57999C9.179 4.45827 9.1558 4.33757 9.10971 4.22491C9.06362 4.11226 8.99555 4.00991 8.90949 3.92384C8.82342 3.83777 8.72107 3.7697 8.60842 3.72361C8.49576 3.67752 8.37506 3.65433 8.25334 3.65539C8.13163 3.65645 8.01134 3.68173 7.8995 3.72978C7.78767 3.77782 7.68652 3.84765 7.60196 3.9352L1.18529 10.3519C1.01344 10.5238 0.916904 10.7569 0.916904 11C0.916904 11.243 1.01344 11.4761 1.18529 11.648Z"
                    fill="white"
                  />
                </svg>
              </div>
              <div className="col-4 text-center">
                <p
                  className="mb-0 text-white text-center"
                  style={{ fontSize: "20px", fontWeight: 500 }}
                >
                  Agency
                </p>
              </div>
            </div>
            <div
              className="main-section px-2 mt-3"
              style={{ paddingTop: "14px" }}
            >
              <div className="p-0"></div>
              <div
                className="d-flex justify-content-between  align-items-center"
                style={{
                  marginTop: "65px",
                  borderRadius: "14px",
                  padding: "12px",
                  backgroundImage: `url(${gredientImage})`,
                  backgroundRepeat: "no-repeat",
                }}
              >
                <div className="bd-content ">
                  <div className="d-flex align-items-center">
                    <div>
                      <img
                        src={admin?.image ? admin?.image : male}
                        style={{
                          height: "40px",
                          width: "40px",
                          objectFit: "cover",
                          borderRadius: "50%",
                        }}
                        alt=""
                      />
                    </div>
                    <div className="ms-3">
                      <p
                        className="mb-0 fw-bolder text-white"
                        style={{ fontSize: "18px", fontWeight: 700 }}
                      >
                        {admin?.name ? admin?.name : "-"}
                      </p>
                      <p
                        className="mb-0 fw-bolder"
                        style={{ fontSize: "13px", color: "#EFCFE5" }}
                      >
                        Code : {admin?.agencyCode}
                      </p>
                    </div>
                  </div>

                  
                </div>
                <div className="date-picker">
                  <div className="date-picker"></div>
                </div>
              </div>

              <div
                className="mt-4"
                style={{ border: "1px solid #372143", borderRadius: "14px" }}
              >
                <div
                  className="d-flex justify-content-between align-items-center px-3 py-2"
                  style={{
                    background: "#372143",
                    borderBottom: "1px solid #372143",
                    paddingLeft: "12px",
                    paddingRight: "12px",
                    borderRadius: "14px 14px 0px 0px",
                  }}
                >
                  <div>
                    <h3
                      className=" mt-1"
                      style={{
                        fontWeight: 700,
                        color: "#FF1C60",
                        fontSize: "18px",
                      }}
                    >
                      My Income
                    </h3>
                  </div>

                  <div className="d-flex align-items-center">
                    <p
                      className="mb-0"
                      style={{
                        color: "#E2CAD9",
                        fontSize: "12px",
                        cursor: "pointer",
                      }}
                      onClick={() => handleOpenAgencyRedeemDetails(admin)}
                    >
                      Redeem Details
                      <img
                        src={leftArrow}
                        alt=""
                        height={20}
                        width={20}
                        style={{ marginLeft: "5px" , color : "black" }}
                      />
                    </p>
                  </div>
                </div>
                <div
                  style={{
                    background: "#241330",
                    borderRadius: "0px 0px 14px 14px",
                  }}
                >
                  <div
                    className="d-flex justify-content-between align-items-center px-3 py-2"
                    style={{
                      background: "#241330",
                      borderBottom: "1px solid #372143",
                      paddingLeft: "12px",
                      paddingRight: "12px",
                    }}
                  >
                    <div>
                      <h3
                        className="mt-1 text-color"
                        style={{ fontWeight: 400, fontSize: "16px" }}
                      >
                        Wallet Rcoin
                      </h3>
                    </div>

                    <div>
                      <p
                        className=" mt-1"
                        style={{
                          fontWeight: 600,
                          color: "#FF1C60",
                          fontSize: "16px",
                        }}
                      >
                        <img
                          src={rCoin}
                          alt=""
                          height={22}
                          width={22}
                          style={{ marginRight: "10px" }}
                        />
                        {admin?.rCoin ? admin?.rCoin.toFixed(0) : 0}
                      </p>
                    </div>
                  </div>
                </div>
              </div>

              <div className="mt-4 main-host">
                <div className="d-flex justify-content-center">
                  <button
                    className="bg-black px-4 py-2"
                    style={{
                      fontWeight: 700,
                      fontSize: "19px",
                      outline: "none",
                      border: "none",
                      borderBottomLeftRadius: "20px",
                      borderBottomRightRadius: "20px",
                      color: "#FF1C60",
                    }}
                  >
                    Add New Host
                  </button>
                </div>
                <h2
                  className="d-flex justify-content-center mt-4"
                  style={{
                    color: "#D1FF03",
                    fontWeight: 800,
                    fontSize: "34px",
                    display: "flex",
                    alignItems: "center",
                  }}
                >
                  <span
                    style={{
                      fontSize: "14px",
                      marginTop: "10px",
                      marginRight: "5px",
                    }}
                  >
                  </span>
                  {setting?.agencyCommission}
                  %
                </h2>

                <p
                  className="d-flex justify-content-center"
                  style={{
                    color: "#D1FF03",
                    fontWeight: 500,
                    fontSize: "14px",
                    marginTop: "4px",
                  }}
                >
                  Commission From Host
                </p>

                <div className="d-flex justify-content-center">
                  <button
                    className="text-white d-flex justify-content-center align-items-center"
                    style={{
                      fontWeight: 600,
                      backgroundColor: "#F2205F",
                      borderRadius: "34px",
                      border: "2px solid #FFFFFF",
                      paddingLeft: "56px",
                      paddingRight: "56px",
                      marginBottom: "10px",
                      fontSize: "18px",
                      height: "40px",
                    }}
                    onClick={handleOpenCreator}
                  >
                    Add Host
                  </button>
                </div>
              </div>

              <div
                className="mt-4"
                style={{ border: "1px solid #372143", borderRadius: "10px" }}
              >
                <div
                  className="d-flex justify-content-between align-items-center py-2"
                  style={{
                    background: "#372143",
                    borderBottom: "1px solid #372143",
                    paddingLeft: "10px",
                    paddingRight: "10px",
                    borderRadius: "10px 10px 0px 0px",
                  }}
                >
                  <div>
                    <h3
                      className=" mt-1"
                      style={{
                        fontWeight: 700,
                        color: "#FF1C60",
                        fontSize: "18px",
                      }}
                    >
                      Host
                    </h3>
                  </div>

                  <div>
                    <p
                      className="mb-0"
                      style={{
                        color: "#E2CAD9",
                        fontSize: "12px",
                        fontWeight: 400,
                        cursor: "pointer",
                      }}
                      onClick={() => handleOpenCreatorDetails(admin?._id)}
                    >
                      Host Request
                      <img
                        src={leftArrow}
                        alt=""
                        height={22}
                        width={22}
                        style={{ marginLeft: "5px" }}
                      />
                    </p>
                  </div>
                </div>

                {/* <div
                  className="px-3 py-2"
                  style={{
                    background: "#241330",
                    borderBottom: "1px solid #372143",
                    paddingLeft: "10px",
                    paddingRight: "10px",
                    borderRadius: "10px 10px 0px 0px",
                  }}
                >
                  <div
                    className="d-flex justify-content-between align-items-center mt-2"
                    style={{
                      background: "#D1FF03",
                      borderBottom: "1px solid #372143",
                      borderRadius: "8px",
                      padding: "11px",
                    }}
                  >
                    <div>
                      <h3
                        className="text-size text-dark mb-0 mt-0"
                        style={{
                          fontWeight: 500,
                          marginTop: "14px",
                          fontSize: "14px",
                        }}
                      >
                        Fix Commission Rate:
                      </h3>
                    </div>

                    <div>
                      <p
                        className="text-dark mb-0"
                        style={{ fontWeight: 700, fontSize: "15px" }}
                      >
                        {setting?.agencyCommission ? setting?.agencyCommission : 0}%
                      </p>
                    </div>
                  </div>
                </div> */}


              </div>

              <div
                className="mt-3"
                style={{
                  borderRadius: "12px",
                  paddingBottom: "28px",
                }}
              >
                <div
                  className="d-flex justify-content-between align-items-center px-3 py-2"
                  style={{
                    background: "#372143",
                    borderBottom: "1px solid #372143",
                    paddingLeft: "10px",
                    paddingRight: "10px",
                    borderRadius: "14px 14px 0px 0px",
                  }}
                >
                  <div>
                    <h3
                      className="mt-1"
                      style={{
                        fontWeight: 700,
                        color: "#FF1C60",
                        fontSize: "18px",
                      }}
                    >
                      Host Details
                    </h3>
                  </div>

                  <div
                    className="cursor-pointer"
                    onClick={() => navigate("/agencypanel/creators")}
                  >
                    <p
                      className="mb-0"
                      style={{ color: "#E2CAD9", fontSize: "12px" }}
                    >
                      {"Show More"}
                      <img
                        src={leftArrow}
                        alt=""
                        height={22}
                        width={22}
                        style={{ marginLeft: "5px" }}
                      />
                    </p>
                  </div>
                </div>
                {user?.map((data) => {
                  return (
                    <>
                      <div
                        className="p-2 creatorDetails"
                        style={{
                          background: "#241330",
                        }}
                      >
                        <div
                          style={{
                            borderRadius: "10px",
                            paddingTop: "12px",
                            background: "#2C1B38",
                            backgroundRepeat: "no-repeat",
                            padding: "13px",
                          }}
                        >
                          <div className="bd-content ">
                            <div className="d-flex justify-content-between">
                              <div>
                                <img
                                  src={data?.image ? data?.image : male}
                                  style={{
                                    height: "40px",
                                    width: "40px",
                                    objectFit: "cover",
                                    borderRadius: "50%",
                                  }}
                                  alt=""
                                />
                              </div>
                              <div className="ms-3">
                                <div className="d-flex">
                                  <p
                                    className="mb-0 fw-bolder text-white me-2"
                                    style={{ fontSize: "15px" }}
                                  >
                                    {data?.name ? data?.name : "-"}
                                  </p>
                                  <span
                                    className="text-white text-center m-auto"
                                    style={{
                                      border: "none",
                                      background: "#7B11E3",
                                      borderRadius: "4px",
                                      marginLeft: "10px",
                                      height:"20px",
                                      width:"50px",
                                    }}
                                  >
                                    {"Host"}
                                  </span>
                                </div>
                                <p
                                  className="fw-bolder"
                                  style={{ fontSize: "12px", color: "#EFCFE5" }}
                                >
                                  ID : {data?.uniqueId ? data?.uniqueId : "-"}
                                </p>
                              </div>

                              <label class="switch s-icons s-outline s-outline-secondary mr-2 mb-0 margin-left">
                                <input
                                  type="checkbox"
                                  checked={data?.isBlock}
                                  onChange={() => handleClick(data)}
                                />
                                <span class="slider round"></span>
                              </label>
                            </div>



                            <div
                              className="d-flex justify-content-between align-items-center "
                              style={{ width: "100%" }}
                            >
                              <div
                                className="text-white creator"
                                style={{
                                  fontWeight: 500,
                                  backgroundColor: "#F2205F",
                                  borderRadius: "7px",
                                  fontSize: "10px",
                                  paddingLeft: "20px",
                                  paddingRight: "20px",
                                  paddingTop: "5px",
                                  paddingBottom: "5px",
                                }}
                              >
                                Host Income:
                                <img
                                  src={rCoin}
                                  alt=""
                                  height={14}
                                  width={14}
                                  style={{
                                    marginRight: "5px",
                                    marginLeft: "5px",
                                  }}
                                />
                                {data?.currentCoin ? data?.currentCoin : 0}
                              </div>
                              <button
                                style={{ border: "none", background: "none" }}
                                onClick={() => handleOpenHostHistory(data?._id)}
                              >
                                <p
                                  className="mb-0 history"
                                  style={{
                                    color: "#E2CAD9",
                                    fontSize: "12px",
                                  }}
                                >
                                  History
                                  <img
                                    src={leftArrow}
                                    alt=""
                                    height={22}
                                    width={22}
                                    style={{ marginLeft: "5px" }}
                                  />
                                </p>
                              </button>
                            </div>
                          </div>
                        </div>
                      </div>
                    </>
                  );
                })}
              </div>
            </div>
          </div>
        </div>
      </div>
      <CoinSeller />
      <AgencyRedeemCreate />
    </>
  );
};

export default Admin;
