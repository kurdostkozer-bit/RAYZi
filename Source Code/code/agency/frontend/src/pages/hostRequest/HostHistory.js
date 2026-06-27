import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useLocation } from "react-router-dom";
import {
  getHostCallhistory,
  getHostGifthistory,
  getHostLivehistory,
} from "../store/history/history.action";
import "react-datepicker/dist/react-datepicker.css";
import dayjs from "dayjs";
import DateRangePicker from "react-bootstrap-daterangepicker";

const HostHistory = () => {
  const { hostHitory, total } = useSelector((state) => state?.history);
  const dispatch = useDispatch();
  const location = useLocation();
  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [loadingMore, setLoadingMore] = useState(false);
  const [type, setType] = useState("gift");
  const [startDate, setStartDate] = useState(dayjs().startOf("month").toDate());
  const [endDate, setEndDate] = useState(dayjs().endOf("month").toDate());

  const startAllDate = "1970-01-01";
  const endAllDate = dayjs().format("YYYY-MM-DD");
  const hostId = location?.state?.state;
  const [hasMore, setHasMore] = useState(true);

  useEffect(() => {
    const startDate = dayjs().startOf("month").format("YYYY-MM-DD");
    const endDate = dayjs().endOf("month").format("YYYY-MM-DD");
    if (type === "live") {
      dispatch(getHostLivehistory(hostId, startDate, endDate));
    } else if (type === "call") {
      dispatch(getHostCallhistory(hostId, startDate, endDate));
    } else {
      dispatch(
        getHostGifthistory(
          hostId,
          type,
          activePage,
          rowsPerPage,
          startDate,
          endDate
        )
      );
    }
  }, [hostId, type, activePage, rowsPerPage]);

  const handleApply = (event, picker) => {
    const start = dayjs(picker.startDate).format("YYYY-MM-DD");
    const end = dayjs(picker.endDate).format("YYYY-MM-DD");

    setStartDate(start);
    setEndDate(end);

    if (type === "live") {
      dispatch(getHostLivehistory(hostId, start, end));
    } else if (type === "call") {
      dispatch(getHostCallhistory(hostId, start, end));
    } else {
      dispatch(
        getHostGifthistory(hostId, type, activePage, rowsPerPage, start, end)
      );
    }
  };

  return (
    <>
      <div
        class="page-content"
        style={{
          background: "#241330",
        }}
      >
        <div
          className="row mb-2 "
          style={{
            zIndex: "9",

            paddingTop: "15px",
            paddingBottom: "15px",
            paddingLeft: "22px",
            width: "100%",
            top: "0",
            background: "#231C2C",
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
              onClick={() => window.history.back()}
            >
              <path
                d="M1.18529 11.648L7.60196 18.0647C7.77484 18.2317 8.0064 18.3241 8.24674 18.322C8.48709 18.3199 8.717 18.2235 8.88696 18.0535C9.05692 17.8836 9.15332 17.6537 9.15541 17.4133C9.1575 17.173 9.0651 16.9414 8.89812 16.7685L4.04621 11.9166H20.1667C20.4098 11.9166 20.643 11.82 20.8149 11.6481C20.9868 11.4762 21.0834 11.2431 21.0834 11C21.0834 10.7568 20.9868 10.5237 20.8149 10.3518C20.643 10.1799 20.4098 10.0833 20.1667 10.0833H4.04621L8.89812 5.23137C8.98568 5.14681 9.05551 5.04566 9.10355 4.93382C9.15159 4.82198 9.17688 4.7017 9.17794 4.57999C9.179 4.45827 9.1558 4.33757 9.10971 4.22491C9.06362 4.11226 8.99555 4.00991 8.90949 3.92384C8.82342 3.83777 8.72107 3.7697 8.60842 3.72361C8.49576 3.67752 8.37506 3.65433 8.25334 3.65539C8.13163 3.65645 8.01134 3.68173 7.8995 3.72978C7.78767 3.77782 7.68652 3.84765 7.60196 3.9352L1.18529 10.3519C1.01344 10.5238 0.916904 10.7569 0.916904 11C0.916904 11.243 1.01344 11.4761 1.18529 11.648Z"
                fill="white"
              />
            </svg>
          </div>
          <div className="col-4 text-center">
            <p
              className="mb-0 text-white"
              style={{ fontSize: "20px", fontWeight: 500 }}
            >
              History
            </p>
          </div>
        </div>
        <div class="main-wrapper">
          <div className="main-section p-2">
            <div className="d-flex align-items-center justify-content-between">
              <h4
                className="mt-1 fs-2"
                style={{ fontWeight: 700, color: "#EA1A59" }}
              >
                Host History
              </h4>
              <div>
                <div className="date-picker">
                  <div className="date-picker d-flex justify-content-end ms-auto">
                    <DateRangePicker
                      initialSettings={{
                        startDate: undefined,
                        endDate: undefined,
                        ranges: {
                          All: [new Date("1970-01-01"), dayjs().toDate()],
                          Today: [dayjs().toDate(), dayjs().toDate()],
                          Yesterday: [
                            dayjs().subtract(1, "days").toDate(),
                            dayjs().subtract(1, "days").toDate(),
                          ],

                          "Last 7 Days": [
                            dayjs().subtract(6, "days").toDate(),
                            dayjs().toDate(),
                          ],
                          "Last 30 Days": [
                            dayjs().subtract(29, "days").toDate(),
                            dayjs().toDate(),
                          ],
                          "This Month": [
                            dayjs().startOf("month").toDate(),
                            dayjs().endOf("month").toDate(),
                          ],
                          "Last Month": [
                            dayjs()
                              .subtract(1, "month")
                              .startOf("month")
                              .toDate(),
                            dayjs()
                              .subtract(1, "month")
                              .endOf("month")
                              .toDate(),
                          ],
                        },
                        maxDate: new Date(),
                        singleDatePicker: false,
                        linkedCalendars: false,
                      }}
                      // onCallback={handleCallback}
                      onApply={handleApply}
                    >
                      <input
                        type="text"
                        readOnly
                        placeholder="Select Date Range"
                        // onClick={handleInputClick}
                        className={`daterange float-right  mr-4  text-center`}
                        value={
                          (startDate === startAllDate &&
                            endDate === endAllDate) ||
                          (startDate === "All" && endDate === "All")
                            ? "Select Date Range"
                            : dayjs(startDate).format("MM/DD/YYYY") &&
                              dayjs(endDate).format("MM/DD/YYYY")
                            ? `${dayjs(startDate).format(
                                "MM/DD/YYYY"
                              )} - ${dayjs(endDate).format("MM/DD/YYYY")}`
                            : "Select Date Range"
                        }
                        style={{
                          // width: "85%",
                          fontWeight: 500,
                          cursor: "pointer",
                          background: "#372143",
                          color: "#E1DFE4",
                          display: "flex",
                          width: "100%",
                          justifyContent: "end",
                          fontSize: "13px",
                          padding: "10px",
                          maxWidth: "226px",
                          borderRadius: "64px",
                          border: "1px solid transparent",
                          height: "48px !important",
                        }}
                      />
                    </DateRangePicker>
                  </div>
                </div>
              </div>
            </div>

            <div
              className="d-flex justify-content-center my-3"
              style={{
                border: "1px solid #30253E",
                borderRadius: "8px",
              }}
            >
              <button
                style={{
                  fontSize: "14px",
                }}
                onClick={() => setType("gift")}
                className={`${
                  type === "gift" ? "activeBtn" : "disabledBtn"
                } mx-4 my-1`}
              >
                Gift
              </button>
              <button
                style={{
                  fontSize: "14px",
                }}
                onClick={() => setType("call")}
                className={`${
                  type === "call" ? "activeBtn" : "disabledBtn"
                } mx-4 my-1`}
              >
                Call
              </button>

              <button
                style={{
                  fontSize: "14px",
                }}
                onClick={() => setType("live")}
                className={`${
                  type === "live" ? "activeBtn" : "disabledBtn"
                } mx-4 my-1`}
              >
                Live
              </button>
            </div>

            {type === "gift" && (
              <div className="agency-detail">
                {hostHitory?.length > 0 ? (
                  <div className="row">
                    {hostHitory?.map((data) => {
                      return (
                        <>
                          <div className="col-12 mb-2">
                            <div
                              className="agency-invitation p-2"
                              style={{ backgroundColor: "#372143" }}
                            >
                              <div className="row">
                                <div className="col-4 text-center">
                                  <span
                                    style={{
                                      color: "#fff",
                                      fontSize: "8px",
                                    }}
                                  >
                                    Username
                                  </span>
                                  <div className="">
                                    <span
                                      className="mb-0 ms-1 "
                                      style={{
                                        color: "#fff",
                                        fontSize: "12px",
                                      }}
                                    >
                                      {data?.name}
                                    </span>
                                  </div>
                                </div>
                                <div className="col-4 text-center">
                                  <span
                                    style={{
                                      color: "#fff",
                                      fontSize: "8px",
                                    }}
                                  >
                                    Coin Income
                                  </span>
                                  <div className="d-flex align-items-center justify-content-center">
                                    <img
                                      src={require("../assets/images/rcoin.png")}
                                      style={{ height: "15px", width: "15px" }}
                                      alt=""
                                    />
                                    <span
                                      className="mb-0 ms-1 fw-bolder"
                                      style={{
                                        color: "#fff",
                                        fontSize: "12px",
                                      }}
                                    >
                                      {data?.totalRCoin ? data?.totalRCoin : 0}
                                    </span>
                                  </div>
                                </div>

                                <div className="col-4 text-center">
                                  <span
                                    style={{
                                      color: "#fff",
                                      fontSize: "8px",
                                    }}
                                  >
                                    Date
                                  </span>
                                  <div className="">
                                    <p
                                      className="mb-0 ms-1 "
                                      style={{
                                        color: "#fff",
                                        fontSize: "12px",
                                      }}
                                    >
                                      {data?.date}
                                    </p>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </>
                      );
                    })}
                  </div>
                ) : (
                  <div className="d-flex justify-content-center align-items-center my-4">
                    <span>No data found</span>
                  </div>
                )}
              </div>
            )}

            {type === "call" && (
              <div className="agency-detail">
                {hostHitory?.length > 0 ? (
                  <div className="row">
                    {hostHitory?.map((data) => {
                      return (
                        <>
                          <div className="col-12 mb-3">
                            <div
                              className="agency-invitation p-3"
                              style={{ backgroundColor: "#372143" }}
                            >
                              <div className="row">
                                <div className="d-flex justify-content-between align-items-center">
                                  <div className=" text-center">
                                    <div className="d-flex align-items-center justify-content-center">
                                      <span
                                        className="mb-0 ms-1 fw-bolder"
                                        style={{
                                          color: "#fff",
                                          fontSize: "12px",
                                        }}
                                      >
                                        {data?.userName ? data?.userName : "-"}
                                      </span>
                                    </div>
                                  </div>
                                  <div className=" text-center">
                                    <div className="d-flex align-items-center justify-content-center">
                                      {data?.callType === "MissedCall" && (
                                        <span
                                          style={{
                                            backgroundColor: "red",
                                            color: "#fff",
                                            borderRadius: "8px",
                                            fontSize: "10px",
                                          }}
                                          className="px-2 py-1"
                                        >
                                          Missed Call
                                        </span>
                                      )}
                                      <img
                                        src={require("../assets/images/rcoin.png")}
                                        className="ms-2"
                                        style={{
                                          height: "15px",
                                          width: "15px",
                                        }}
                                        alt=""
                                      />

                                      <span
                                        className="mb-0 ms-1 fw-bolder"
                                        style={{
                                          color: "#fff",
                                          fontSize: "12px",
                                        }}
                                      >
                                        {data?.coin ? data?.coin : 0}
                                      </span>
                                    </div>
                                  </div>
                                </div>
                                <div className="d-flex align-items-center justify-content-between mt-2">
                                  <div className=" text-center">
                                    <div className="">
                                      <p
                                        className="mb-0 ms-1 "
                                        style={{
                                          color: "#fff",
                                          fontSize: "12px",
                                        }}
                                      >
                                        {data?.date?.split(",")[0]}
                                      </p>
                                    </div>
                                  </div>
                                  <div className=" text-center">
                                    <div className="">
                                      <span
                                        className="mb-0 ms-1 "
                                        style={{
                                          color: "#fff",
                                          fontSize: "12px",
                                        }}
                                      >
                                        {data?.callStartTime?.split(",")[1]} To
                                        {data?.callEndTime?.split(",")[1]
                                          ? data?.callEndTime?.split(",")[1]
                                          : "-"}
                                      </span>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </>
                      );
                    })}
                  </div>
                ) : (
                  <div className="d-flex justify-content-center align-items-center my-4">
                    <span>No data found</span>
                  </div>
                )}
              </div>
            )}

            {type === "live" && (
              <div className="agency-detail">
                {hostHitory?.length > 0 ? (
                  <div className="row">
                    {hostHitory?.map((data) => {
                      return (
                        <>
                          <div className="col-12 mb-2">
                            <div
                              className="agency-invitation p-2"
                              style={{ backgroundColor: "#372143" }}
                            >
                              <div className="row">
                                <div className="col-3 text-center">
                                  <span
                                    style={{
                                      color: "#fff",
                                      fontSize: "8px",
                                    }}
                                  >
                                    Duration
                                  </span>
                                  <div className="">
                                    <span
                                      className="mb-0 ms-1 "
                                      style={{
                                        color: "#fff",
                                        fontSize: "12px",
                                      }}
                                    >
                                      {data?.duration}
                                    </span>
                                  </div>
                                </div>
                                <div className="col-3 text-center">
                                  <span
                                    style={{
                                      color: "#fff",
                                      fontSize: "8px",
                                    }}
                                  >
                                    Gift
                                  </span>
                                  <div className="d-flex align-items-center justify-content-center">
                                    <svg
                                      width="12"
                                      height="12"
                                      viewBox="0 0 24 24"
                                      fill="none"
                                      xmlns="http://www.w3.org/2000/svg"
                                    >
                                      <path
                                        d="M12 1C10.9 1 10 1.9 10 3V5H4C2.9 5 2 5.9 2 7V19C2 20.1 2.9 21 4 21H20C21.1 21 22 20.1 22 19V7C22 5.9 21.1 5 20 5H14V3C14 1.9 13.1 1 12 1ZM4 7H20V19H4V7ZM12 3C12.55 3 13 3.45 13 4V6H11V4C11 3.45 11.45 3 12 3ZM14 8H10C9.45 8 9 8.45 9 9V13C9 13.55 9.45 14 10 14H14C14.55 14 15 13.55 15 13V9C15 8.45 14.55 8 14 8ZM10 10H14V12H10V10Z"
                                        fill="red"
                                      />
                                    </svg>

                                    <span
                                      className="mb-0 ms-1 fw-bolder"
                                      style={{
                                        color: "#fff",
                                        fontSize: "12px",
                                      }}
                                    >
                                      {data?.gifts ? data?.gifts : 0}
                                    </span>
                                  </div>
                                </div>

                                <div className="col-3 text-center">
                                  <span
                                    style={{
                                      color: "#fff",
                                      fontSize: "8px",
                                    }}
                                  >
                                    RCoin
                                  </span>
                                  <div className="">
                                    <img
                                      src={require("../assets/images/rcoin.png")}
                                      style={{ height: "15px", width: "15px" }}
                                      alt=""
                                    />
                                    <span
                                      className="mb-0 ms-1 "
                                      style={{
                                        color: "#fff",
                                        fontSize: "12px",
                                      }}
                                    >
                                      {data?.rCoin}
                                    </span>
                                  </div>
                                </div>
                                <div className=" text-center col-3">
                                  <div className="">
                                    <span
                                      className="mb-0 ms-1 "
                                      style={{
                                        color: "#fff",
                                        fontSize: "12px",
                                      }}
                                    >
                                      {data?.startTime?.split(",")[0]}
                                    </span>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </>
                      );
                    })}
                  </div>
                ) : (
                  <div className="d-flex justify-content-center align-items-center my-4">
                    <span>No data found</span>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default HostHistory;
