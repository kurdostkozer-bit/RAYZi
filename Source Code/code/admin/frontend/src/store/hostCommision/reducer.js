import { CLOSE_HOST_COMMISSION_DIALOG, CREATE_NEW_HOST_COMMISSION, DELETE_HOST_COMMISSION, EDIT_HOST_COMMISSION, GET_HOST_COMMISSION, OPEN_HOST_COMMISSION_DIALOG } from "./type";


const initialState = {
  hostCommission: [],
  dialog: false,
  dialogData: null,
};

const hostCommissionReducer = (state = initialState, action) => {
  switch (action.type) {
    case GET_HOST_COMMISSION:
    
      return {
        ...state,
        hostCommission: action.payload?.commission,
      };
    case CREATE_NEW_HOST_COMMISSION:
      const data = [...state.hostCommission];
      data.unshift(action.payload);
      return {
        ...state,
        hostCommission: data,
      };
    case EDIT_HOST_COMMISSION:
      return {
        ...state,
        hostCommission: state.hostCommission.map((hostCommission) => {
          if (hostCommission._id === action.payload.id) return action.payload.data;
          else return hostCommission;
        }),
      };
    case DELETE_HOST_COMMISSION:
      return {
        ...state,
        hostCommission: state.hostCommission.filter((hostCommission) => hostCommission._id !== action.payload),
      };
    case OPEN_HOST_COMMISSION_DIALOG:
      return {
        ...state,
        dialog: true,
        dialogData: action.payload || null,
      };
    case CLOSE_HOST_COMMISSION_DIALOG:
      return {
        ...state,
        dialog: false,
        dialogData: null,
      };

    default:
      return state;
  }
};

export default hostCommissionReducer;
