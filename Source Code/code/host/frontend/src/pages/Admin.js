import React, { forwardRef, useEffect, useState } from "react";

// js
import "../assets/js/main.min.js";

//router
import { NavLink as Link, useHistory, useRouteMatch } from "react-router-dom";

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
import {
  getHostEarning,
  getHosthistory,
} from "../store/history/history.action.js";
import DailyIncome from "./history/DailyIncome.js";
import RoomData from "./history/RoomData.js";
import VideoCall from "./history/VideoCall.js";
import rCoin from "../assets/images/rcoin.png";
import leftArrow from "../assets/images/leftArrow.png";
// import { getLiveRoomHistory } from "../store/liveRoom/action.js";
// import { getChatRoomHistory } from "../store/chatRoom/action.js";
import { getVideoCallHistory } from "../store/videoCall/action.js";
import { permissionError } from "../util/Alert.js";
import { Toast } from "../util/Toast.js";
import HostSettlement from "./HostSettlement.js";
import gredientImage from "../assets/images/gredientrectengle.png";
import Diamond from "../assets/images/Diamond.svg";
import Copy from "../assets/images/copy.svg"

const Admin = () => {
  // const location = useRouteMatch();
  const dispatch = useDispatch();
  const admin = useSelector((state) => state.admin.seller);
  const hostHistory = useSelector((state) => state.history.hostHistory);
  const hostId = localStorage.getItem("hostId");

  const currentMonth = moment().format("YYYY-MM");

  const [selectedDate, setSelectedDate] = useState(currentMonth);
  const [type, setType] = useState(1);

  useEffect(() => {
    dispatch(getProfile(hostId));
    dispatch(getHosthistory(currentMonth, hostId));
    dispatch(getHostEarning(hostId, currentMonth));
    dispatch(getVideoCallHistory(hostId, currentMonth));
    // dispatch(getChatRoomHistory(hostId, currentMonth));
    // dispatch(getLiveRoomHistory(hostId, currentMonth));
  }, [dispatch, currentMonth, hostId]);

  const handleDateChange = (date) => {
    const selectedDateObject = moment(date).format("YYYY-MM");
    setSelectedDate(selectedDateObject);

    console.log("selectedDate", selectedDate);

    dispatch(getHosthistory(selectedDateObject, hostId));
    dispatch(getHostEarning(hostId, selectedDateObject));
    dispatch(getVideoCallHistory(hostId, selectedDateObject));
    // dispatch(getChatRoomHistory(hostId, selectedDateObject));
    // dispatch(getLiveRoomHistory(hostId, selectedDateObject));
  };

  const handleBack = () => {
    window.showAndroidToast();
  };

  const ExampleCustomInput = forwardRef(({ value, onClick }, ref) => (
    <button
      className="btn-gray rounded-pill px-2 py-1"
      style={{ border: "none" }}
      onClick={onClick}
      ref={ref}
    >
      {value}
    </button>
  ));

  const handleCopy = async (link) => {
    if (admin?.isActive === false) return permissionError();
    try {
      await navigator.clipboard.writeText(link); // Use await to ensure clipboard action is completed.
      Toast("success", `Copy Success.`);
    } catch (error) {
      console.error("Copy failed:", error);
      Toast("error", "Copy Failed");
    }
  };

  return (
    <>
      <div class="page-container">
        <div class="page-content">
          <div class="main-wrapper p-0">
            <div className="main-section">
              <div
                className="row p-3 mb-2"
                style={{
                  zIndex: "9",
                  position: "fixed",
                  width: "100%",
                  top: "0",
                  background: "rgb(35, 28, 44)",
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
                <div
                  className="col-5 text-center"
                  style={{
                    background: "rgb(35, 28, 44)",
                  }}
                >
                  <p
                    className=" fw-bold mb-0"
                    style={{ fontSize: "15px", color: "white" }}
                  >
                    Host Center
                  </p>
                </div>
              </div>
              <div className="px-3 " style={{ marginTop: "65px" }}>
                <div
                  className="d-flex justify-content-between align-items-center mt-3"
                  style={{
                    borderRadius: "14px",
                    padding: "12px",
                    backgroundImage: `url(${gredientImage})`,
                    backgroundRepeat: "no-repeat",
                  }}
                >
                  <div className="bd-content">
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
                          UniqueId : {admin?.uniqueId}
                        </p>
                      </div>
                    </div>
                  </div>
                  <div className="date-picker">
                    <div className="date-picker d-flex justify-content-end ms-auto">
                      <DatePicker
                        selected={selectedDate}
                        dateFormat="yyyy/MM"
                        showMonthYearPicker
                        onChange={(date) => handleDateChange(date)}
                        customInput={<ExampleCustomInput />}
                        style={{ fontWeight: "bold" }}
                      />
                    </div>
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
                    <div className="d-flex justify-content-between align-items-center">
                      <h3
                        className=" mt-1"
                        style={{
                          fontWeight: 700,
                          color: "#FF1C60",
                          fontSize: "18px",
                        }}
                      >
                        Diamond
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
                          src={Diamond}
                          alt=""
                          height={22}
                          width={22}
                          style={{ marginRight: "10px" }}
                        />
                        {admin?.diamond ? admin?.diamond.toFixed(0) : 0}
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
                          This Week Income
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
                <div className=" p-2 mt-3" style={{ borderRadius: "10px" }}>
                  <div
                    className="d-flex align-items-center justify-content-between"
                    style={{
                      borderRadius: "14px",
                      padding: "12px",
                      backgroundImage: `url(${gredientImage})`,
                      backgroundRepeat: "no-repeat",
                    }}
                  >
                    <h6 className="fw-bold mb-0 text-white">My Agency</h6>
                    <div>
                      <div className="d-flex align-items-center justify-content-end">
                        <img
                          src={
                            !admin?.hostAgency?.image?.includes("\\")
                              ? admin?.hostAgency?.image
                              : male
                          }
                          style={{
                            height: "28px",
                            width: "28px",
                            objectFit: "cover",
                            borderRadius: "50%",
                          }}
                          alt=""
                        />
                        <h6 className="fw-bold ms-2 mb-0 text-white">
                          {admin?.hostAgency?.name
                            ? admin?.hostAgency?.name
                            : "-"}
                        </h6>
                      </div>
                      <div className="d-flex align-items-center justify-content-end mt-2">
                        <h6 className="fw-bold mb-0 text-white">
                          ID :{" "}
                          {admin?.hostAgency?.uniqueId
                            ? admin?.hostAgency?.uniqueId
                            : "-"}
                        </h6>
                        <button
                          className="ms-1 pe-0"
                          style={{
                            border: "none",
                            backgroundColor: "transparent",
                          }}
                          onClick={() =>
                            handleCopy(admin?.hostAgency?.uniqueId)
                          }
                        >

<img
                          src={Copy}
                          alt=""
                          height={22}
                          width={22}
                          style={{ marginRight: "10px" }}
                        />
                         
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="d-flex justify-content-around mt-4 mb-3">
                  <div className="ms-3">
                    <h4
                      className={`${
                        type === 2 ? "text-white fw-bold" : "text-gray"
                      } mb-0`}
                      style={{ fontSize: "12px", cursor: "pointer" }}
                      onClick={() => setType(2)}
                    >
                      Payment History
                      <div className="d-flex justify-content-center align-items-center">
                        {type === 2 && (
                          <hr className="text-white d-flex justify-content-center" />
                        )}
                      </div>
                    </h4>
                  </div>
                  <div className="ms-3">
                    <h4
                      className={`${
                        type === 1 ? "text-white fw-bold" : "text-gray"
                      } mb-0`}
                      style={{ fontSize: "12px", cursor: "pointer" }}
                      onClick={() => setType(1)}
                    >
                      Daily Income
                      <div className="d-flex justify-content-center align-items-center">
                        {type === 1 && (
                          <hr className="text-white d-flex justify-content-center" />
                        )}
                      </div>
                    </h4>
                  </div>

                  <div className="ms-3">
                    <h4
                      className={`${
                        type === 3 ? "text-white fw-bold" : "text-gray"
                      } mb-0`}
                      style={{ fontSize: "12px", cursor: "pointer" }}
                      onClick={() => setType(3)}
                    >
                      Video Call
                      <div className="d-flex justify-content-center align-items-center">
                        {type === 3 && (
                          <hr className="text-white d-flex justify-content-center" />
                        )}
                      </div>
                    </h4>
                  </div>
                </div>

                {type === 1 && (
                  <>
                    <DailyIncome />
                  </>
                )}
                {type === 2 && (
                  <>
                    <HostSettlement />
                  </>
                )}

                {type === 3 && (
                  <>
                    <VideoCall />
                  </>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Admin;
