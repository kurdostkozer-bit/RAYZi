import React from "react";
import { useSelector } from "react-redux";

const VideoCall = () => {
  const { videoCallHistory } = useSelector((state) => state.videoCallHistory);

  console.log("videoCallHistory", videoCallHistory);

  return (
    <div>
      <div
        className="col-12 p-2"
        style={{
          borderRadius: "10px",
          background: "#241330",
        }}
      >
        {videoCallHistory?.length > 0 ? (
          <>
            <div
              className=""
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
                      fontWeight: 500,
                      color: "#FF1C60",
                      fontSize: "18px",
                    }}
                  >
                    Date
                  </h3>
                </div>

                <h3
                  className=" mt-1"
                  style={{
                    fontWeight: 500,
                    color: "#FF1C60",
                    fontSize: "18px",
                  }}
                >
                  Duration (Sec)
                </h3>
              </div>
              {videoCallHistory &&
                videoCallHistory?.map((data) => {
                  return (
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
                            {data?.date ? data?.date : "-"}
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
                            {data?.callDurationSeconds
                              ? data?.callDurationSeconds
                              : 0}
                          </p>
                        </div>
                      </div>
                    </div>
                  );
                })}
            </div>
          </>
        ) : (
          <div
            className="d-flex justify-content-center align-items-center"
            style={{ background: "#241330" }}
          >
            <span>No data found.</span>
          </div>
        )}
      </div>
    </div>
  );
};

export default VideoCall;
