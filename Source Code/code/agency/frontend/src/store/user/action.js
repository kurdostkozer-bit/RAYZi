import axios from "axios";
import { Toast } from "../../util/Toast";
import {
  GET_USER,
  GET_COINSELLER_UNIQUEID,
  CREATE_HOST_REQUEST,
  HOST_DISABLE,
} from "./types";
import { baseURL, key } from "../../util/Config";

export const getUser = (agency_id) => (dispatch) => {
  axios
    .get(
      `agency/agencyWiseHost?agencyId=${agency_id}&start=${1}&limit=${5000}&search=ALL&startDate=ALL&endDate=ALL`
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
  fetch(`${baseURL}user/getUsersUniqueIdForAgency
?search=${search}`, {
    headers: {
      key,
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      return response.json();
    })
    .then((data) => {
      dispatch({
        type: GET_COINSELLER_UNIQUEID,
        payload: data.data,
      });
    })
    .catch((error) => console.log("Fetch error:", error));
};
export const createHost = (agencyId, uniqueId) => (dispatch) => {
  axios
    .post(`agency/createHost?agencyId=${agencyId}&userId=${uniqueId}`)
    .then((res) => {
      dispatch({
        type: CREATE_HOST_REQUEST,
        payload: res.data.data,
      });
      if (res.data.status) {
        Toast("success", "Host request sent successfully");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};

export const disableHost = (host) => (dispatch) => {
  axios
    .patch(`host/isBlock?hostId=${host._id}`)
    .then((res) => {
      console.log(res.data.host);
      dispatch({
        type: HOST_DISABLE,
        payload: { data: res.data.host, id: host._id },
      });
      Toast(
        "success",
        `${host.name} Is ${
          host.isBlock !== true ? "disable" : "UnDisable"
        } Successfully!`
      );
    })
    .catch((error) => {
      console.log(error);
      Toast("error", error.message);
    });
};
