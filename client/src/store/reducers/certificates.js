import { SET_KEYUSAGES, SET_ALL_CERTIFICATES } from "../actionTypes";

const DEFAULT_STATE = {
  keyUsages: null,
  allCertificates: null,
};

export default (state = DEFAULT_STATE, action) => {
  switch (action.type) {
    case SET_KEYUSAGES: {
      return {
        ...state,
        keyUsages: action.keyUsages,
      };
    }
    case SET_ALL_CERTIFICATES: {
      return {
        ...state,
        allCertificates: action.allCertificates,
      };
    }
    default:
      return state;
  }
};
