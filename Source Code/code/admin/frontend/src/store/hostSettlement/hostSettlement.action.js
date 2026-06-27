import axios from "axios";
import * as ActionType from "./hostSettlement.type";
import   {Toast}  from "../../util/Toast";
import { apiInstanceFetch } from "../../util/api";

export const hostWiseSettlement =
  (id, dayType, startDate, endDate, start, limit, search, sort, typeSort) =>
  (dispatch) => {
    apiInstanceFetch
      .get(
        `admin/hostSettlement/agencyWiseHostSettlement?agencyId=${id}&type=${dayType}&startDate=${startDate}&endDate=${endDate}&start=${start}&limit=${limit}&search=${search}&sort=${sort}&sortType=${typeSort}`
      )
      .then((res) => {
        if (res.status) {
          dispatch({
            type: ActionType.GET_AGENCY_HOST_SETTLEMENT,
            payload: {
              data: res.history,
              historyCount: res.historyCount,
              totalRevenue: res.totalRevenue,
            },
          });
        }
      })
      .catch((err) => {
        Toast(err.message);
      });
  };

export const allHostSettlement = (startDate, endDate) => (dispatch) => {
  apiInstanceFetch
    .get(
      `admin/agencySettlement/getAllSettlement?startDate=${startDate}&endDate=${endDate}`
    )
    .then((res) => {
      if (res.status) {
        dispatch({
          type: ActionType.GET_ALL_SETTLEMENT,
          payload: {
            data: res.history,
            historyCount: res.historyCount,
          },
        });
      }
    })
    .catch((error) => console.log(error));
};

export const getHostSettlement =
  (payload) =>
  (dispatch) => {
    apiInstanceFetch
      .get(
        `hostSettlement/getPendingOrSolvedAll?startDate=${payload?.startDate}&endDate=${payload?.endDate}&type=${payload?.type}&start=${payload?.currentPage}&limit=${payload?.rowsPerPage}&search=${payload?.search}`
      )
      .then((res) => {
        dispatch({
          type: ActionType.GET_AGENCY_SETTLEMENT,
          payload: {
            data: res.history,
            historyCount: res.historyCount,
          },
        });
      })
      .catch((error) => console.log(error));
  };

export const paidSettlement = (id, type) => (dispatch) => {
  axios
    .put(`hostSettlement/updatePaidSettlement/${id}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: ActionType.UPDATE_PAID_SETTLEMENT,
          payload: { id: id, type: type },
        });
        Toast("Success", "Paid Successfully");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((err) => {
      Toast(err.message);
    });
};

export const actionUpdatePending = (id, data, type) => (dispatch) => {
  axios
    .patch(`admin/agencySettlement/actionInSettlement/${id}`, data)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: ActionType.ACTION_UPDATE_PENDING,
          payload: { data: res.data.history, id: id, type: type },
        });
        Toast("success", "Update successfully");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      Toast(error.message);
    });
};

export const getWeeklySettlement = (date) => (dispatch) => {
  apiInstanceFetch
    .get(`admin/agencySettlement/getAllAgencyInfo?date=${date}`)
    .then((res) => {
      if (res.status) {
        dispatch({
          type: ActionType.GET_ALL_WEEKLY_SETTLEMENT,
          payload: { data: res.history, date: date },
        });
      } else {
        Toast("error", res.message);
      }
    })
    .catch((error) => {
      console.log(error.message);
    });
};
