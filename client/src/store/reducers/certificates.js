import { SET_KEYUSAGES } from "../actionTypes";

const DEFAULT_STATE = {
  keyUsages: null,
};

export default (state = DEFAULT_STATE, action) => {
  switch (action.type) {
    case SET_KEYUSAGES: {
      return {
        ...state,
        keyUsages: action.keyUsages,
      };
    }
    default:
      return state;
  }
};
