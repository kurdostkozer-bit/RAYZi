import { connect, useDispatch, useSelector } from "react-redux";
import { allAgencySettlement } from "../../../store/settlement/settlement.action";
import { useEffect, useState } from "react";
// import Table from "../../extra/Table";
// import Pagination from "../../extra/Pagination";
// import Searching from "../../extra/Searching";
// import Title from "../../extra/Title";
// import Button from "../../extra/Button";
import AgencyWeekSettlement from "./AgencyWeekSettlement";
import { OPEN_DIALOGUE_SETTLEMENT } from "../../../store/settlement/settlement.type";
import SettlementActionDialogue from "./SettlementActionDialogue";
import BonusInfo from "./BonusInfo";
import { Link, useNavigate  } from "react-router-dom";
import dayjs from "dayjs";
import { Tooltip } from "@mui/material";
import Pagination from "../../../pages/Pagination";

const AllSettlement = (props) => {
  // const { dialogue, dialogueType, dialogueData } = useSelector(
  //   (state) => state.dialogue
  // );
  const dispatch = useDispatch();
  const { settlement } = useSelector((state) => state.settlement);
  const [data, setData] = useState([]);
  const [startDate, setStartDate] = useState("ALL");
  const [endDate, setEndDate] = useState("ALL");
  const [page, setPage] = useState(1);
  const [activePage, setActivePage] = useState(20);
  const [search, setSearch] = useState("");
  const navigate = useNavigate();

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleOpen = () => {
    dispatch({ type: OPEN_DIALOGUE_SETTLEMENT });
  };

  const handleChangeRowsPerPage = (event) => {
    setActivePage(parseInt(event, 10));
    setPage(0);
  };

  useEffect(() => {
    dispatch(allAgencySettlement(page, activePage));
  }, [page, activePage]);

  useEffect(() => {
    setData(settlement);
  }, [settlement]);

  const handleFilterData = (filteredData) => {
    if (typeof filteredData === "string") {
      setSearch(filteredData);
    } else {
      setData(filteredData);
    }
  };
  const handleProfile = (row, type) => {
    navigate("/admin/agencyWeekSettlement", {
      state: { id: row?._id, data: row },
    });
  };

  const handleSearch = () => {
    const value = search.trim().toLowerCase();

    if (value) {
      const filteredData = settlement.filter((data) => {
        return (
          data?.name?.toLowerCase().includes(value) ||
          data?.uniqueId?.toString().includes(value) ||
          data?.agencyCode?.toString().includes(value) ||
          data?.totalCoin?.toString().includes(value)
        );
      });
      setData(filteredData);
    } else {
      setData(settlement);
    }
  };

  const handleUserInfo = (user) => {
    // sessionStorage.setItem("user", JSON.stringify(user));
    navigate("/admin/weeklySettlement");
  };

 

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-white">Agency Settlement</h3>
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
                    <th>Start Date</th>
                    <th>End Date</th>
                    <th>Name</th>
                    <th>Rcoin</th>
                    {/* <th>Penalty/Bonus</th>/ */}
                    <th>Total Coin</th>
                    <th>Total Amount </th>
                    {/* <th>Redeem Enable</th> */}
                    <th>Amount</th>
                    <th>Info</th>
                  </tr>
                </thead>
                <tbody className="text-center">
                  {data?.length > 0 ? (
                    data.map((item, dataIndex) =>
                      item?.agency?.map((value, agencyIndex) => (
                        <tr key={`${dataIndex}-${agencyIndex}`}>
                          <td>
                            {(page - 1) * page + dataIndex + 1}
                          </td>
                          <td>{value?.startDate}</td>
                          <td>{value?.endDate}</td>
                          <td>{value?.agencyId?.name}</td>
                          <td>{value?.coinEarned}</td>

                          {/* <td>{value?.bonusOrPenltyAmount}</td> */}
                          <td>
                            {value?.totalCoinEarned
                              ? value?.totalCoinEarned
                              : 0}
                          </td>
                          <td>{value?.finalAmountTotal}</td>
                          <td>{value?.amount}</td>
                          <td>
                            <Tooltip title="Info">
                              <button
                                type="button"
                                className="btn btn-sm btn-info"
                                onClick={() => handleUserInfo(item)}
                              >
                                <i className="fas fa-info-circle fa-lg"></i>
                              </button>
                            </Tooltip>
                          </td>
                        </tr>
                      ))
                    )
                  ) : (
                    <tr>
                      <td colSpan="12" align="center">
                        Nothing to show!!
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
              {/* <Pagination
                  activePage={activePage}
                  rowsPerPage={page}
                  userTotal={total}
                  handleRowsPerPage={handleChangePage}
                  handlePageChange={handleChangePage}
                /> */}
            </div>
            {/* <BonusInfo /> */}
          </div>
        </div>
      </div>
    </>
  );
};

export default connect(null, { allAgencySettlement })(AllSettlement);
