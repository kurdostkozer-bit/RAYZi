import axios from "axios";
import * as ActionType from "./type";
import { apiInstanceFetch } from "../../util/api";

// get SVGA
export const getAdmissionSVGA = (type) => (dispatch) => {
  apiInstanceFetch
    .get(`svga/all?type=${type}`)
    .then((res) => {
      if (res.status) {
        
        dispatch({
          type: ActionType.GET_ADMISSION_CAR_GIF,
          payload: res.data,
        });
      }
    })
    .catch((error) => console.log("error", error));
};

// Create SVGA
export const crateAdmissionSVGA = (data) => (dispatch) => {
  axios
    .post(`svga/create?type=svga`, data)
    .then((res) => {
      if (res.data.status) {
        
        dispatch({
          type: ActionType.CERATE_ADMISSION_CAR_GIF,
          payload: res.data.data,
        });
      }
    })
    .catch((error) => console.log("error", error));
};

export const updateAdmissionSVGA = (id, data) => (dispatch) => {
  axios
    .patch(`svga/${id}?type=svga`, data)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: ActionType.UPDATE_ADMISSION_CAR_GIF,
          payload: { data: res.data.data, id: id },
        });
      }
    })
    .catch((error) => console.log("error", error));
};

export const deleteAdmissionSVGA = (id) => (dispatch) => {
  axios
    .delete(`svga/${id}?type=svga`)
    .then((res) => {
      if (res.data.status) {
        
        dispatch({ type: ActionType.DELETE_ADMISSION_CAR_GIF, payload: id });
      }
    })
    .catch((error) => console.log("error", error));
};
