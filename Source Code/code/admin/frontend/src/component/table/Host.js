import React, { useEffect, useState } from 'react';

//dayjs
import dayjs from 'dayjs';

//jquery
import $ from 'jquery';

//redux
import { connect, useDispatch, useSelector } from 'react-redux';

//action
import { getUser, handleBlockUnblockSwitch } from '../../store/user/action';

//routing
import { Link, useNavigate } from 'react-router-dom';

//MUI
import { Tooltip } from '@mui/material';

// import arraySort from "array-sort";

//image
import Male from '../../assets/images/male.png';

//pagination
import Pagination from '../../pages/Pagination';

//Date Range Picker
import { DateRangePicker } from 'react-date-range';
//Calendar Css
import 'react-date-range/dist/styles.css'; // main style file
import 'react-date-range/dist/theme/default.css'; // theme css file

//MUI icon
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { getHost } from '../../store/host/action';

const Host = (props) => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const [data, setData] = useState([]);

  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [search, setSearch] = useState('ALL');

  useEffect(() => {
    dispatch(
        getHost(activePage, rowsPerPage, search)
    );
  }, [dispatch, activePage, rowsPerPage]);

  const { host ,total} = useSelector(
    (state) => state.host
  );
  
  useEffect(() => {
    setData(host);
  }, [host]);

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
    sessionStorage.setItem('user', JSON.stringify(user));
    navigate('/admin/user/detail');
  };
  const handleUserHistory = (user) => {
    sessionStorage.setItem('user', JSON.stringify(user));
    navigate('/admin/user/history');
  };

  // set default image

  $(document).ready(function () {
    $('img').bind('error', function () {
      // Set the default image
      $(this).attr('src', Male);
    });
  });

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-light" style={{ color: '#e4eeff' }}>
              Host
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
                  Host
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
                          }
                        }}
                        onKeyPress={(e) => {
                          if (e.key === 'Enter') {
                            e.preventDefault();
                            dispatch(
                                getHost(
                                activePage,
                                rowsPerPage,
                                search,

                              )
                            );
                            setActivePage(1);
                          }
                        }}
                      />
                      <div
                        className="input-group-prepend border-0"
                        htmlFor="searchBar"
                        onClick={() => {
                          // Use setSearch with the value of the input field
                          setSearch(document.getElementById('searchBar').value);
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
                    <th>Image</th>
                    <th>Name</th>
                    <th>UniqueId</th>
                    <th>Gender</th>
                    <th>RCoin</th>
                    <th>Country</th>
                    <th>Level</th>
                    <th>isBlock</th>
                    <th>Agency</th>
                    <th>Info</th>
                    <th>History</th>
                  </tr>
                </thead>
                <tbody>
                  {data?.length > 0 ? (
                    data?.map((data, index) => {
                      return (
                        <tr key={index}>
                          <td>{index + 1}</td>
                          <td>
                            <img
                              height="50px"
                              width="50px"
                              alt="app"
                              src={data.image ? data.image : Male}
                              style={{
                                boxShadow: '0 5px 15px 0 rgb(105 103 103 / 0%)',
                                border: '2px solid #fff',
                                borderRadius: 10,
                                objectFit: 'cover',
                                display: 'block',
                              }}
                              className="mx-auto"
                            />
                          </td>
                          <td>{data.name ? data.name : '-'}</td>
                          <td>{data.uniqueId ? data.uniqueId : '-'}</td>
                          <td>{data.gender ? data.gender : '-'}</td>
                          <td className="text-danger">
                            {data.rCoin ? data.rCoin : '0'}
                          </td>
                          <td className="text-success">
                            {data.country ? data.country : '-'}
                          </td>
                          <td className="text-warning">
                            {data.level.name ? data.level.name : '-'}
                          </td>
                          <td>
                            <label className="switch">
                              <input
                                type="checkbox"
                                checked={data.isBlock}
                                onChange={() =>
                                  handleBlockUnblockSwitch_(data._id)
                                }
                              />
                              <span className="slider">
                                <p
                                  style={{
                                    fontSize: 12,
                                    marginLeft: `${
                                      data.isBlock ? '-24px' : '35px'
                                    }`,
                                    color: '#000',
                                    marginTop: '6px',
                                  }}
                                >
                                  {data.isBlock ? 'Yes' : 'No'}
                                </p>
                              </span>
                            </label>
                          </td>
                          <td>
                            {data?.hostAgency?.name ? data?.hostAgency?.name : '-'}
                          </td>
                          <td>
                            <Tooltip title="Info">
                              <button
                                type="button"
                                className="btn btn-sm btn-info"
                                onClick={() => handleUserInfo(data)}
                              >
                                <i className="fas fa-info-circle fa-lg"></i>
                              </button>
                            </Tooltip>
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
                      <td colSpan="12" align="center">
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
    </>
  );
};

export default Host
