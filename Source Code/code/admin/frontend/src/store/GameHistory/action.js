import axios from "axios";
import { GET_GAME_HISTORY, RESET_COIN } from "./type";
import {
  CLOSE_SPINNER_PROGRESS,
  OPEN_SPINNER_PROGRESS,
} from "../spinner/types";
import { Toast } from "../../util/Toast";
import { apiInstanceFetch } from "../../util/api";
import { baseURL, key } from "../../util/Config";

export const getGameHistory =
  (start, limit, startDate, endDate) => (dispatch) => {
    dispatch({ type: OPEN_SPINNER_PROGRESS });
    const requestOptions = {
      method: "GET",
      headers: { "Content-Type": "application/json", key: ` ${key}` },
    };
    const url = `${baseURL}history/teenpatti?start=${start}&limit=${limit}&startDate=${startDate}&endDate=${endDate}`;

    fetch(url, requestOptions)
      .then((response) => {
        if (!response.ok) {
          throw new Error(
            `Network response was not ok: ${response.statusText}`
          );
        }
        return response.json();
      })
      .then((res) => {
        
        dispatch({ type: GET_GAME_HISTORY, payload: res });
      })
      .catch((error) => {
        console.error("Fetch error:", error);
      })
      .finally(() => {
        dispatch({ type: CLOSE_SPINNER_PROGRESS });
      });
  };
export const rouletteCasinoHistory =
  (start, limit, startDate, endDate) => (dispatch) => {
    dispatch({ type: OPEN_SPINNER_PROGRESS });
    const requestOptions = {
      method: "GET",
      headers: { "Content-Type": "application/json", key: ` ${key}` },
    };
    const url = `${baseURL}history/rouletteCasino?start=${start}&limit=${limit}&startDate=${startDate}&endDate=${endDate}`;

    fetch(url, requestOptions)
      .then((response) => {
        if (!response.ok) {
          throw new Error(
            `Network response was not ok: ${response.statusText}`
          );
        }
        return response.json();
      })
      .then((res) => {
        
        dispatch({ type: GET_GAME_HISTORY, payload: res });
      })
      .catch((error) => {
        console.error("Fetch error:", error);
      })
      .finally(() => {
        dispatch({ type: CLOSE_SPINNER_PROGRESS });
      });
  };
export const ferryWheelHistory =
  (start, limit, startDate, endDate) => (dispatch) => {
    dispatch({ type: OPEN_SPINNER_PROGRESS });
    const requestOptions = {
      method: "GET",
      headers: { "Content-Type": "application/json", key: ` ${key}` },
    };
    const url = `${baseURL}history/ferryWheel?start=${start}&limit=${limit}&startDate=${startDate}&endDate=${endDate}`;

    fetch(url, requestOptions)
      .then((response) => {
        if (!response.ok) {
          throw new Error(
            `Network response was not ok: ${response.statusText}`
          );
        }
        return response.json();
      })
      .then((res) => {
        
        dispatch({ type: GET_GAME_HISTORY, payload: res });
      })
      .catch((error) => {
        console.error("Fetch error:", error);
      })
      .finally(() => {
        dispatch({ type: CLOSE_SPINNER_PROGRESS });
      });
  };

export const resetGameCoin = () => (dispatch) => {
  apiInstanceFetch
    .get("gameAdminCoin/reset")
    .then((res) => {
      if (res.status) {
        dispatch({ type: RESET_COIN, payload: res.gameAdminCoin.coin });
        Toast("success", "Admin Diamond Reset Successfully");
      } else {
        Toast("error", res.message);
      }
    })
    .catch((error) => console.log("error", error));
};
