import axios from "axios";
import { Toast } from "../../util/Toast";

import { ACCEPT_HOST_REQUEST, GET_HOST_REQUEST } from "./types";

export const getHostRequest = (id, type) => (dispatch) => {
  
  axios
    .get(`hostRequest/requestGetByAgency?agencyId=${id}&type=${type}`)
    .then((res) => {
      
      if (res.data.status) {
        dispatch({ type: GET_HOST_REQUEST, payload: res.data?.data });
      } else {
        Toast("error", res.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};

export const acceptHostRequest = (id, type) => (dispatch) => {
  axios
    .patch(`hostRequest/acceptOrDecline?requestId=${id}&type=${type}` )
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: ACCEPT_HOST_REQUEST,
          payload: id,
        });
        if (type === "declined") {
          Toast("success", "Decline Success!!");
        }
        if (type === "accept") {
          Toast("success", "Accept Success!!");
        }
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};
