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
