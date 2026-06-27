import React, { useEffect, useState } from "react";

//react-redux
import { useSelector, connect, useDispatch } from "react-redux";

//routing
import { Link } from "react-router-dom";

import arraySort from "array-sort";

//action
import { getRedeem } from "../../../store/redeem/action";
import { getSetting } from "../../../store/setting/action";

//MUI
import { TablePagination } from "@mui/material";

//dayjs
import dayjs from "dayjs";
import { getAgencyRedeem } from "../../../store/agenyRedeem/action";
import Pagination from "../../../pages/Pagination";

const TablePaginationActions = React.lazy(() => import("../TablePagination"));

const DeclineRedeemTable = (props) => {
  const dispatch = useDispatch();
  const [coinSort, setCoinSort] = useState(true);
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [activePage, setActivePage] = useState(1);

  useEffect(() => {
    dispatch(getSetting());
    dispatch(getAgencyRedeem("decline"));
  }, [dispatch]);

  const redeem = useSelector((state) => state.agencyRedeem.agencyRedeem);
  const setting = useSelector((state) => state.setting.setting);

  useEffect(() => {
    setData(redeem);
  }, [redeem]);

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };

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

  const handleCoinSort = () => {
    setCoinSort(!coinSort);
    arraySort(data, "rCoin", { reverse: coinSort });
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
                <thead>
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
                    <th>Decline date</th>
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
                          <td>{data?.agency?.name || "-"}</td>
                          <td>{data?.agency?.agencyCode || "-"}</td>
                          {/* <td>{data?.paymentGateway}</td> */}

                          <td>{data?.description}</td>
                          <td>{data?.rCoin}</td>
                          <td>
                            {(data?.rCoin / setting?.rCoinForCashOut).toFixed(2)}
                          </td>

                          <td>
                            {dayjs(data?.updatedAt).format("DD MMM, YYYY")}
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

export default connect(null, { getRedeem, getSetting })(DeclineRedeemTable);
