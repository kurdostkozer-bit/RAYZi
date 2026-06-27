import setToken from "../../util/SetToken";

import { jwtDecode } from "jwt-decode";

import { key } from "../../util/Config";
import setDevKey from "../../util/SetDevKey";

import {
  CLOSE_ADMIN_DIALOG,
  GET_SELLER,
  OPEN_ADMIN_DIALOG,
  SET_ADMIN,
  UNSET_ADMIN,
  UPDATE_PROFILE,
} from "./types";

const initialState = {
  isAuth: false,
  admin: {},
  seller: {},
  dialog: false,
  dialogData: null,
};

const adminReducer = (state = initialState, action) => {
  let decoded;

  switch (action.type) {
    case SET_ADMIN:
      if (action.payload) {
        decoded = jwtDecode(action.payload);
      }
      setToken(action.payload);
      setDevKey(key);
      localStorage.setItem("AGENCY", JSON.stringify(decoded));
      localStorage.setItem("TOKEN", action.payload);
      localStorage.setItem("KEY", key);

      return {
        ...state,
        isAuth: true,
        admin: decoded,
        seller: decoded,
      };

    case UNSET_ADMIN:
      localStorage.removeItem("TOKEN");
      localStorage.removeItem("KEY");

      setDevKey(null);
      setToken(null);
      return {
        ...state,
        isAuth: false,
        admin: {},
        seller: {},
      };

    case UPDATE_PROFILE:
      return {
        ...state,
        admin: {
          ...state.admin,
          _id: action.payload._id,
          name: action.payload.name,
          email: action.payload.email,
          image: action.payload.image,
          flag: action.payload.flag,
        },
      };

    case GET_SELLER:
      return {
        ...state,
        seller: action?.payload,
      };
    case OPEN_ADMIN_DIALOG:
      return {
        ...state,
        dialog: true,
        dialogData: action.payload || null,
      };
    case CLOSE_ADMIN_DIALOG:
      return {
        ...state,
        dialog: false,
        dialogData: null,
      };
    default:
      return state;
  }
};

export default adminReducer;
