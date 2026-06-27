import { Toast } from "../../util/Toast";
import axios from "axios";
import {
  BLOCK_UNBLOCK_SWITCH,
  GET_HISTORY,
  EDIT_COIN,
  GET_FAKE_USER,
  INSERT_FAKE_USER,
  GET_COUNTRY,
  EDIT_FAKE_USER,
} from "./Type";
import { baseURL, key } from "../../util/Config";
import {
  CLOSE_SPINNER_PROGRESS,
  OPEN_SPINNER_PROGRESS,
} from "../spinner/types";
import { apiInstanceFetch } from "../../util/api";

export const getFakeUser =
  (start, limit, search, sDate, eDate, type) => (dispatch) => {
    apiInstanceFetch
      .get(
        `user/getFakeData?start=${start}&limit=${limit}&search=${search}&startDate=${sDate}&endDate=${eDate}&fakeDataType=${type}`
      )
      .then((res) => {
        if (res.status) {
          let male, female;
          if (res.maleFemale) {
            res.maleFemale.map((data) => {
              if (data._id === "Female") return (female = data.gender);
              if (data._id === "Male") return (male = data.gender);
            });
          }
          dispatch({
            type: GET_FAKE_USER,
            payload: {
              user: res.user,
              activeUser: res.activeUser,
              total: res.total,
              male: male,
              female: female,
            },
          });
          dispatch({ type: CLOSE_SPINNER_PROGRESS });
        } else {
          Toast("error", res.message);
        }
      })
      .catch((error) => {
        console.error("Error in fetch:", error);
        Toast("error", error.message);
      });
  };

//insert fake User
export const insertUser = (data, pkVideoType) => (dispatch) => {
  axios
    .post(`/user/AddFakeUser?fakeDataType=${pkVideoType}`, data)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: INSERT_FAKE_USER, payload: res.data.user });
        Toast("success", "User Insert Successful");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};

//edit fake user
export const editUser = (id, data, pkVideoType) => (dispatch) => {
  axios
    .patch(`user/updateFakeUser?userId=${id}&fakeDataType=${pkVideoType}`, data)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: EDIT_FAKE_USER,
          payload: { data: res.data.user, id: id },
        });
        Toast("success", "User Edit Successful");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};
export const deleteUser = () => (dispatch) => {};

export const handleBlockUnblockSwitch = (userId) => (dispatch) => {
  axios
    .patch(`user/blockUnblock/${userId}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: BLOCK_UNBLOCK_SWITCH, payload: res.data.user });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};

export const userHistory = (data) => (dispatch) => {
  axios
    .post(`history`, data)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: GET_HISTORY, payload: res.data });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};

export const editCoin = (data) => (dispatch) => {
  axios
    .post(`/user/addLessCoin`, data)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: EDIT_COIN,
          payload: { data: res.data.user, id: data.userId },
        });
        Toast("success", "Update Successful!!");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};

//get country
export const getCountry = () => (dispatch) => {
  apiInstanceFetch
    .get(`location/country`)
    .then((res) => {
      if (res.status) {
        dispatch({
          type: GET_COUNTRY,
          payload: res.country,
        });
      } else {
        Toast("error", res.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};

//insert fake User
export const insertApiUser = (data) => (dispatch) => {
  axios
    .post(`multipleFakeUser`, data)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: INSERT_FAKE_USER,
          payload: res.data.MultipleFakeUser,
        });
        Toast("success", "User Insert Successful");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};

//edit fake user
export const editApiUser = (id, data) => (dispatch) => {
  axios
    .patch(`/fakeUser/${id}`, data)
    .then((res) => {
      if (res.data.status) {
        dispatch({
          type: EDIT_FAKE_USER,
          payload: { data: res.data.user, id: id },
        });
        Toast("success", "User Edit Successful");
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => {
      Toast("error", error.message);
    });
};

export const getApiUser =
  (start, limit, searchValue, sDate, eDate) => (dispatch) => {
    const requestOptions = {
      method: "GET",
      headers: { key: key },
    };
    fetch(
      `${baseURL}user/getUsers?type=fakeUserAPI&search=${searchValue}&eDate=${eDate}&sDate=${sDate}`,
      requestOptions
    )
      .then((response) => response.json())
      .then((res) => {
        if (res.status) {
          let male, female;
          if (res.maleFemale) {
            res.maleFemale.map((data) => {
              if (data._id === "Female") return (female = data.gender);
              if (data._id === "Male") return (male = data.gender);
            });
          }
          dispatch({
            type: GET_FAKE_USER,
            payload: {
              user: res.user,
              activeUser: res.activeUser,
              total: res.total,
              male: male,
              female: female,
            },
          });
        } else {
          Toast("error", res.message);
        }
      })
      .catch((error) => Toast("error", error.message));
  };
