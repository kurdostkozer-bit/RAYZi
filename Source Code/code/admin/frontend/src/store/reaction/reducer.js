import * as ActionType from "./type";

const initialState = {
    reaction: [],
    dialog: false,
    dialogData: null,
};

const reactionReducer = (state = initialState, action) => {
    switch (action.type) {
        case ActionType.GET_REACTION:
            return {
                ...state,
                reaction: action.payload,
            };
        case ActionType.ADD_REACTION:
            
            const data = [...state.reaction];
            data.unshift(action.payload.data);
            return {
                ...state,
                reaction: data,
            };
        case ActionType.UPDATE_REACTION:
            
            return {
                ...state,
                reaction: state.reaction.map((reaction) => {
                    
                    if (reaction._id === action.payload.id) return action.payload.data;
                    else return reaction;
                }),
            };
        case ActionType.DELETE_REACTION:
            return {
                ...state,
                reaction: state.reaction.filter((reaction) => reaction._id !== action.payload),
            };
        case ActionType.OPEN_REACTION_DIALOG:
            return {
                ...state,
                dialog: true,
                dialogData: action.payload || null,
            };
        case ActionType.CLOSE_REACTION_DIALOG:
            return {
                ...state,
                dialog: false,
                dialogData: null,
            };

        default:
            return state;
    }
};

export default reactionReducer;
