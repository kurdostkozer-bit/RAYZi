import { GET_COINSELLER_UNIQUEID, GET_USER , HOST_DISABLE } from "./types";

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

      case HOST_DISABLE:
        return {
          ...state,
          user: state?.user.map((data) =>
            data._id === action.payload.id ? action.payload.data : data
          ),
          hostProfile: action.payload.data,
        };
    default:
      return state;
  }
};

export default userReducer;
