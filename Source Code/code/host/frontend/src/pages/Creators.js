import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getUser } from "../store/user/action";
import male from "../assets/images/male.png";
import { Link, useHistory } from "react-router-dom/cjs/react-router-dom";
import { CreatorDetails } from "../component/dialog/CreatorDetails";

const Creators = () => {
  const dispatch = useDispatch();
  const history = useHistory();

  const { user } = useSelector((state) => state.user);
  const agencyId = localStorage.getItem("agencyId");

  useEffect(() => {
    dispatch(getUser(agencyId));
  }, [dispatch, agencyId]);

  const handleOpenHostHistory = (id) => {
    history.push("/agencypanel/hosthistory", { state: id });
  };

  return (
    <>
      <div
        class="page-container"
        style={{
          background: "#241330",
        }}
      >
        <div class="page-content">
          <div class="main-wrapper ps-0">
            <div className="main-section">
              <div className="row mb-2">
                <div
                  className="row p-3 mb-2 "
                  style={{
                    zIndex: "9",
                    position: "fixed",
                    width: "100%",
                    top: "0",
                    background: "#231C2C",
                    borderBottomLeftRadius: "20px",
                    borderBottomRightRadius: "20px",
                  }}
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
                  <div className="col-4 text-center">
                    <p
                      className="fw-bold mb-0 text-white"
                      style={{ fontSize: "18px" }}
                    >
                      Host
                    </p>
                  </div>
                </div>
              </div>

              <CreatorDetails />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Creators;
