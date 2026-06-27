import { Link } from "react-router-dom";
import { connect, useDispatch, useSelector } from "react-redux";
import { useEffect, useState } from "react";
import {
  OPEN_DIALOG_SETTLEMENT_BANK_DETAILS,
  OPEN_DIALOGUE_SETTLEMENT,
} from "../../../store/settlement/settlement.type";
import {
  getAgencySettlement,
  paidSettlement,
} from "../../../store/settlement/settlement.action";
import SettlementActionDialogue from "./SettlementActionDialogue";
import { Tooltip } from "@mui/material";
import Pagination from "../../../pages/Pagination";
import {  warning } from "../../../util/Alert";
import SettlementBankDetailsDialogue from "./SettlementBankDetailsDialogue";

const SettlementPending = (props) => {
  const {
    agencySettlement,
    historyCount,
    dialog: open,
    dialogData,
    dialogType,
  } = useSelector((state) => state.settlement);
  const dispatch = useDispatch();
  const [data, setData] = useState([]);

  const [startDate, setStartDate] = useState("ALL");
  const [endDate, setEndDate] = useState("ALL");
  const [currentPage, setCurrentPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [search, setSearch] = useState("");

  const type = 1;
  useEffect(() => {
    const payload = {
      startDate,
      endDate,
      type,
      currentPage,
      rowsPerPage,
      search,
    };
    dispatch(getAgencySettlement(payload));
  }, [startDate, endDate, type, currentPage, rowsPerPage, search]);

  useEffect(() => {
    setData(agencySettlement);
  }, [agencySettlement]);

  const handlePayment = (data) => {


    dispatch({ type: OPEN_DIALOG_SETTLEMENT_BANK_DETAILS, payload: data });
  };

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const handleRowsPerPage = (value) => {
    setCurrentPage(1);
    setRowsPerPage(value);
  };

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-white">Agency Pending Settlement</h3>
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
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-8 float-left"></div>
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
                        onChange={(e) => {
                          if (e.target.value.length >= 0) {
                            setSearch(e.target.value);
                            setCurrentPage(1);
                          }
                        }}
                        onKeyPress={(e) => {
                          if (e.key === "Enter") {
                            e.preventDefault();
                            setSearch(e.target.value);
                            setCurrentPage(1);
                          }
                        }}
                      />
                      <div className="input-group-prepend border-0">
                        <div
                          id="button-addon4"
                          className="btn text-danger"
                          onClick={() => {
                            // Use setSearch with the value of the input field
                            setSearch(
                              document.getElementById("searchBar").value
                            );
                            setCurrentPage(1);
                          }}
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
              <table className="table table-striped">
                <thead className="text-center">
                  <tr>
                    <th>No.</th>
                    <th>Agency Name</th>
                    <th>Agencycode</th>
                    <th style={{ width: "250px" }}> Date</th>
                    <th>Agency Coin</th>
                    <th>Commission</th>
                    <th>Commission (%)</th>
                    <th>Bonus/Penalty</th>
                    <th>Total Coin</th>
                    {/* <th>Total Amount</th> */}
                    <th>Amount ($)</th>
                    <th>Pay</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody className="text-center">
                  {data?.length > 0 ? (
                    data.map((value, agencyIndex) => (
                      <tr key={`${agencyIndex}`}>
                        <td>
                          {(currentPage - 1) * rowsPerPage + agencyIndex + 1}
                        </td>
                        <td>{value?.agency?.name}</td>
                        <td>{value?.agency?.agencyCode}</td>
                        <td>
                          {value?.startDate} To {value?.endDate}
                        </td>

                        <td>{value?.coinEarned}</td>
                        <td>{value?.commissionCoinEarned}</td>
                        <td>{value?.agencyCommissionPercentage}</td>
                        <td>{value?.bonusOrPenltyAmount}</td>
                        {/* <td>{value?.totalCoinEarned || 0}</td> */}
                        <td>{value?.finalAmountTotal}</td>
                        <td>{value?.dollar}</td>
                        <td>
                          <Tooltip title="Info">
                            <button
                              type="button"
                              className="btn btn-sm btn-info"
                              onClick={() => handlePayment(value)}
                            >
                              Pay
                            </button>
                          </Tooltip>
                        </td>
                        <td>
                          <Tooltip title="Info">
                            <button
                              type="button"
                              className="btn btn-sm btn-info"
                              onClick={() =>
                                dispatch({
                                  type: OPEN_DIALOGUE_SETTLEMENT,
                                  payload: {
                                    data: value,
                                    id: value._id,
                                    type: "settlementActionDialogue",
                                  },
                                })
                              }
                            >
                              Action
                            </button>
                          </Tooltip>
                        </td>
                      </tr>
                    ))
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
                activePage={currentPage}
                rowsPerPage={rowsPerPage}
                userTotal={historyCount}
                handleRowsPerPage={handleRowsPerPage}
                handlePageChange={handlePageChange}
              />
              <SettlementActionDialogue />
              <SettlementBankDetailsDialogue />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default connect(null, { getAgencySettlement, paidSettlement })(
  SettlementPending
);
