import React, { useEffect } from "react";
import { useState } from "react";
import { Link } from "react-router-dom";
import $ from "jquery";
//MUI icon
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

//pagination
import Pagination from "../../pages/Pagination";

//Date Range Picker
import { DateRangePicker } from "react-date-range";
//Calendar Css
import "react-date-range/dist/styles.css"; // main style file
import "react-date-range/dist/theme/default.css"; // theme css file

import dayjs from "dayjs";
import { connect, useDispatch, useSelector } from "react-redux";
import {
  getGameHistory,
  resetGameCoin,
  rouletteCasinoHistory,
} from "../../store/GameHistory/action";
import TeenPatti from "./gameHistory/TeenPatti";
import FerryWheel from "./gameHistory/FerryWheel";
import RouletteCasino from "./gameHistory/RouletteCasino";

function GameHistory  ()  {
  const [type, setType] = useState(() => {
    // Retrieve the saved tab type from localStorage, default to "TeenPatti"
    return localStorage.getItem("selectedTab") || "TeenPatti";
  });

  useEffect(() => {
    // Save the selected tab type to localStorage whenever it changes
    localStorage.setItem("selectedTab", type);
  }, [type]);

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-white">Game History</h3>
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
                  Game History
                </li>
              </ol>
            </nav>
          </div>
        </div>
        <div className="row">
          <div className="col-12">
            <div className="my-2">
              <button
                type="button"
                className={`btn btn-sm ${
                  type === "TeenPatti" ? "btn-info" : "disabledBtn"
                }`}
                onClick={() => setType("TeenPatti")}
              >
                <span className="">Teen Patti</span>
              </button>
              <button
                type="button"
                className={`btn btn-sm ${
                  type === "RouletteCasino" ? "btn-danger" : "disabledBtn"
                } ms-3`}
                onClick={() => setType("RouletteCasino")}
              >
                <span className="">Roulette Casino</span>
              </button>
              <button
                type="button"
                className={`btn btn-sm ${
                  type === "ferryWheel" ? "btn-success" : "disabledBtn"
                } ms-3`}
                onClick={() => setType("ferryWheel")}
              >
                <span className="">Ferry Wheel</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      {type === "TeenPatti" ? (
        <TeenPatti />
      ) : type === "ferryWheel" ? (
        <FerryWheel />
      ) : type === "RouletteCasino" ? (
        <RouletteCasino />
      ) : (
        ""
      )}
     
    </>
  );
};

export default GameHistory;