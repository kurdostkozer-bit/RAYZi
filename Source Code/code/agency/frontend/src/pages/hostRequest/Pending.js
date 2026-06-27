import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  acceptHostRequest,
  getHostRequest,
} from "../../store/hostRequest/action";
import { getProfile } from "../../store/admin/action";
import male from "../../assets/images/male.png";
import ReasonDialogue from "./ReasonDialogue";
import { OPEN_REASON_DIALOGUE } from "../../store/hostRequest/types";
import { permissionError } from "../../util/Alert";

const Pending = (type) => {
  const { request } = useSelector((state) => state.hostRequest);
  const admin = useSelector((state) => state.admin.seller);

  const agencyId = localStorage.getItem("agencyId");
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(getProfile(agencyId));
    dispatch(getHostRequest(agencyId, 1));
  }, [agencyId]);

  const handleAccept = (id) => {
    if (admin?.isActive === false) return permissionError();
    dispatch(acceptHostRequest(id, "accept"));
  };

  const handleDecline = (id, type) => {
    if (admin?.isActive === false) return permissionError();
    dispatch(acceptHostRequest(id, "decline"));
  };
  

  return (
    <>
      {request?.length > 0 ? (
        <>
          {request?.map((data, index) => {
            return (
              <div
                className="d-flex justify-content-between p-3"
                style={{
                  borderRadius: "12px",
                  paddingTop: "12px",
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
                              // onClick={onClick}
                              // ref={ref}
                            >
                              {"User"}
                            </button>
                          </div>
                        </div>
                      </div>
                      <p
                        className="fw-bolder mb-0"
                        style={{ fontSize: "12px", color: "#EFCFE5" }}
                      >
                        ID : {data?.user?.uniqueId}
                      </p>
                    </div>
                  </div>
                </div>
                <div className="align-items-center align-self-center"> Mobile: <span className="fw-bold " style={{color:'rgb(239, 207, 229)'}}> {data?.mobile}</span></div>
                <div className="">
                  <div className="d-flex align-items-center justify-content-betwenn">
                    <button
                      className="py-2 me-3 px-2"
                      style={{
                        backgroundColor: "#CFF3FF",
                        borderRadius: "8px",
                        border: "none",
                      }}
                      onClick={() => handleAccept(data?._id)}
                    >
                      <svg
                        width="24"
                        height="24"
                        viewBox="0 0 24 24"
                        fill="none"
                        xmlns="http://www.w3.org/2000/svg"
                      >
                        <path
                          d="M11.9999 21.4286C17.2071 21.4286 21.4284 17.2072 21.4284 12C21.4284 6.79273 17.2071 2.57141 11.9999 2.57141C6.7926 2.57141 2.57129 6.79273 2.57129 12C2.57129 17.2072 6.7926 21.4286 11.9999 21.4286Z"
                          fill="#00A1F6"
                          stroke="#00A1F6"
                          stroke-width="2.48008"
                        />
                        <path
                          d="M10.17 13.5432L8.22373 11.5969C7.86321 11.2364 7.26813 11.2364 6.90762 11.5969C6.5471 11.9574 6.5471 12.5525 6.90762 12.913L9.51191 15.5173C9.51193 15.5173 9.51195 15.5174 9.51197 15.5174C9.68642 15.692 9.92307 15.7902 10.1699 15.7905L10.1701 15.7905C10.417 15.7902 10.6536 15.692 10.8281 15.5174C10.8281 15.5174 10.8281 15.5173 10.8281 15.5173L16.026 10.3194C16.3866 9.95892 16.3866 9.36384 16.026 9.00332C15.6655 8.6428 15.0704 8.6428 14.7099 9.00332L10.17 13.5432Z"
                          fill="white"
                          stroke="white"
                          stroke-width="0.248008"
                        />
                      </svg>
                    </button>
                    <button
                      className="py-2 px-2"
                      style={{
                        backgroundColor: "#FFF1F1",
                        borderRadius: "8px",
                        border: "none",
                      }}
                      onClick={() => handleDecline(data?._id, "decline")}
                    >
                      <svg
                        width="24"
                        height="24"
                        viewBox="0 0 24 24"
                        fill="none"
                        xmlns="http://www.w3.org/2000/svg"
                      >
                        <path
                          d="M12.0003 21.4286C17.2076 21.4286 21.4289 17.2072 21.4289 12C21.4289 6.79273 17.2076 2.57141 12.0003 2.57141C6.79309 2.57141 2.57178 6.79273 2.57178 12C2.57178 17.2072 6.79309 21.4286 12.0003 21.4286Z"
                          fill="#F90008"
                          stroke="#F90008"
                          stroke-width="2.48008"
                        />
                        <path
                          d="M15.1436 8.85696L8.85794 15.1426M8.85791 8.85693L15.1436 15.1426"
                          stroke="white"
                          stroke-width="2.14286"
                          stroke-linecap="round"
                        />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </>
      ) : (
        <>
          <div className="d-flex justify-content-center align-items-center my-4">
            <span>No data found</span>
          </div>
        </>
      )}
      <ReasonDialogue />
    </>
  );
};

export default Pending;
