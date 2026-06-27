import React, { useEffect, useState } from "react";

//redux
import { connect, useDispatch, useSelector } from "react-redux";

//action
import { getCommission, deleteCommission } from "../../store/commision/action";

//config
import { baseURL } from "../../util/Config";

//routing
import { Link } from "react-router-dom";

//MUI
import { TablePagination, Tooltip } from "@mui/material";

// dialog
import CommissionDialog from "../dialog/CommisionDialog";

//sweet alert
import { alert, warning } from "../../util/Alert";

import $ from "jquery";
//image
import noImage from "../../assets/images/noImage.png";
import { OPEN_COMMISSION_DIALOG } from "../../store/commission/type";
import arraySort from "array-sort";
import Pagination from "../../pages/Pagination";

const TablePaginationActions = React.lazy(() => import("./TablePagination"));

const CommissionTable = (props) => {
  const dispatch = useDispatch();
  const [data, setData] = useState([]);
  const [coinSort, setCoinSort] = useState(true);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [activePage, setActivePage] = useState(1);

  useEffect(() => {
    dispatch(getCommission());
  }, []);

  const commission = useSelector((state) => state?.commission?.commission);

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
    setPage(pageNumber)
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };
  $(document).ready(function () {
    $("img").bind("error", function () {
      $(this).attr("src", noImage);
    });
  });

  const handleSearch = (e) => {
    const value = e.target.value.trim().toUpperCase();
    if (value) {
      const data = commission?.filter((data) => {
        return data?.URL?.toUpperCase()?.indexOf(value) > -1;
      });
      setData(data);
    } else {
      return setData(commission);
    }
  };

  const handleOpen = () => {


    dispatch({ type: OPEN_COMMISSION_DIALOG });
  };

  const handleDelete = (commissionId) => {


    const data = warning();
    data
      .then((isDeleted) => {
        if (isDeleted) {
          dispatch(deleteCommission(commissionId));
        }
      })
      .catch((err) => console.log(err));
  };

  const handleEdit = (data) => {


    dispatch({ type: OPEN_COMMISSION_DIALOG, payload: data });
  };

  const handleCoinSort = () => {
    setCoinSort(!coinSort);
    arraySort(data, "upperCoin", { reverse: coinSort });
  };
  return (
    <>
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
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-4 float-right mt-3 mt-lg-0 mt-xl-0"></div>
              </div>
            </div>
            <div className="card-body card-overflow">
              <div className="d-sm-flex align-items-center justify-content-between mb-4"></div>

              <table className="table table-striped custom-table">
                <thead>
                  <tr>
                    <th>No.</th>
                    <th onClick={handleCoinSort} style={{ cursor: "pointer" }}>
                      Upper Coin {coinSort ? " ▼" : " ▲"}
                    </th>
                    <th>Percentage</th>
                    <th>Edit</th>
                    <th>Delete</th>
                  </tr>
                </thead>
                <tbody>
                  {data?.length > 0 ? (
                    data?.map((data, index) => {
                      return (
                        <tr key={index}>
                          <td>{index + 1}</td>
                          <td>{data.upperCoin}</td>
                          <td className="text-success">
                            {data.amountPercentage + "%"}
                          </td>
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
                            <Tooltip title="Delete">
                              <button
                                type="button"
                                className="btn btn-danger btn-sm"
                                onClick={() => handleDelete(data?._id)}
                              >
                                <i className="fas fa-trash-alt fa-lg"></i>
                              </button>
                            </Tooltip>
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
                userTotal={commission.length}
                handleRowsPerPage={handleRowsPerPage}
                handlePageChange={handlePageChange}
              />
            </div>
          </div>
        </div>
      </div>
      <CommissionDialog />
    </>
  );
};

export default connect(null, { getCommission, deleteCommission })(
  CommissionTable
);
