import axios from "axios";
import * as ActionType from "./type";
import { apiInstanceFetch } from "../../util/api";

// get avatarFrame
export const getAvatarFrame = (type) => (dispatch) => {
  apiInstanceFetch
    .get(`svga/all?type=${type}`)
    .then((res) => {
      if (res.status) {
        dispatch({
          type: ActionType.GET_AVATAR_FRAME_GIF,
          payload: res.data,
        });
      }
    })
    .catch((error) => console.log("error", error));
};

// Create avatarFrame
export const crateAvatarFrame = (data) => (dispatch) => {
  axios
    .post(`svga/createFrame`, data)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: ActionType.CERATE_AVATAR_FRAME_GIF,
          payload: res.data.data,
        });
      }
    })
    .catch((error) => console.log("error", error));
};

export const updateAvatarFrame = (id, data) => (dispatch) => {
  axios
    .patch(`svga/${id}?type=frame`, data)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: ActionType.UPDATE_AVATAR_FRAME_GIF,
          payload: { data: res.data.data, id },
        });
      }
    })
    .catch((error) => console.log("error", error));
};

export const deleteAvatarFrame = (id) => (dispatch) => {
  axios
    .delete(`svga/${id}?type=frame`)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: ActionType.DELETE_AVATAR_FRAME_GIF, payload: id });
      }
    })
    .catch((error) => console.log("error", error));
};
