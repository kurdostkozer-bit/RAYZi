import React, { useEffect, useState } from "react";
import Title from "../../extra/Title";
import Searching from "../../extra/Searching";
import Button from "../../extra/Button";
import Table from "../../extra/Table";
import Pagination from "../../extra/Pagination";
import { connect, useDispatch, useSelector } from "react-redux";
import { hostWiseSettlement } from "../../../store/hostSettlement/hostSettlement.action";
import { useLocation, useNavigate } from "react-router-dom";
import Analytics from "../../extra/Analytics";
import { CLOSE_DIALOGUE } from "../../store/dialogue/dialogue.type";

const HostSettleMent = (props) => {
  const { agencyHostSettlement, historyCount, totalRevenue } = useSelector(
    (state) => state.settlement
  );
  const { dialogueData } = useSelector((state) => state.dialogue);
  const { state } = useLocation();
  const [currentPage, setCurrentPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(20);
  const [search, setSearch] = useState("ALL");
  const [data, setData] = useState([]);
  const [startDate, setStartDate] = useState(
    state
      ? state?.startDate
      : dialogueData?.startDate
      ? dialogueData?.startDate
      : "ALL"
  );
  const [endDate, setEndDate] = useState(
    state
      ? state?.endDate
      : dialogueData?.endDate
      ? dialogueData?.endDate
      : "ALL"
  );
  const [dayType, setDayType] = useState("WEEK");
  const [sort, setSort] = useState("_id");
  const [sortType, setSortType] = useState(0);

  const dispatch = useDispatch();
  const navigate = useNavigate();
  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
  };
  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event, 10));
    setCurrentPage(1);
  };

  const stateID = JSON.parse(sessionStorage.getItem("stateWeekSettlement"))
  

  useEffect(() => {
    if (dialogueData) {
      props.hostWiseSettlement(
        dialogueData?._id,
        dayType,
        startDate,
        endDate,
        currentPage,
        rowsPerPage,
        search == "" ? "ALL" : search,
        sort,
        sortType
      );
    } else {
      props.hostWiseSettlement(
        state?.id ? state?.id : stateID?.id ,
        state?.type ? state?.type : stateID?.type,
        state?.startDate ? state?.startDate : stateID?.startDate,
        state?.endDate ? state?.endDate : stateID?.endDate,
        currentPage,
        rowsPerPage,
        search == "" ? "ALL" : search,
        sort,
        sortType
      );
      // setStartDate(state?.startDate);
      // setEndDate(state?.endDate);
    }
  }, [
    dialogueData,
    state,
    startDate,
    endDate,
    currentPage,
    rowsPerPage,
    search,
    sort,
    sortType,
  ]);
  useEffect(() => {
    window.history.pushState(null, window.location.href);
    window.addEventListener("popstate", goPrevious);
    return () => {
      window.removeEventListener("popstate", goPrevious);
    };
  }, []);
  const goPrevious = () => {
    if (dialogueData) {
      dispatch({ type: CLOSE_DIALOGUE });
      sessionStorage.removeItem("dialogueData");
    } else {
      navigate(-1);
    }
  };

  useEffect(() => {
    setData(agencyHostSettlement);
  }, [agencyHostSettlement]);

  const handleFilterData = (filteredData) => {
    if (typeof filteredData === "string") {
      setSearch(filteredData);
    } else {
      setData(filteredData);
    }
  };

  const showHostSettlement = (data) => {
    const state = {
      id: data?.hostId,
      name: data?.host?.name,
      startDate: data?.startDate,
      endDate: data?.endDate,
    };
    sessionStorage.setItem("stateAgencySettlement" ,  JSON.stringify(state))
    navigate("/admin/host/HostHistory", {
      state: state,
    });
  };

  const mapData = [
    {
      Header: "NO",
      width: "20px",
      Cell: ({ index }) => (
        <span>{(currentPage - 1) * rowsPerPage + parseInt(index) + 1}</span>
      ),
    },
    {
      Header: "Host Id",
      // body: "host.uniqueId",
      Cell: ({ row }) => <span>{row.host?.uniqueId}</span>,
    },
    {
      Header: "Host Name",
      body: "host.name",
      Cell: ({ row }) => <span>{row?.host?.name}</span>,
    },
    {
      Header: "Host Coin",
      body: "coinEarned",
      sorting: { type: "server" },
      Cell: ({ row }) => (
        <span
          onClick={() => showHostSettlement(row)}
          className="cursor text-success"
        >
          {row?.coinEarned}
        </span>
      ),
    },
    {
      Header: "Bonus/Penalty",
      body: "bonusOrPenaltyAmount",
      Cell: ({ row }) => (
        <span
          style={{
            color:
              row?.bonusOrPenaltyAmount > 0
                ? "green"
                : row?.bonusOrPenaltyAmount < 0
                ? "red"
                : "black",
          }}
        >
          {row?.bonusOrPenaltyAmount}
        </span>
      ),
    },
    {
      Header: "Final",
      body: "amount",
      sorting: { type: "server" },
      Cell: ({ row }) => (
        <span
          onClick={() => showHostSettlement(row)}
          className="cursor textPink"
        >
          {row?.amount}
        </span>
      ),
    },
    {
      Header: "Amount",
      body: "dollar",
      sorting: { type: "server" },
      Cell: ({ row }) => (
        <span
          onClick={() => showHostSettlement(row)}
          className="cursor text-primary"
        >
          {row.dollar + " $ "}
        </span>
      ),
    },
    {
      Header: "Date",
      body: "startDate",
    },
  ];

  const handleChildValue = (value) => {
    setSortType(sortType === -1 ? 1 : -1);
    setSort(value);
  };
  const name = data?.[0]?.agencyId?.name;
  return (
    <div className="settlementTable  position-relative">
      <div className="settlementHeader primeHeader boxBetween">
        <Title name={`Agency Settlement`} />
      </div>
      <div className="d-flex justify-content-between mx-3 mt-4">
        <h5>
          <span className="text-primary">
            {stateID ? stateID?.agencyName : dialogueData?.name}
          </span>
          ' Agency
        </h5>
        <button className="btn btn-primary btn-icon px-4" onClick={goPrevious}>
          <i className="fa-solid fa-angles-left text-white fs-6"></i>
        </button>
      </div>
      <div className="primeHeader mt-4 row">
        <div className="col-md-6 p-0 d-flex">
        
            <Analytics
              analyticsStartDate={startDate}
              analyticsStartEnd={endDate}
              analyticsStartDateSet={setStartDate}
              analyticsStartEndSet={setEndDate}
            />
          
          <div className="p-0 boxCenter">
            <span className="totalTex text-nowrap"> Total Revenue :</span>
            <span className="totalDollar text-center">
              {totalRevenue ? totalRevenue : "0"}
            </span>
          </div>
        </div>

        <div
          className="d-flex justify-content-end col-md-6 m-0 p-0 col-sm-12"
          style={{ alignSelf: "center" }}
        >
          <Searching
            type={"server"}
            data={agencyHostSettlement}
            setData={setData}
            setSearchData={setSearch}
            onFilterData={handleFilterData}
            serverSearching={handleFilterData}
            button={true}
            column={mapData}
            placeholder={"Searching Agency..."}
          />
        </div>
      </div>

      <div className="userMain">
        <div className="tableMain mt-3">
          <Table
            data={data}
            mapData={mapData}
            serverPerPage={rowsPerPage}
            serverPage={currentPage}
            type={"server"}
            onChildValue={handleChildValue}
          />
        </div>
      </div>
      <div className="paginationFooter">
        <Pagination
          component="div"
          count={agencyHostSettlement?.length}
          type={"server"}
          serverPerPage={rowsPerPage}
          totalData={historyCount}
          serverPage={currentPage}
          onPageChange={handleChangePage}
          setCurrentPage={setCurrentPage}
          onRowsPerPageChange={handleChangeRowsPerPage}
        />
      </div>
      <div className="userFooter primeFooter"></div>
    </div>
  );
};

export default connect(null, { hostWiseSettlement })(HostSettleMent);
