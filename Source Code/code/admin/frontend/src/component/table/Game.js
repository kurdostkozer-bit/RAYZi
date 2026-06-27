import React, { useEffect, useState } from "react";

//redux
import { connect, useDispatch, useSelector } from "react-redux";

//action
import { getGame, deleteGame } from "../../store/game/action";

//config
import { baseURL } from "../../util/Config";

//routing
import { Link } from "react-router-dom";

//MUI
import { TablePagination, Tooltip } from "@mui/material";

// type

//sweet alert
import { alert, warning } from "../../util/Alert";

//image
import noImage from "../../assets/images/noImage.png";
import dayjs from "dayjs";
import GameDialog from "../dialog/GameDialog";
import { OPEN_GAME_DIALOG } from "../../store/game/types";
import Pagination from "../../pages/Pagination";

const TablePaginationActions = React.lazy(() => import("./TablePagination"));

const GameTable = (props) => {
  const dispatch = useDispatch();
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
    const [activePage, setActivePage] = useState(1);
  

  const settingId = useSelector((state) => state.game.settingId);

  useEffect(() => {
    dispatch(getGame());
  }, [dispatch]);

  const game = useSelector((state) => state.game.game);

  useEffect(() => {
    setData(game);
  }, [game]);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleOpen = () => {

    dispatch({ type: OPEN_GAME_DIALOG });
  };

  const handleDelete = (id) => {


    const data = warning();
    data
      .then((isDeleted) => {
        if (isDeleted) {
      
          props.deleteGame(settingId, id);
          alert("Deleted!", `Game has been deleted!`, "success");
        }
      })
      .catch((err) => console.log(err));
  };

  const handleEdit = (data) => {


    dispatch({ type: OPEN_GAME_DIALOG, payload: data });
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
            <h3 className="mb-3 text-light">Game</h3>
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
                  Game
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

              <table className="table table-striped text-center">
                <thead>
                  <tr>
                    <th>No.</th>
                    <th>Image</th>
                    <th>Name</th>
                    <th>Link</th>
                    <th>Min win Percent</th>
                    <th>Max win Percent</th>
                    <th>Created At</th>
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
                          <td
                            style={{
                              display: "flex",
                              alignItems: "center",
                              justifyContent: "center",
                            }}
                          >
                            <img
                              height="50px"
                              width="50px"
                              alt="app"
                              src={data.image ? data.image : noImage}
                              style={{
                                boxShadow: "0 5px 15px 0 rgb(105 103 103 / 0%)",
                                border: "2px solid #fff",
                                borderRadius: 10,
                                float: "left",
                              }}
                            />
                          </td>
                          <td>{data?.name}</td>
                          <td style={{ textAlign: "left" }}>
                            <a
                              target="_blank"
                              href={data?.link}
                              style={{ color: "#9a9cab" }}
                            >
                              {data?.link}
                            </a>
                          </td>
                          <td>{data?.minWinPercent + " %"}</td>
                          <td>{data?.maxWinPercent + " %"}</td>

                          <td>
                            {dayjs(data?.createdAt).format("DD MMM,YYYY")}
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
                userTotal={data?.length}
                handleRowsPerPage={handleRowsPerPage}
                handlePageChange={handlePageChange}
              />
              {/* <TablePagination
                id="pagination"
                component="div"
                rowsPerPageOptions={[
                  5,
                  10,
                  25,
                  100,
                  { label: "All", value: -1 }
                ]}
                count={data?.length}
                rowsPerPage={rowsPerPage}
                page={page}
                SelectProps={{
                  inputProps: { "aria-label": "rows per page" },
                  native: true,
                }}
                classes="menuItem"
                onChangePage={handleChangePage}
                onChangeRowsPerPage={handleChangeRowsPerPage}
                ActionsComponent={TablePaginationActions}
              /> */}
            </div>
          </div>
        </div>
      </div>
      <GameDialog />
    </>
  );
};

export default connect(null, { getGame, deleteGame })(GameTable);
