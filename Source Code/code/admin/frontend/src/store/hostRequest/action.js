import axios from "axios";
import { Toast } from "../../util/Toast";

import { apiInstanceFetch } from "../../util/api";
import { ACCEPT_HOST_REQUEST, GET_HOST_REQUEST } from "./type";

export const getHostRequest = (start, limit, type) => (dispatch) => {
  apiInstanceFetch
    .get(`hostRequest/index?type=${type}&start=${start}&limit=${limit}`)
    .then((res) => {
      if (res.status) {
        dispatch({ type: GET_HOST_REQUEST, payload: res });
      } else {
        Toast("error", res.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};

export const acceptHostRequest = (id, type, reason) => (dispatch) => {
  axios
    .patch(
      `hostRequest/acceptOrDecline?requestId=${id}&type=${type}&reason=${reason}`
    )
    .then((res) => {
        
      if (res.data.status) {
        if (type === "decline") return Toast("success", "Decline Success!!");
        if (type === "accept") return Toast("success", "Accept Success!!");
        dispatch({
          type: ACCEPT_HOST_REQUEST,
          payload: id,
        });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};

export const  acceptHostReq = (id, type) => (dispatch) => {
  axios
    .patch(`hostRequest/acceptOrDecline?requestId=${id}&type=${type}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: ACCEPT_HOST_REQUEST,
          payload: id,
        });
        if (type === "decline") {
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

export const acceptHostRequestWithAgecyCode =
  (id, type, agencyCode) => (dispatch) => {
    axios
      .patch(
        `hostRequest/acceptOrDecline?requestId=${id}&type=${type}&agencyCode=${agencyCode}`
      )
      .then((res) => {
        
        if (res.data.status) {
          dispatch({
            type: ACCEPT_HOST_REQUEST,
            payload: id,
          });
          if (type === "decline") {
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
