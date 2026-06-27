import axios from "axios";
import { Toast } from "../../util/Toast";
import {
  GET_GIFT,
  CREATE_NEW_GIFT,
  EDIT_GIFT,
  CLOSE_GIFT_DIALOG,
  DELETE_GIFT,
  CLOSE_SVGA_DIALOG,
  CREATE_NEW_GIFT_SVGA,
} from "./types";
import { apiInstanceFetch } from "../../util/api";

const GiftClick = sessionStorage.getItem("GiftClick");

export const getGift = (categoryId) => (dispatch) => {
  apiInstanceFetch
    .get(`gift/${categoryId === "ALL" ? "all" : categoryId}`)
    .then((res) => {
      if (res.status) {
        dispatch({ type: GET_GIFT, payload: res.gift });
      } else {
        Toast("error", res.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const createNewGift = (data) => (dispatch) => {
  axios
    .post(`gift`, data)
    .then((res) => {
      if (res.data.status) {
        Toast("success", "Gift created successfully!");
        dispatch({ type: CLOSE_GIFT_DIALOG });
        dispatch({ type: CREATE_NEW_GIFT, payload: res.data.gift });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const editGift = (data, giftId, categoryID) => (dispatch) => {
  axios
    .patch(`gift/${giftId}`, data)
    .then((res) => {
      
      if (res.data.status) {
        Toast("success", "Gift updated successfully!");
        dispatch({ type: CLOSE_GIFT_DIALOG });
        dispatch({
          type: EDIT_GIFT,
          payload: { data: res.data.gift, id: giftId, categoryID: categoryID },
        });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
export const deleteGift = (giftId) => (dispatch) => {
  axios
    .delete(`gift/${giftId}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: DELETE_GIFT, payload: giftId });

        setTimeout(() => {
          GiftClick !== null && (window.location.href = "/admin/gift");
        }, 100);
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};

// svga

export const createNewGiftSvga = (formData, categoryID) => (dispatch) => {
  axios
    .post("/gift/svgaAdd", formData)
    .then((res) => {
      if (res.data.status) {
        Toast("success", "SVGA add successfully");

        dispatch({
          type: CREATE_NEW_GIFT_SVGA,
          payload: {
            giftData: res.data.data,
            categoryID: categoryID,
          },
        });
      } else {
        Toast("error", res.data.message);
      }
      dispatch({ type: CLOSE_SVGA_DIALOG });
    })
    .catch((error) => console.log(error));
};
