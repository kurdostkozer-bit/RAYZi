import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getAdminCashOut } from "../../store/history/history.action";
import InfiniteScroll from "react-infinite-scroll-component";

const CashOut = (props) => {
  const { historyCashOut, total } = useSelector((state) => state.history);
  const dispatch = useDispatch();
  const agencyId = localStorage.getItem("agencyId");
  const date = props?.selectedDate;

  const [hasMore, setHasMore] = useState(true);
  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [loadingMore, setLoadingMore] = useState(false);

  useEffect(() => {
    dispatch(getAdminCashOut(agencyId, activePage, rowsPerPage, date, "ALL"));
  }, [agencyId, activePage, rowsPerPage, date, "ALL"]);

  const fetchData = () => {
    if (!loadingMore) {
      setLoadingMore(true);
      setTimeout(() => {
        if (historyCashOut.length < total) {
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
      {historyCashOut?.length > 0 ? (
        <>
          <InfiniteScroll
            dataLength={historyCashOut?.length}
            next={fetchData}
            hasMore={hasMore}
            loader={
              total > 10 ? (
                <p className="text-dark text-center">Loading...</p>
              ) : null
            }
            endMessage={<p className="text-center mt-2">No more items</p>}
          >
            <div className="row">
              {historyCashOut?.map((data) => {
                return (
                  <>
                    <div className="col-12 mb-2 mt-1">
                      <div className="agency-invitation bg-white p-2">
                        <div className="d-flex align-items-center justify-content-between">
                          <h5 className="text-dark">
                            ₹{data?.amount ? data?.amount : 0}
                          </h5>
                          {data?.status === 0 && (
                            <div
                              className="p-1 d-flex"
                              style={{
                                borderRadius: "30px",
                                backgroundColor: "#fff9c1",
                              }}
                            >
                              <i
                                class="fa-solid fa-check text-white p-1"
                                style={{
                                  borderRadius: "50%",
                                  backgroundColor: "#e1cc17",
                                  fontSize: "8px",
                                }}
                              ></i>

                              <span
                                className="ms-1"
                                style={{ color: "#645a00", fontSize: "10px" }}
                              >
                                Pending
                              </span>
                            </div>
                          )}
                          {data?.status === 1 && (
                            <div
                              className="p-1 d-flex"
                              style={{
                                borderRadius: "30px",
                                backgroundColor: "#d4ffd4",
                              }}
                            >
                              <i
                                class="fa-solid fa-check text-white p-1"
                                style={{
                                  borderRadius: "50%",
                                  backgroundColor: "green",
                                  fontSize: "8px",
                                }}
                              ></i>

                              <span
                                className="ms-1"
                                style={{ color: "green", fontSize: "10px" }}
                              >
                                Success
                              </span>
                            </div>
                          )}
                          {data?.status === 2 && (
                            <div
                              className="p-1 d-flex"
                              style={{
                                borderRadius: "30px",
                                backgroundColor: "#FFF1F1",
                              }}
                            >
                              <i
                                class="fa-solid fa-check text-white p-1"
                                style={{
                                  borderRadius: "50%",
                                  backgroundColor: "red",
                                  fontSize: "8px",
                                }}
                              ></i>

                              <span
                                className="ms-1"
                                style={{ color: "#470000", fontSize: "10px" }}
                              >
                                Decline
                              </span>
                            </div>
                          )}
                        </div>
                        <div className="d-flex align-items-center justify-content-between mt-3">
                          <span>Payment method</span>
                          <div>
                            <p className="mb-0">
                              {data?.paymentGateway
                                ? data?.paymentGateway
                                : "-"}
                            </p>
                          </div>
                        </div>
                        <div className="mt-2">
                          <span>
                            {data?.acceptDeclineDate
                              ? data?.acceptDeclineDate
                              : "-"}
                          </span>
                        </div>
                      </div>
                    </div>
                  </>
                );
              })}
            </div>
          </InfiniteScroll>
        </>
      ) : (
        <>
          <div className="d-flex justify-content-center align-items-center my-4">
            <span>No data found</span>
          </div>
        </>
      )}
    </>
  );
};

export default CashOut;
