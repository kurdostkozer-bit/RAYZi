import React, { useEffect } from "react";
import rCoin from "../../assets/images/r coin 2.png";
import { useDispatch, useSelector } from "react-redux";
import male from "../../assets/images/male.png";
import leftArrow from "../../assets/images/leftArrow.png";
import { getUser } from "../../store/user/action";
import { useNavigate } from "react-router-dom"; // Updated import

export const CreatorDetails = () => {
  const { user } = useSelector((state) => state.user);
  const dispatch = useDispatch();
  const navigate = useNavigate(); // Use useNavigate instead of useHistory
  const agencyId = localStorage.getItem("agencyId");

  useEffect(() => {
    dispatch(getUser(agencyId));
  }, [dispatch, agencyId]);

  const handleOpenHostHistory = (id) => {
    navigate("/agencypanel/hosthistory", { state: { id } }); // Updated to use navigate
  };

  return (
    <div
      className=""
      style={{ borderRadius: "12px", marginTop: "50px", paddingBottom: "28px" }}
    >
      <div
        className="p-2 creatorDetails"
        style={{
          background: "#241330",
        }}
      >
        {user?.map((data) => (
          <div
            key={data?._id}
            className="p-2 creatorDetails"
            style={{
              background: "#241330",
            }}
          >
            <div
              style={{
                borderRadius: "10px",
                paddingTop: "12px",
                background: "#2C1B38",
                backgroundRepeat: "no-repeat",
                padding: "13px",
              }}
            >
              <div className="bd-content">
                <div className="d-flex">
                  <div>
                    <img
                      src={data?.image ? data?.image : male}
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
                    <div className="d-flex">
                      <p
                        className="mb-0 fw-bolder text-white"
                        style={{ fontSize: "15px" }}
                      >
                        {data?.name ? data?.name : "-"}
                      </p>
                      <button
                        className="text-white px-3 py-1"
                        style={{
                          border: "none",
                          background: "#7B11E3",
                          borderRadius: "4px",
                          marginLeft: "10px",
                        }}
                      >
                        {"Host"}
                      </button>
                    </div>
                    <p
                      className="fw-bolder"
                      style={{ fontSize: "12px", color: "#EFCFE5" }}
                    >
                      ID : {data?.uniqueId ? data?.uniqueId : "-"}
                    </p>
                  </div>
                </div>

                <div
                  className="d-flex justify-content-between align-items-center "
                  style={{ width: "100%" }}
                >
                  <div
                    className="text-white creator"
                    style={{
                      fontWeight: 500,
                      backgroundColor: "#F2205F",
                      borderRadius: "7px",
                      fontSize: "10px",
                      paddingLeft: "20px",
                      paddingRight: "20px",
                      paddingTop: "5px",
                      paddingBottom: "5px",
                    }}
                  >
                    Host Income:
                    <img
                      src={rCoin}
                      alt=""
                      height={14}
                      width={14}
                      style={{
                        marginRight: "5px",
                        marginLeft: "5px",
                      }}
                    />
                    {data?.currentCoin ? data?.currentCoin : 0}
                  </div>
                  <button
                    style={{ border: "none", background: "none" }}
                    onClick={() => handleOpenHostHistory(data?._id)}
                  >
                    <p
                      className="mb-0 history"
                      style={{
                        color: "#E2CAD9",
                        fontSize: "12px",
                      }}
                    >
                      History
                      <img
                        src={leftArrow}
                        alt=""
                        height={22}
                        width={22}
                        style={{ marginLeft: "5px" }}
                      />
                    </p>
                  </button>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
