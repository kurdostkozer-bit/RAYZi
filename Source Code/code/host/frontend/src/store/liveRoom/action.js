import axios from "axios";
import { Toast } from "../../util/Toast";
import { GET_HOST_LIVE_ROOM_HISTORY } from "./types";

// export const getLiveRoomHistory = (hostId, month) => (dispatch) => {
//   axios
//     .get(`host/liveStreaming?hostId=${hostId}&month=${month}`)
//     .then((res) => {
      
//       dispatch({
//         type: GET_HOST_LIVE_ROOM_HISTORY,
//         payload: { history: res.data.data, total: res.data.total },
//       });
//     })
//     .catch((error) => console.log("error", error));
// };
