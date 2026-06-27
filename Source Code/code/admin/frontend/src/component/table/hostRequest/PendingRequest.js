import React, { useEffect, useState } from "react";

//react-redux
import { useSelector, useDispatch } from "react-redux";

//routing
import { Link, useNavigate } from "react-router-dom";

//dayjs
import dayjs from "dayjs";

import arraySort from "array-sort";
import {
  acceptHostReq,
  acceptHostRequest,
  getHostRequest,
} from "../../../store/hostRequest/action";
import Pagination from "../../../pages/Pagination";
import AcceptedRequest from "./AcceptedRequest";
import DeclineRequest from "./DeclineRequest";
import {
  OPEN_AGENCY_CODE_DIALOGUE,
  OPEN_BANK_DETAILS_DIALOGUE,
  OPEN_REASON_DIALOGUE,
} from "../../../store/hostRequest/type";
import ReasonDialogue from "./ReasonDialogue";
import AddAgencyCodeDialogue from "./AddAgencyCodeDialogue";

import BankDetailsDialogue from "./BankDetailsDialogue";
import { getUser } from "../../../store/user/action";
// import ReasonDialogue from "./ReasonDialogue";
// import AddAgencyCodeDialogue from "./AddAgencyCodeDialogue";

const PendingRequest = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { request, total } = useSelector((state) => state.hostRequest);

  const [data, setData] = useState([]);
  const [search, setSearch] = useState("");
  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const [sDate, setsDate] = useState(() => {
    const startOfMonth = new Date();
    startOfMonth.setDate(1);
    return dayjs(startOfMonth).format("YYYY/M/DD");
  });

  const [eDate, seteDate] = useState(() => {
    const endOfMonth = new Date();
    endOfMonth.setMonth(endOfMonth.getMonth() + 1, 0);
    return dayjs(endOfMonth).format("YYYY/M/DD");
  });

  // Add formatted date variables
  const formattedStart = sDate === "ALL" ? "ALL" : dayjs(sDate).format("YYYY/M/DD");
  const formattedEnd = eDate === "ALL" ? "ALL" : dayjs(eDate).format("YYYY/M/DD");

  useEffect(() => {
    dispatch(getHostRequest(activePage, rowsPerPage, 1));
  }, [activePage, rowsPerPage, 1]);

 
  useEffect(() => {
    setData(request);
  }, [request]);




  //   pagination

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };

  
  const handleUserInfo = (user) => {
    sessionStorage.setItem("user", JSON.stringify(user));
    navigate("/admin/user/detail", {
      state: {
        id: user?.user?._id,
      },
    })
  };

  const handleAcceptDecline = (data, type) => {

    if (
      (data?.agencyCode === "" || data?.agencyCode == null) &&
      type === "accept"
    ) {
      dispatch({
        type: OPEN_AGENCY_CODE_DIALOGUE,
        payload: { data: data, type: type },
      });
    }
    else {
      dispatch({
        type: OPEN_BANK_DETAILS_DIALOGUE,
        payload: { data: data, type: type },
      });
    }
  };

  const handleDecline = (id, type) => {
    dispatch({ type: OPEN_REASON_DIALOGUE, payload: { id: id, type: type } });
  };

  const handleSearch = () => {
    const value = search.trim().toLowerCase();

    if (value) {
      const filteredData = data.filter((data) => {
        return (
          data?.user?.name?.toLowerCase().includes(value) ||
          data?.agencyCode?.toString().includes(value)
        );
      });
      setData(filteredData);
    } else {
      setSearch("")
      setData(request);
    }
  };

  return (
    <>
      <div className="row">
        <div className="col">
          <div className="card">
            <div className="card-header pb-0">
              <div className="row my-3">
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-8 float-left"></div>
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-4 float-right">
                  <div className="input-group mb-3 border rounded-pill">
                      <input
                        type="search"
                        id="searchBar"
                        autoComplete="off"
                        placeholder="What're you searching for?"
                        aria-describedby="button-addon4"
                        className="form-control bg-none border-0 rounded-pill searchBar"
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        onKeyDown={(e) => {
                          if (e.key === "Enter") {
                            handleSearch(); // Trigger search on Enter key
                          }
                        }}
                      />
                      <div className="input-group-prepend border-0">
                        <div
                          id="button-addon4"
                          className="btn text-danger"
                          onClick={handleSearch}
                        >
                          <i className="fas fa-search mt-2"></i>
                        </div>
                      </div>
                    </div>

                </div>
              </div>
            </div>
            <div className="card-body card-overflow">
              <div className="d-sm-flex align-items-center justify-content-between mb-4"></div>

              <table className="table table-striped">
                <thead className="text-white">
                  <tr>
                    <th>No.</th>
                    <th>Image</th>
                    <th>User Name</th>
                    <th>Agency Code</th>
                    <th>CreatedAt</th>
                    <th>Accept</th>
                    <th>Decline</th>
                  </tr>
                </thead>
                <tbody className="t">
                  {data?.length > 0 ? (
                    data?.map((data, index) => {
                      return (
                        <tr key={index}>
                          <td>{index + 1}</td>
                          <td>
                            <img
                              height="50px"
                              width="50px"
                              alt="app"
                              src={data?.user?.image}
                              onClick={() => handleUserInfo(data)}
                              // onClick={() => navigate("/admin/user/detail", {
                              //   state : {
                              //     id : data?.user?._id
                              //   }
                              // })}
                              style={{
                                boxShadow: "0 5px 15px 0 rgb(105 103 103 / 0%)",
                                border: "2px solid #fff",
                                borderRadius: 10,
                                objectFit: "cover",
                                display: "block",
                              }}
                              className="mx-auto"
                            />
                          </td>
                          <td>{data?.user?.name}</td>
                          <td>{data?.agencyCode ? data?.agencyCode : "-"}</td>

                          <td>
                            {dayjs(data?.createdAt).format("DD MMM, YYYY")}
                          </td>
                          <td>
                            <button
                              className="btn btn-sm text-white success"
                              onClick={() =>
                                handleAcceptDecline(data, "accept")
                              }
                            >
                              <i className="fa fa-check"></i> Accept
                            </button>
                          </td>
                          <td>
                            <button
                              className="btn btn-sm text-white danger"
                              onClick={() =>
                                handleDecline(data, "decline")
                              }
                            >
                              <i className="fas fa-times"></i> Decline
                            </button>
                          </td>
                        </tr>
                      );
                    })
                  ) : (
                    <tr>
                      <td colSpan="10" align="center">
                        Nothing to show!!
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
              <Pagination
                activePage={activePage}
                rowsPerPage={rowsPerPage}
                userTotal={total}
                handleRowsPerPage={handleRowsPerPage}
                handlePageChange={handlePageChange}
              />
            </div>
          </div>
        </div>
      </div>
      <ReasonDialogue />
      <AddAgencyCodeDialogue />
      <BankDetailsDialogue />
    </>
  );
};

export default PendingRequest;
