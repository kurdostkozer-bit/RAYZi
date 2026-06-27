import axios from "axios";
import { Toast } from "../../util/Toast";

import { apiInstanceFetch } from "../../util/api";
import {
  CLOSE_HOST_COMMISSION_DIALOG,
  CREATE_NEW_HOST_COMMISSION,
  DELETE_HOST_COMMISSION,
  EDIT_HOST_COMMISSION,
  GET_HOST_COMMISSION,
} from "./type";
import { key } from "../../util/Config";

export const getHostCommission = () => (dispatch) => {
  apiInstanceFetch
    .get(`commission/get?type=2`)
    .then((res) => {
      if (res.status) {
        dispatch({ type: GET_HOST_COMMISSION, payload: res });
      } else {
        Toast("error", res.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};

export const createNewHostCommission = (data) => (dispatch) => {
  axios
    .post(`commission/store`, data)
    .then((res) => {
      if (res.data.status) {
        Toast("success", "Commission created successfully!");
        dispatch({ type: CLOSE_HOST_COMMISSION_DIALOG });
        dispatch({
          type: CREATE_NEW_HOST_COMMISSION,
          payload: res.data?.commission,
        });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const editHostCommission = (commissionId, data) => (dispatch) => {
  axios
    .patch(
      `commission/update?commissionRateId=${commissionId}`,
      data
    )
    .then((res) => {
      if (res.data.status) {
        Toast("success", "Commission updated successfully!");
        dispatch({ type: CLOSE_HOST_COMMISSION_DIALOG });
        dispatch({
          type: EDIT_HOST_COMMISSION,
          payload: { data: res.data?.commission, id: commissionId },
        });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const deleteHostCommission = (commissionId) => (dispatch) => {
  axios
    .delete(`commission/delete?commissionRateId=${commissionId}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: DELETE_HOST_COMMISSION, payload: commissionId });
        Toast("success", "Commission Deleted successfully!");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};
