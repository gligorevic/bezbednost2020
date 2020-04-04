import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { getAllKeyUsages } from "../../store/actions/certificates";

import { makeStyles } from "@material-ui/core/styles";
import Input from "@material-ui/core/Input";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import FormControl from "@material-ui/core/FormControl";
import Select from "@material-ui/core/Select";
import Checkbox from "@material-ui/core/Checkbox";
import ListItemText from "@material-ui/core/ListItemText";
import TextField from "@material-ui/core/TextField";

const useStyles = makeStyles((theme) => ({
  formControl: {
    margin: theme.spacing(1),
    minWidth: 270,
    maxWidth: 300,
  },
  root: {
    display: "flex",
    flexDirection: "column",
  },
  textCenter: {
    textAlign: "center",
  },
}));

const AdminCertificateForm = ({
  getAllKeyUsages,
  keyUsages,
  usages,
  setUsage,
  state,
  setState,
}) => {
  const classes = useStyles();

  useEffect(() => {
    getAllKeyUsages();
  }, []);

  const handleChange = (event) => {
    setUsage(event.target.value);
  };

  const handleChangeTextField = (e) => {
    setState({ ...state, [e.target.name]: e.target.value });
  };

  return (
    <>
      <h1 className={classes.textCenter}>Certificate data</h1>
      <form className={classes.root} noValidate autoComplete="off">
        <TextField
          name="commonName"
          onChange={handleChangeTextField}
          value={state.commonName}
          label="CommonName"
        />
        <TextField
          name="organization"
          onChange={handleChangeTextField}
          value={state.organization}
          label="Organization"
        />
        <TextField
          name="organizationalUnit"
          onChange={handleChangeTextField}
          value={state.organizationalUnit}
          label="Organization Unit"
        />
        <TextField
          name="city"
          onChange={handleChangeTextField}
          value={state.city}
          label="City"
        />
        <TextField
          name="countryOfState"
          onChange={handleChangeTextField}
          value={state.countryOfState}
          label="CountryOfState"
        />
        <TextField
          name="country"
          onChange={handleChangeTextField}
          value={state.country}
          label="Country"
        />
        <TextField
          name="mail"
          onChange={handleChangeTextField}
          value={state.mail}
          label="Mail"
        />

        <FormControl className={classes.formControl}>
          <InputLabel id="demo-mutiple-checkbox-label">KeyUsage</InputLabel>
          {keyUsages && keyUsages.length > 0 && (
            <Select
              labelId="demo-mutiple-checkbox-label"
              id="demo-mutiple-checkbox"
              multiple
              value={usages}
              onChange={handleChange}
              input={<Input />}
              renderValue={(selected) => selected.join(", ")}
            >
              {keyUsages.map((usage) => (
                <MenuItem key={usage} value={usage}>
                  <Checkbox checked={usages.indexOf(usage) > -1} />
                  <ListItemText primary={usage} />
                </MenuItem>
              ))}
            </Select>
          )}
        </FormControl>
      </form>
    </>
  );
};

const mapStateToProps = (state) => ({
  keyUsages: state.certificates.keyUsages,
});

export default connect(mapStateToProps, { getAllKeyUsages })(
  AdminCertificateForm
);
