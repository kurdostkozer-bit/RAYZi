import { GET_HOST_LIVE_ROOM_HISTORY } from "./types";

const initialState = {
  liveRoomHistory: [],
};

const liveRoomReducer = (state = initialState, action) => {
  switch (action.type) {
    case GET_HOST_LIVE_ROOM_HISTORY:
      
      return {
        ...state,
        liveRoomHistory: action.payload.history,
      };
    default:
      return state;
  }
};

export default liveRoomReducer;
