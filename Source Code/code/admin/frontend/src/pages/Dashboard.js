import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { Chart as ChartJS } from 'chart.js/auto';
import { Line, Doughnut, Chart } from 'react-chartjs-2';

//dayjs
import dayjs from 'dayjs';

//datepicker
import DateRangePicker from 'react-bootstrap-daterangepicker';
import 'bootstrap-daterangepicker/daterangepicker.css';

// action
import { getDashboard, getAnalytic } from '../store/dashboard/action';
import { getSetting } from '../store/setting/action';
import { connect, useDispatch, useSelector } from 'react-redux';

// Register the date adapter

const Dashboard = (props) => {
  let label = [];
  let data = [];
  let data1 = [];
  const dispatch = useDispatch();
  const [type, setType] = useState('USER');

  const dashboard = useSelector((state) => state.dashboard.dashboard);
  const analytic = useSelector((state) => state.dashboard.analytic);
  const setting = useSelector((state) => state.setting.setting);

  var date = new Date();
  var firstDay = new Date(date.getFullYear(), date.getMonth() - 1, 1); // Previous month's first day
  var lastDay = new Date(date.getFullYear(), date.getMonth(), 0); // Previous month's last day

  const startDate = dayjs(firstDay).format('YYYY-MM-DD');
  const endDate = dayjs(lastDay).format('YYYY-MM-DD');
  const [sDate, setSDate] = useState('ALL');
  const [eDate, setEDate] = useState('ALL');
  const [analyticData, setAnalyticData] = useState([]);

  useEffect(() => {
    dispatch(getDashboard());
    dispatch(getSetting());
    dispatch(getAnalytic('USER', sDate, eDate));
  }, [dispatch]);

  useEffect(() => {
    setAnalyticData(analytic);
  }, [analytic]);

  if (analyticData.length > 0) {
    if (type === 'REVENUE' && analyticData.length === 1) {
      analyticData[0]?.coinRevenue?.map((date) => {
        label.push(date._id);
      });

      analyticData[0]?.vipRevenue?.map((date) => {
        label.push(date._id);
      });
      const uniqLabel = [...new Set(label)];

      uniqLabel.map((data__) => {
        const count = analyticData[0]?.coinRevenue?.find(
          (element) => element._id === data__
        );
        if (count) {
          data.push(setting.currency === '$' ? count.dollar : count.rupee);
        } else {
          data.push(0);
        }
        const count_ = analyticData[0]?.vipRevenue?.find(
          (element) => element._id === data__
        );
        if (count_) {
          data1.push(setting.currency === '$' ? count_.dollar : count_.rupee);
        } else {
          data1.push(0);
        }
      });
    } else {
      analyticData.map((data_) => {
        label.push(data_._id);
        data.push(data_.count);
      });
    }
  }

  const handleAnalytic = (type) => {
    setSDate('ALL');
    setEDate('ALL');
    setType(type);
    if (type !== 'ACTIVE USER') {
      dispatch(getAnalytic(type, sDate, eDate));
    }
  };
  const labelFormate = label?.map((item) => dayjs(item).format('DD-MM-YYYY'));
  const chartData = {
    labels: labelFormate,
    datasets: [
      {
        label: type?.toUpperCase(),
        data: data,
        fill: true,
        backgroundColor: 'rgba(255, 99, 132, 0.1)',
        borderColor: 'rgb(255, 99, 132)',
        lineTension: 0.5,
      },
    ],
  };
  const multiLineChartData = {
    labels: labelFormate,
    datasets: [
      {
        label: 'Coin Revenue',
        data: data,
        fill: true,
        backgroundColor: 'rgba(255, 99, 132, 0.1)',
        borderColor: 'rgb(255, 99, 132)',
        lineTension: 0.5,
      },
      {
        label: 'VIP Revenue',
        data: data1,
        fill: true,
        backgroundColor: 'rgb(205,235,255,0.1)',
        borderColor: '#68B9F0',
        lineTension: 0.5,
      },
    ],
  };

  const pieChartData = {
    maintainAspectRatio: false,
    responsive: false,
    labels: ['Total User', 'Active User'],
    datasets: [
      {
        data: [dashboard.totalUser, dashboard.activeUser],
        backgroundColor: ['#e8538f', '#fc9494'],
        hoverBackgroundColor: ['#e8538f', '#fc9494'],
      },
    ],
  };

  //Apply button function for analytic
  const handleApply = (event, picker) => {
    picker.element.val(
      picker.startDate.format('YYYY-MM-DD') +
        ' - ' +
        picker.endDate.format('YYYY-MM-DD')
    );
    const dayStart = dayjs(picker.startDate).format('YYYY-MM-DD');

    const dayEnd = dayjs(picker.endDate).format('YYYY-MM-DD');

    setSDate(dayStart);
    setEDate(dayEnd);

    dispatch(getAnalytic(type, dayStart, dayEnd));
  };

  //Cancel button function for analytic
  const handleCancel = (event, picker) => {
    picker.element.val('');

    dispatch(getAnalytic(type, startDate, endDate));
  };

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-white">Dashboard</h3>
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
              </ol>
            </nav>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col-lg-12 col-md-12 col-sm-12">
          <div className="row">
            <div className="col-lg-3 col-md-4 col-sm-6">
              <Link to={'/admin/user'} className="card stats-card">
                <div
                  className="card-body pointer-cursor"
                  onClick={() => handleAnalytic('USER')}
                >
                  <div className="stats-info">
                    <h5 className="card-title">
                      {dashboard.totalUser ? dashboard.totalUser : 0}
                      {/* <span className="stats-change stats-change-danger">-8%</span> */}
                    </h5>
                    <p className="stats-text">Total User</p>
                  </div>
                  <div className="stats-icon change-danger">
                    <i className="material-icons">people_alt</i>
                  </div>
                </div>
              </Link>
            </div>
            <div className="col-lg-3 col-md-4 col-sm-6">
              <Link to={'/admin/user'} className="card stats-card">
                <div
                  className="card-body pointer-cursor"
                  onClick={() => handleAnalytic('LIVE USER')}
                >
                  <div className="stats-info">
                    <h5 className="card-title">
                      {dashboard.liveUser ? dashboard.liveUser : 0}
                      {/* <span className="stats-change stats-change-danger">-8%</span> */}
                    </h5>
                    <p className="stats-text">Total Live User</p>
                  </div>
                  <div className="stats-icon change-success">
                    <i className="material-icons">perm_identity</i>
                  </div>
                </div>
              </Link>
            </div>
            <div className="col-lg-3 col-md-4 col-sm-6">
              <Link to={'/admin/user'} className="card stats-card">
                <div
                  className="card-body pointer-cursor"
                  onClick={() => handleAnalytic('ACTIVE USER')}
                >
                  <div className="stats-info">
                    <h5 className="card-title">
                      {dashboard.activeUser ? dashboard.activeUser : 0}
                    </h5>
                    <p className="stats-text">Total Active User</p>
                  </div>
                  <div className="stats-icon change-pink">
                    <i className="material-icons">group</i>
                  </div>
                </div>
              </Link>
            </div>
            <div className="col-lg-3 col-md-4 col-sm-6">
              <Link to={'/admin/user'} className="card stats-card">
                <div
                  className="card-body pointer-cursor"
                  onClick={() => handleAnalytic('VIP')}
                >
                  <div className="stats-info">
                    <h5 className="card-title">
                      {dashboard.vipUser ? dashboard.vipUser : 0}
                    </h5>
                    <p className="stats-text">Total VIP User</p>
                  </div>
                  <div className="stats-icon change-danger">
                    <i className="material-icons">settings_accessibility</i>
                  </div>
                </div>
              </Link>
            </div>
            <div className="col-lg-3 col-md-4 col-sm-6">
              <Link to={'/admin/planHistory'} className="card stats-card">
                <div
                  className="card-body pointer-cursor"
                  onClick={() => handleAnalytic('REVENUE')}
                >
                  <div className="stats-info">
                    <h5 className="card-title">
                      {dashboard.revenue
                        ? setting.currency === '$'
                          ? dashboard.revenue?.dollar
                          : dashboard.revenue?.rupee
                        : 0}
                      {setting.currency}
                      {/* <span className="stats-change stats-change-danger">-8%</span> */}
                    </h5>
                    <p className="stats-text">Total Revenue</p>
                  </div>
                  <div className="stats-icon change-success">
                    <i className="material-icons">account_balance_wallet</i>
                  </div>
                </div>
              </Link>
            </div>
            <div className="col-lg-3 col-md-4 col-sm-6">
              <Link to={'/admin/post'} className="card stats-card">
                <div
                  className="card-body pointer-cursor"
                  onClick={() => handleAnalytic('POST')}
                >
                  <div className="stats-info">
                    <h5 className="card-title">
                      {dashboard.post ? dashboard.post : 0}
                    </h5>
                    <p className="stats-text">Total Post</p>
                  </div>
                  <div className="stats-icon change-pink">
                    <i className="material-icons">post_add</i>
                  </div>
                </div>
              </Link>
            </div>
            <div className="col-lg-3 col-md-4 col-sm-6">
              <Link to={'/admin/video'} className="card stats-card">
                <div
                  className="card-body pointer-cursor"
                  onClick={() => handleAnalytic('VIDEO')}
                >
                  <div className="stats-info">
                    <h5 className="card-title">
                      {dashboard.video ? dashboard.video : 0}
                    </h5>
                    <p className="stats-text">Total Video</p>
                  </div>
                  <div className="stats-icon change-danger">
                    <i className="material-icons">voice_chat</i>
                  </div>
                </div>
              </Link>
            </div>
            <div className="col-lg-3 col-md-4 col-sm-6">
              <Link to={'/admin/reportedUser'} className="card stats-card">
                <div
                  className="card-body pointer-cursor"
                  onClick={() => handleAnalytic('REPORT')}
                >
                  <div className="stats-info">
                    <h5 className="card-title">
                      {dashboard.report ? dashboard.report : 0}
                    </h5>
                    <p className="stats-text">Total Report User</p>
                  </div>
                  <div className="stats-icon change-success">
                    <i className="material-icons">next_plan</i>
                  </div>
                </div>
              </Link>
            </div>
          </div>
        </div>
      </div>

      {/* chart */}
      <h4 className="text-white">{type}</h4>
      <div className="d-flex justify-content-end">
        <DateRangePicker
          initialSettings={{
            autoUpdateInput: false,
            locale: {
              cancelLabel: 'Clear',
            },
            maxDate: new Date(),
            buttonClasses: ['btn btn-dark'],
          }}
          onApply={handleApply}
          onCancel={handleCancel}
        >
          <input
            type="text"
            readOnly
            className="form-control float-left text-center"
            placeholder="Select Date"
            style={{ width: 195, fontWeight: 700 }}
          />
        </DateRangePicker>
      </div>

      {data?.length > 0 ? (
        type === 'ACTIVE USER' ? (
          <div className="pie mt-3">
            <Doughnut data={pieChartData} options={{ responsive: true }} />
          </div>
        ) : type === 'REVENUE' ? (
          <div className="pie mt-3">
            <div className="d-flex justify-content-end">
              <DateRangePicker
                initialSettings={{
                  autoUpdateInput: false,
                  locale: {
                    cancelLabel: 'Clear',
                  },
                  maxDate: new Date(),
                  buttonClasses: ['btn btn-dark'],
                }}
                onApply={handleApply}
                onCancel={handleCancel}
              >
                <input
                  type="text"
                  readOnly
                  className="form-control float-left"
                  placeholder="Select Date"
                  style={{ width: 180, fontWeight: 700 }}
                />
              </DateRangePicker>
            </div>
            <Line data={multiLineChartData} options={{ responsive: true }} />
          </div>
        ) : (
          <div className="rows mt-3">
            <Line data={chartData} options={{ responsive: true }} />
          </div>
        )
      ) : (
        <p className="text-center">Chart not Available</p>
      )}
    </>
  );
};

export default connect(null, { getDashboard, getSetting, getAnalytic })(
  Dashboard
);
