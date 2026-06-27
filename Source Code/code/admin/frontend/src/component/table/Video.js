import React, { useEffect, useState } from 'react';

//jquery
import $ from 'jquery';

//redux
import { connect, useDispatch, useSelector } from 'react-redux';

//action
import { getVideo, deleteVideo } from '../../store/video/action';

//routing
import { Link, useNavigate } from 'react-router-dom';

// dayjs
import dayjs from 'dayjs';

// base url
import { baseURL } from '../../util/Config';

import {  warning, alert } from '../../util/Alert';

//pagination
import Pagination from '../../pages/Pagination';

//MUI icon
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

//Date Range Picker
import { DateRangePicker } from 'react-date-range';
//Calendar Css
import 'react-date-range/dist/styles.css'; // main style file
import 'react-date-range/dist/theme/default.css'; // theme css file

//image
import noImage from '../../assets/images/noImage.png';

const VideoTable = (props) => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const [data, setData] = useState([]);
  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const [date, setDate] = useState([]);
  const [sDate, setsDate] = useState('ALL');
  const [eDate, seteDate] = useState('ALL');

  const [showDatePicker, setShowDatePicker] = useState(false);

  const maxDate = new Date();
  useEffect(() => {
    $('#card').click(() => {
      $('#datePicker').removeClass('show');
    });
  }, []);

  useEffect(() => {
    dispatch(getVideo(null, activePage, rowsPerPage, sDate, eDate));
  }, [dispatch, activePage, rowsPerPage]);

  const { video, totalVideo } = useSelector((state) => state.video);



  useEffect(() => {
    setData(video);
  }, [video]);

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
    $('#datePicker').removeClass('show');
    setData(video);
  }, [date, video]);

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };

  const handleVideoDetail = (videoId) => {
    navigate({
      pathname: `/admin/video/detail`,
      state: { id: videoId },
    });
  };

  const handleDelete = (videoId) => {


    const data = warning();
    data
      .then((isDeleted) => {
        if (isDeleted) {
      
          props.deleteVideo(videoId);
          alert('Deleted!', `Relite has been deleted!`, 'success');
        }
      })
      .catch((err) => console.log(err));
  };

  const getAllVideo = () => {
    setActivePage(1);
    setsDate('ALL');
    seteDate('ALL');
    setShowDatePicker(false);
    dispatch(getVideo(null, activePage, rowsPerPage, sDate, eDate));
  };

  const collapsedDatePicker = () => {
    setShowDatePicker(!showDatePicker);
  };

  const handleApplyFilter = () => {
    setShowDatePicker(false);
    dispatch(getVideo(null, activePage, rowsPerPage, sDate, eDate));
    setsDate('ALL');
    seteDate('ALL');
  };

  return (
    <>
      <div className="page-title">
        {props.type !== 'realVideo' && (
          <div className="row">
            <div className="col-12 col-md-6 order-md-1 order-last">
              <h3 className="mb-3 text-light">Relite</h3>
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
                    Relite
                  </li>
                </ol>
              </nav>
            </div>
          </div>
        )}
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
                      onClick={getAllVideo}
                    >
                      All
                    </button>
                    <button
                      className="btn btn-info ml-5"
                      onClick={collapsedDatePicker}
                    >
                      Analytics
                      <ExpandMoreIcon />
                    </button>
                    <p style={{ paddingLeft: 10 }} className="my-2">
                      {sDate !== 'ALL' && sDate + ' to ' + eDate}
                    </p>
                  </div>
                </div>
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-4 float-right"></div>
                {showDatePicker && (
                  <div
                    className="position-absolute mt-5 pt-5 rounded p-3"
                    style={{ zIndex: 1000 }}
                  >
                    <div className="container">
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
                            setActivePage(1);
                            setsDate(dayStart);
                            seteDate(dayEnd);
                          }}
                          showSelectionPreview={true}
                          moveRangeOnFirstSelection={false}
                          ranges={date}
                          direction="horizontal"
                        />
                      </div>
                      <button
                        className="btn btn-danger mt-3"
                        onClick={handleApplyFilter}
                      >
                        Apply Filter
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </div>
            <div className="card-body card-overflow">
              <table className="table table-striped table-center ">
                <thead className="text-center">
                  <tr>
                    <th>No.</th>
                    <th>Video</th>
                    <th>Location</th>
                    <th>Like</th>
                    <th>Comment</th>
                    <th>Created At</th>
                    <th>Detail</th>
                    <th>Delete</th>
                  </tr>
                </thead>
                <tbody className="text-center">
                  {data.length > 0 ? (
                    data.map((data, index) => {
                      return (
                        <tr key={index}>
                          <td>{index + 1}</td>
                          <td
                            style={{
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'center',
                            }}
                          >
                            <video
                              height="50px"
                              width="50px"
                              controls
                              src={data.video ? baseURL + data.video : noImage}
                              className="post-image"
                              alt=""
                              style={{
                                boxShadow: '0 5px 15px 0 rgb(105 103 103 / 0%)',
                                border: '2px solid #fff',
                                borderRadius: 10,
                                float: 'left',
                                objectFit: 'cover',
                              }}
                            />
                          </td>
                          <td>{data.location ? data.location : '-'}</td>
                          <td className="text-danger">{data.like}</td>
                          <td className="text-success">{data.comment}</td>
                          <td>{data.date}</td>
                          <td>
                            <button
                              type="button"
                              className="btn btn-sm btn-info"
                              onClick={() => handleVideoDetail(data?._id)}
                            >
                              Detail
                            </button>
                          </td>
                          <td>
                            <button
                              type="button"
                              className="btn btn-sm btn-danger"
                              onClick={() => handleDelete(data._id)}
                            >
                              Delete
                            </button>
                          </td>
                        </tr>
                      );
                    })
                  ) : (
                    <tr>
                      <td colSpan="8" align="center">
                        Nothing to show!!
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
              <Pagination
                activePage={activePage}
                rowsPerPage={rowsPerPage}
                userTotal={totalVideo}
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

export default connect(null, { getVideo, deleteVideo })(VideoTable);
