import axios from "axios";
import { Toast } from "../../util/Toast";
import {
  GET_USER,
  GET_COINSELLER_UNIQUEID,
  CREATE_HOST_REQUEST,
} from "./types";
import { baseURL, key } from "../../util/Config";

export const getUser = (agency_id) => (dispatch) => {
  axios
    .get(
      `agency/agencyWiseHost?agencyId=${agency_id}&start=${1}&limit=${20}&search=ALL&startDate=ALL&endDate=ALL`
    )
    .then((res) => {
      if (res?.data?.status) {
        dispatch({
          type: GET_USER,
          payload: {
            agencyWiseHost: res?.data?.data,
            total: res?.data?.total,
          },
        });
      } else {
        Toast("error", res?.data?.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};

export const getCoinSellerUniqueId = (search) => (dispatch) => {
  axios
    .get(`user/getUsersUniqueId?search=${search}`)
    .then((res) => {
      dispatch({
        type: GET_COINSELLER_UNIQUEID,
        payload: res.data.data,
      });
    })
    .catch((error) => console.log(error));
};
export const createHost = (agencyId, uniqueId) => (dispatch) => {
  axios
    .post(`agency/createHost?agencyId=${agencyId}&userId=${uniqueId}`)
    .then((res) => {
      dispatch({
        type: CREATE_HOST_REQUEST,
        payload: res.data.data,
      });
      Toast("success", "Host request sent successfully");
    })
    .catch((error) => console.log(error));
};
