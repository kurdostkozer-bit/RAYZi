import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useLocation } from "react-router-dom";
import { getHosthistory } from "../store/history/history.action";
import InfiniteScroll from "react-infinite-scroll-component";

const HostHistory = () => {
  const { hostHitory, total } = useSelector((state) => state.history);

  const dispatch = useDispatch();
  const location = useLocation();
  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [loadingMore, setLoadingMore] = useState(false);
  const [hasMore, setHasMore] = useState(true);

  const hostId = location?.state?.state;

  useEffect(() => {
    dispatch(getHosthistory(hostId, activePage, rowsPerPage));
  }, [hostId, activePage, rowsPerPage]);

  const fetchData = () => {
    if (!loadingMore) {
      setLoadingMore(true);
      setTimeout(() => {
        if (hostHitory?.length < total) {
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
      <div class="page-content">
        <div class="main-wrapper">
          <div className="main-section p-2">
            <div className="row mb-2">
              <div className="col-4 d-flex align-items-center">
                <i
                  class="fa-solid fa-arrow-left text-dark"
                  style={{ fontSize: "16px" }}
                  onClick={() => window.history.back()}
                ></i>
              </div>
              <div className="col-4 text-center">
                <p
                  className="text-dark fw-bold mb-0"
                  style={{ fontSize: "16px", fontWeight: "500" }}
                >
                  History
                </p>
              </div>
            </div>
            <div className="d-flex align-items-center justify-content-between">
              <h4
                className="text-dark fw-bold pt-2 mt-2"
                style={{ fontSize: "15px" }}
              >
                Host History
              </h4>
              <div></div>
            </div>

            <div className="agency-detail">
              {hostHitory?.length > 0 ? (
                <InfiniteScroll
                  dataLength={hostHitory.length}
                  next={fetchData}
                  hasMore={hasMore}
                  loader={
                    total > 20 && (
                      <p className="text-dark text-center">Loading...</p>
                    )
                  }
                  endMessage={
                    total > 20 && (
                      <p className="text-center mt-2">No more items</p>
                    )
                  }
                >
                  <div className="row">
                    {hostHitory?.map((data) => {
                      return (
                        <>
                          <div className="col-12 mb-2">
                            <div className="agency-invitation bg-white p-2">
                              <div className="row">
                                <div className="col-4 text-center">
                                  <span
                                    style={{
                                      color: "#a7a7a7",
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
                                        color: "#ff8300",
                                        fontSize: "12px",
                                      }}
                                    >
                                      {data?.coin ? data?.coin : 0}
                                    </span>
                                  </div>
                                </div>
                                <div className="col-4 text-center">
                                  <span
                                    style={{
                                      color: "#a7a7a7",
                                      fontSize: "8px",
                                    }}
                                  >
                                    Time
                                  </span>
                                  <div className="">
                                    <span
                                      className="mb-0 ms-1 "
                                      style={{
                                        color: "#000",
                                        fontSize: "12px",
                                      }}
                                    >
                                      {data?.date.split(",")[1]}
                                    </span>
                                  </div>
                                </div>
                                <div className="col-4 text-center">
                                  <span
                                    style={{
                                      color: "#a7a7a7",
                                      fontSize: "8px",
                                    }}
                                  >
                                    Date
                                  </span>
                                  <div className="">
                                    <p
                                      className="mb-0 ms-1 "
                                      style={{
                                        color: "#000",
                                        fontSize: "12px",
                                      }}
                                    >
                                      {data?.date?.split(",")[0]}
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
                </InfiniteScroll>
              ) : (
                <div className="d-flex justify-content-center align-items-center my-4">
                  <span>No data found</span>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default HostHistory;
