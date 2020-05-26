import { SET_CURRENT_USER, USER_LOGOUT } from "../actionTypes";
import axios from "axios";
import jwtDecode from "jwt-decode";

export const setUser = (user) => ({
  type: SET_CURRENT_USER,
  user,
});

export const setAuthorizationToken = (token) => {
  if (token) {
    axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  } else {
    delete axios.defaults.headers.common["Authorization"];
  }
};

export function logout() {
  return (dispatch) => {
    localStorage.clear();
    setAuthorizationToken(false);
    dispatch(setUser({}));
    dispatch({ type: USER_LOGOUT });
  };
}

export const authUser = (userData) => async (dispatch) => {
  try {
    const jwt = await axios.post("/auth/login", userData);
    const pureJwt = jwt.data.slice(7);
    localStorage.setItem("jwtToken", pureJwt);
    setAuthorizationToken(pureJwt);
    const decodedToken = jwtDecode(pureJwt);
    console.log(decodedToken);
    dispatch(setUser(decodedToken));
  } catch (err) {
    console.log(err);
  }
};

export const registrate = (userData) => async (dispatch) => {
  try {
    const response = await axios.post("/auth/user", userData);
    return response;
  } catch (err) {
    console.log(err);
    return err.response;
  }
};
