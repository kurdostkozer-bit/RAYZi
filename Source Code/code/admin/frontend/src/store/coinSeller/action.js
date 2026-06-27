import axios from "axios";
import { Toast } from "../../../src/util/Toast";
import * as CoinSellerType from "./type";
import { apiInstanceFetch } from "../../util/api";
import { baseURL, key } from "../../util/Config";

// GET coinSeller
export const getCoinSeller = (start, limit) => (dispatch) => {
  apiInstanceFetch
    .get(`coinSeller/getAll?start=${start}&limit=${limit}`)
    .then((res) => {
      dispatch({
        type: CoinSellerType.GET_COINSELLER,
        payload: res,
      });
    })
    .catch((error) => console.log(error));
};

// GET coinSeller UniqueId
export const getCoinSellerUniqueId = (start, limit, search) => (dispatch) => {
  const token = sessionStorage.getItem("TOKEN");
  fetch(
    `${baseURL}user/getUsersUniqueId?start=${start}&limit=${limit}&search=${search}`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        key: `${key}`,
        Authorization: `${token}`,
      },
    }
  ).then((res) => res.json()).then((res) => {
      dispatch({
        type: CoinSellerType.GET_COINSELLER_UNIQUEID,
        payload: res.data,
      });
    })
    .catch((error) => console.log(error));
};

// CREATE coinSeller
export const addCoinSeller = (data) => (dispatch) => {
  axios
    .post(
      `coinSeller/create?uniqueId=${data?.uniqueId}&coin=${data?.coin}&mobileNumber=${data?.mobileNumber}&countryCode=${data?.countryCode}`
    )
    .then((res) => {
      if (res.data.status === true) {
        dispatch({
          type: CoinSellerType.ADD_COINSELLER,
          payload: res.data.data,
        });
        Toast("success", "coinSeller Add Successfully");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};

// UPDATE coinSeller
export const editCoinSeller = (id, coin) => (dispatch) => {
  axios
    .patch(`coinSeller/coinByadmin?coinSellerId=${id}&coin=${coin}`)
    .then((res) => {
      if (res.data.status === true) {
        dispatch({
          type: CoinSellerType.EDIT_COINSELLER,
          payload: { coinSeller: res.data.coinSeller, id },
        });
        Toast("success", "coinSeller Update Successfully");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};

// DELETE AGANCY
export const deleteCoinSeller = (id) => (dispatch) => {
  axios
    .patch(`coinSeller/activeOrNot?coinSellerId=${id}`)
    .then((res) => {
      dispatch({
        type: CoinSellerType.DELETE_COINSELLER,
        payload: { data: res.data.coinSeller, id: id },
      });
      Toast(
        "success",
        res.data.coinSeller?.isActive === true
          ? "Enable Successfully"
          : "disable Successfully"
      );
    })
    .catch((error) => console.log(error));
};

// Show coinSeller
export const showCoinSeller = (id) => (dispatch) => {
  axios
    .patch(`coinSeller/show/${id}`)
    .then((res) => {
      dispatch({
        type: CoinSellerType.SHOW_COINSELLER,
        payload: { data: res.data.data, id },
      });
    })
    .catch((error) => console.log(error));
};

// UPDATE coinSeller
export const AddMoneyByAdmin = (data, id) => (dispatch) => {
  axios
    .patch(`coinSeller/coinByadmin?coinSellerId=${id}&coin=${data?.coin}`)
    .then((res) => {
      if (res.data.status === true) {
        dispatch({
          type: CoinSellerType.ADD_MONEY_BY_ADMIN,
          payload: { coinSeller: res.data.data, id: id },
        });
        Toast("success", "Add coin Successfully");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};

export const lessCoinSellerCoin = (data, id) => (dispatch) => {
  axios
    .patch(`coinSeller/coinLessByAdmin?coinSellerId=${id}&coin=${data?.coin}`)
    .then((res) => {
      if (res.data.status === true) {
        dispatch({
          type: CoinSellerType.LESS_MONEY_BY_ADMIN,
          payload: { coinSeller: res.data.data, id },
        });
        Toast("success", "coinSeller Update Successfully");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};

// GET coinSeller history
export const getCoinSellerHistory = (id, start, limit) => (dispatch) => {
  axios
    .get(
      `coinSellerHistory/getCoinSellerHistory?coinSellerId=${id}&start=${start}&limit=${limit}`
    )
    .then((res) => {
      dispatch({
        type: CoinSellerType.GET_COINSELLER_HISTORY,
        payload: res.data,
      });
    })
    .catch((error) => console.log(error));
};

export const MobileNumberByAdmin = (data, id) => (dispatch) => {
  axios
    .patch(
      `coinSeller/editMobileNumber?coinSellerId=${id}&mobileNumber=${data?.mobileNumber}&countryCode=${data?.countryCode}`
    )
    .then((res) => {
      if (res.data.status === true) {
        dispatch({
          type: CoinSellerType.MOBILE_NUMBER_BY_ADMIN,
          payload: { coinSeller: res.data.coinSeller, id: id },
        });
        Toast("success", "Mobile Number Change Successfully");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => console.log(error));
};
