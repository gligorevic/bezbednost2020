import {
  SET_KEYUSAGES,
  SET_ALL_CERTIFICATES,
  SET_ALL_REVOCATED_CERTS,
} from "../actionTypes";

const DEFAULT_STATE = {
  keyUsages: null,
  allCertificates: null,
  allRevocatedCerts: null,
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
    case SET_ALL_REVOCATED_CERTS: {
      return {
        ...state,
        allRevocatedCerts: action.allRevocatedCerts,
      };
    }
    default:
      return state;
  }
};
