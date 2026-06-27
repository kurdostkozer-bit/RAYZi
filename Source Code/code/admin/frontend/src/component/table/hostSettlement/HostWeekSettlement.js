import { connect, useDispatch, useSelector } from "react-redux";
import {
  getWeeklySettlement,
  paidSettlement,
} from "../../../store/hostSettlement/hostSettlement.action";
import { useEffect, useState } from "react";
// import Title from "../../extra/Title";
// import Button from "../../extra/Button";
import {
  OPEN_DIALOGUE_SETTLEMENT,
  CLOSE_DIALOGUE_SETTLEMENT,
} from "../../../store/settlement/settlement.type";
import { useLocation } from "react-router-dom";
import SettlementActionDialogue from "./SettlementActionDialogue";
import BonusInfo from "./BonusInfo";
import { useNavigate  } from "react-router-dom";

const AgencyWeekSettlement = (props) => {
  const { dialogue, dialogueType, dialogueData } = useSelector(
    (state) => state.dialogue
  );
  const state = useLocation();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const { settlement, date, weekSettlement } = useSelector(
    (state) => state.settlement
  );
  const [data, setData] = useState([]);
  const [type, setType] = useState("0");
  const [filed, setField] = useState("");
  const [endDate, setEndDate] = useState("ALL");
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(20);
  const [search, setSearch] = useState("");

  useEffect(() => {
    dispatch(getWeeklySettlement(state?.state?.data?.startDate));
  }, [dispatch,dialogue]);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event, 10));
    setPage(0);
  };

  useEffect(() => {
    setData(weekSettlement);
  }, [weekSettlement]);

  const handleFilterData = (filteredData) => {
    if (typeof filteredData === "string") {
      setSearch(filteredData);
    } else {
      setData(filteredData);
    }
  };

  const handlePayment = (id) => {
    props.paidSettlement(id, "weekSettlement");
  };

  const handleActionPage = (row, type) => {
    dispatch({
      type: OPEN_DIALOGUE_SETTLEMENT,
      payload: {
        data: row,
        type: "agencySettlementActionDialogue",
      },
    });
    let dialogueData_ = {
      dialogue: true,
      type,
      dialogueData: row,
    };
    sessionStorage.setItem("weekSettlement", JSON.stringify(dialogueData_));
  };
  const handleLoadSettlementPage = (data) => {
    const state = {
      id: data?.agencyId?._id,
      type: "custom",
      startDate: data?.startDate,
      endDate: data?.endDate,
      agencyName: data?.agencyId?.name,
    };
    sessionStorage.setItem("stateWeekSettlement", JSON.stringify(state));
    navigate("/admin/agencySettlement" , {state : state});

  };
  const handleChildValue = (value) => {
    setType(type === 0 ? 1 : 0);
    setField(value);
  };

  const mapData = [
    {
      Header: "No",
      width: "20px",
      Cell: ({ index }) => (
        <span>{page * rowsPerPage + parseInt(index) + 1}</span>
      ),
    },
    { Header: "Agency Code", body: "agencyId.code" },
    { Header: "Agency Name", body: "agencyId.name", style: { width: "200px" } },

    {
      Header: "Revenue",
      body: "coinEarned",
      sorting: { type: "client" },
      Cell: ({ row }) => (
        <div className="bocBorder">
          <span
            onClick={() => handleLoadSettlementPage(row)}
            className="cursor  fw-bold "
          >
            {row?.coinEarned} &nbsp;
          </span>
        </div>
      ),
    },
    {
      Header: "Commission (%)",
      body: "agencyCommisionPercentage",
      sorting: { type: "client" },
     
    },
    {
      Header: "Commission",
      body: "commissionCoinEarned",
      sorting: { type: "client" },
    },
    {
      Header: "Total Revenue",
      body: "totalCoinEarned",
      sorting: { type: "client" },
    },
    {
      Header: "Penalty/Bonus",
      body: "bonusOrPenltyAmount",
      sorting: { type: "client" },
      Cell: ({ row }) => <span onClick={() => {
        dispatch({
          type: OPEN_DIALOGUE_SETTLEMENT,
          payload: { type: "bonusInfo", data: row },
        });
      }} className={`cursor ${row?.bonusOrPenltyAmount > 0 ? "text-success" : row?.bonusOrPenltyAmount === 0 ? "text-dark" : "text-danger"}`}>{row?.bonusOrPenltyAmount}</span>

    },
    {
      Header: "Final ",
      body: "amount",
      sorting: { type: "client" },
      Cell: ({ row }) => (
        <span
          onClick={() => handleLoadSettlementPage(row)}
          className="cursor textPink fw-bold"
        >
          {row?.amount} &nbsp;
        </span>
      ),
    },
    {
      Header: "Amount",
      Cell: ({ row }) => (
        <span
          onClick={() => handleLoadSettlementPage(row)}
          className="cursor text-primary"
        >
          {row?.dollar.toFixed(2) + " $ "}
        </span>
      ),
    },
    {
      Header: "Pay",
      body: "pay",
      Cell: ({ row }) => (
        <div>
          {row?.statusOfTransaction === 1 ? (
            <button
              newClass={` boxCenter userBtn text-white fs-6 border`}
              btnColor={`bg-primary`}
              btnName={`Pay`}
              // btnIcon={`fa-solid fa-info`}
              onClick={() => handlePayment(row._id)}
              style={{ borderRadius: "5px", margin: "auto", width: "100px" }}
            />
          ) : (
            <i className="fa-solid fa-circle-check fs-4 text-primary p-2"></i>
          )}
        </div>
      ),
    },
    {
      Header: "Action",
      body: "action",
      Cell: ({ row }) => (
        <div>
          {row?.statusOfTransaction == 1 ? (
            <button
              newClass={` boxCenter userBtn text-white fs-6 border`}
              btnColor={`bgPink`}
              btnName={`Action`}
              onClick={() =>
                handleActionPage(row, "agencySettlementActionDialogue")
              }
              style={{ borderRadius: "5px", margin: "auto", width: "100px" }}
            />
          ) : (
            <i className="fa-solid fa-circle-check fs-4 textPink p-2"></i>
          )}
        </div>
      ),
    },
    {
      Header: "Payout Date",
      Cell: ({ row }) => <span>{row?.payoutDate ? row?.payoutDate : "-"}</span>,
      sorting: { type: "client" },
    },
  ];
  return (
    <>
      <div className="mainSettlementTable">
        {dialogue && dialogueType === "agencySettlementActionDialogue" && (
          <SettlementActionDialogue />
        )}
        {dialogue && dialogueType === "bonusInfo" && <BonusInfo />}
        <div className="userTable">
          <div className="userHeader primeHeader ">
            <div className="boxBetween">
              <title name={`Weekly Settlement`} />
              {/* <button className="btn btn-primary btn-icon px-4">
                <i className="fa-solid fa-angles-left text-white fs-6"></i>
              </button> */}
            </div>

            <div className="d-flex justify-content-end col-md-12 ">
              {/* <div className="col-6 my-3">
                <Searching
                  data={weekSettlement}
                  column={mapData}
                  onFilterData={handleFilterData}
                  serverSearching={handleFilterData}
                  button={true}
                  searchValue={search}
                  setSearchValue={setSearch}
                  placeholder={"Searching ..."}
                  type={`client`}
                  setData={setData}
                />
              </div> */}
            </div>
          </div>
          <div className="userMain">
            {/* <div className="tableMain mt-3">
              <Table
                data={data}
                mapData={mapData}
                PerPage={rowsPerPage}
                Page={page}
                type={"client"}
                onChildValue={handleChildValue}
              />
            </div> */}
            {/* <Pagination
              component="div"
              count={weekSettlement?.length}
              serverPage={page}
              type={"client"}
              onPageChange={handleChangePage}
              serverPerPage={rowsPerPage}
              totalData={weekSettlement?.length}
              onRowsPerPageChange={handleChangeRowsPerPage}
            /> */}
            <div className="primeMain"></div>
          </div>
          <div className="userFooter primeFooter"></div>
        </div>
      </div>
    </>
  );
};

export default connect(null, { getWeeklySettlement, paidSettlement })(
  AgencyWeekSettlement
);
