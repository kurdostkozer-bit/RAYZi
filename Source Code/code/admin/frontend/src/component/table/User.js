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

const UserTable = (props) => {
  const navigate = useNavigate();
  const maxDate = new Date();
  const dispatch = useDispatch();

  const [data, setData] = useState([]);

  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [search, setSearch] = useState('ALL');

  const [date, setDate] = useState(() => {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    const endOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);

    return [
      {
        startDate: startOfMonth,
        endDate: endOfMonth,
        key: 'selection',
      },
    ];
  });

  // const [sDate, setsDate] = useState(() => {
  //   const startOfMonth = new Date();
  //   startOfMonth.setDate(1);
  //   return dayjs(startOfMonth).format('YYYY/M/DD');
  // });

  // const [eDate, seteDate] = useState(() => {
  //   const endOfMonth = new Date();
  //   endOfMonth.setMonth(endOfMonth.getMonth() + 1, 0);
  //   return dayjs(endOfMonth).format('YYYY/M/DD');
  // });

  const [sDate, setsDate] = useState('ALL');
  const [eDate, seteDate] = useState('ALL');

  // Add formatted date variables
  const formattedStart =
    sDate === 'ALL' ? 'ALL' : dayjs(sDate).format('YYYY/M/DD');
  const formattedEnd =
    eDate === 'ALL' ? 'ALL' : dayjs(eDate).format('YYYY/M/DD');

  useEffect(() => {
    $('#card').click(() => {
      $('#datePicker');
    });
  }, []);

  useEffect(() => {
    dispatch(
      getUser(activePage, rowsPerPage, search, formattedStart, formattedEnd)
    );
  }, [dispatch, activePage, rowsPerPage]);

  const { user, activeUser, male, female, totalUser } = useSelector(
    (state) => state.user
  );
  useEffect(() => {
    setData(user);
  }, [user]);

  useEffect(() => {
    if (date.length === 0) {
      setDate([
        {
          startDate: new Date(),
          endDate: new Date(),
          key: 'selection',
        },
      ]);
    }
    $('#datePicker');
    setData(user);
  }, [date, user]);

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

  const getAllUser = () => {
    setActivePage(1);
    setRowsPerPage(10);
    setSearch('ALL');
    setsDate('ALL');
    seteDate('ALL');
    $('#datePicker').css('display', 'none');
    dispatch(getUser(activePage, rowsPerPage, search, sDate, eDate));
  };

  const collapsedDatePicker = () => {
    const datePicker = $('#datePicker');
    if (datePicker.css('display') === 'none') {
      datePicker.css('display', 'block');
    } else {
      datePicker.css('display', 'none');
    }
  };

  // set default image

  $(document).ready(function () {
    $('img').bind('error', function () {
      // Set the default image
      $(this).attr('src', Male);
    });
  });

  const handleDateFilter = () => {
    setActivePage(1);
    $('#datePicker').css('display', 'none');
    dispatch(
      getUser(activePage, rowsPerPage, search, formattedStart, formattedEnd)
    );
    setsDate('ALL');
    seteDate('ALL');
  };

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-light" style={{ color: '#e4eeff' }}>
              User
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
                  User
                </li>
              </ol>
            </nav>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col-lg-12 col-md-2 col-sm-12">
          <div className="row">
            <div className="col-lg-4">
              <div className="card stats-card">
                <div className="card-body">
                  <div className="stats-info">
                    <h5 className="card-title">
                      {male ? male : 0}
                      {/* <span className="stats-change stats-change-danger">-8%</span> */}
                    </h5>
                    <p className="stats-text">Male</p>
                  </div>
                  <div className="stats-icon change-danger">
                    <i className="material-icons">male</i>
                  </div>
                </div>
              </div>
            </div>
            <div className="col-lg-4">
              <div className="card stats-card">
                <div className="card-body">
                  <div className="stats-info">
                    <h5 className="card-title">
                      {female ? female : 0}
                      {/* <span className="stats-change stats-change-danger">-8%</span> */}
                    </h5>
                    <p className="stats-text">Female</p>
                  </div>
                  <div className="stats-icon change-success">
                    <i className="material-icons">female</i>
                  </div>
                </div>
              </div>
            </div>
            <div className="col-lg-4">
              <div className="card stats-card">
                <div className="card-body">
                  <div className="stats-info">
                    <h5 className="card-title">{activeUser}</h5>
                    <p className="stats-text">Total Active User</p>
                  </div>
                  <div className="stats-icon change-pink">
                    <i className="material-icons">people</i>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col">
          <div className="card" id="card">
            <div className="card-header pb-0">
              <div className="row my-3">
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-8 float-left">
                  <div className="text-left align-sm-left d-md-flex d-lg-flex justify-content-start">
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
                      {formattedStart !== 'ALL' &&
                        formattedStart + ' to ' + formattedEnd}
                    </p>
                  </div>
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
                          }
                        }}
                        onKeyPress={(e) => {
                          if (e.key === 'Enter') {
                            e.preventDefault();
                            dispatch(
                              getUser(
                                activePage,
                                rowsPerPage,
                                search,
                                formattedStart,
                                formattedEnd
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
                <div
                  id="datePicker"
                  className="date-picker-wrapper mt-5 pt-5"
                  aria-expanded="false"
                  style={{ display: 'none' }}
                >
                  <div className="container table-responsive">
                    <div key={JSON.stringify(date)}>
                      <DateRangePicker
                        maxDate={maxDate}
                        onChange={(item) => {
                          setDate([item.selection]);
                          const dayStart = dayjs(
                            item.selection.startDate
                          ).format('YYYY/M/DD');
                          const dayEnd = dayjs(item.selection.endDate).format(
                            'YYYY/M/DD'
                          );
                          setsDate(dayStart);
                          seteDate(dayEnd);
                        }}
                        showSelectionPreview={true}
                        moveRangeOnFirstSelection={false}
                        ranges={date}
                        direction="horizontal"
                      />
                    </div>
                    <div className="mt-3">
                      <button
                        className="btn btn-danger"
                        onClick={handleDateFilter}
                      >
                        Apply Filter
                      </button>
                    </div>
                  </div>
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
                    <th>isHost</th>
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
                          <td>{data?.isHost === false ? 'No' : 'Yes'}</td>
                          <td>
                            {data?.hostAgency ? data?.hostAgency?.name : '-'}
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
                userTotal={totalUser}
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

export default connect(null, { getUser, handleBlockUnblockSwitch })(UserTable);
