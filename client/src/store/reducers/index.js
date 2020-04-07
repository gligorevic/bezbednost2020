import { combineReducers } from "redux";
import certificates from "./certificates";

const appReducer = combineReducers({ certificates });

const rootReducer = (state, action) => {
  if (action.type === "USER_LOGOUT") {
    state = undefined;
  }

  return appReducer(state, action);
};

export default rootReducer;
