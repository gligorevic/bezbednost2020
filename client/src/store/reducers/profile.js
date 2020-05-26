import { SET_PROFILE } from "../actionTypes";

const DEFAULT_STATE = {
  profile: null,
};

export default (state = DEFAULT_STATE, action) => {
  switch (action.type) {
    case SET_PROFILE:
      return {
        ...state,
        profile: action.profile,
        error: null,
      };
    default:
      return state;
  }
};
