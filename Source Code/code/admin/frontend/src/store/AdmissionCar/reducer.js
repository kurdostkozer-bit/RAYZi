import * as ActionType from "./type";

const initialState = {
  admissionSVGA: [],
  Dialogue: false,
  DialogueData: null,
  DialogueType: "",
};

export const admissionSVGAReducer = (state = initialState, action) => {
  switch (action.type) {
    case ActionType.GET_ADMISSION_CAR_GIF:
      return {
        ...state,
        admissionSVGA: action.payload,
      };

    case ActionType.OPEN_DIALOGUE_ADMISSION_CAR:
      return {
        ...state,
        Dialogue: true,
        DialogueData: action.payload?.data ? action.payload?.data : null,
        DialogueType: action.payload?.type ? action.payload?.type : "",
      };

    case ActionType.CLOSE_DIALOGUE_ADMISSION_CAR:
      return {
        ...state,
        Dialogue: false,
        DialogueData: null,
        DialogueType: "",
      };
    case ActionType.CERATE_ADMISSION_CAR_GIF:
      const data = [...state.admissionSVGA, action.payload];
      return {
        ...state,
        admissionSVGA: data,
      };

    case ActionType.UPDATE_ADMISSION_CAR_GIF:
      return {
        ...state,
        admissionSVGA: state.admissionSVGA.map((admissionSVGA) => {
          if (admissionSVGA._id === action.payload.id)
            return action.payload.data;
          else return admissionSVGA;
        }),
      };

    case ActionType.DELETE_ADMISSION_CAR_GIF:
      return {
        ...state,
        admissionSVGA: state.admissionSVGA.filter(
          (data) => data._id !== action.payload && data
        ),
      };
    default:
      return state;
  }
};
