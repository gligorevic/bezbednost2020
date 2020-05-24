import React from "react";
import { Route, Redirect } from "react-router-dom";

import { connect } from "react-redux";

const PrivateAdminRoute = ({ user, component: Component, ...rest }) => (
  <Route
    {...rest}
    render={(props) =>
      !user.role || !user.role.some(role => role.name && role.name === "ROLE_ADMIN") ? (
        <Redirect to="/login" />
      ) : (
        <Component {...props} />
      )
    }
  />
);

const mapStateToProps = (state) => ({
  user: state.user.user,
});

export default connect(mapStateToProps)(PrivateAdminRoute);
