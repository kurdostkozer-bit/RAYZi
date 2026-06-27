import { Tooltip } from "@mui/material";
import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Pagination from "../pages/Pagination";
import $ from "jquery";
import Male from "../assets/images/male.png";
import { connect, useDispatch, useSelector } from "react-redux";
import {
  getCoinSeller,
  showCoinSeller,
  deleteCoinSeller,
} from "../store/coinSeller/action";
import {
  ADD_MOBILE_OPEN_DIALOGUE,
  ADD_MONEY_OPEN_DIALOGUE,
  LESS_MONEY_OPEN_DIALOGUE,
  OPEN_COINSELLER_DIALOGUE,
} from "../store/coinSeller/type";
import CoinSellerAdd from "../component/dialog/CoinSellerAdd";
import dayjs from "dayjs";
import CoinSellerAddCoin from "../component/dialog/CoinSellerAddCoin";
import { baseURL } from "../util/Config";
import CoinSellerLessCoin from "../component/dialog/CoinSellerLessCoin";
import MobileNumberModel from "../component/dialog/MobileNumberModel";


const CoinSeller = (props) => {
  const { coinSeller, total } = useSelector((state) => state.coinSeller);
  const [data, setData] = useState([]);
  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(20);
  const [search, setSearch] = useState("");


  const dispatch = useDispatch();

  useEffect(() => {
    props.getCoinSeller(activePage, rowsPerPage);
  }, [activePage, rowsPerPage]);

  useEffect(() => {
    setData(coinSeller);
  }, [coinSeller]);

  useEffect(() => {
    handleSearch();
  }, [search, coinSeller]);

  const handleBlockUnblockSwitch_ = (data) => {
    props.liveCut(data.liveStreamingId, data?.liveUserId?._id, data?.username);
  };

  const navigate = useNavigate();

  const handleUserInfo = (user) => {
    navigate({ pathname: "/admin/user/detail", state: user });
  };

  const handleUserHistory = (user) => {
    navigate("/admin/coinSeller/history", { state: user });
  };

  $(document).ready(function () {
    $("img").bind("error", function () {
      $(this).attr("src", Male);
    });
  });

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };

  const handleSearch = () => {
    const value = search.trim().toLowerCase();

    if (value) {
      const filteredData = coinSeller.filter((data) => {
        return (
          data?.user?.name?.toLowerCase().includes(value) ||
          data?.uniqueId?.toString().includes(value) ||
          data?.coin?.toString().includes(value) ||
          data?.spendCoin?.toString().includes(value)
        );
      });
      setData(filteredData);
    } else {
      setData(coinSeller);
    }
  };

  const handleOpen = () => {

    dispatch({ type: OPEN_COINSELLER_DIALOGUE });
  };

  const handleEdit = (data) => {
    dispatch({ type: OPEN_COINSELLER_DIALOGUE, payload: data });
  };

  const handleShow_ = (value) => {
    props.showCoinSeller(value);
  };

  const handleDisable = (value) => {


    props.deleteCoinSeller(value);
  };

  const handleGiveCoin = (value) => {

    dispatch({ type: ADD_MONEY_OPEN_DIALOGUE, payload: value });
  };

  const handleLessCoin = (value) => {


    dispatch({ type: LESS_MONEY_OPEN_DIALOGUE, payload: value });
  };

  const handleGiveMobile = (value) => {


    dispatch({ type: ADD_MOBILE_OPEN_DIALOGUE, payload: value });
  };

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3" style={{ color: "#e4eeff" }}>
              Coin Seller
            </h3>
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
                  CoinSeller
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
                  <button
                    type="button"
                    className="btn waves-effect waves-light btn-danger btn-sm float-left"
                    onClick={handleOpen}
                    id="CoinSellerAdd"
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
                      <div
                        className="input-group-prepend border-0"
                        onClick={handleSearch}
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
                    <th>Image</th>
                    <th>Name</th>
                    <th>Unique Id</th>
                    <th>Coin</th>
                    <th>Spend Coin</th>
                    <th>Mobile Number</th>
                    <th>Created At</th>
                    <th>Give Coin</th>
                    <th>Less Coin</th>
                    <th>Is Active</th>
                    <th>History</th>
                  </tr>
                </thead>
                <tbody>
                  {data.length > 0 ? (
                    data.map((data, index) => {
                      return (
                        <tr key={index}>
                          <td>{(activePage - 1) * rowsPerPage + index + 1}</td>
                          <td>
                            <img
                              height="50px"
                              width="50px"
                              alt="app"
                              src={data?.user?.image ? data?.user?.image : ""}
                              style={{
                                boxShadow: "0 5px 15px 0 rgb(105 103 103 / 0%)",
                                border: "2px solid #fff",
                                borderRadius: 10,
                                float: "left",
                                objectFit: "cover",
                              }}
                            />
                          </td>
                          <td>{data?.user?.name ? data?.user?.name : "-"}</td>
                          <td>{data?.user?.uniqueId}</td>
                          <td>{data?.coin}</td>
                          <td>{data?.spendCoin}</td>
                          <td>
                            <div
                              className="showEditNumber"
                              style={{
                                display: "flex",
                                justifyContent: "center",
                                alignItems: "center",
                              }}
                            >
                              <span>
                                {data?.mobileNumber
                                  ? (data?.countryCode
                                      ? "+" + data?.countryCode + " "
                                      : "") + data?.mobileNumber
                                  : "-"}
                              </span>
                              <Tooltip title="Mobile Number">
                                <i
                                  className="fa fa-pen fa-lg text-primary"
                                  style={{
                                    marginLeft: "16px",
                                    fontSize: "14px",
                                    cursor: "pointer",
                                  }}
                                  onClick={() => handleGiveMobile(data)}
                                ></i>
                              </Tooltip>
                            </div>
                          </td>
                          <td>
                            {dayjs(data?.createdAt).format("DD MMM YYYY")}
                          </td>
                          <td>
                            <Tooltip title="Give Coin">
                              <button
                                type="button"
                                className="btn btn-sm btn-info"
                                onClick={() => handleGiveCoin(data?._id)}
                              >
                                <i className="fa fa-edit fa-lg"></i>
                              </button>
                            </Tooltip>
                          </td>
                          <td>
                            <Tooltip title="Give Coin">
                              <button
                                type="button"
                                className="btn btn-sm btn-info"
                                onClick={() => handleLessCoin(data?._id)}
                              >
                                <i className="fa fa-edit fa-lg"></i>
                              </button>
                            </Tooltip>
                          </td>

                          <td>
                            <label className="switch">
                              <input
                                type="checkbox"
                                checked={data?.isActive}
                                onChange={() => handleDisable(data?._id)}
                              />
                              <span className="slider">
                                <p
                                  style={{
                                    fontSize: 12,
                                    marginLeft: `${
                                      data?.isActive ? "-22px" : "35px"
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
                          <td>
                            <Tooltip title="History">
                              <button
                                type="button"
                                className="btn btn-sm btn-success"
                                onClick={() => handleUserHistory(data)}
                              >
                                <i className="fas fa-history fa-lg"></i>
                              </button>
                            </Tooltip>
                          </td>
                        </tr>
                      );
                    })
                  ) : (
                    <tr>
                      <td colSpan="20" align="center">
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
      <CoinSellerAdd />
      <CoinSellerAddCoin />
      <CoinSellerLessCoin />
      <MobileNumberModel />
    </>
  );
};

export default connect(null, {
  getCoinSeller,
  deleteCoinSeller,
  showCoinSeller,
})(CoinSeller);
