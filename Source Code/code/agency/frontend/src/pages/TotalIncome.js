import React, { forwardRef, useEffect, useState } from "react";
import Income from "./agencyincome/Income";
import CashOut from "./agencyincome/CashOut";
import moment from "moment";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import dayjs from "dayjs";
import DateRangePicker from "react-bootstrap-daterangepicker";
import { useDispatch, useSelector } from "react-redux";
import { getAdminHistory } from "../store/history/history.action";
import InfiniteScroll from "react-infinite-scroll-component";

const TotalIncome = () => {
  const { history, total } = useSelector((state) => state.history);

  const [startDate, setStartDate] = useState(dayjs().startOf("month").toDate());
  const [endDate, setEndDate] = useState(dayjs().endOf("month").toDate());

  const agencyId = localStorage.getItem("agencyId");

  const dispatch = useDispatch();
  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [loadingMore, setLoadingMore] = useState(false);
  const [hasMore, setHasMore] = useState(true);

  const startAllDate = "1970-01-01";
  const endAllDate = dayjs().format("YYYY-MM-DD");

  useEffect(() => {
    const startDate = dayjs().startOf("month").format("YYYY-MM-DD");
    const endDate = dayjs().endOf("month").format("YYYY-MM-DD");
    dispatch(getAdminHistory(agencyId, startDate, endDate));
  }, [agencyId]);

  const handleApply = (event, picker) => {
    const start = dayjs(picker.startDate).format("YYYY-MM-DD");
    const end = dayjs(picker.endDate).format("YYYY-MM-DD");

    setStartDate(start);
    setEndDate(end);

    dispatch(getAdminHistory(agencyId, start, end));
  };

  const fetchData = () => {
    if (!loadingMore) {
      setLoadingMore(true);
      setTimeout(() => {
        if (history.length < total) {
          setActivePage(activePage + 1);
          setRowsPerPage(rowsPerPage + 10);
          setLoadingMore(false);
        } else {
          setHasMore(false);
        }
      }, 500); // Adjust delay as needed
    }
  };

  return (
    <>
      <div class="page-container">
        <div class="page-content">
          <div class="main-wrapper ps-0">
            <div className="main-section">
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
                  paddingLeft: "22px",
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
                    className="mb-0 text-white text-center"
                    style={{ fontSize: "20px", fontWeight: 500 }}
                  >
                    History
                  </p>
                </div>
              </div>

              <div
                className="d-flex align-items-center justify-content-between py-3 px-2"
                style={{ marginTop: "75px" }}
              >
                <p className="mb-0 text-main">History</p>

                <div className="date-picker">
                  <div className="date-picker">
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

              {history?.length > 0 ? (
                <div className="px-2">
                  {history.map((data, index) => {
                    return (
                      <>
                        <div className="col-12 mb-2 mb-3 main-settlement">
                          <div className="main-settlement-header p-3 d-flex justify-content-between align-items-center">
                            <p className="date-content mb-0">
                              {data?.startDate} to {data?.endDate}
                            </p>
                            <p className="dollar-content mb-0">
                              {data?.finalAmountTotal} ({data?.dollar} $)
                            </p>
                            <div></div>
                            {data?.statusOfTransaction === 1 && (
                              <p className="status-content pt-1 ps-1">
                                Pending
                              </p>
                            )}
                            {data?.statusOfTransaction === 2 && (
                              <p className="status-content-paid pt-1 ps-1">
                                Paid
                              </p>
                            )}
                          </div>
                          <div className="main-settlement-body p-2">
                            <div className="row">
                              <div className="col-6">
                                <p className="text-main-12">Host Coins</p>
                                <p className="text-Light">{data?.coinEarned}</p>
                              </div>
                              <div
                                className="col-6"
                                style={{ borderLeft: "1px solid #2C1B38" }}
                              >
                                <p className="text-main-12">
                                  Agency Commission (
                                  {data?.agencyCommissionPercentage}%)
                                </p>
                                <p className="text-Light">
                                  {data?.commissionCoinEarned}
                                </p>
                              </div>
                            </div>
                            <p
                              className="dollar-content mb-0 pb-2"
                              style={{ borderBottom: "1px solid  #2C1B38" }}
                            >
                              Total coins : {data?.commissionCoinEarned} +{" "}
                              {data?.bonusOrPenltyAmount} bonus coins ={" "}
                              {data?.finalAmountTotal} Coins
                            </p>
                          </div>
                          <p className="mb-2 ms-2">
                            Note : {data?.note ? data?.note : "-"}
                          </p>
                        </div>
                      </>
                    );
                  })}
                </div>
              ) : (
                <>
                  <div className="d-flex justify-content-center align-items-center my-4">
                    <span>No data found</span>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default TotalIncome;
