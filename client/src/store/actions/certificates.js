import { SET_KEYUSAGES } from "../actionTypes";
import axios from "axios";

export const setKeyUsages = (keyUsages) => ({
  type: SET_KEYUSAGES,
  keyUsages,
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
