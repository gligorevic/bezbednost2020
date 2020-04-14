import {
  SET_KEYUSAGES,
  SET_ALL_CERTIFICATES,
  SET_ALL_REVOCATED_CERTS,
} from "../actionTypes";
import axios from "axios";

export const setKeyUsages = (keyUsages) => ({
  type: SET_KEYUSAGES,
  keyUsages,
});

export const setAllCertificates = (allCertificates) => ({
  type: SET_ALL_CERTIFICATES,
  allCertificates,
});

export const setAllRevocatedCerts = (allRevocatedCerts) => ({
  type: SET_ALL_REVOCATED_CERTS,
  allRevocatedCerts,
});

export const getAllKeyUsages = () => async (dispatch, getState) => {
  try {
    const state = getState();
    if (
      state.certificates.keyUsages &&
      state.certificates.keyUsages.length > 0
    ) {
    } else {
      const keyUsages = await axios.get("/api/admin/getAllKeyUsages");
      dispatch(setKeyUsages(keyUsages.data));
    }
  } catch (err) {
    console.log(err);
  }
};

export const getAllCertificates = () => async (dispatch) => {
  try {
    const allCertificates = await axios.get("/api/admin/getAllCerts");
    dispatch(setAllCertificates(allCertificates.data));
  } catch (err) {
    console.log(err);
  }
};

export const getAllRevocatedCerts = () => async (dispatch) => {
  try {
    const allRevocatedCerts = await axios.get(
      "/api/admin/getAllRevocatedCerts"
    );
    dispatch(setAllRevocatedCerts(allRevocatedCerts.data));
  } catch (err) {
    console.log(err);
  }
};
