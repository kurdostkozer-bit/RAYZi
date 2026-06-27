import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import rCoin from "../assets/images/rcoin.png";

const HostSettlement = () => {
  const { hostHistory } = useSelector((state) => state.history);
  const [data, setData] = useState([]);
  useEffect(() => {
    setData(hostHistory);
  }, [hostHistory]);

  console.log("data", hostHistory);
  return (
    <div>
      <>
        {data?.length > 0 ? (
          <>
            {data.map((data) => {
              return (
                <>
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
                            fontWeight: 500,
                            color: "#FF1C60",
                            fontSize: "18px",
                          }}
                        >
                          {data?.startDate} TO {data?.endDate}
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
                        className="d-flex justify-content-between align-items-center px-3 py-2"
                        style={{
                          background: "#241330",
                          borderBottom: "1px solid #372143",
                          paddingLeft: "12px",
                          paddingRight: "12px",
                        }}
                      >
                        <div>
                          <span
                            className="mt-1 text-color"
                            style={{ fontWeight: 400, fontSize: "12px" }}
                          >
                            Total Coin: {data?.amount}
                          </span>
                        </div>

                        <div>
                          <span
                            className="mt-1 text-color"
                            style={{ fontWeight: 400, fontSize: "12px" }}
                          >
                            {data
                              ? data.statusOfTransaction === 1
                                ? "Payment date: Pending"
                                : `Payment date: ${data.payoutDate || "N/A"}`
                              : "Loading..."}
                          </span>
                        </div>
                        <div>
                          <p
                            className=" mt-2"
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

                            {data?.dollar ? data?.dollar : 0}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                </>
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
      </>
    </div>
  );
};

export default HostSettlement;
