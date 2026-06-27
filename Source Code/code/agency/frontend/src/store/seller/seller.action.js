import axios from "axios";
import { Toast } from "../../util/Toast";
import * as ActionType from "./seller.type";

export const getCoinSellerHistory = (id , start , limit) => (dispatch) => {

  axios
    .get(`coinSellerHistory/history/${id}?start=${start}&limit=${limit}`)
    .then((res) => {
      
      dispatch({ type: ActionType.GET_HISTORY, payload: {data : res.data.coinSeller ,total :res.data.total} });
    })
    .catch((error) => Toast("error", error.message));
};

export const createCoinData = (data) => (dispatch) => {
  
  axios
    .post(`coinSeller/coinSellerToUser`, data)
    .then((res) => {
      
      if (res.data.status) {
        Toast("success", "coin send successfully!");
        dispatch({
          type: ActionType.COIN_DATA_CREATE,
          payload: res.data.sellerHistory,
        });
        
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log("error", error));
};

// USER HISTORY_TABLE

export const getUserHistory = (id , coinSellerId) => (dispatch) => {
  
  axios
    .get(`coinSellerHistory/userHistory?userId=${id}&coinSellerId=${coinSellerId}`)
    .then((res) => {
      dispatch({
        type: ActionType.HISTORY_TABLE,
        payload: res.data,
      });
    })
    .catch((error) => Toast("error", error.message));
};
