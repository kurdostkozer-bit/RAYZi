import axios from "axios";
import { Toast } from "../../util/Toast";

import {
  GET_REDEEM,
  ACCEPT_REDEEM,
  GET_SETTING,
  GET_MY_REDEEM,
  NEW_REDEEM_CREATE,
  UPDATE_REDEEM_AGENCY,
} from "./types";

export const getRedeem = (type, agencyId, start, limit) => (dispatch) => {
  axios
    .get(
      `agency/getUserRedeem?type=${type}&agencyId=${agencyId}&start=${start}&limit=${limit}`
    )
    .then((res) => {
      if (res?.data?.status) {
        dispatch({
          type: GET_REDEEM,
          payload: {
            data: res?.data?.redeem,
            total: res?.data?.total,
          },
        });
      } else {
        Toast("error", res?.data?.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};
export const getSetting = () => (dispatch) => {
  axios
    .get("setting")
    .then((res) => {
      if (res?.data?.status) {
        dispatch({ type: GET_SETTING, payload: res?.data?.setting });
      } else {
        Toast("error", res?.data?.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const acceptRedeem = (id, type) => (dispatch) => {
  axios
    .put(`agencyRedeem/acceptUserRedeem/${id}?type=${type}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: ACCEPT_REDEEM,
          payload: {
            data: res.data.data,
            id: id,
          },
        });
        if (type === "accept") return Toast("success", "Accept Success!!");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};

export const getMyRedeem =
  (agencyId) => (dispatch) => {
    axios
      .get(
        `agencyRedeem/getAgencyWise?agencyId=${agencyId}`
      )
      .then((res) => {
        if (res?.data?.status) {
          dispatch({
            type: GET_MY_REDEEM,
            payload: { data: res?.data?.data, total: res?.data?.total, totalRevenue : res?.data?.totalRevenue },
          });
        } else {
          Toast("error", res?.data?.message);
        }
      })
      .catch((error) => {
        Toast("error", error.message);
      });
  };

export const createAgencyRedeem = (data) => (dispatch) => {
  axios
    .post(`agencyRedeem/store`, data)
    .then((res) => {
      if (res.data.status) {
        Toast("success", "New Redeem Request Create successfully!");
        dispatch({ type: NEW_REDEEM_CREATE, payload: res?.data?.data });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log("error", error));
};

export const editRedeemAgency = (id, data) => (dispatch) => {
  axios
    .patch(`agencyRedeem/${id}`, data)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: UPDATE_REDEEM_AGENCY,
          payload: {
            data: res.data.data,
            id: id,
          },
        });
        Toast("success", "Update Redeem Request  successfully!");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};
