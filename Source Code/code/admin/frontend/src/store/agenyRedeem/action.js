import axios from "axios";
import { Toast } from "../../util/Toast";

import {
  GET_REDEEM,
  ACCEPT_REDEEM,
  GET_MY_REDEEM,
  NEW_REDEEM_CREATE,
  DECLINE_REDEEM,
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
export const getAgencyRedeem = (type) => (dispatch) => {
  apiInstanceFetch
    .get(`agencyRedeem/getAgencyRedeem?type=${type}`)
    .then((res) => {
      if (res.status) {
        dispatch({ type: GET_REDEEM, payload: res.data });
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
    .patch(`agencyRedeem/update?agencyRedeemId=${id}&type=${type}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: ACCEPT_REDEEM, payload: id });
        Toast("success", "Accept Success!!");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};

export const declineRedeem = (id, type, reason) => (dispatch) => {
  axios
    .patch(
      `agencyRedeem/update?agencyRedeemId=${id}&type=${type}&reason=${reason}`
    )
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: DECLINE_REDEEM, payload: id });

        Toast("success", "Decline Success!!");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      console.log(error);
      Toast("error", error.message);
    });
};
