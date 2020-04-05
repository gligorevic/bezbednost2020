import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import Divider from "@material-ui/core/Divider";
import ListItemText from "@material-ui/core/ListItemText";
import Typography from "@material-ui/core/Typography";
import Paper from "@material-ui/core/Paper";
import Chip from "@material-ui/core/Chip";

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
}));

export default function ConfirmIssuing({ certificate, issuer, usages }) {
  const classes = useStyles();

  return (
    <>
      <Paper>
        <List>
          <h3 style={{ textAlign: "center" }}>Certificate Info</h3>
          <ListItem alignItems="flex-start">
            <ListItemText
              primary="Common Name:"
              secondary={
                <Typography
                  component="span"
                  variant="body2"
                  className={classes.inline}
                  color="textPrimary"
                >
                  {certificate.commonName}
                </Typography>
              }
            />
          </ListItem>
          <ListItem alignItems="flex-start">
            <ListItemText
              primary="Organization:"
              secondary={
                <Typography
                  component="span"
                  variant="body2"
                  className={classes.inline}
                  color="textPrimary"
                >
                  {certificate.organization}
                </Typography>
              }
            />
          </ListItem>

          <ListItem alignItems="flex-start">
            <ListItemText
              primary="Organization Unit:"
              secondary={
                <Typography
                  component="span"
                  variant="body2"
                  className={classes.inline}
                  color="textPrimary"
                >
                  {certificate.organizationalUnit}
                </Typography>
              }
            />
          </ListItem>
          <Divider component="li" />
          <ListItem alignItems="flex-start">
            <ListItemText
              primary="Email:"
              secondary={
                <Typography
                  component="span"
                  variant="body2"
                  className={classes.inline}
                  color="textPrimary"
                >
                  {certificate.city}
                </Typography>
              }
            />
          </ListItem>
          <ListItem alignItems="flex-start">
            <ListItemText
              primary="Email:"
              secondary={
                <Typography
                  component="span"
                  variant="body2"
                  className={classes.inline}
                  color="textPrimary"
                >
                  {certificate.mail}
                </Typography>
              }
            />
          </ListItem>
          <ListItem alignItems="flex-start">
            <ListItemText
              primary="Country:"
              secondary={
                <Typography
                  component="span"
                  variant="body2"
                  className={classes.inline}
                  color="textPrimary"
                >
                  {certificate.country}
                </Typography>
              }
            />
          </ListItem>
          <ListItem>
            <ListItemText
              primary="Key Usages:"
              secondary={
                <Typography
                  component="div"
                  variant="body2"
                  className={classes.inline}
                  color="textPrimary"
                >
                  {usages &&
                    usages.map((usage) => (
                      <Chip
                        style={{ marginRight: 7, marginBottom: 5 }}
                        clickable
                        color="primary"
                        label={usage}
                      />
                    ))}
                </Typography>
              }
            />
          </ListItem>
          <Divider component="li" />
          <h3 style={{ textAlign: "center" }}>Issuer Info</h3>
          <ListItem alignItems="flex-start">
            <ListItemText
              primary="Issuer:"
              secondary={
                <Typography
                  component="span"
                  variant="body2"
                  className={classes.inline}
                  color="textPrimary"
                >
                  {issuer.name}
                </Typography>
              }
            />
          </ListItem>
          <ListItem alignItems="flex-start">
            <ListItemText
              primary="Organization:"
              secondary={
                <Typography
                  component="span"
                  variant="body2"
                  className={classes.inline}
                  color="textPrimary"
                >
                  {issuer.organization}
                </Typography>
              }
            />
          </ListItem>
        </List>
      </Paper>
      <h4 style={{ textAlign: "center" }}>Generate Certificate?</h4>
    </>
  );
}
