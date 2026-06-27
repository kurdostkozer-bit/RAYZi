import React, { useEffect, useState } from "react";

//dayjs
import dayjs from "dayjs";

//jquery
import $ from "jquery";

//redux
import { connect, useDispatch, useSelector } from "react-redux";

//action
import {
  getAgencyWiseHost,
  handleBlockUnblockSwitch,
  redeemEnableHost,
} from "../store/agency/action";

//routing
import { Link, useNavigate  } from "react-router-dom";

//MUI
import { Tooltip } from "@mui/material";

// import arraySort from "array-sort";

//image
import Male from "../assets/images/male.png";

//pagination
import Pagination from "../pages/Pagination";

//Date Range Picker
import { DateRangePicker } from "react-date-range";
//Calendar Css
import "react-date-range/dist/styles.css"; // main style file
import "react-date-range/dist/theme/default.css"; // theme css file

//MUI icon
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { useLocation } from "react-router-dom";

const AgencyWiseHost = (props) => {
  const navigate = useNavigate();
  const maxDate = new Date();
  const dispatch = useDispatch();

  // const [coinSort, setCoinSort] = useState(true);
  // const [followerSort, setFollowerSort] = useState(true);
  // const [followingSort, setFollowingSort] = useState(true);
  const [data, setData] = useState([]);

  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [search, setSearch] = useState("ALL");

  const [date, setDate] = useState([]);
  const [sDate, setsDate] = useState("ALL");
  const [eDate, seteDate] = useState("ALL");
  const location = useLocation();

  useEffect(() => {
    $("#card").click(() => {
      $("#datePicker");
    });
  }, []);

  useEffect(() => {
    dispatch(
      getAgencyWiseHost(location?.state?._id, activePage, rowsPerPage, search)
    );
  }, [dispatch, activePage, rowsPerPage, search]);

  const { agencyWiseHost, totalAgencyWiseHost } = useSelector(
    (state) => state.agency
  );
  useEffect(() => {
    setData(agencyWiseHost);
  }, [agencyWiseHost]);

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };

  const handleBlockUnblockSwitch_ = (userId) => {
    props.handleBlockUnblockSwitch(userId);
  };

  const handleUserInfo = (user) => {
    sessionStorage.setItem("user", JSON.stringify(user));
    navigate("/admin/user/detail");
  };
  const handleUserHistory = (user) => {
    sessionStorage.setItem("user", JSON.stringify(user));
    navigate("/admin/user/history");
  };

  const getAllUser = () => {
    setActivePage(1);
    setsDate("ALL");
    seteDate("ALL");
    $("#datePicker");
    dispatch(getAgencyWiseHost(activePage, rowsPerPage, sDate, eDate));
  };

  const collapsedDatePicker = () => {
    $("#datePicker").toggleClass("collapse");
  };

  const handleEnabledRedeem = (id) => {
    dispatch(redeemEnableHost(id));
  };

  // set default image

  $(document).ready(function () {
    $("img").bind("error", function () {
      // Set the default image
      $(this).attr("src", Male);
    });
  });

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
          <button
              className="btn btn-danger custom-btn"
              onClick={() => navigate(-1)}
            >
              <i className="fas fa-chevron-left"></i> Go Back
            </button>
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
                <li className="breadcrumb-item active " aria-current="page">
                  Agency
                </li>
                <li className="breadcrumb-item active " aria-current="page">
                  Agency Wise Users
                </li>
              </ol>
            </nav>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col">
          <div className="card" id="card">
            <div className="card-header pb-0">
              <div className="row my-3">
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-8 float-left">
                  <h6
                    style={{
                      color: "#7d7f8c",
                      textTransform: "capitalize",
                      fontSize: "20px",
                    }}
                  >
                    {`${location?.state?.name}'s Host`}{" "}
                  </h6>
                </div>
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-4 float-right">
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
                            setActivePage(1);
                          }
                        }}
                        onKeyPress={(e) => {
                          if (e.key === "Enter") {
                            e.preventDefault();
                            setSearch(e.target.value);
                            setActivePage(1);
                          }
                        }}
                      />
                      <div
                        className="input-group-prepend border-0"
                        htmlFor="searchBar"
                        onClick={() => {
                          // Use setSearch with the value of the input field
                          setSearch(document.getElementById("searchBar").value);
                          setActivePage(1);
                        }}
                      >
                        <div id="button-addon4" className="btn text-danger">
                          <i className="fas fa-search mt-2"></i>
                        </div>
                      </div>
                    </div>
                  </form>
                </div>
              </div>
            </div>
            <div className="card-body card-overflow pt-0">
              <table className="table table-striped mt-5 text-center">
                <thead>
                  <tr>
                    <th>No.</th>
                    <th>User</th>
                    <th>Unique Id</th>
                    <th>Mobile Number</th>
                    <th>Age</th>

                    {/* <th onClick={handleCoinSort} style={{ cursor: "pointer" }}>
                      RCoin {coinSort ? " ▼" : " ▲"}
                    </th> */}
                    <th>Diamond</th>
                    <th>Coin</th>
                    <th>Country</th>
                    <th>Level</th>
                    <th>Follower</th>

                    <th>Following</th>
                    {/* <th>Redeem Enable</th> */}
                  </tr>
                </thead>
                <tbody>
                  {data?.length > 0 ? (
                    data?.map((data, index) => {
                      return (
                        <tr key={index}>
                          <td>{(activePage - 1) * rowsPerPage + index + 1}</td>
                          <td className="d-flex">
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
                            <span className="d-flex align-items-center">
                              {data?.name ? data?.name : "-"}
                            </span>
                          </td>

                          <td>{data?.uniqueId ? data?.uniqueId : "-"}</td>
                          <td>{data?.mobileNumber ? data?.mobileNumber : "-"}</td>
                          <td>{data?.age ? data?.age : "-"}</td>
                          <td className="text-info">
                            {data?.diamond ? data?.diamond : "-"}
                          </td>
                          <td className="text-danger">
                            {data?.rCoin ? data?.rCoin : "0"}
                          </td>
                          <td className="text-success">
                            {data?.country ? data?.country : "-"}
                          </td>
                          <td className="text-warning">
                            {data?.level?.name ? data?.level?.name : "-"}
                          </td>
                          <td>{data?.followers ? data?.followers : "0"}</td>
                          <td>{data?.following ? data?.following : "0"}</td>
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
                        </tr>
                      );
                    })
                  ) : (
                    <tr>
                      <td colSpan="16" align="center">
                        Nothing to show!!
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>

              <Pagination
                activePage={activePage}
                rowsPerPage={rowsPerPage}
                userTotal={totalAgencyWiseHost}
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

export default connect(null, { getAgencyWiseHost, handleBlockUnblockSwitch })(
  AgencyWiseHost
);
