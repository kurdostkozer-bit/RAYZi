import axios from "axios";
import { Toast } from "../../util/Toast";

import { SET_ADMIN, SIGNUP_ADMIN, UNSET_ADMIN, UPDATE_PROFILE } from "./types";
import { apiInstanceFetch } from "../../util/api";
import { projectName } from "../../util/Config";

export const signupAdmin = (signup) => (dispatch) => {
  axios
    .post("/admin/signup", signup)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: SIGNUP_ADMIN });
        Toast("success", "Signup Successfully!");
        window.location.reload();
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      Toast("error", error);
    });
};

export const login = (data) => (dispatch) => {
  axios
    .post("admin/login", data)
    .then((res) => {
      if (res.data.status) {
        
        Toast("success", `You have successfully logged in ${projectName}`);
        setTimeout(() => {
          window.location.href = "/admin/dashboard";
        }, 10);
        dispatch({ type: SET_ADMIN, payload: res.data.token });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) =>{
    });
};

export const sendEmail = (data) => (dispatch) => {
  axios
    .post("admin/sendEmail", data)
    .then((res) => {
      if (res.data.status) {
        Toast(
          "success",
          "Mail has been sent successfully. Sometimes mail has been landed on your spam!"
        );
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};

export const getProfile = () => (dispatch) => {
  apiInstanceFetch
    .get("admin/profile")
    .then((res) => {
    
      if (res.status) {
        dispatch({ type: UPDATE_PROFILE, payload: res.admin });
      } else {
        Toast("error", res.message);
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
          window.location.href = "/";
        }, [3000]);
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const updateNameEmail = (data) => (dispatch) => {
  axios
    .patch("admin", data)
    .then((res) => {
      if (res.data.status) {
        Toast("success", "Profile updated successfully.");
        dispatch({ type: UPDATE_PROFILE, payload: res.data.admin });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};


export const updateCode = (signup) => (dispatch) => {
  axios
    .patch("admin/updateCode", signup)
    .then((res) => {
      if (res.data.status) {
        Toast("success", "Purchase Code Update Successfully !");
        setTimeout(() => {
          window.location.href = "/login";
        }, 3000);
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      Toast("error", error);
    });
};
