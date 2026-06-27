import axios from "axios";
import * as ActionType from "./settlement.type";
import { Toast } from "../../util/Toast";
import { apiInstanceFetch } from "../../util/api";

export const hostWiseAgencySettlement =
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

export const allAgencySettlement = (start, limit) => (dispatch) => {
  apiInstanceFetch
    .get(`agencySettlement/getAllSettlement?start=${start}&limit=${limit}`)
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

export const getAgencySettlement = (payload) => (dispatch) => {
  apiInstanceFetch
    .get(
      `agencySettlement/pendingOrSolved?startDate=${payload?.startDate}&endDate=${payload?.endDate}&type=${payload?.type}&start=${payload?.currentPage}&limit=${payload?.rowsPerPage}&search=${payload?.search}`
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
    .put(`agencySettlement/updatePaidHistroy/${id}`)
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


export const actionUpdatePending = (data, id, type) => (dispatch) => {
  axios
    .patch(`agencySettlement/actionInSettlement/${id}`, data)
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

export const getWeeklySettlement = (start, limit, date) => (dispatch) => {
  apiInstanceFetch
    .get(
      `agencySettlement/getAllAgencySettlemtforPayOuts?start=${start}&limit=${limit}&&agencyId=66aa152760aba4f6211802e3&startDate=ALL&endDate=ALL`
    )
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
