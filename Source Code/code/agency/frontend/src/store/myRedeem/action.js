import axios from "axios";
import { Toast } from "../../util/Toast";
import { apiInstanceFetch } from "../../util/api";
import {
  GET_MY_REDEEM_AGENCY_PANEL,
  GET_PERTICULAR_AGENCY_USER_REDEEM_REQUEST,
  NEW_REDEEM_CREATE,
} from "./type";

export const getMyRedeem =
  (agencyId, type, page, rowsPerPage) => (dispatch) => {
    apiInstanceFetch
      .get(
        `agencyRedeem/getAgencyWise?agencyId=${agencyId}&type=${type}&start=${page}&limit=${rowsPerPage}`
      )
      .then((res) => {
        if (res?.status) {
          dispatch({
            type: GET_MY_REDEEM_AGENCY_PANEL,
            payload: {
              data: res?.data,
              total: res?.total,
              totalRevenue: res?.totalRevenue,
            },
          });
        } else {
          Toast("error", res?.message);
        }
      })
      .catch((error) => {
        Toast("error", error.message);
      });
  };
export const getParticularAgencyUserRedeem =
  (agencyId, page, rowsPerPage, type) => (dispatch) => {
    apiInstanceFetch
      .get(
        `agency/getUserRedeem?agencyId=${agencyId}&type=${type}&start=${page}&limit=${rowsPerPage}`
      )
      .then((res) => {
        if (res?.status) {
          dispatch({
            type: GET_PERTICULAR_AGENCY_USER_REDEEM_REQUEST,
            payload: {
              data: res?.data,
              total: res?.total,
              totalRevenue: res?.totalRevenue,
            },
          });
        } else {
          Toast("error", res?.message);
        }
      })
      .catch((error) => {
        Toast("error", error.message);
      });
  };

export const createAgencyRedeem = (data) => (dispatch) => {
  axios
    .post(`agencyRedeem/store`, data)
    .then((res) => {
      if (res.data.status) {
        Toast("success", "New Redeem Request Create successfully!");
        dispatch({ type: NEW_REDEEM_CREATE, payload: res?.data?.data });
      } else {
        Toast("error", res.data.message);
      }
    })
    .catch((error) => ("error", error));
};
