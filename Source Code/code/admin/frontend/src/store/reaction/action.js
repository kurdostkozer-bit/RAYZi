import * as ActionType from "./type"
import { Toast } from "../../util/Toast";
import { apiInstanceFetch } from "../../util/api";
import axios from "axios";


export const getReaction = () => (dispatch) => {
    apiInstanceFetch
        .get(`reaction/getReaction`)
        .then((res) => {
            if (res.status) {
                dispatch({ type: ActionType.GET_REACTION, payload: res.data });
            } else {
                Toast("error", res.message);
            }
        })
        .catch((error) => Toast("error", error.message));
};

export const createReaction = (data) => (dispatch) => {
    axios
        .post(`reaction/add`, data)
        .then((res) => {
            if (res.data.status) {
                Toast("success", "Reaction created successfully!");
                dispatch({ type: ActionType.CLOSE_REACTION_DIALOG });
                dispatch({ type: ActionType.ADD_REACTION, payload: res.data });
            } else {
                Toast("error", res.data.message);
            }
        })
        .catch((error) => Toast("error", error.message));
};
export const editReaction = (id, data) => (dispatch) => {
    axios
        .patch(`reaction/update?reactionId=${id}`, data)
        .then((res) => {
            if (res.data.status) {
                Toast("success", "Reaction updated successfully!");
                dispatch({ type: ActionType.CLOSE_REACTION_DIALOG });
                dispatch({
                    type: ActionType.UPDATE_REACTION,
                    payload: { data: res.data,id },
                });
            } else {
                Toast("error", res.data.message);
            }
        })
        .catch((error) => Toast("error", error.message));
};
export const deleteReaction = (id) => (dispatch) => {
    axios
        .delete(`reaction/delete?reactionId=${id}`)
        .then((res) => {
            if (res.data.status) {
                dispatch({ type: ActionType.DELETE_REACTION, payload: id });
                Toast("success", "Reaction deleted successfully!");
            } else {
                Toast("error", res.data.message);
            }
        })
        .catch((error) => console.log(error));
};