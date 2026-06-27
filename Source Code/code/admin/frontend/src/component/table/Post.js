import React, { useEffect, useState } from 'react';

//jquery
import $ from 'jquery';

//redux
import { connect, useDispatch, useSelector } from 'react-redux';

//action
import { getPost, deletePost } from '../../store/post/action';

//routing
import { Link, useNavigate } from 'react-router-dom';

//pagination
import Pagination from '../../pages/Pagination';

// dayjs
import dayjs, { Dayjs } from 'dayjs';

// base url
import { baseURL } from '../../util/Config';

//alert
import { warning, alert } from '../../util/Alert';

//MUI icon
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

//image
import noImage from '../../assets/images/noImage.png';

//Date Range Picker
import { DateRangePicker } from 'react-date-range';
//Calendar Css
import 'react-date-range/dist/styles.css'; // main style file
import 'react-date-range/dist/theme/default.css'; // theme css file

const PostTable = (props) => {
  const navigate = useNavigate();
  const dispatch = useDispatch();


  const { post, totalPost } = useSelector((state) => state.post);

  const [data, setData] = useState([]);
  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const [date, setDate] = useState([]);
  const [sDate, setsDate] = useState('ALL');
  const [eDate, seteDate] = useState('ALL');

  const maxDate = new Date();
  useEffect(() => {
    $('#card').click(() => {
      $('#datePicker').removeClass('show');
    });
  }, []);

  useEffect(() => {
    dispatch(getPost(null, activePage, rowsPerPage, sDate, eDate));
  }, [dispatch, activePage, rowsPerPage]);

  useEffect(() => {
    setData(post);
  }, [post]);

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
    setData(post);
  }, [date, post]);

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };
  const handleDelete = (postId) => {


    const data = warning();
    data
      .then((isDeleted) => {
        if (isDeleted) {
      
          props.deletePost(postId);
          alert('Deleted!', `Post has been deleted!`, 'success');
        }
      })
      .catch((err) => console.log(err));
  };

  const getAllPost = () => {
    setActivePage(1);
    setsDate('ALL');
    seteDate('ALL');
    $('#datePicker').removeClass('show');
    dispatch(getPost(null, activePage, rowsPerPage, sDate, eDate));
  };

  const collapsedDatePicker = () => {
    const datePicker = $('#datePicker');
    if (datePicker.css('display') === 'none') {
      datePicker.css('display', 'block');
    } else {
      datePicker.css('display', 'none');
    }
  };

  const handleDateFilter = () => {
    setActivePage(1);
    $('#datePicker').css('display', 'none');
    dispatch(getPost(null, activePage, rowsPerPage, sDate, eDate));
    setsDate('ALL');
    seteDate('ALL');
  };

  return (
    <>
      <div className="page-title">
        {props.type !== 'realPost' && (
          <div className="row">
            <div className="col-12 col-md-6 order-md-1 order-last">
              <h3 className="mb-3 text-light">Social Post</h3>
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
                    Social Post
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
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-8 float-left my-3">
                  <div className="text-left align-sm-left d-md-flex d-lg-flex justify-content-start">
                    <button
                      className="btn btn-info"
                      style={{ marginRight: 5 }}
                      onClick={getAllPost}
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
                      {sDate !== 'ALL' && sDate + ' to ' + eDate}
                    </p>
                  </div>
                </div>
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-4 float-right"></div>
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
            <div className="card-body card-overflow">
              <table className="table table-striped">
                <thead className="text-center">
                  <tr>
                    <th>No.</th>
                    <th>Post</th>
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
                          <td>
                            <img
                              height="50px"
                              width="50px"
                              alt="app"
                              src={data.post ? baseURL + data.post : noImage}
                              style={{
                                boxShadow: '0 5px 15px 0 rgb(105 103 103 / 0%)',
                                border: '2px solid #fff',
                                borderRadius: 10,
                                float: 'left',
                                objectFit: 'cover',
                              }}
                            />
                          </td>
                          <td>{data.location}</td>
                          <td className="text-danger">{data.like}</td>
                          <td className="text-success">{data.comment}</td>
                          <td>{dayjs(data.date).format('DD MMM YYYY ')}</td>
                          <td>
                            <button
                              type="button"
                              className="btn btn-sm btn-info"
                              onClick={() => {
                                sessionStorage.setItem(
                                  'PostDetail',
                                  JSON.stringify(data)
                                );
                                navigate('/admin/post/detail');
                              }}
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
                userTotal={totalPost}
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

export default connect(null, { getPost, deletePost })(PostTable);
