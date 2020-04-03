import React from "react";
import "./App.css";
import { Provider } from "react-redux";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import Home from "./components/home/Home";
function App() {
  return <Home></Home>;
}

export default App;
