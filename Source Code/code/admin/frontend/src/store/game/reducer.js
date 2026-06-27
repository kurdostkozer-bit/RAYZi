import {
  CLOSE_GAME_DIALOG,
  CREATE_NEW_GAME,
  DELETE_GAME,
  EDIT_GAME,
  GAME_SETTING_ID,
  GET_GAME,
  OPEN_GAME_DIALOG,
} from "./types";

const initialState = {
  game: [],
  settingId:"",
  dialog: false,
  dialogData: null,
};

const gameReducer = (state = initialState, action) => {
  switch (action.type) {
    case GET_GAME:
      return {
        ...state,
        game: action.payload,
      };
      case GAME_SETTING_ID:
        return {
          ...state,
          settingId: action.payload,
        };
    case CREATE_NEW_GAME:
      return {
        ...state,
        game: action.payload,
      };
    case EDIT_GAME:
      return {
        ...state,
        game: action.payload,
      };
    case DELETE_GAME:
      return {
        ...state,
        game: action.payload,
      };
    case OPEN_GAME_DIALOG:
      return {
        ...state,
        dialog: true,
        dialogData: action.payload || null,
      };
    case CLOSE_GAME_DIALOG:
      return {
        ...state,
        dialog: false,
        dialogData: null,
      };

    default:
      return state;
  }
};

export default gameReducer;
