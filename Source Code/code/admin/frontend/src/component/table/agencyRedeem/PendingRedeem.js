import React, { useEffect, useState } from "react";

//react-redux
import { useSelector, connect, useDispatch } from "react-redux";

//routing
import { Link } from "react-router-dom";

//action
import { getSetting } from "../../../store/setting/action";

//MUI
import { TablePagination } from "@mui/material";

//dayjs
import dayjs from "dayjs";

import arraySort from "array-sort";
import {
  acceptRedeem,
  declineRedeem,
  getAgencyRedeem,
} from "../../../store/agenyRedeem/action";

import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
import { Cancel } from "@mui/icons-material";
import Pagination from "../../../pages/Pagination";

//sweet alert

const TablePaginationActions = React.lazy(() => import("../TablePagination"));

const PendingRedeemTable = (props) => {
  const dispatch = useDispatch();

  const [coinSort, setCoinSort] = useState(true);
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [openDialogue, setOpenDialogue] = useState(false);
  const [reason, setReason] = useState("");
  const [errors, setErrors] = useState("");
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [openAccept, setOpenAccept] = useState(false);
  const [activePage, setActivePage] = useState(1);

  useEffect(() => {
    dispatch(getSetting());
    dispatch(getAgencyRedeem("pending"));
  }, [dispatch]);

  const redeem = useSelector((state) => state.agencyRedeem.agencyRedeem);
  const setting = useSelector((state) => state.setting.setting);

  useEffect(() => {
    setData(redeem);
  }, [redeem]);

  const handleSearch = (e) => {
    const value = e.target.value.toUpperCase()
      ? e.target.value.trim().toUpperCase()
      : e.target.value.trim();
    if (value) {
      const data = redeem.filter((data) => {
        return (
          data?.userId?.name?.toUpperCase()?.indexOf(value) > -1 ||
          data?.paymentGateway?.toUpperCase()?.indexOf(value) > -1 ||
          data?.description?.toUpperCase()?.indexOf(value) > -1 ||
          data?.rCoin?.toString()?.indexOf(value) > -1
        );
      });
      setData(data);
    } else {
      return setData(redeem);
    }
  };

  const handleAcceptDecline = (id, type) => {
    props.acceptRedeem(id, type);
  };

  const handleCoinSort = () => {
    setCoinSort(!coinSort);
    arraySort(data, "rCoin", { reverse: coinSort });
  };

  const validateDecline = () => {
    let error = {};
    let isValid = true;
    if (!reason || reason === "") {
      error.reason = "Please enter valid reason!";
      isValid = false;
    }
    setErrors(error);
    return isValid;
  };

  const handleDeclineSubmit = (type) => {
    if (validateDecline()) {
      dispatch(declineRedeem(selectedRequest?._id, type, reason));
      setOpenDialogue(false);
      setReason("");
    }
  };

  const handleAcceptSubmit = (type) => {
    dispatch(acceptRedeem(selectedRequest?._id, type));
    setOpenAccept(false);
  };

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
    setPage(pageNumber)
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };

  return (
    <>
      <Dialog
        open={openDialogue}
        aria-labelledby="responsive-dialog-title"
        onClose={() => {
          setOpenDialogue(false);
          setReason("");
        }}
        disableBackdropClick
        disableEscapeKeyDown
        fullWidth
        maxWidth="xs"
      >
        <DialogTitle id="responsive-dialog-title">
          <span className="text-danger font-weight-bold h4"> Add Reason </span>
        </DialogTitle>

        <IconButton
          style={{
            position: "absolute",
            right: 0,
          }}
        >
          <Tooltip title="Close">
            <Cancel
              className="text-danger"
              onClick={() => {
                setOpenDialogue(false);
                setReason("");
              }}
            />
          </Tooltip>
        </IconButton>
        <DialogContent>
          <div className="modal-body pt-1 px-1 pb-3">
            <div className="d-flex flex-column">
              <form>
                <div className="form-group">
                  <label className="mb-2 text-gray">Reason</label>
                  <input
                    type="text"
                    className="form-control"
                    required=""
                    placeholder="Enter Valid Reason"
                    value={reason}
                    onChange={(e) => {
                      setReason(e.target.value);

                      if (!e.target.value) {
                        return setErrors({
                          ...errors,
                          reason: "Reason is Required!",
                        });
                      } else {
                        return setErrors({
                          ...errors,
                          reason: "",
                        });
                      }
                    }}
                  />
                  {errors.reason && (
                    <div className="ml-2 mt-1">
                      {errors.reason && (
                        <div className="pl-1 text__left">
                          <span className="text-red">{errors.reason}</span>
                        </div>
                      )}
                    </div>
                  )}
                </div>
                <div className="mt-5">
                  <button
                    type="button"
                    className="btn btn-outline-info ml-2 btn-round float__right icon_margin"
                    onClick={() => {
                      setOpenDialogue(false);
                      setReason("");
                    }}
                  >
                    Close
                  </button>
                  <button
                    type="button"
                    className="btn btn-round float__right btn-danger"
                    onClick={() => {
                      handleDeclineSubmit("decline");
                    }}
                  >
                    Submit
                  </button>
                </div>
              </form>
            </div>
          </div>
        </DialogContent>
      </Dialog>

      <Dialog
        open={openAccept}
        aria-labelledby="responsive-dialog-title"
        onClose={() => {
          setOpenAccept(false);
        }}
        disableBackdropClick
        disableEscapeKeyDown
        fullWidth
        style={{ maxHeight: "600px", marginTop: "100px" }}
      >
        <DialogTitle id="responsive-dialog-title">
          <span className="text-danger font-weight-bold h4">
            Whould like to approve a Agency redeem request?
          </span>
        </DialogTitle>

        <IconButton
          style={{
            position: "absolute",
            right: 0,
          }}
        >
          <Tooltip title="Close">
            <Cancel
              className="text-danger"
              onClick={() => {
                setOpenAccept(false);
              }}
            />
          </Tooltip>
        </IconButton>
        <DialogContent>
          <div className="modal-body pt-1 px-1 pb-3">
            <div className="d-flex flex-column">
              <form>
                <div className="mt-5">
                  <button
                    type="button"
                    className="btn btn-outline-info ml-2 btn-round float__right icon_margin"
                    onClick={() => {
                      setOpenAccept(false);
                    }}
                  >
                    Close
                  </button>

                  <button
                    type="button"
                    className="btn btn-round float__right btn-danger"
                    onClick={() => {
                      handleAcceptSubmit("accept");
                    }}
                  >
                    Submit
                  </button>
                </div>
              </form>
            </div>
          </div>
        </DialogContent>
      </Dialog>

      <div className="row">
        <div className="col">
          <div className="card">
            <div className="card-header pb-0">
              <div className="row my-3">
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-8 float-left"></div>
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-4 float-right">
                  <form action="">
                    <div className="input-group mb-3 border rounded-pill">
                      <div className="input-group-prepend border-0">
                        <div id="button-addon4" className="btn text-danger">
                          <i className="fas fa-search mt-2"></i>
                        </div>
                      </div>
                      <input
                        type="search"
                        placeholder="What're you searching for?"
                        aria-describedby="button-addon4"
                        className="form-control bg-none border-0 rounded-pill searchBar"
                        onChange={handleSearch}
                      />
                    </div>
                  </form>
                </div>
              </div>
            </div>
            <div className="card-body card-overflow">
              <div className="d-sm-flex align-items-center justify-content-between mb-4"></div>

              <table className="table table-striped">
                <thead className="text-white">
                  <tr>
                    <th>No.</th>
                    <th>Agency Name</th>
                    <th>Agency Code</th>
                    {/* <th>Payment Gateway</th> */}
                    <th>Description</th>
                    <th onClick={handleCoinSort} style={{ cursor: "pointer" }}>
                      RCoin {coinSort ? " ▼" : " ▲"}
                    </th>
                    <th>
                      {setting.currency === "£"
                        ? "GBP(£)"
                        : setting.currency === "$"
                        ? "Dollar($)"
                        : "Rupee(₹)"}
                    </th>
                    <th>CreatedAt</th>
                    <th>Accept</th>
                    <th>Decline</th>
                  </tr>
                </thead>
                <tbody className="t">
                  {data?.length > 0 ? (
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
                          <td>{data.agency?.name}</td>
                          <td>{data.agency?.agencyCode}</td>

                          {/* <td>{data.paymentGateway}</td> */}

                          <td>{data.description}</td>
                          <td>{data.rCoin}</td>
                          <td>
                            {(data.rCoin / setting.rCoinForCashOut).toFixed(2)}
                          </td>

                          <td>
                            {dayjs(data.createdAt).format("DD MMM, YYYY")}
                          </td>
                          <td>
                            <button
                              className="btn btn-sm text-white success"
                              onClick={() => {
                                setOpenAccept(true);
                                setSelectedRequest(data);
                              }}
                            >
                              <i className="fa fa-check"></i> Accept
                            </button>
                          </td>
                          <td>
                            <button
                              className="btn btn-sm text-white danger"
                              onClick={() => {
                                setSelectedRequest(data);
                                setOpenDialogue(true);
                              }}
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
                userTotal={redeem.length}
                handleRowsPerPage={handleRowsPerPage}
                handlePageChange={handlePageChange}
              />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default connect(null, { getAgencyRedeem, acceptRedeem, getSetting })(
  PendingRedeemTable
);
