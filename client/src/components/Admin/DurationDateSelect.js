import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import Divider from "@material-ui/core/Divider";
import ListItemText from "@material-ui/core/ListItemText";
import Typography from "@material-ui/core/Typography";
import Paper from "@material-ui/core/Paper";
import {
  MuiPickersUtilsProvider,
  KeyboardDatePicker,
} from "@material-ui/pickers";
import { Grid } from "@material-ui/core";
import DateFnsUtils from "@date-io/date-fns";

const useStyles = makeStyles((theme) => ({
  formControl: {
    margin: "0px auto",
    minWidth: 260,
  },
  selectEmpty: {
    marginTop: theme.spacing(1),
  },
  textCenter: {
    textAlign: "center",
  },
  inline: {
    display: "inline",
  },
  paper: {
    padding: 35,
  },
}));

export default function DurationDateSelect({
  certificate,
  issuer,
  selectedDateFrom,
  setSelectedDateFrom,
  selectedDateEnd,
  setSelectedDateEnd,
}) {
  const classes = useStyles();

  const handleDateChangeFrom = (date) => {
    setSelectedDateFrom(date);
  };

  const handleDateChangeEnd = (date) => {
    setSelectedDateEnd(date);
  };

  return (
    <Paper className={classes.paper}>
      <Grid container>
        <Grid style={{ textAlign: "center" }} item sm={12} lg={3}>
          <h3>Select duration of certificate</h3>

          <Typography
            component="span"
            variant="body2"
            className={classes.inline}
            color="textPrimary"
          >
            Common Name: <em>{certificate.commonName}</em>
          </Typography>
          <Divider />
        </Grid>

        <Grid style={{ textAlign: "center" }} item sm={12} md={6} lg={3}>
          <h3>Select start date</h3>
          <MuiPickersUtilsProvider utils={DateFnsUtils} id="fromDate">
            <KeyboardDatePicker
              id="date-picker-dialog"
              label="From: "
              format="MM/dd/yyyy"
              KeyboardButtonProps={{
                "aria-label": "change date",
              }}
              minDate={issuer.notBefore}
              maxDate={selectedDateEnd}
              value={selectedDateFrom}
              onChange={handleDateChangeFrom}
            />
          </MuiPickersUtilsProvider>
        </Grid>

        <Grid style={{ textAlign: "center" }} item sm={12} md={6} lg={3}>
          <h3>Select end date</h3>
          <MuiPickersUtilsProvider utils={DateFnsUtils} id="endDate">
            <KeyboardDatePicker
              id="date-picker-dialog2"
              label="To: "
              format="MM/dd/yyyy"
              KeyboardButtonProps={{
                "aria-label": "change date",
              }}
              minDate={issuer.notBefore}
              value={selectedDateEnd}
              maxDate={issuer.notAfter}
              onChange={handleDateChangeEnd}
            />
          </MuiPickersUtilsProvider>
        </Grid>
      </Grid>
    </Paper>
  );
}
