import {
    CLOSE_COMMISSION_DIALOG,
    CREATE_NEW_COMMISSION,
    DELETE_COMMISSION,
    EDIT_COMMISSION,
    GET_COMMISSION,
    OPEN_COMMISSION_DIALOG,
  } from "./type";
  
  const initialState = {
    commission: [],
    dialog: false,
    dialogData: null,
  };
  
  const commissionReducer = (state = initialState, action) => {
    switch (action.type) {
      case GET_COMMISSION:
        return {
          ...state,
          commission: action.payload.commission,   
        };
      case CREATE_NEW_COMMISSION:
        const data = [...state.commission];
        data.unshift(action.payload);
        return {
          ...state,
          commission: data,
        };
      case EDIT_COMMISSION:
        return {
          ...state,
          commission: state.commission.map((commission) => {
            if (commission._id === action.payload.id) return action.payload.data;
            else return commission;
          }),
        };
      case DELETE_COMMISSION:
        return {
          ...state,
          commission: state.commission.filter((commission) => commission._id !== action.payload),
        };
      case OPEN_COMMISSION_DIALOG:
        return {
          ...state,
          dialog: true,
          dialogData: action.payload || null,
        };
      case CLOSE_COMMISSION_DIALOG:
        return {
          ...state,
          dialog: false,
          dialogData: null,
        };
  
      default:
        return state;
    }
  };
  
  export default commissionReducer;
  