import React from "react";
import { Provider } from "react-redux";
import "./App.css";
import Admin from "./components/Admin/Admin";
import AdminProfile from "./components/Home/AdminProfil";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import { configureStore } from "./store/index";
import Home from "./components/Home/Home";
import Login from "./components/Pages/Login";
import PrivateAdminRoute from "./routing/PrivateAdminRoute";
import { setAuthorizationToken, setUser } from "./store/actions/auth";
import jwtDecode from "jwt-decode";

const store = configureStore();

if (localStorage.jwtToken) {
  const decodedToken = jwtDecode(localStorage.jwtToken);
  const now = new Date();
  if (Date.parse(now) / 1000 >= decodedToken.exp) {
    try {
      setAuthorizationToken(false);
      store.dispatch(setUser({}));
    } catch (err) {
      console.log(err);
    }
  } else {
    try {
      setAuthorizationToken(localStorage.jwtToken);
      store.dispatch(setUser(decodedToken));
    } catch (err) {
      store.dispatch(setUser({}));
    }
  }
}

function App() {
  return (
    <Provider store={store}>
      <Router className="App">
        <Switch>
          <Route exact path="/" component={Home} />
          <PrivateAdminRoute exact path="/admin" component={AdminProfile} />
          <PrivateAdminRoute
            exact
            path="/admin/issueCertificate"
            component={Admin}
          />
          <Route exact path="/login" render={(props) => <Login {...props} />} />
        </Switch>
      </Router>
    </Provider>
  );
}

export default App;
