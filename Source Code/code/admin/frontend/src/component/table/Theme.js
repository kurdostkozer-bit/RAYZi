import React, { useEffect, useState } from "react";

//redux
import { connect, useDispatch, useSelector } from "react-redux";

//action
import { getTheme, deleteTheme } from "../../store/Theme/theme.action";

//config

//routing
import { Link } from "react-router-dom";

//MUI
// import { TablePagination, Tooltip } from "@mui/material";

// type
import { TablePagination, Tooltip } from "@mui/material";
import { OPEN_THEME_DIALOG } from "../../store/Theme/theme.type";
import { alert, warning } from "../../util/Alert";
// dialog
import ThemeDialog from "../../component/dialog/ThemeDialog";

//image
import noImage from "../../assets/images/noImage.png";
import dayjs from "dayjs";
import TablePaginationActions from "./TablePaginationActions";
import { Toast } from "../../util/Toast";
import { baseURL } from "../../util/Config";
import Pagination from "../../pages/Pagination";


const ThemeTable = (props) => {
  const dispatch = useDispatch();
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [activePage, setActivePage] = useState(1);



  useEffect(() => {
    dispatch(getTheme()); 
  }, [dispatch]);

  const theme = useSelector((state) => state.theme.theme);

  useEffect(() => {
    setData(theme);
  }, [theme]);

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
    setPage(pageNumber)
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };

  const handleOpen = () => {


    dispatch({ type: OPEN_THEME_DIALOG });
  };

  const handleDelete = (id) => {


    const data = warning();
    data
      .then((isDeleted) => {
        if (isDeleted) {
      
          props.deleteTheme(id);
          alert("Deleted!", `Theme has been deleted!`, "success");
        }
      })
      .catch((err) => console.log(err));
  };

  const handleEdit = (data) => {


    dispatch({ type: OPEN_THEME_DIALOG, payload: data });
  };

  return (
    <>
<div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-white">Theme</h3>
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
                Theme
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
                    id="StickerDialog"
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

              <table className="table table-striped">
                <thead>
                  <tr>
                    <th>No.</th>
                    <th>Image</th>
                    <th>Created At</th>
                    <th>Updated At</th>
                    <th>Edit</th>
                    <th>Delete</th>
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
                          <td style={{display : "flex",alignItems : "center", justifyContent : "center"}}>
                            <img
                              height="50px"
                              width="50px"
                              alt="app"
                              src={
                                data.theme ? baseURL + data.theme : noImage
                              }
                              style={{
                                boxShadow: "0 5px 15px 0 rgb(105 103 103 / 0%)",
                                border: "2px solid #fff",
                                borderRadius: 10,
                                objectFit:'cover',
                                float: "left",
                              }}
                            />
                          </td>
                          <td>{dayjs(data.createdAt).format("DD MMM,YYYY")}</td>
                          <td>{dayjs(data.updatedAt).format("DD MMM,YYYY")}</td>

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
                userTotal={theme.length}
                handleRowsPerPage={handleRowsPerPage}
                handlePageChange={handlePageChange}
              />
            </div>
          </div>
        </div>
      </div>
      <ThemeDialog />
    </>
  );
};

export default connect(null, { getTheme, deleteTheme })(ThemeTable);
