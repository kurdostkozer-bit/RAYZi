import axios from "axios";
import { Toast } from "../../util/Toast";
import {
  CLOSE_COMMISSION_DIALOG,
  CREATE_NEW_COMMISSION,
  DELETE_COMMISSION,
  EDIT_COMMISSION,
  GET_COMMISSION,
} from "./type";
import { apiInstanceFetch } from "../../util/api";
import { key } from "../../util/Config";

export const getCommission = () => (dispatch) => {
  apiInstanceFetch
    .get(`commission/get?type=1`)
    .then((res) => {
      if (res.status) {
        dispatch({ type: GET_COMMISSION, payload: res });
      } else {
        Toast("error", res.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};

export const createNewCommission = (data) => (dispatch) => {
  axios
    .post(`commission/store`, data)
    .then((res) => {
      if (res.data.status) {
        Toast("success", "Commission created successfully!");
        dispatch({ type: CLOSE_COMMISSION_DIALOG });
        dispatch({
          type: CREATE_NEW_COMMISSION,
          payload: res.data?.commission,
        });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const editCommission = (commissionId, data) => (dispatch) => {
  axios
    .patch(`commission/update?commissionRateId=${commissionId}`, data)
    .then((res) => {
      if (res.data.status) {
        Toast("success", "Commission updated successfully!");
        dispatch({ type: CLOSE_COMMISSION_DIALOG });
        dispatch({
          type: EDIT_COMMISSION,
          payload: { data: res.data?.commission, id: commissionId },
        });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const deleteCommission = (commissionId) => (dispatch) => {
  axios
    .delete(`commission/delete?commissionRateId=${commissionId}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: DELETE_COMMISSION, payload: commissionId });
        Toast("success", "Commission Deleted successfully!");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};
