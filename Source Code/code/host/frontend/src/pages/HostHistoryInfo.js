import React from "react";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getHosthistoryInfo } from "../store/history/history.action";
import { useHistory, useLocation } from "react-router-dom";
import rCoin from "../assets/images/rcoin.png";

const HostHistoryInfo = () => {
  const { infoHistory } = useSelector((state) => state.history);
  const dispatch = useDispatch();
  const hostId = localStorage.getItem("hostId");

  console.log("infoHistory", infoHistory);

  const location = useLocation();

  const date = location.state;

  // console.log("location", date);

  useEffect(() => {
    dispatch(getHosthistoryInfo(date, hostId));
  }, [date, hostId]);

  return (
    <>
      <div class="page-container">
        <div class="page-content">
          <div class="main-wrapper p-0">
            <div className="main-section">
              <div
                className="row p-3 mb-2"
                style={{ position: "fixed", width: "100%", top: "0" }}
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
                <div className="col-5 text-center ">
                  <p
                    className="text-white fw-bold mb-0"
                    style={{ fontSize: "18px" }}
                  >
                    History
                  </p>
                </div>
              </div>

              <div className="row p-2" style={{ marginTop: "65px" }}>
                {infoHistory?.length > 0 ? (
                  <>
                    {infoHistory?.map((data) => {
                      return (
                        <div
                          className="mt-4"
                          style={{
                            border: "1px solid #372143",
                            borderRadius: "14px",
                          }}
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
                                Gift Income
                              </h3>
                            </div>
                          </div>
                          <div
                            style={{
                              background: "#241330",
                              borderRadius: "0px 0px 14px 14px",
                            }}
                          >
                            <div
                              className="d-flex justify-content-between align-items-center px-3 py-1"
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
                                  Income
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

                                  {data?.rCoin}
                                </p>
                              </div>
                            </div>
                          </div>
                        </div>
                      );
                    })}
                  </>
                ) : (
                  <>
                    <div className="d-flex justify-content-center align-items-center my-4">
                      <span>No data found.</span>
                    </div>
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

export default HostHistoryInfo;
