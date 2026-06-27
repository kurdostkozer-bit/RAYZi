import { Toast } from "../../util/Toast";
import { apiInstanceFetch } from "../../util/api";
import {
  GET_AGENCY,
  CREATE_NEW_AGENCY,
  EDIT_AGENCY,
  CLOSE_AGENCY_DIALOG,
  SET_CREATE_AGENCY_DONE,
  SET_UPDATE_AGENCY_DONE,
  ENABLE_DISABLE_AGENCY,
  GET_AGENCY_DROPDOWN,
  GET_AGENCY_WISE_HOST,
  BLOCK_UNBLOCK_SWITCH_AGENCYUSERS,
  ACCEPT_DECLINE_AGENCY,
  GET_UNVERIFIED_AGENCY,
  REDEEM_ENDABLED_SWITCH_AGENCY,
  REDEEM_ENDABLED_SWITCH_HOST,
  GET_AGENCY_HISTORY,
} from "./type";

import axios from "axios";

export const getAgency = (start, limit) => (dispatch) => {
  apiInstanceFetch
    .get(`agency/index?start=${start}&limit=${limit}`)
    .then((res) => {
      dispatch({ type: GET_AGENCY, payload: res });
    })
    .catch((error) => console.log(error));
};
export const getUnVerifiedAgency = (start, limit) => (dispatch) => {
  apiInstanceFetch
    .get(`agency/unverifiedAgency`)
    .then((res) => {
      dispatch({ type: GET_UNVERIFIED_AGENCY, payload: res });
    })
    .catch((error) => console.log(error));
};

export const acceptAgency = (agencyId) => (dispatch) => {
  axios
    .patch(`agency/verifyAgency?agencyId=${agencyId}`)
    .then((res) => {
      dispatch({ type: ACCEPT_DECLINE_AGENCY, payload: agencyId });
    })
    .catch((error) => console.log(error));
};

export const deleteAgency = (agencyId) => (dispatch) => {
  axios
    .delete(`agency/declineAgency?agencyId=${agencyId}`)
    .then((res) => {
      dispatch({ type: ACCEPT_DECLINE_AGENCY, payload: agencyId });
    })
    .catch((error) => console.log(error));
};

export const getAgencyWiseHost = (id, start, limit, search) => (dispatch) => {
  apiInstanceFetch
    .get(
      `agency/agencyWiseHost?agencyId=${id}&start=${start}&limit=${limit}&startDate=${"ALL"}&endDate=${"ALL"}&search=${search}`
    )
    .then((res) => {
      dispatch({
        type: GET_AGENCY_WISE_HOST,
        payload: { data: res?.data, total: res?.total },
      });
    })
    .catch((error) => console.log(error));
};

export const handleBlockUnblockSwitch = (userId) => (dispatch) => {
  axios
    .patch(`user/blockUnblock/${userId}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: BLOCK_UNBLOCK_SWITCH_AGENCYUSERS,
          payload: res.data.user,
        });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const redeemEnableAgency = (userId) => (dispatch) => {
  axios
    .patch(`agencyRedeem/handleRedeem?agencyId=${userId}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: REDEEM_ENDABLED_SWITCH_AGENCY,
          payload: res.data.data,
        });
        Toast(
          "success",
          `${
            res.data?.data?.redeemEnable === true
              ? "Enabled Successfully"
              : "Disabled Successfully"
          }`
        );
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};

export const getAgencyDropdown = () => (dispatch) => {
  apiInstanceFetch
    .get("agency/agencyDropDown")
    .then((res) => {
      if (res.status) {
        dispatch({ type: GET_AGENCY_DROPDOWN, payload: res.data });
      }
    })
    .catch((error) => console.log(error));
};

export const createNewAgency = (formData) => (dispatch) => {
  axios
    .post("agency/store", formData)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: CREATE_NEW_AGENCY, payload: res.data.data });
        dispatch({ type: CLOSE_AGENCY_DIALOG });
        dispatch({ type: SET_CREATE_AGENCY_DONE });
        Toast("success", "Agency Create Successfully");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};
export const editAgency = (formData, id) => (dispatch) => {
  axios
    .patch(`agency/update?agencyId=${id}`, formData)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: EDIT_AGENCY,
          payload: { data: res.data.data, id },
        });
        dispatch({ type: CLOSE_AGENCY_DIALOG });
        dispatch({ type: SET_UPDATE_AGENCY_DONE });
        Toast("success", "Agency Update Successfully");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};

export const enableDisableAgency = (id) => (dispatch) => {
  axios
    .patch(`agency/activeOrNot?agencyId=${id}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: ENABLE_DISABLE_AGENCY, payload: res.data.data });
        Toast(
          "success",
          `${
            res.data?.data?.isActive === true
              ? "Disable Successfully"
              : "Enable Successfully"
          }`
        );
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};
export const getAgencyHistory = (startDate, endDate, start, limit) => (dispatch) => {
  apiInstanceFetch
    .get(`history/allAgencyHistory?agencyId=&startDate=${startDate}&endDate=${endDate}&start=${start}&limit=${limit}`)
    .then((res) => {
      dispatch({ type: GET_AGENCY_HISTORY, payload: res.agencyResults, total: res.total });
    })
    .catch((error) => console.log(error));
};
export const redeemEnableHost = (id) => (dispatch) => {
  axios
    .put(`redeem/handleRedeem?userId=${id}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: REDEEM_ENDABLED_SWITCH_HOST, payload: res.data.data });
        Toast(
          "success",
          `${
            res.data?.data?.redeemEnable === true
              ? "Enabled Successfully"
              : "Disabled Successfully"
          }`
        );
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};
