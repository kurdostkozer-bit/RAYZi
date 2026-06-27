import * as ActionType from "./type";

const initialState = {
  avatarFrame: [],
  Dialogue: false,
  DialogueData: null,
};

export const avatarFrameReducer = (state = initialState, action) => {
  switch (action.type) {
    case ActionType.GET_AVATAR_FRAME_GIF:
      return {
        ...state,
        avatarFrame: action.payload,
      };

    case ActionType.OPEN_DIALOGUE_AVATAR_FRAME:
      

      return {
        ...state,
        Dialogue: true,
        DialogueData: action.payload || null,
      };

    case ActionType.CLOSE_DIALOGUE_AVATAR_FRAME:
      return {
        ...state,
        Dialogue: false,
        DialogueData: null,
      };
    case ActionType.CERATE_AVATAR_FRAME_GIF:
      const data = [...action.payload, ...state.avatarFrame];
      return {
        ...state,
        avatarFrame: data,
      };

    case ActionType.UPDATE_AVATAR_FRAME_GIF:
      
    return {
      ...state,
      avatarFrame: state.avatarFrame.map((avatarFrame) => {
        if (avatarFrame._id === action.payload.id) return action.payload.data;
        else return avatarFrame;
      }),
    };

    case ActionType.DELETE_AVATAR_FRAME_GIF:
      return {
        ...state,
        avatarFrame: state.avatarFrame.filter(
          (data) => data._id !== action.payload && data
        ),
      };

    default:
      return state;
  }
};
