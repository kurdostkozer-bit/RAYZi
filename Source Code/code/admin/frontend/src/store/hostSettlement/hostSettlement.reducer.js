import * as ActionType from "./hostSettlement.type";

const initialState = {
  settlement: [],
  allSettlement: [],
  agencySettlement: [],
  history: {},
  dialog: false,
  dialogData: null,
  historyCount: null,
  weekSettlement: {},
  date: "",
  hostWiseAgencySettlement: null,
  totalRevenue: null,
};

export const settlementReducer = (state = initialState, action) => {
  switch (action.type) {
    //Get Entire Settlement
    case ActionType.GET_ALL_SETTLEMENT:
      return {
        ...state,
        settlement: action.payload.data,
        historyCount: action.payload.historyCount,
      };

    //Get Agency Settlement
    case ActionType.GET_AGENCY_SETTLEMENT:
      return {
        ...state,
        agencySettlement: action.payload.data,
        historyCount: action.payload.historyCount,
      };

    case ActionType.UPDATE_PAID_SETTLEMENT:
      return {
        ...state,
        agencySettlement: state.agencySettlement.filter(
          (data) => data?._id !== action.payload.id && data
        ),

        weekSettlement:
          action.payload.type === "weekSettlement" &&
          state?.weekSettlement?.map((item) => {
            if (item?._id === action.payload.id) {
              return {
                ...item,
                statusOfTransaction: 2,
              };
            }
            return item;
          }),
      };
    case ActionType.ACTION_UPDATE_PENDING:
      return {
        ...state,
        agencySettlement:
          action.payload.type === "settlementActionDialogue" &&
          state.agencySettlement?.map((item) => {
            if (item?._id === action.payload.id) {
              return {
                ...item,
                bonusOrPenltyAmount: action.payload.data.bonusOrPenltyAmount,
                note: action.payload.data.note,
                amount: action.payload.data.amount,
                dollar: action.payload.data.dollar,
              };
            } else {
              return item;
            }
          }),
        weekSettlement:
          action.payload.type === "agencySettlementActionDialogue" &&
          state.weekSettlement?.map((item) => {
            if (item?._id === action.payload.id) {
              return {
                ...item,
                bonusOrPenltyAmount: action.payload.data.bonusOrPenltyAmount,
                note: action.payload.data.note,
                amount: action.payload.data.amount,
                dollar: action.payload.data.dollar,
              };
            } else {
              return item;
            }
          }),
      };

    //Get Agency Host Settlement
    case ActionType.GET_AGENCY_HOST_SETTLEMENT:
      return {
        ...state,
        agencyHostSettlement: action.payload.data,
        historyCount: action.payload.historyCount,
        totalRevenue: action.payload.totalRevenue,
      };

    case ActionType.GET_ALL_WEEKLY_SETTLEMENT:
      return {
        ...state,
        weekSettlement: action.payload.data,
        date: action.payload.date,
      };

    case ActionType.OPEN_DIALOGUE_SETTLEMENT:
      return {
        ...state,
        dialog: true,
        dialogData: action.payload || null,
      };
    case ActionType.CLOSE_DIALOGUE_SETTLEMENT:
      return {
        ...state,
        dialog: false,
        dialogData: null,
      };

    default:
      return state;
  }
};
