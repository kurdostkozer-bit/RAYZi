import axios from "axios";
import { Toast } from "../../util/Toast";
import {
  CLOSE_GAME_DIALOG,
  CREATE_NEW_GAME,
  DELETE_GAME,
  EDIT_GAME,
  GAME_SETTING_ID,
  GET_GAME,
  SETTING_ID,
} from "./types";
import { apiInstanceFetch } from "../../util/api";

export const getGame = () => (dispatch) => {
  apiInstanceFetch
    .get(`setting`)
    .then((res) => {
      if (res.status) {
        dispatch({ type: GET_GAME, payload: res?.setting?.game });
        dispatch({ type: GAME_SETTING_ID, payload: res?.setting?._id });
      } else {
        Toast("error", res.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const createNewGame = (data, settingId) => (dispatch) => {
  axios
    .patch(`setting/addGame/${settingId}`, data)
    .then((res) => {
      if (res.data.status) {
        Toast("success", "Game created successfully!");
        dispatch({ type: CLOSE_GAME_DIALOG });
        dispatch({ type: CREATE_NEW_GAME, payload: res.data?.setting?.game });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const editGame = (gameId, settingId, data) => (dispatch) => {
  axios
    .patch(`setting/updateGame/${settingId}`, data)
    .then((res) => {
      if (res.data.status) {
        Toast("success", "Game updated successfully!");
        dispatch({ type: CLOSE_GAME_DIALOG });
        dispatch({
          type: EDIT_GAME,
          payload: res.data?.setting?.game,
        });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const deleteGame = (settingId,id) => (dispatch) => {
  axios
    .delete(`setting/deleteGame/${settingId}?gameId=${id}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: DELETE_GAME,   payload: res.data?.setting?.game, });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};
