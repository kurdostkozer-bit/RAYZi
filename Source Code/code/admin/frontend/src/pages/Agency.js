import React, { useEffect, useState } from "react";

//redux
import { connect, useDispatch, useSelector } from "react-redux";

//action
import {
  getAgency,
  enableDisableAgency,
  redeemEnableAgency,
} from "../store/agency/action";

//routing
import $ from "jquery";
import { Link } from "react-router-dom";
import Male from "../assets/images/male.png";
//MUI
import { Tooltip } from "@mui/material";

import AgencyDialogue from "../component/dialog/AgencyDialogue";

//sweet alert
import { alert, warning } from "../util/Alert";
import { OPEN_AGENCY_DIALOG } from "../store/agency/type";
import dayjs from "dayjs";
import { useNavigate } from "react-router-dom";
import Pagination from "./Pagination";

const AgencyDetails = (props) => {
  const dispatch = useDispatch();
  const [data, setData] = useState([]);
  const navigate = useNavigate();
  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [search, setSearch] = useState("");


  useEffect(() => {
    dispatch(getAgency(activePage, rowsPerPage));
  }, [activePage, rowsPerPage]);

  const { agency, total } = useSelector((state) => state.agency);

  useEffect(() => {
    setData(agency);
  }, [agency]);

  useEffect(() => {
    handleSearch();
  }, [search, agency]);

  //   pagination

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };

  $(document).ready(function () {
    $("img").bind("error", function () {
      // Set the default image
      $(this).attr("src", Male);
    });
  });

  const handleSearch = () => {
    const value = search.trim().toLowerCase();

    if (value) {
      const filteredData = agency.filter((data) => {
        return (
          data?.name?.toLowerCase().includes(value) ||
          data?.uniqueId?.toString().includes(value) ||
          data?.agencyCode?.toString().includes(value) ||
          data?.totalCoin?.toString().includes(value)
        );
      });
      setData(filteredData);
    } else {
      setData(agency);
    }
  };

  const handleOpen = () => {


    dispatch({ type: OPEN_AGENCY_DIALOG });
  };

  const handleDelete = (planId) => {
    const data = warning();
    data
      .then((isDeleted) => {
        if (isDeleted) {
          props.enableDisableAgency(planId);
          alert("Deleted!", `Plan has been deleted!`, "success");
        }
      })
      .catch((err) => console.log(err));
  };

  const handleEdit = (data) => {


    dispatch({ type: OPEN_AGENCY_DIALOG, payload: data });
  };

  const handleIsTop = (id) => {


    dispatch(enableDisableAgency(id));
  };

  const handleEnabledRedeem = (id) => {
    dispatch(redeemEnableAgency(id));
  };

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-white">Agency</h3>
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
                  Agency
                </li>
              </ol>
            </nav>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col">
          <div className="card">
            <div className="card-header pb-0">
              <div className="row my-3">
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-8 float-left">
                  <button
                    type="button"
                    className="btn waves-effect waves-light btn-danger btn-sm float-left"
                    onClick={handleOpen}
                    id="bannerDialog"
                  >
                    <i className="fa fa-plus"></i>
                    <span className="icon_margin">New</span>
                  </button>
                </div>
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-4 float-right mt-3 mt-lg-0 mt-xl-0">
                  <form action="">
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
                  </form>
                </div>
              </div>
            </div>
            <div className="card-body card-overflow">
              <div className="d-sm-flex align-items-center justify-content-between mb-4"></div>

              <table className="table table-striped">
                <thead className="text-center">
                  <tr>
                    <th>No.</th>
                    {/* <th>BD </th> */}
                    <th>Agency</th>
                    <th>UniqueId</th>
                    <th>Agency Code</th>
                    <th>MobileNumber</th>
                    <th>Total Coin</th>
                    <th>Created At </th>
                    <th>Is Active</th>
                    {/* <th>Redeem Enable</th> */}
                    <th>Action</th>
                    <th>Host</th>
                  </tr>
                </thead>
                <tbody className="text-center">
                  {data?.length > 0 ? (
                    data.map((data, index) => {
                      return (
                        <tr key={index}>
                          <td>{(activePage - 1) * rowsPerPage + index + 1}</td>

                          <td className="d-flex align-items-center justify-content-left">
                            <img
                              height="50px"
                              width="50px"
                              alt="app"
                              src={data?.image ? data?.image : Male}
                              style={{
                                boxShadow: "0 5px 15px 0 rgb(105 103 103 / 0%)",
                                border: "2px solid #fff",
                                borderRadius: 10,
                                objectFit: "cover",
                                display: "block",
                              }}
                              className=""
                            />
                            <span className="ms-2 d-flex align-items-center">
                              {data?.name}
                            </span>
                          </td>

                          <td>{data?.uniqueId}</td>
                          <td>{data?.agencyCode}</td>
                          <td>{data?.mobile}</td>

                          <td className="text-success">
                            {data?.totalCoin ? data?.totalCoin : 0}
                          </td>
                          <td>
                            {dayjs(data?.createdAt).format("DD MMM, YYYY")}
                          </td>

                          <td>
                            <label className="switch">
                              <input
                                type="checkbox"
                                checked={data?.isActive}
                                onChange={() => handleIsTop(data?._id)}
                              />
                              <span className="slider">
                                <p
                                  style={{
                                    fontSize: 12,
                                    marginLeft: `${
                                      data?.isActive ? "-24px" : "35px"
                                    }`,
                                    color: "#000",
                                    marginTop: "6px",
                                  }}
                                >
                                  {data?.isActive ? "Yes" : "No"}
                                </p>
                              </span>
                            </label>
                          </td>
                          {/* <td>
                            <label className="switch">
                              <input
                                type="checkbox"
                                checked={data?.redeemEnable}
                                onChange={() => handleEnabledRedeem(data?._id)}
                              />
                              <span className="slider">
                                <p
                                  style={{
                                    fontSize: 12,
                                    marginLeft: `${
                                      data?.redeemEnable ? "-24px" : "35px"
                                    }`,
                                    color: "#000",
                                    marginTop: "6px",
                                  }}
                                >
                                  {data?.redeemEnable ? "Yes" : "No"}
                                </p>
                              </span>
                            </label>
                          </td> */}
                          <td>
                            <Tooltip title="Edit">
                              <button
                                type="button"
                                className="btn btn-sm btn-info"
                                onClick={() => handleEdit(data)}
                              >
                                <i className="fa fa-edit fa-lg"></i>
                              </button>
                            </Tooltip>
                          </td>
                          <td>
                            <div className="d-flex justify-content-center">
                              <Tooltip title="Host">
                                <button
                                  type="button"
                                  className="btn btn-sm btn-success d-flex align-items-center"
                                  style={{ backgroundColor: "#fc9494" }}
                                  onClick={() =>
                                    navigate("/admin/agency/agencyWiseHost",{state: data})
                                  }
                                >
                                  <i
                                    className="material-icons"
                                    style={{ fontSize: "20px" }}
                                  >
                                    people
                                  </i>
                                </button>
                              </Tooltip>
                            </div>
                          </td>
                        </tr>
                      );
                    })
                  ) : (
                    <tr>
                      <td colSpan="12" align="center">
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
      <AgencyDialogue />
    </>
  );
};

export default connect(null, { getAgency, enableDisableAgency })(AgencyDetails);
