import React, { useEffect, useState } from 'react';

//redux
import { connect, useDispatch, useSelector } from "react-redux";

//action
import {
  getBanner,
  deleteBanner,
} from "../../store/banner/action";

//config
import { baseURL } from "../../util/Config"

//routing
import { Link } from "react-router-dom";

//MUI
import { TablePagination, Tooltip } from "@mui/material";

// type
import { OPEN_BANNER_DIALOG } from "../../store/banner/types";

// dialog
import BannerDialog from "../dialog/Banner";

//sweet alert
import { alert,  warning } from "../../util/Alert";

import $ from 'jquery'
//image
import noImage from "../../assets/images/noImage.png";
import Pagination from "../../pages/Pagination";

const TablePaginationActions = React.lazy(() => import('./TablePagination'));

const BannerTable = (props) => {

  const dispatch = useDispatch();
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [activePage, setActivePage] = useState(1);
  const [totalBanner, setTotalBanner] = useState(null)

  
  useEffect(() => {
    dispatch(getBanner());
  }, [dispatch]);

  const banner = useSelector((state) => state.banner.banner);

  useEffect(() => {
    setData(banner);
    setTotalBanner(banner.length)
  }, [banner]);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };
  $(document).ready(function () {
    $("img").bind("error", function () {
      $(this).attr("src", noImage);
    });
  });

  const handleSearch = (e) => {
    const value = e.target.value.trim().toUpperCase();
    if (value) {
      const data = banner.filter((data) => {
        return data?.URL?.toUpperCase()?.indexOf(value) > -1;
      });
      setData(data);
    } else {
      return setData(banner);
    }
  };



  const handleOpen = () => {

    dispatch({ type: OPEN_BANNER_DIALOG });
  };

  const handleDelete = (bannerId) => {

    const data = warning();
    data
      .then((isDeleted) => {
        if (isDeleted) {
          
          props.deleteBanner(bannerId);
          alert("Deleted!", `Banner has been deleted!`, "success");
        }
      })
      .catch((err) => console.log(err));
  };

  const handleEdit = (data) => {

    dispatch({ type: OPEN_BANNER_DIALOG, payload: data });
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-white">Banner</h3>
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
                  Banner
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
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-4 float-right mt-3 mt-lg-0 mt-xl-0">
                  {/* <form action="">
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
                  </form> */}
                </div>
              </div>
            </div>
            <div className="card-body card-overflow">
              <div className="d-sm-flex align-items-center justify-content-between mb-4"></div>

              <table className="table table-striped">
                <thead className="text-center">
                  <tr>
                    <th>No.</th>
                    <th>Image</th>
                    <th>URL</th>
                  
                    <th>Edit</th>
                    <th>Delete</th>
                  </tr>
                </thead>
                <tbody className="text-center">
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
                          <td>
                            <img
                              height="70px"
                              width="100px"
                              alt="app"
                              src={data?.image ? baseURL + data?.image : noImage}
                              style={{
                                boxShadow: "0 5px 15px 0 rgb(105 103 103 / 0%)",
                                border: "2px solid #fff",
                                borderRadius: 10,
                                display: "block",
                              }}
                              className="mx-auto"
                            />
                          </td>
                          <td>
                            <Link to={data?.URL} target="_blank">
                              {data.URL ? data.URL : '-'}
                            </Link>
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
                                onClick={() => handleDelete(data._id)}
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
                      <td colSpan="6" align="center">
                        Nothing to show!!
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
              <Pagination
                activePage={activePage}
                rowsPerPage={rowsPerPage}
                userTotal={totalBanner}
                handleRowsPerPage={handleRowsPerPage}
                handlePageChange={handlePageChange}
              />
            </div>
          </div>
        </div>
      </div>
      <BannerDialog />
    </>
  );
};

export default connect(null, { getBanner, deleteBanner })(BannerTable);
