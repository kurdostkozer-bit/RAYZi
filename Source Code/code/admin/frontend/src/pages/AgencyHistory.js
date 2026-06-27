import React, { useEffect, useState } from "react";

//react-redux
import { useSelector, useDispatch } from "react-redux";

//routing
import { Link } from "react-router-dom";

//jquery
import $ from "jquery";

//dayjs
import dayjs from "dayjs";
import Pagination from "../pages/Pagination";

import { getAgencyHistory } from "../store/agency/action";

//MUI icon

//Date Range Picker
import { DateRangePicker } from "react-date-range";

const AgencyHistory = () => {
  const dispatch = useDispatch();
  const { agencyHistory, agencyHistoryTotal } = useSelector(
    (state) => state.agency
  );
  const [data, setData] = useState([]);
  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [date, setDate] = useState([]);
  const [sDate, setsDate] = useState("ALL");
  const [eDate, seteDate] = useState("ALL");

  const maxDate = new Date();

  useEffect(() => {
    $("#card").click(() => {
      $("#datePicker");
    });
  }, []);

  useEffect(() => {
    dispatch(getAgencyHistory(sDate, eDate, activePage, rowsPerPage));
  }, [activePage, rowsPerPage]);

  useEffect(() => {
    setData(agencyHistory);
  }, [agencyHistory]);

  useEffect(() => {
    if (date.length === 0) {
      setDate([
        {
          startDate: new Date(),
          endDate: new Date(),
          key: "selection",
        },
      ]);
    }
    $("#datePicker");
    setData(agencyHistory);
  }, [date, agencyHistory]);

  const collapsedDatePicker = () => {
    const datePicker = $("#datePicker");
    if (datePicker.css('display') === 'none') {
      datePicker.css('display', 'block');
    } else {
      datePicker.css('display', 'none');
    }
  };

  //   const getAllUser = () => {
  //     setActivePage(1);
  //     setsDate("ALL");
  //     seteDate("ALL");
  //     $("#datePicker");
  //     dispatch(getAgencyHistory(activePage, rowsPerPage, sDate, eDate));
  //   };

  //   pagination

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };
  
  const handleDateFilter = () => {
    setActivePage(1);
    $("#datePicker").css('display', 'none');
    dispatch(getAgencyHistory(sDate, eDate, activePage, rowsPerPage));
  };

  return (
    <>
      <div className="row">
        <div className="col-12 col-md-6 order-md-1 order-last">
          <h3 className="mb-3 text-white">Agency History</h3>
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
                Agency History
              </li>
            </ol>
          </nav>
        </div>
      </div>
      <button
        className="collapsed btn btn-info ml-5 mt-3"
        value="check"
        data-toggle="collapse"
        data-target="#datePicker"
        onClick={collapsedDatePicker}
      >
        Analytics
        {/* <ExpandMoreIcon /> */}
      </button>
      <p style={{ paddingLeft: 10 }} className="my-2 ">
        {sDate !== "ALL" && sDate + " to " + eDate}
      </p>
      <div className="row">
        <div className="col">
          <div className="card">
            <div className="card-header pb-0">
              <div className="row my-3">
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-8 float-left"></div>
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-4 float-right"></div>
              </div>
            </div>

            <div
                  id="datePicker"
                  className="date-picker-wrapper mt-5 pt-5"
                  aria-expanded="false"
                  style={{ display: 'none' }}
                >
                  <div className="container table-responsive">
                    <div key={JSON.stringify(date)}>
                      <DateRangePicker
                        maxDate={maxDate}
                        onChange={(item) => {
                          setDate([item.selection]);
                          const dayStart = dayjs(item.selection.startDate).format("YYYY/M/DD");
                          const dayEnd = dayjs(item.selection.endDate).format("YYYY/M/DD");
                          setsDate(dayStart);
                          seteDate(dayEnd);
                        }}
                        showSelectionPreview={true}
                        moveRangeOnFirstSelection={false}
                        ranges={date}
                        direction="horizontal"
                      />
                    </div>
                    <div className="mt-3">
                      <button className="btn btn-danger" onClick={handleDateFilter}>
                        Apply Filter
                      </button>
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
                    <th>Agency Earning</th>
                    <th>Host Earning</th>
                    <th>Host Count</th>
                    <th>CreatedAt</th>
                  </tr>
                </thead>
                <tbody className="t">
                  {data?.length > 0 ? (
                    data?.map((data, index) => {
                      return (
                        <tr key={index}>
                          <td>{index + 1}</td>
                          <td>{data?.agencyName ? data?.agencyName : "-"}</td>
                          <td>{data?.agencyCode ? data?.agencyCode : "-"}</td>
                          <td>
                            {data?.totalAgencyEarning
                              ? data?.totalAgencyEarning.toFixed(0)
                              : 0}
                          </td>
                          <td>
                            {data?.totalHostEarning
                              ? data?.totalHostEarning.toFixed(0)
                              : 0}
                          </td>

                          <td>{data?.hostCount ? data?.hostCount : "-"}</td>

                          <td>
                            {dayjs(data?.createdAt).format("DD MMM, YYYY")}
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
                userTotal={agencyHistory.length}
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

export default AgencyHistory;
