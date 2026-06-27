import React, { useEffect, useState } from 'react';

//jquery
import $ from 'jquery';

//dayjs
import dayjs from 'dayjs';

//redux
import { connect, useSelector } from 'react-redux';

//action
import { vipPlanHistory } from '../../store/vipPlan/action';

//pagination
import Pagination from '../../pages/Pagination';

//MUI icon
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

//Date Range Picker
import { DateRangePicker } from 'react-date-range';
//Calendar Css
import 'react-date-range/dist/styles.css'; // main style file
import 'react-date-range/dist/theme/default.css'; // theme css file

//routing
import { Link } from 'react-router-dom';

import PurchaseVipPlan from './history/PurchaseVipPlan';

const PurchaseVIPPlanTable = (props) => {
  const [data, setData] = useState([]);
  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(2);
  const [date, setDate] = useState([]);
  const [sDate, setsDate] = useState('ALL');
  const [eDate, seteDate] = useState('ALL');
  const { history, totalPlan } = useSelector((state) => state.vipPlan);

  const maxDate = new Date();
  useEffect(() => {
    $('#card').click((e) => {
      if (
        !$(e.target).closest('.rdrDateRangePickerWrapper').length &&
        !$(e.target).closest('.rdrDateInput').length
      ) {
        $('#datePicker').css('display', 'none');
      }
    });
  }, []);

  useEffect(() => {
    props.vipPlanHistory(null, activePage, rowsPerPage, sDate, eDate);
  }, [activePage, rowsPerPage]);

  useEffect(() => {
    setData(history);
  }, [history]);

  useEffect(() => {
    if (date.length === 0) {
      const today = new Date();
      const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
      const lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0);

      setDate([
        {
          startDate: firstDay,
          endDate: lastDay,
          key: 'selection',
        },
      ]);

      // Set initial date range for API
      const dayStart = dayjs(firstDay).format('M/DD/YYYY');
      const dayEnd = dayjs(lastDay).format('M/DD/YYYY');
      setsDate(dayStart);
      seteDate(dayEnd);
    }
    $('#datePicker').css('display', 'none');
    setData(history);
  }, [history]);

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };

  const getAllHistory = () => {
    setActivePage(1);
    setsDate('ALL');
    seteDate('ALL');
    $('#datePicker').css('display', 'none');
    props.vipPlanHistory(null, activePage, rowsPerPage, sDate, eDate);
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
    props.vipPlanHistory(null, 1, rowsPerPage, sDate, eDate);
    $('#datePicker').css('display', 'none');
  };

  return (
    <>
      <div className="page-title">
        {props.type !== 'vipPlanHistory' && (
          <div className="row">
            <div className="col-12 col-md-6 order-md-1 order-last">
              <h3 className="mb-3 text-white ">Purchase VIP Plan History</h3>
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
          <div className="card" id="card">
            <div className="card-header pb-0">
              <div className="row my-3">
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-8 float-left">
                  <div className="text-left align-sm-left d-md-flex d-lg-flex justify-content-start">
                    <button
                      className="btn btn-info"
                      style={{ marginRight: 5 }}
                      onClick={getAllHistory}
                    >
                      All
                    </button>
                    <button
                      className="btn btn-info ml-5"
                      value="check"
                      data-toggle="show"
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
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-4 float-right mt-3 mt-lg-0 mt-xl-0"></div>
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
                          ).format('M/DD/YYYY');
                          const dayEnd = dayjs(item.selection.endDate).format(
                            'M/DD/YYYY'
                          );
                          setsDate(dayStart);
                          seteDate(dayEnd);
                        }}
                        showSelectionPreview={true}
                        moveRangeOnFirstSelection={false}
                        ranges={date}
                        direction="horizontal"
                        editableDateInputs={true}
                      />
                      {sDate !== 'ALL' && (
                        <div className="mt-3">
                          <button
                            className="btn btn-danger"
                            onClick={handleDateFilter}
                          >
                            Apply Filter
                          </button>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div className="card-body card-overflow pt-0">
              <PurchaseVipPlan data={data} />
              <Pagination
                activePage={activePage}
                rowsPerPage={rowsPerPage}
                userTotal={totalPlan}
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

export default connect(null, { vipPlanHistory })(PurchaseVIPPlanTable);
