import { SET_PROFILE } from "../actionTypes";
import axios from "axios";

export const setProfile = (profile) => ({
  type: SET_PROFILE,
  profile,
});

export const getProfile = () => async (dispatch, getState) => {
  try {
    const profileState = getState().profile.profile;
    if (profileState === null) {
      const email = getState().user.user.username;
      const profile = await axios.get(`/auth/user/${email}`);
      dispatch(setProfile(profile.data));
    }
  } catch (err) {
    console.log(err);
  }
};
