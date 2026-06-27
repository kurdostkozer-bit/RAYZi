import React, { useEffect, useState } from 'react';

//redux
import { connect, useDispatch, useSelector } from 'react-redux';

//action
import {
  getCoinPlan,
  deleteCoinPlan,
  isTop,
} from '../../store/coinPlan/action';

//routing
import { Link } from 'react-router-dom';

//MUI
import { TablePagination, Tooltip } from '@mui/material';

// type
import { OPEN_COIN_PLAN_DIALOG } from '../../store/coinPlan/types';

// dialog
import CoinPlanDialog from '../dialog/CoinPlan';

//sweet alert
import { alert, warning } from '../../util/Alert';
import Pagination from '../../pages/Pagination';

const TablePaginationActions = React.lazy(() => import('./TablePagination'));

const CoinPlanTable = (props) => {
  const dispatch = useDispatch();
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [activePage, setActivePage] = useState(1);



  useEffect(() => {
    dispatch(getCoinPlan());
  }, [dispatch]);

  const coinPlan = useSelector((state) => state.coinPlan.coinPlan);

  useEffect(() => {
    setData(coinPlan);
  }, [coinPlan]);

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
      const data = coinPlan.filter((data) => {
        return (
          data?.tag?.toUpperCase()?.indexOf(value) > -1 ||
          data?.dollar?.toString()?.indexOf(value) > -1 ||
          data?.rupee?.toString()?.indexOf(value) > -1 ||
          data?.diamonds?.toString()?.indexOf(value) > -1
        );
      });
      setData(data);
    } else {
      return setData(coinPlan);
    }
  };

  const handleOpen = () => {


    dispatch({ type: OPEN_COIN_PLAN_DIALOG });
  };

  const handleDelete = (planId) => {

    const data = warning();
    data
      .then((isDeleted) => {
        if (isDeleted) {
      
          props.deleteCoinPlan(planId);
          alert('Deleted!', `Plan has been deleted!`, 'success');
        }
      })
      .catch((err) => console.log(err));
  };

  const handleEdit = (data) => {


    dispatch({ type: OPEN_COIN_PLAN_DIALOG, payload: data });
  };

  const handleIsTop = (id) => {


    props.isTop(id);
  };

  return (
    <>
      <div className="page-title">
        {props.type !== 'coinPlan' && (
          <div className="row">
            <div className="col-12 col-md-6 order-md-1 order-last">
              <h3 className="mb-3 text-white">Coin Plan</h3>
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
                    Plan
                  </li>
                </ol>
              </nav>
            </div>
          </div>
        )}
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
                <thead className="text-center">
                  <tr>
                    <th>No.</th>
                    <th>Diamonds</th>
                    <th>Dollar</th>
                    <th>Rupee</th>
                    <th>Tag</th>
                    <th>isTop</th>
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
                          <td>{data.diamonds}</td>
                          <td>{data.dollar}</td>
                          <td>{data.rupee}</td>
                          <td>{data.tag ? data.tag : '-'}</td>
                          <td>
                            <label className="switch">
                              <input
                                type="checkbox"
                                checked={data.isTop}
                                onChange={() => handleIsTop(data._id)}
                              />
                              <span className="slider">
                                <p
                                  style={{
                                    fontSize: 12,
                                    marginLeft: `${
                                      data.isTop ? '-24px' : '35px'
                                    }`,
                                    color: '#000',
                                    marginTop: '6px',
                                  }}
                                >
                                  {data.isTop ? 'Yes' : 'No'}
                                </p>
                              </span>
                            </label>
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
                userTotal={coinPlan.length}
                handleRowsPerPage={handleRowsPerPage}
                handlePageChange={handlePageChange}
              />
            </div>
          </div>
        </div>
      </div>
      <CoinPlanDialog />
    </>
  );
};

export default connect(null, { getCoinPlan, deleteCoinPlan, isTop })(
  CoinPlanTable
);
