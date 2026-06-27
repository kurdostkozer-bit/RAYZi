// import axios from "axios";
// import { Toast } from "../../util/Toast";
// import { GET_HOST_CHAT_ROOM_HISTORY } from "./types";

// export const getChatRoomHistory = (hostId, month) => (dispatch) => {
//   axios
//     .get(`host/chatRoom?hostId=${hostId}&month=${month}`)
//     .then((res) => {
//       dispatch({
//         type: GET_HOST_CHAT_ROOM_HISTORY,
//         payload: { history: res.data.data, total: res.data.total },
//       });
//     })
//     .catch((error) => console.log("error", error));
// };
