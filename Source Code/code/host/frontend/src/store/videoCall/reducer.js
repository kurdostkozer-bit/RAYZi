import { GET_HOST_VIDEO_CALL_HISTORY } from "./types";

const initialState = {
  videoCallHistory: [],
  totalCommission: 0,
};

const videoCallRoomReducer = (state = initialState, action) => {
  switch (action.type) {
    case GET_HOST_VIDEO_CALL_HISTORY:
      return {
        ...state,
        videoCallHistory: action.payload.history,
      };
    default:
      return state;
  }
};

export default videoCallRoomReducer;
