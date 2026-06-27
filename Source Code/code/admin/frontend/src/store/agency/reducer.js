import {
    OPEN_AGENCY_DIALOG,
    CLOSE_AGENCY_DIALOG,
    GET_AGENCY,
    CREATE_NEW_AGENCY,
    EDIT_AGENCY,
    SET_CREATE_AGENCY_DONE,
    UNSET_CREATE_AGENCY_DONE,
    SET_UPDATE_AGENCY_DONE,
    UNSET_UPDATE_AGENCY_DONE,
    ENABLE_DISABLE_AGENCY,
    GET_AGENCY_DROPDOWN,
    GET_AGENCY_WISE_HOST,
    BLOCK_UNBLOCK_SWITCH_AGENCYUSERS,
    ACCEPT_DECLINE_AGENCY,
    GET_UNVERIFIED_AGENCY,
    REDEEM_ENDABLED_SWITCH_AGENCY,
    REDEEM_ENDABLED_SWITCH_HOST,
    GET_AGENCY_HISTORY,
  } from "./type";
  
  const initialState = {
    agency: [],
    total: 0,
    agencyWiseHost: [],
    agencyHistory: [],
    agencyHistoryTotal: 0,
    totalAgencyWiseHost: 0,
    agencyDropdown: [],
    dialog: false,
    dialogData: null,
    createDone: false,
    updateDone: false,
  };
  
  const agencyReducer = (state = initialState, action) => {
    switch (action.type) {
      case GET_AGENCY:
        return {
          ...state,
          agency: action.payload.data,
          total: action.payload.total,
        };
  
      case GET_UNVERIFIED_AGENCY:
        return {
          ...state,
          agency: action.payload.data,
        };
      case GET_AGENCY_HISTORY:
        return {
          ...state,
          agencyHistory: action.payload,
          agencyHistoryTotal: action?.payload?.total
        };
      case ACCEPT_DECLINE_AGENCY:
        return {
          ...state,
          agency: state.agency.filter((agency) => agency?._id !== action.payload),
        };
      case GET_AGENCY_WISE_HOST:
        return {
          ...state,
          agencyWiseHost: action.payload.data,
          totalAgencyWiseHost: action.payload.total,
        };
      case BLOCK_UNBLOCK_SWITCH_AGENCYUSERS:
        return {
          ...state,
          agencyWiseHost: state.agencyWiseHost.map((agency) => {
            if (agency._id === action.payload._id)
              return {
                ...agency,
                isBlock: action.payload.isBlock,
              };
            else return agency;
          }),
        };
      case REDEEM_ENDABLED_SWITCH_HOST:
        return {
          ...state,
          agencyWiseHost: state.agencyWiseHost.map((agency) => {
            if (agency._id === action.payload._id)
              return {
                ...agency,
                redeemEnable: action.payload.redeemEnable,
              };
            else return agency;
          }),
        };
      case REDEEM_ENDABLED_SWITCH_AGENCY:
        return {
          ...state,
          agency: state.agency.map((agency) => {
            if (agency?._id === action?.payload?._id)
              return {
                ...agency,
                redeemEnable: action.payload.redeemEnable,
              };
            else return agency;
          }),
        };
      case GET_AGENCY_DROPDOWN:
        return {
          ...state,
          agencyDropdown: action.payload,
        };
  
      case CREATE_NEW_AGENCY:
        const data = [...state.agency];
        data.unshift(action.payload);
        return {
          ...state,
          agency: data,
        };
  
      case EDIT_AGENCY:
        return {
          ...state,
          agency: state.agency.map((agency) => {
            if (agency._id === action.payload.id) return action.payload.data;
            else return agency;
          }),
        };
  
      case SET_CREATE_AGENCY_DONE:
        return {
          ...state,
          createDone: true,
        };
      case UNSET_CREATE_AGENCY_DONE:
        return {
          ...state,
          createDone: false,
        };
      case SET_UPDATE_AGENCY_DONE:
        return {
          ...state,
          updateDone: true,
        };
      case UNSET_UPDATE_AGENCY_DONE:
        return {
          ...state,
          updateDone: false,
        };
      case OPEN_AGENCY_DIALOG:
        return {
          ...state,
          dialog: true,
          dialogData: action.payload || null,
        };
      case CLOSE_AGENCY_DIALOG:
        return {
          ...state,
          dialog: false,
          dialogData: null,
        };
      case ENABLE_DISABLE_AGENCY:
        return {
          ...state,
          agency: state.agency.map((agency) => {
            if (agency?._id === action?.payload?._id)
              return {
                ...agency,
                isActive: action?.payload?.isActive,
              };
            else return agency;
          }),
        };
      default:
        return state;
    }
  };
  
  export default agencyReducer;
  