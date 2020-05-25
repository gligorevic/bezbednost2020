import { combineReducers } from "redux";
import certificates from "./certificates";
import user from "./user";

const appReducer = combineReducers({ certificates, user });

const rootReducer = (state, action) => {
  if (action.type === "USER_LOGOUT") {
    state = undefined;
  }

  return appReducer(state, action);
};

export default rootReducer;
