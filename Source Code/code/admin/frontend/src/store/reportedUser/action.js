import axios from "axios";
import { Toast } from "../../util/Toast";

import { GET_REPORTED_USER } from "./types";
import { apiInstanceFetch } from "../../util/api";

export const getReportedUser = () => (dispatch) => {
  apiInstanceFetch
    .get("report")
    .then((res) => {
      if (res.status) {
        dispatch({ type: GET_REPORTED_USER, payload: res.report });
      } else {
        Toast("error", res.message);
      }
    })
    .catch((error) => Toast("error", error.message));
};
