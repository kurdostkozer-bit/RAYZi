import axios from "axios";
import { Toast } from "../../util/Toast";

import {
  GET_REDEEM,
  ACCEPT_REDEEM,
  GET_MY_REDEEM,
  NEW_REDEEM_CREATE,
} from "./types";
import { apiInstanceFetch } from "../../util/api";

export const getAgencyWiseRedeem =
  (agencyId, start, limit, type) => (dispatch) => {
    apiInstanceFetch
      .get(
        `agency/getUserRedeem?agencyId=${agencyId}&start=${start}&limit=${limit}&type=${type}`
      )
      .then((res) => {
        if (res.status) {
          dispatch({ type: GET_REDEEM, payload: res.redeem });
        } else {
          Toast("error", res.message);
        }
      })
      .catch((error) => {
        Toast("error", error.message);
      });
  };
export const getRedeem = (type) => (dispatch) => {
  apiInstanceFetch
    .get(`redeem?type=${type}`)
    .then((res) => {
      if (res.status) {
        dispatch({ type: GET_REDEEM, payload: res.redeem });
      } else {
        Toast("error", res.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};

export const acceptRedeem = (id, type) => (dispatch) => {
  axios
    .patch(`redeem/${id}?type=${type}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: ACCEPT_REDEEM, payload: id });
        {
          type === "decline" && Toast("error", "Decline Success!!");
        }
        {
          type === "accept" && Toast("success", "Accept Success!!");
        }
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};
