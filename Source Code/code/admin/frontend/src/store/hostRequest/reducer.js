import {
  ACCEPT_HOST_REQUEST,
  CLOSE_AGENCY_CODE_DIALOGUE,
  CLOSE_BANK_DETAILS_DIALOGUE,
  CLOSE_REASON_DIALOGUE,
  GET_HOST_REQUEST,
  OPEN_AGENCY_CODE_DIALOGUE,
  OPEN_BANK_DETAILS_DIALOGUE,
  OPEN_REASON_DIALOGUE,
} from "./type";

const initialState = {
  request: [],
  total: 0,
  dialog: false,
  dialogData: null,
  dialog1: false,
  dialogData1: null,
  dialog2: false,
  dialogData2: null,
};

const hostRequestReducer = (state = initialState, action) => {
  switch (action.type) {
    case GET_HOST_REQUEST:
      return {
        ...state,
        request: action.payload.data,
        total: action.payload.total,
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
    case OPEN_AGENCY_CODE_DIALOGUE:
      return {
        ...state,
        dialog1: true,
        dialogData1: action.payload || null,
      };
    case CLOSE_AGENCY_CODE_DIALOGUE:
      return {
        ...state,
        dialog1: false,
        dialogData1: null,
      };
    case OPEN_BANK_DETAILS_DIALOGUE:
      return {
        ...state,
        dialog2: true,
        dialogData2: action.payload || null,
      };
    case CLOSE_BANK_DETAILS_DIALOGUE:
      return {
        ...state,
        dialog2: false,
        dialogData2: null,
      };
    default:
      return state;
  }
};

export default hostRequestReducer;
