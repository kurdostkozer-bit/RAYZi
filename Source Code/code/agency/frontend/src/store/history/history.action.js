import axios from "axios";
import * as ActionType from "./history.type";

// get admin history

export const getAdminHistory =
  (id, startDate, endDate) => (dispatch) => {
    axios
      .get(
        `agencySettlement/agencySettlementForAgency?agencyId=${id}&startDate=${startDate}&endDate=${endDate}`
      )
      .then((res) => {
        dispatch({
          type: ActionType.GET_ADMIN_HISTORY,
          payload: { history: res.data.history },
        });
      })
      .catch((error) => console.log("error", error));
  };
export const getAdminCashOut = (id, start, limit, date, type) => (dispatch) => {
  axios
    .get(
      `agencyRedeem/getAgencyWise?agencyId=${id}&start=${start}&limit=${limit}&month=${date}&type=${type}`
    )
    .then((res) => {
      dispatch({
        type: ActionType.GET_ADMIN_CASHOUT,
        payload: { history: res.data.data, total: res.data.total },
      });
    })
    .catch((error) => console.log("error", error));
};

export const getHostCallhistory =
  (id, startDate, endDate) => (dispatch) => {
    axios
      .get(
        `host/hostCallHistoryForAgency?hostId=${id}&startDate=${startDate}&endDate=${endDate}`
      )
      .then((res) => {

        dispatch({
          type: ActionType.GET_HOST_HISTORY,
          payload: { history: res.data.data,total: res.data.total },
        });
      })
      .catch((error) => console.log("error", error));
  };

  export const getHostGifthistory =
  (id, type, start, limit, startDate, endDate) => (dispatch) => {
    axios
      .get(
        `host/hostCoinEarningForAgency?hostId=${id}&startDate=${startDate}&endDate=${endDate}`
      )
      .then((res) => {
        dispatch({
          type: ActionType.GET_HOST_HISTORY,
          payload: { history: res.data.data,total: res.data.total },
        });
      })
      .catch((error) => console.log("error", error));
  };

  export const getHostLivehistory =
  (id,  startDate, endDate) => (dispatch) => {
    axios
      .get(
        `host/hostLiveHistoryForAgency?hostId=${id}&startDate=${startDate}&endDate=${endDate}`
      )
      .then((res) => {
        dispatch({
          type: ActionType.GET_HOST_LIVE_HISTORY,
          payload: { history: res.data.data,total: res.data.total },
        });
      })
      .catch((error) => console.log("error", error));
  };

export const getAgencyEarning = (id, date) => (dispatch) => {
  axios
    .get(`weekHistory/agencyEarning?agencyId=${id}&month=${date}`)
    .then((res) => {
      dispatch({
        type: ActionType.GET_ADMIN_HISTORY,
        payload: { history: res.data.data },
      });
    })
    .catch((error) => console.log("error", error));
};
