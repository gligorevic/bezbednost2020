import React from "react";
import { Provider } from "react-redux";
import "./App.css";
import Admin from "./components/Admin/Admin";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import { configureStore } from "./store/index";

const store = configureStore();

function App() {
  return (
    <Provider store={store}>
      <Router className="App">
        <Switch>
          <Route exact path="/" render={() => <div>Home stranica</div>} />
          <Route exact path="/admin/issueCertificate" component={Admin} />
        </Switch>
      </Router>
    </Provider>
  );
}

export default App;
