import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getAdminHistory } from "../../store/history/history.action";
import InfiniteScroll from "react-infinite-scroll-component";

const Income = (props) => {

  const startDate = props?.startDate;
  const endDate = props?.endDate;

  





  return (
    <>
      {/* {history?.length > 0 ? (
        <InfiniteScroll
          dataLength={history?.length}
          next={fetchData}
          hasMore={hasMore}
          loader={
            total > 10 ? (
              <p className="text-dark text-center">Loading...</p>
            ) : null
          }
          endMessage={<p className="text-center mt-2">No more items</p>}
        >
          <div className="row">
            {history?.map((data) => {
              return (
                <>
                  <div className="col-12 mb-2 mt-1">
                    <div className="agency-invitation bg-white p-2">
                      <div className="row">
                        <div className="col-4">
                          <span
                            style={{
                              color: "#a7a7a7",
                              fontSize: "8px",
                            }}
                          >
                            Coin Income
                          </span>
                          <div className="d-flex align-items-center">
                            <img
                              src={require("../../assets/images/rcoin.png")}
                              style={{ height: "20px", width: "20px" }}
                              alt=""
                            />
                            <span
                              className="mb-0 ms-1 fw-bolder"
                              style={{
                                color: "#ff8300",
                                fontSize: "15px",
                              }}
                            >
                              {data?.coin ? data?.coin : 0}
                            </span>
                          </div>
                        </div>
                        <div className="col-4">
                          <span
                            style={{
                              color: "#a7a7a7",
                              fontSize: "8px",
                            }}
                          >
                            Percent
                          </span>
                          <div className="">
                            <span
                              className="mb-0 ms-1 fw-bolder"
                              style={{
                                color: "#000",
                                fontSize: "15px",
                              }}
                            >
                              {data?.percent ? data?.percent + "%" : 0}
                            </span>
                          </div>
                        </div>
                        <div className="col-4">
                          <span
                            style={{
                              color: "#a7a7a7",
                              fontSize: "8px",
                            }}
                          >
                            Date
                          </span>
                          <div className="">
                            <p
                              className="mb-0 ms-1 "
                              style={{
                                color: "#000",
                                fontSize: "12px",
                              }}
                            >
                              {data?.startDate}
                            </p>

                            <p
                              className="mb-0 ms-1 "
                              style={{
                                color: "#000",
                                fontSize: "12px",
                              }}
                            >
                              {data?.endDate}
                            </p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </>
              );
            })}
          </div>
        </InfiniteScroll>
      ) : (
       
      )} */}

    
      <div className="col-12 mb-2 mt-1 main-settlement">
        <div className="main-settlement-header p-3 d-flex justify-content-between align-items-center">
          <p className="date-content mb-0">Date : 01/03/2024 to 06/05/2024</p>
          <p className="dollar-content mb-0">563 (25$ USD)</p>
          <p></p>
          <p className="status-content pt-1 ps-1">Pending</p>
        </div>
        <div className="main-settlement-body p-2">
          <div className="row">
            <div className="col-6">
              <p className="text-main-12">Host Coins</p>
              <p className="text-Light">50032</p>
            </div>
            <div className="col-6" style={{ borderLeft: "1px solid #2C1B38" }}>
              <p className="text-main-12">Agency Commission (10%)</p>
              <p className="text-Light">536</p>
            </div>
          </div>
          <p
            className="dollar-content pb-2"
            style={{ borderBottom: "1px solid  #2C1B38" }}
          >
            Total coins : 536 + 50 bonus coins = 563 Coins
          </p>
        </div>
      </div>
      <div className="col-12 mb-2 mt-1 main-settlement">
        <div className="main-settlement-header p-3 d-flex justify-content-between align-items-center">
          <p className="date-content mb-0">Date : 01/03/2024 to 06/05/2024</p>
          <p className="dollar-content mb-0">563 (25$ USD)</p>
          <p></p>
          <p className="status-content pt-1 ps-1">Pending</p>
        </div>
        <div className="main-settlement-body p-2">
          <div className="row">
            <div className="col-6">
              <p className="text-main-12">Host Coins</p>
              <p className="text-Light">50032</p>
            </div>
            <div className="col-6" style={{ borderLeft: "1px solid #2C1B38" }}>
              <p className="text-main-12">Agency Commission (10%)</p>
              <p className="text-Light">536</p>
            </div>
          </div>
          <p
            className="dollar-content pb-2"
            style={{ borderBottom: "1px solid  #2C1B38" }}
          >
            Total coins : 536 + 50 bonus coins = 563 Coins
          </p>
        </div>
      </div>
      <div className="col-12 mb-2 mt-1 main-settlement">
        <div className="main-settlement-header p-3 d-flex justify-content-between align-items-center">
          <p className="date-content mb-0">Date : 01/03/2024 to 06/05/2024</p>
          <p className="dollar-content mb-0">563 (25$ USD)</p>
          <p></p>
          <p className="status-content pt-1 ps-1">Pending</p>
        </div>
        <div className="main-settlement-body p-2">
          <div className="row">
            <div className="col-6">
              <p className="text-main-12">Host Coins</p>
              <p className="text-Light">50032</p>
            </div>
            <div className="col-6" style={{ borderLeft: "1px solid #2C1B38" }}>
              <p className="text-main-12">Agency Commission (10%)</p>
              <p className="text-Light">536</p>
            </div>
          </div>
          <p
            className="dollar-content pb-2"
            style={{ borderBottom: "1px solid  #2C1B38" }}
          >
            Total coins : 536 + 50 bonus coins = 563 Coins
          </p>
        </div>
      </div>
    </>
  );
};

export default Income;
