import axios from "axios";
import { Toast } from "../../util/Toast";

import { GET_COMMISSION_TYPE, GET_COMMISSIONS } from "./types";

export const getAgencyCommission = (page, rowsPerPage) => (dispatch) => {
  axios
    .get(`commissionRate/get`)
    .then((res) => {
      if (res?.data?.status) {
        dispatch({
          type: GET_COMMISSIONS,
          payload: { data: res?.data?.commission, total: res?.data?.total },
        });
      } else {
        Toast("error", res?.data?.message);
      }
    })
    .catch((error) => {
      console.log(error);
      Toast("error", error.message);
    });
};

export const getAgencyTypeCommission = (type) => (dispatch) => {
  axios
    .get(`commission/get?type=1`)
    .then((res) => {
      if (res?.data?.status) {
        dispatch({
          type: GET_COMMISSION_TYPE,
          payload: { data: res?.data?.commission },
        });
      } else {
        Toast("error", res?.data?.message);
      }
    })
    .catch((error) => {
      console.log(error);
      Toast("error", error.message);
    });
};
