import {
  ACCEPT_HOST_REQUEST,
  CLOSE_AGENCY_CODE_DIALOGUE,
  CLOSE_REASON_DIALOGUE,
  GET_HOST_REQUEST,
  OPEN_AGENCY_CODE_DIALOGUE,
  OPEN_REASON_DIALOGUE,
} from "./types";

const initialState = {
  request: [],
  dialog: false,
  dialogData: null,
};

const hostRequestReducer = (state = initialState, action) => {
  switch (action.type) {
    case GET_HOST_REQUEST:
      
     
      return {
        ...state,
        request: action.payload,
      };

    case ACCEPT_HOST_REQUEST:
      return {
        ...state,
        request: state.request.filter(
          (request) => request._id !== action?.payload
        ),
      };
    case OPEN_REASON_DIALOGUE:
      return {
        ...state,
        dialog: true,
        dialogData: action.payload || null,
      };
    case CLOSE_REASON_DIALOGUE:
      return {
        ...state,
        dialog: false,
        dialogData: null,
      };

    default:
      return state;
  }
};

export default hostRequestReducer;
