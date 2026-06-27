import axios from 'axios';
import { Toast } from '../../util/Toast';
import {
  GET_REDEEM_OPT,
  CREATE_NEW_REDEEM_OPT,
  EDIT_REDEEM_OPT,
  CLOSE_REDEEM_OPT_DIALOG,
  DELETE_REDEEM_OPT,
  TOGGEL_REDEEM_OPT_STATUS,
  GET_REDEEM_OPT_DATA,
} from './types';
import { apiInstanceFetch } from '../../util/api';

export const getRedeemOptions = () => (dispatch) => {
  apiInstanceFetch
    .get(`paymentMethod/getPaymentMethods`)
    .then((res) => {
      if (res.status) {
        dispatch({ type: GET_REDEEM_OPT, payload: res.data });
      } else {
        Toast('error', res.message);
      }
    })
    .catch((error) => Toast('error', error.message));
};

export const getRedeemOptionsDropdown = () => (dispatch) => {
  apiInstanceFetch
    .get(`paymentMethod/fetchPaymentMethods`)
    .then((res) => {
      if (res.status) {
        dispatch({ type: GET_REDEEM_OPT_DATA, payload: res.data });
      } else {
        Toast('error', res.message);
      }
    })
    .catch((error) => Toast('error', error.message));
};

export const createRedeemOption = (data) => (dispatch) => {
  axios
    .post(`paymentMethod/addPaymentMethod`, data)
    .then((res) => {
      if (res.data.status) {
        Toast('success', 'Redeem option created successfully!');
        dispatch({ type: CLOSE_REDEEM_OPT_DIALOG });
        dispatch({ type: CREATE_NEW_REDEEM_OPT, payload: res.data.data });
      } else {
        Toast('error', res.data.message);
      }
    })
    .catch((error) => Toast('error', error.message));
};
export const editRedeemOption = (data) => (dispatch) => {
  axios
    .patch(`paymentMethod/updatePaymentMethod`, data)
    .then((res) => {
      if (res.data.status) {
        Toast('success', 'Redeem Option updated successfully!');
        dispatch({ type: CLOSE_REDEEM_OPT_DIALOG });
        dispatch({
          type: EDIT_REDEEM_OPT,
          payload: { data: res.data.data, id: data?.id },
        });
      } else {
        Toast('error', res.data.message);
      }
    })
    .catch((error) => Toast('error', error.message));
};
export const deleteRedeemOption = (optId) => (dispatch) => {
  axios
    .delete(`paymentMethod/deletePaymentMethod?id=${optId}`)
    .then((res) => {
      if (res.data.status) {
        dispatch({ type: DELETE_REDEEM_OPT, payload: optId });
      } else {
        Toast('error', res.data.message);
      }
    })
    .catch((error) => console.log(error));
};

export const updateRedeemOptStatus = (optId) => (dispatch) => {
  axios
    .patch(`paymentMethod/togglePaymentMethodStatus?id=${optId}`)
    .then((res) => {
      if (res?.data?.status) {
        dispatch({ type: TOGGEL_REDEEM_OPT_STATUS, payload: res.data.data });
        Toast('success', res?.data?.message);
      } else {
        Toast('error', res.data.message);
      }
    })
    .catch((error) => console.log(error));
};
