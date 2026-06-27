import React, { useState, useEffect } from "react";
import { useDispatch, useSelector, connect } from "react-redux";
import { Link } from "react-router-dom";
import { TablePagination, Tooltip } from "@mui/material";
import dayjs from "dayjs";
import { baseURL } from "../../../util/Config";
import noImage from "../../../assets/images/noImage.png";
import { getComplain, solvedComplain } from "../../../store/complain/action";
import { OPEN_COMPLAIN_DIALOG } from "../../../store/complain/types";
import ComplainDetails from "../../dialog/ComplainDetails";

import Male from "../../../assets/images/male.png"

const TablePaginationActions = React.lazy(() => import("../TablePagination"));

const ComplainRequest = (props) => {
  const dispatch = useDispatch();


  const [type, setType] = useState(() => {
    return localStorage.getItem('complainRequestTab') || "Pending"
  });
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const handleTabChange = (newType) => {
    setType(newType);
    localStorage.setItem('complainRequestTab', newType);
  };

  useEffect(() => {
    dispatch(getComplain(type.toLowerCase()));
  }, [dispatch, type]);

  const complain = useSelector((state) => state.complain.complain);


  useEffect(() => {
    setData(complain);
  }, [complain]);

  // ... rest of the handlers (handleChangePage, handleChangeRowsPerPage, handleSearch, etc.)

  const handleSolvedComplain = (id) => {

    props.solvedComplain(id);
  };

  const handleViewComplainDetail = (data) => {
    dispatch({ type: OPEN_COMPLAIN_DIALOG, payload: data });
  };

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-white">Complain Request</h3>
          </div>
          <div className="col-12 col-md-6 order-md-2 order-first">
            <nav aria-label="breadcrumb" className="breadcrumb-header float-start float-lg-end">
              <ol className="breadcrumb">
                <li className="breadcrumb-item">
                  <Link to="/admin/dashboard" className="text-danger">Dashboard</Link>
                </li>
                <li className="breadcrumb-item active" aria-current="page">Complain Request</li>
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
              className={`btn btn-sm ${type === "Solved" ? "btn-danger" : "disabledBtn"} ms-3`}
              onClick={() => handleTabChange("Solved")}
            >
              <span className="">Solved</span>
            </button>
          </div>
        </div>
      </div>

      {/* Table Section */}
      <div className="card-body card-overflow">
        <div className="d-sm-flex align-items-center justify-content-between mb-4"></div>

        <table className="table table-striped">
          <thead>
            <tr>
              <th>No.</th>
              <th>User</th>
              <th>Complain Image</th>
              <th>Message</th>
              <th>Contact</th>
              <th>CreatedAt</th>
                    <th>Solved</th>
                    <th>Details</th>

              {/* <th>Created At</th>
              <th>Updated At</th>
              <th>Edit</th>
              <th>Delete</th> */}
            </tr>
          </thead>
          <tbody>
            {data.length > 0 ? (
              (rowsPerPage > 0
                ? data.slice(
                  page * rowsPerPage,
                  page * rowsPerPage + rowsPerPage
                )
                : data
              ).map((data, index) => {
                return (
                  <tr key={index}>
                    <td>{index + 1}</td>
                    <td className="d-flex align-items-center justify-content-center">
                      <img
                        height="50px"
                        width="50px"
                        alt="app"
                        src={data?.userId?.image ? data?.userId?.image : Male}
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
                        {data?.userId?.name}
                      </span>
                    </td>
                    <td className="mx-auto">
                      <div className="d-flex justify-content-center">
                      <img
                        height="50px"
                        width="50px"
                        alt="app"
                        src={data?.userId?.image ? data?.userId?.image : Male}
                        style={{
                          boxShadow: "0 5px 15px 0 rgb(105 103 103 / 0%)",
                          border: "2px solid #fff",
                          borderRadius: 10,
                          objectFit: "cover",
                          display: "block",
                        }}
                        className=""
                      />
                      </div>
                    </td>

                    <td>{data?.message ? data?.message : "-"}</td>
                    <td>{data?.contact ? data?.contact : "-"}</td>
                    <td>
                            {dayjs(data.createdAt).format("DD MMM, YYYY")}
                          </td>

                          <td>
                            <label className="switch">
                              <input
                                type="checkbox"
                                checked={data.solved}
                                onChange={() => handleSolvedComplain(data._id)}
                              />
                              <span className="slider">
                                <p
                                  style={{
                                    fontSize: 12,
                                    marginLeft: `${
                                      data.solved ? "5px" : "35px"
                                    }`,
                                    color: "#000",
                                    marginTop: "6px",
                                  }}
                                >
                                  {data.solved ? "Yes" : "No"}
                                </p>
                              </span>
                            </label>
                          </td>
                          <td>
                            <Tooltip title="Complain Details">
                              <button
                                type="button"
                                className="btn btn-sm btn-info"
                                onClick={() => handleViewComplainDetail(data)}
                              >
                                <i className="fas fa-info-circle fa-lg"></i>
                              </button>
                            </Tooltip>
                          </td>



                  </tr>
                );
              })
            ) : (
              <tr>
                <td colSpan="6" align="center">
                  Nothing to show!!
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
      {/* <div className="card">
     
        <div className="card-body card-overflow">
          <table className="table table-striped">
            <thead className="text-center">
              <tr>
                <th>No.</th>
                <th>User</th>
                <th>Complain Image</th>

              </tr>
            </thead>
            <tbody className="text-center">
              {complain?.length > 0 ? (
                complain?.map((data, index) => {
                  return (
                    <tr key={index}>
                      <td>{index + 1}</td>
                      <td className="d-flex align-items-center justify-content-center">
                        <img
                          height="50px"
                          width="50px"
                          alt="app"
                          src={data?.userId?.image ? data?.userId?.image : Male}
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
                          {data?.userId?.name}
                        </span>
                      </td>

                      <td className="">
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
        </div>
      </div> */}
      <ComplainDetails />
    </>
  );
};

export default connect(null, { getComplain, solvedComplain })(ComplainRequest); 