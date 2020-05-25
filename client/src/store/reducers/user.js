import { SET_CURRENT_USER } from "../actionTypes";

const DEFAULT_STATE = {
  isAuthenticated: false,
  user: {},
};

export default (state = DEFAULT_STATE, action) => {
  switch (action.type) {
    case SET_CURRENT_USER:
      return {
        ...state,
        isAuthenticated: !!Object.keys(action.user).length,
        user: action.user,
        error: null,
      };
    default:
      return state;
  }
};
