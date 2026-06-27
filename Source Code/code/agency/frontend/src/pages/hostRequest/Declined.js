import React, { useEffect } from "react";
import { getHostRequest } from "../../store/hostRequest/action";
import { useDispatch, useSelector } from "react-redux";
import { getProfile } from "../../store/admin/action";
import male from "../../assets/images/male.png";

const Declined = () => {
  const { request } = useSelector((state) => state.hostRequest);

  const agencyId = localStorage.getItem("agencyId");
  const dispatch = useDispatch();


  useEffect(() => {
    dispatch(getHostRequest(agencyId, 3));
    dispatch(getProfile(agencyId));
  }, [agencyId]);

  return (
    <>
      {request?.length > 0 ? (
        <>
          {request?.map((data, index) => {
            
            return (
              <div
                className="d-flex justify-content-between align-items-center p-3"
                style={{
                  borderRadius: "12px",
                  background: "#2C1B38",
                  backgroundRepeat: "no-repeat",
                  marginTop: "22px",
                }}
              >
                <div className="bd-content">
                  <div className="d-flex justify-content-between">
                    <div>
                      <img
                        src={data?.user?.image ? data?.user?.image : male}
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
                      <div className="d-flex ">
                        <p
                          className="mb-0 fw-bolder text-white"
                          style={{ fontSize: "15px" }}
                        >
                          {data?.name ? data?.name : "-"}
                        </p>
                        <div className="date-picker">
                          <div className="date-picker d-flex justify-content-end ms-auto">
                            <button
                              className="crtrbtn"
                              style={{
                                background: "#7B11E3",
                                marginLeft: "10px",
                              }}
                            
                            >
                              {"User"}
                            </button>
                          </div>
                        </div>
                      </div>
                      <p
                        className="fw-bolder"
                        style={{ fontSize: "12px", color: "#EFCFE5" }}
                      >
                        ID : {data?.user?.uniqueId}
                      </p>
                    </div>
                  </div>
                </div>
                <div
                  className="p-2 d-flex align-items-center justify-content-center"
                  style={{
                    borderRadius: "30px",
                    backgroundColor: "#ffd4d4",
                    height: "30px",
                  }}
                >
                  <svg
                    width="22"
                    height="22"
                    viewBox="0 0 22 22"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                    style={{
                      borderRadius: "50%",
                      backgroundColor: "red",
                    }}
                  >
                    <path
                      d="M11.0066 2.00306e-06C4.93145 -0.00362479 0.00362891 4.91829 2.00319e-06 10.9934C-0.0036249 17.0685 4.91829 21.9964 10.9934 22C13.9121 22.0048 16.7124 20.8465 18.7749 18.7815C20.8382 16.7199 21.9983 13.9233 22 11.0066C22.0036 4.93145 17.0817 0.00362891 11.0066 2.00306e-06ZM11.0069 20.8996C5.53949 20.9033 1.10433 16.4741 1.10057 11.0067C1.09687 5.53936 5.52606 1.10413 10.9934 1.10043C13.6202 1.09573 16.1406 2.13799 17.9968 3.99658C19.8537 5.85174 20.898 8.36841 20.8997 10.9933C20.9035 16.4606 16.4743 20.8958 11.0069 20.8996ZM11.7714 11L15.2726 7.49883C15.4822 7.28491 15.4822 6.94257 15.2726 6.72865C15.0599 6.51164 14.7116 6.50815 14.4946 6.72079L10.9934 10.222L7.49225 6.72086C7.27833 6.51124 6.93599 6.51124 6.72207 6.72086C6.50506 6.9335 6.50156 7.28182 6.71421 7.49883L10.2154 11L6.71421 14.5012C6.61104 14.6043 6.55315 14.7442 6.55308 14.8901C6.55308 15.194 6.79937 15.4403 7.10323 15.4404C7.24918 15.4406 7.38922 15.3826 7.49225 15.2792L10.9934 11.778L14.4946 15.2792C14.5976 15.3826 14.7377 15.4406 14.8836 15.4404C15.0295 15.4403 15.1693 15.3824 15.2725 15.2793C15.4874 15.0645 15.4874 14.7161 15.2726 14.5012L11.7714 11Z"
                      fill="#fff"
                    />
                  </svg>

                  <span className="ms-1" style={{ color: "#810000" }}>
                    Declined
                  </span>
                </div>
              </div>
            );
          })}
        </>
      ) : (
        <>
          <div className="d-flex justify-content-center align-items-center my-4 text-white">
            <span>No data found</span>
          </div>
        </>
      )}
    </>
  );
};

export default Declined;
