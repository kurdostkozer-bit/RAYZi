import moment from "moment";
import React from "react";
import { useSelector } from "react-redux";
import { navigate, useNavigate } from "react-router-dom";
import rCoin from "../../assets/images/rcoin.png";


const DailyIncome = () => {
  const { hostHitory } = useSelector((state) => state.history);
  const navigate = useNavigate();

  const handleOpenHistory = (data) => {
    navigate("/host/historyInfo", { state: data?.date });
  };
  return (
    <>
      {hostHitory?.length > 0 ? (
        <>
          {hostHitory.map((data) => {
            return (
              <>
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
                            fontWeight: 500,
                            color: "#FF1C60",
                            fontSize: "18px",
                          }}
                        >
                          Date :
                          {moment(data?.date).format("DD-MM")}
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
                              onClick={() => handleOpenHistory(data)}

                          >
                              <img
                              src={rCoin}
                              alt=""
                              height={22}
                              width={22}
                              style={{ marginRight: "10px" }}
                            />
                           
                            {data?.totalRCoin ? data?.totalRCoin.toFixed(0) : 0}
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
  );
};

export default DailyIncome;
