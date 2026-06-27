import axios from "axios";
import { GET_HOST_VIDEO_CALL_HISTORY } from "./types";

export const getVideoCallHistory = (hostId, month) => (dispatch) => {
  axios
    .get(`host/callHistory?hostId=${hostId}&month=${month}`)
    .then((res) => {
      dispatch({
        type: GET_HOST_VIDEO_CALL_HISTORY,
        payload: { history: res.data.data, total: res.data.total },
      });
    })
    .catch((error) => console.log("error", error));
};
