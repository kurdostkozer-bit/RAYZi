import {
    GET_HOST,
} from "./type";

//   import { baseURL, key } from "../../util/Config";
  import { apiInstanceFetch } from "../../util/api";
import { Toast } from "../../util/Toast";


export const getHost = (start,limit,search) => (dispatch) => {
    apiInstanceFetch
      .get(
        `host?start=${start}&limit=${limit}&search=${search}`
      )
      .then((res) => {
        if (res.status) {
          dispatch({
            type: GET_HOST,
            payload: {
              data: res.user,
              total:res.total
            },
          });
        } else {
          Toast("error", res.message);
        }
      })
      .catch((error) => Toast("error", error.message));
  };








