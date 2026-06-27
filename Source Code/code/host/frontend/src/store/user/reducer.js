import { GET_COINSELLER_UNIQUEID, GET_USER } from "./types";

const initialState = {
  user: [],

  totalUser: 0,
  userId: [],
};

const userReducer = (state = initialState, action) => {
  switch (action.type) {
    case GET_USER:
      return {
        ...state,
        user: action.payload.agencyWiseHost,
        totalUser: action.payload.total,
      };

    case GET_COINSELLER_UNIQUEID:
      return {
        ...state,
        userId: action.payload,
      };
    default:
      return state;
  }
};

export default userReducer;
