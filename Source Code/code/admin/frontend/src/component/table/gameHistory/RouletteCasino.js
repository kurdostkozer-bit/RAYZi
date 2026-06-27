import React, { useEffect, useState } from "react";
import Pagination from "../../../pages/Pagination";
import { DateRangePicker } from "react-date-range";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { connect, useDispatch, useSelector } from "react-redux";
import {
  resetGameCoin,
  rouletteCasinoHistory,
} from "../../../store/GameHistory/action";
import $ from "jquery";
import dayjs from "dayjs";

function RouletteCasino() {
  const { gameHistory, total, adminCoin } = useSelector(
    (state) => state.gameHistory
  );
  const dispatch = useDispatch();

  const [data, setData] = useState([]);
  const [date, setDate] = useState([
    {
      startDate: new Date(new Date().getFullYear(), new Date().getMonth(), 1), // First day of current month
      endDate: new Date(), // Current date
      key: "selection",
    },
  ]);
  const [sDate, setsDate] = useState(
    dayjs(new Date(new Date().getFullYear(), new Date().getMonth(), 1)).format("YYYY-MM-DD")
  );
  const [eDate, seteDate] = useState(dayjs(new Date()).format("YYYY-MM-DD"));

  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  useEffect(() => {
    dispatch(rouletteCasinoHistory(activePage, rowsPerPage, sDate, eDate));
  }, [dispatch, activePage, rowsPerPage, sDate, eDate]);

  useEffect(() => {
    setData(gameHistory);
  }, [gameHistory]);

  const getAllUser = () => {
    setActivePage(1);
    setsDate("ALL");
    seteDate("ALL");
    dispatch(rouletteCasinoHistory(activePage, rowsPerPage, "ALL", "ALL"));
  };

  const collapsedDatePicker = () => {
    $("#datePicker").toggleClass("collapse");
  };

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };

  const handleResetCoin = () => {
    dispatch(resetGameCoin());
  };

  const handleDateChange = (item) => {
    setDate([item.selection]);
  };

  const applyDateFilter = () => {
    const dayStart = dayjs(date[0].startDate).format("YYYY-MM-DD");
    const dayEnd = dayjs(date[0].endDate).format("YYYY-MM-DD");
    setActivePage(1);
    setsDate(dayStart);
    seteDate(dayEnd);
    dispatch(rouletteCasinoHistory(activePage, rowsPerPage, dayStart, dayEnd));
    
    // Close the date picker
    const datePicker = document.getElementById('datePicker');
    if (datePicker.classList.contains('show')) {
      datePicker.classList.remove('show');
    }
  };

  return (
    <>
      <div className="card mt-3">
        <div className="card-header pb-0">
          <div className="row my-3">
            <div className="col-xs-12 col-sm-12 col-md-6 col-lg-8 float-left">
              <div className="text-left align-sm-left d-md-flex d-lg-flex justify-content-start">
                <button
                  className="btn btn-light text-info"
                  style={{ marginRight: 5 }}
                  onClick={handleResetCoin}
                >
                  Reset Diamond
                </button>
                <button
                  className="btn btn-info"
                  style={{ marginRight: 5 }}
                  onClick={getAllUser}
                >
                  All
                </button>
                <button
                  className="collapsed btn btn-info ml-5"
                  value="check"
                  data-toggle="collapse"
                  data-target="#datePicker"
                  onClick={collapsedDatePicker}
                >
                  Analytics
                  <ExpandMoreIcon />
                </button>
                <p style={{ paddingLeft: 10 }} className="my-2 ">
                  {sDate !== "ALL" && sDate + " to " + eDate}
                </p>
              </div>
            </div>

            <div className="col-xs-12 col-sm-12 col-md-6 col-lg-4 float-right">
              <div className="d-flex justify-content-end mt-3">
                <span className="text-danger">
                  Admin Total Diamonds :
                  <span className="text-info">&nbsp;&nbsp;{adminCoin}</span>
                </span>
              </div>
            </div>
            <div
              id="datePicker"
              className="collapse mt-5 pt-5"
              aria-expanded="false"
            >
              <div className="container table-responsive">
                <div key={JSON.stringify(date)}>
                  <DateRangePicker
                    onChange={handleDateChange}
                    showSelectionPreview={true}
                    moveRangeOnFirstSelection={false}
                    ranges={date}
                    direction="horizontal"
                  />
                  <div className="mt-3">
                    <button 
                      className="btn btn-danger"
                      onClick={applyDateFilter}
                    >
                      Apply Filter
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className="card-body card-overflow">
          <table className="table" style={{ borderCollapse: "collapse" }}>
            <thead className="text-center">
              <tr>
                <th>No</th>
                {/* <th>winner Card</th> */}
                <th>Admin Diamond</th>
                <th>Win/Lose</th>
                <th>winner Diamond Minus</th>
                <th>Total Add Diamond</th>
                <th>Result</th>
                <th>Date</th>
                <th>Time</th>
              </tr>
            </thead>
            <tbody className="text-center">
              {data?.length > 0 ? (
                data?.map((data, index) => {
                  var gameDate = data?.date ? data?.date.split(",") : [];

                  return (
                    <>
                      <tr
                        data-toggle="collapse"
                        data-target={`#demo${index}`}
                        className="accordion-toggle pointer-cursor"
                      >
                        <td>{index + 1}</td>

                        {/* <td>{data?.winnerIndex}</td> */}
                        <td className="text-success">
                          {data?.updatedAdminCoin}
                        </td>
                        <td
                          className={
                            data?.totalAdd + data?.winnerCoinMinus > 0
                              ? "text-success"
                              : "text-danger"
                          }
                        >
                          {data?.totalAdd + data?.winnerCoinMinus > 0
                            ? data?.totalAdd + data?.winnerCoinMinus
                            : Math.abs(data?.totalAdd + data?.winnerCoinMinus)}
                        </td>
                        <td className="text-danger">
                          {parseInt(data?.winnerCoinMinus)}
                        </td>
                        <td className="text-warning">{data?.totalAdd}</td>
                        <td className="text-white">
                          <span
                            style={{
                              padding: "6px 8px",
                              borderRadius: "50%",
                              backgroundColor: `${data?.winnerObj?.color}`,
                            }}
                          >
                            {data?.winnerObj?.no}
                          </span>
                        </td>

                        <td className="text-info">{gameDate[0]}</td>
                        <td className="text-primary">{gameDate[1]}</td>
                      </tr>
                    </>
                  );
                })
              ) : (
                <tr>
                  <td colSpan="7" align="center">
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
    </>
  );
}

export default RouletteCasino;
