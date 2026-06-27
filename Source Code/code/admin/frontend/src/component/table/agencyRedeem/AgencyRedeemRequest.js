import React, { useState } from "react";
//routing
import { Link } from "react-router-dom";
import PendingRedeem from "./PendingRedeem";
import AcceptedRedeem from "./AcceptedRedeem";
import DeclineRedeem from "./DeclineRedeem";

const AgencyRedeemRequest = () => {
  const [type, setType] = useState(() => {
    return localStorage.getItem('agencyRedeemTab') || 'Pending';
  });

  const handleTabChange = (newType) => {
    setType(newType);
    localStorage.setItem('agencyRedeemTab', newType);
  };

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-white">Agency Redeem Request</h3>
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
                  User Redeem Request
                </li>
              </ol>
            </nav>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col-12">
          <div className="my-2">
            <button
              type="button"
              className={`btn btn-sm ${type === "Pending" ? "btn-info" : "disabledBtn"}`}
              onClick={() => handleTabChange("Pending")}
            >
              <span className="">Pending</span>
            </button>
            <button
              type="button"
              className={`btn btn-sm ${type === "Accepted" ? "btn-danger" : "disabledBtn"} ms-3`}
              onClick={() => handleTabChange("Accepted")}
            >
              <span className="">Accepted</span>
            </button>
            <button
              type="button"
              className={`btn btn-sm ${type === "Declined" ? "btn-success" : "disabledBtn"} ms-3`}
              onClick={() => handleTabChange("Declined")}
            >
              <span className="">Declined</span>
            </button>
          </div>
        </div>
      </div>
      {type === "Pending" && (
        <>
          <PendingRedeem />
        </>
      )}
      {type === "Accepted" && (
        <>
          <AcceptedRedeem />
        </>
      )}
      {type === "Declined" && (
        <>
          <DeclineRedeem />
        </>
      )}
    </>
  );
};

export default AgencyRedeemRequest;
