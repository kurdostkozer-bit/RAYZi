import React, { useState } from "react";
import AcceptedRequest from "./AcceptedRequest";
import DeclineRequest from "./DeclineRequest";
import PendingRequest from "./PendingRequest";
import { Link } from "react-router-dom";

const HostRequest = () => {
  const [type, setType] = useState(() => {
    return localStorage.getItem('hostRequestTab') || "Pending"
  });

  const handleTabChange = (newType) => {
    setType(newType);
    localStorage.setItem('hostRequestTab', newType);
  };

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-white">Host Request</h3>
          </div>
          <div className="col-12 col-md-6 order-md-2 order-first">
            <nav
              aria-label="breadcrumb"
              className="breadcrumb-header float-start float-lg-end"
            >
              <ol className="breadcrumb">
                <li className="breadcrumb-item">
                  <Link to="/admin/dashboard" className="text-danger">
                    Dashboard
                  </Link>
                </li>
                <li className="breadcrumb-item active" aria-current="page">
                  Redeem
                </li>
                <li className="breadcrumb-item active" aria-current="page">
                  Pending Redeem
                </li>
              </ol>
            </nav>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col-12">
          <div className="my-2 mb-4">
            <button
              type="button"
              className={`btn btn-sm ${
                type === "Pending" ? "btn-info" : "disabledBtn"
              }`}
              onClick={() => handleTabChange("Pending")}
            >
              <span className="">Pending</span>
            </button>
            <button
              type="button"
              className={`btn btn-sm ${
                type === "Accepted" ? "btn-danger" : "disabledBtn"
              } ms-3`}
              onClick={() => handleTabChange("Accepted")}
            >
              <span className="">Accepted</span>
            </button>
            <button
              type="button"
              className={`btn btn-sm ${
                type === "Declined" ? "btn-success" : "disabledBtn"
              } ms-3`}
              onClick={() => handleTabChange("Declined")}
            >
              <span className="">Declined</span>
            </button>
          </div>
        </div>
      </div>
      {type === "Pending" && (
        <>
          <PendingRequest />
        </>
      )}
      {type === "Accepted" && (
        <>
          <AcceptedRequest />
        </>
      )}
      {type === "Declined" && (
        <>
          <DeclineRequest />
        </>
      )}
    </>
  );
};

export default HostRequest;
