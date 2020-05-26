import { combineReducers } from "redux";
import certificates from "./certificates";
import user from "./user";
import profile from "./profile";

const appReducer = combineReducers({ certificates, user, profile });

const rootReducer = (state, action) => {
  if (action.type === "USER_LOGOUT") {
    state = undefined;
  }

  return appReducer(state, action);
};

export default rootReducer;
