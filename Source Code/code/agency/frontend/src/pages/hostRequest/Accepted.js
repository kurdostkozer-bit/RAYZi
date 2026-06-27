import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getHostRequest } from "../../store/hostRequest/action";
import { getProfile } from "../../store/admin/action";
import male from "../../assets/images/male.png";

const Accepted = () => {
  const { request } = useSelector((state) => state.hostRequest);
  const admin = useSelector((state) => state.admin.seller);


  const agencyId = localStorage.getItem("agencyId");
  const dispatch = useDispatch();


  useEffect(() => {
    dispatch(getHostRequest(admin?.agencyCode, 2));
    dispatch(getProfile(agencyId));
  }, []);

  return (
    <>
      {request?.length > 0 ? (
        <>
          {request?.map((data, index) => {
            return (
              <div className="agency-invitation mt-2 bg-white p-3" key={index}>
                <div className="d-flex justify-content-between align-items-center">
                  <div className="d-flex">
                    <div>
                      <img
                        src={data?.profileImage ? data?.profileImage : male}
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
                      <p
                        className="mb-0 fw-bold text-dark"
                        style={{ fontSize: "15px" }}
                      >
                        {data?.name}
                      </p>
                      <p>
                        ID : {data?.user?.uniqueId ? data?.user?.uniqueId : "-"}
                      </p>
                    </div>
                  </div>
                  <div>
                    <div
                      className="p-2 d-flex"
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
                        }}
                      ></i>

                      <span className="ms-1" style={{ color: "green" }}>
                        Accepted
                      </span>
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
            <span>No data found</span>
          </div>
        </>
      )}
    </>
  );
};

export default Accepted;
