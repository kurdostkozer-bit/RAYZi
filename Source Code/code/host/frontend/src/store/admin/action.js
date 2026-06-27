import axios from "axios";
import { Toast } from "../../util/Toast";

import { GET_SELLER, SET_ADMIN, UNSET_ADMIN, UPDATE_PROFILE } from "./types";
import { key } from "../../util/Config";

export const login = (data) => (dispatch) => {
  axios
    .post(`agency/login?key=${key}`, data)
    .then((res) => {
      console.log(res.data);
      if (res.data.status) {
        Toast("success", "You have successfully logged in Ⴆσσɱҽɾ Lιʋҽ.");
        setTimeout(() => {
          window.location.href = "/host/dashboard";
        }, 100);
        dispatch({ type: SET_ADMIN, payload: res.data.token });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};

export const getProfile = (hostId) => (dispatch) => {
  axios
    .get(`host/profile?hostId=${hostId}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: GET_SELLER, payload: res.data?.data });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      console.log("error", error.message);
    });
};



export const changePassword = (data) => (dispatch) => {
  axios
    .put("admin", data)
    .then((res) => {
      if (res.data.status) {
        Toast("success", "Password changed successfully.");
        setTimeout(() => {
          dispatch({ type: UNSET_ADMIN });
          window.location.href = "/host";
        }, [3000]);
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const updateProfile = (agency_id, data) => (dispatch) => {
  axios
    .patch(`agency/profile/${agency_id}`, data)
    .then((res) => {
      if (res.data.status) {
        Toast("success", "Profile updated successfully.");
        dispatch({ type: UPDATE_PROFILE, payload: res.data?.data });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
