import {
    GET_HOST,
  } from "./type";
  
  const initialState = {
    host: [],
    total: 0,
    
  };
  

  const host = (state = initialState, action) => {    
    switch (action.type) {
      case GET_HOST:
        return {
          ...state,
          host: action.payload.data,   
          total: action.payload.total,   
        };
      
      default:
        return state;
    }
  };
  
  export default host;
  