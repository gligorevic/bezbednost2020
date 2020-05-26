import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { getAllKeyUsages } from "../../store/actions/certificates";

import { makeStyles } from "@material-ui/core/styles";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import Checkbox from "@material-ui/core/Checkbox";
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

  certificateUsage: {
    marginTop: 30,
    marginBottom: 50,
  },
  certificateUsageList: {
    display: "flex",
    justifyContent: "space-between",
  },
}));

const AdminCertificateForm = ({
  getAllKeyUsages,
  keyUsages,
  usages,
  setUsage,
  state,
  setState,
  type,
  setType,
  setCertificate,
}) => {
  const classes = useStyles();

  const certType = { root: "root", ca: "ca", endEntity: "endEntity" };

  const certTypeKeyUsages = {
    root: new Map([
      ["CRL_SIGN", true],
      ["DIGITAL_SIGNATURE", true],
      ["KEY_CERT_SIGN", true],
      ["KEY_ENCIPHERMENT", true],
      ["KEY_AGREEMENT", true],
    ]),
    endEntity: new Map([
      ["DIGITAL_SIGNATURE", true],
      ["KEY_AGREEMENT", true],
    ]),
    ca: new Map([
      ["CRL_SIGN", true],
      ["KEY_CERT_SIGN", true],
      ["DIGITAL_SIGNATURE", true],
      ["KEY_ENCIPHERMENT", true],
      ["KEY_AGREEMENT", true],
    ]),
  };

  useEffect(() => {
    getAllKeyUsages();
  }, []);

  const handleChange = (event) => {
    setUsage(event.target.value);
  };

  const handleToggle = (usage) => {
    const currentIndex = usages.indexOf(usage);
    const newChecked = [...usages];

    if (currentIndex === -1) {
      newChecked.push(usage);
    } else {
      newChecked.splice(currentIndex, 1);
    }

    setUsage(newChecked);
  };

  const handleChangeTextField = (e) => {
    setState({ ...state, [e.target.name]: e.target.value });
  };

  const handleToggleType = (value) => {
    if (type === value) {
      setType(null);
      setUsage([]);
    } else {
      setType(value);
      setUsage([...certTypeKeyUsages[value].keys()]);
    }

    if (value === "root") {
      setCertificate("");
    }
  };

  return (
    <>
      <h1 className={classes.textCenter}>Certificate data</h1>
      <form noValidate autoComplete="off">
        <Grid container spacing={3}>
          <Grid item xs={4}>
            <Paper className={classes.paper}>
              <h3 style={{ textAlign: "center", margin: 0, paddingTop: 14 }}>
                Key Usage
              </h3>
              <List className={classes.root}>
                {keyUsages &&
                  keyUsages.length > 0 &&
                  keyUsages.map((value) => {
                    return (
                      <ListItem
                        key={value}
                        role={undefined}
                        dense
                        button
                        disabled={type != null && type != "ca"}
                        onClick={() => handleToggle(value)}
                      >
                        <ListItemIcon>
                          <Checkbox
                            edge="start"
                            checked={usages.indexOf(value) !== -1}
                            tabIndex={-1}
                            disableRipple
                          />
                        </ListItemIcon>
                        <ListItemText primary={value} />
                      </ListItem>
                    );
                  })}
              </List>
            </Paper>
          </Grid>
          <Grid item xs={8}>
            <Paper className={classes.root} style={{ padding: 20 }}>
              <TextField
                name="commonName"
                onChange={handleChangeTextField}
                required
                value={state.commonName}
                label="CommonName"
              />
              <TextField
                name="organization"
                onChange={handleChangeTextField}
                required
                value={state.organization}
                label="Organization"
              />
              <TextField
                name="organizationalUnit"
                onChange={handleChangeTextField}
                value={state.organizationalUnit}
                required
                label="Organization Unit"
              />
              <TextField
                name="city"
                onChange={handleChangeTextField}
                value={state.city}
                required
                label="City"
              />
              <TextField
                name="countryOfState"
                onChange={handleChangeTextField}
                value={state.countryOfState}
                required
                label="CountryOfState"
              />
              <TextField
                name="country"
                onChange={handleChangeTextField}
                value={state.country}
                required
                label="Country"
              />
              <TextField
                name="mail"
                onChange={handleChangeTextField}
                value={state.mail}
                type="email"
                required
                label="Mail"
                error={
                  state.mail.length > 2 &&
                  state.mail.match(/^\S+@\S+\.\S+$/) === null
                }
                helperText={
                  state.mail.length > 2 &&
                  state.mail.match(/^\S+@\S+\.\S+$/) === null &&
                  "Email format is example@gmail.com"
                }
              />
            </Paper>
            <Paper className={classes.certificateUsage}>
              <h3 style={{ textAlign: "center", margin: 0, paddingTop: 9 }}>
                Templates
              </h3>
              <List className={classes.certificateUsageList}>
                <ListItem
                  role={undefined}
                  dense
                  button
                  onClick={() => handleToggleType(certType.root)}
                >
                  <ListItemIcon>
                    <Checkbox
                      edge="start"
                      checked={type === certType.root}
                      tabIndex={-1}
                      disableRipple
                    />
                  </ListItemIcon>
                  <ListItemText primary="ROOT" />
                </ListItem>
                <ListItem
                  role={undefined}
                  dense
                  button
                  onClick={() => handleToggleType(certType.ca)}
                >
                  <ListItemIcon>
                    <Checkbox
                      edge="start"
                      checked={type === certType.ca}
                      tabIndex={-1}
                      disableRipple
                    />
                  </ListItemIcon>
                  <ListItemText primary="CA" />
                </ListItem>
                <ListItem
                  role={undefined}
                  dense
                  button
                  onClick={() => handleToggleType(certType.endEntity)}
                >
                  <ListItemIcon>
                    <Checkbox
                      edge="start"
                      checked={type === certType.endEntity}
                      tabIndex={-1}
                      disableRipple
                    />
                  </ListItemIcon>
                  <ListItemText primary="END-ENTITY" />
                </ListItem>
              </List>
            </Paper>
          </Grid>
        </Grid>
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
