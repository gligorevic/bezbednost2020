import React, { useEffect, useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import Paper from "@material-ui/core/Paper";
import MenuItem from "@material-ui/core/MenuItem";
import MenuList from "@material-ui/core/MenuList";
import axios from "axios";
import Grid from "@material-ui/core/Grid";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import Divider from "@material-ui/core/Divider";
import ListItemText from "@material-ui/core/ListItemText";
import Typography from "@material-ui/core/Typography";

const useStyles = makeStyles((theme) => ({
  formControl: {
    margin: "0px auto",
    minWidth: 260,
  },
  selectEmpty: {
    marginTop: theme.spacing(2),
  },
  textCenter: {
    textAlign: "center",
  },
  paperSelect: {
    // width: 300,
  },
  inline: {
    display: "inline",
  },
}));

export default function CertificateSelect({
  usages,
  certificate,
  setCertificate,
}) {
  const classes = useStyles();

  const [issuerCerts, setIssuerCerts] = useState([]);

  useEffect(() => {
    (async () => {
      const resp = await axios.get("/api/admin/getAllIssuerCerts");
      console.log(resp.data);
      setIssuerCerts(resp.data);
    })();
  }, []);

  const handleChange = (cert) => {
    setCertificate(cert);
  };

  return (
    <>
      <h1 className={classes.textCenter}>Select Signing Certificate</h1>
      <div>
        <Grid container spacing={3}>
          <Grid item xs={9}>
            <Paper
              className={classes.paper}
              style={{ height: certificate === "" && 500 }}
            >
              {certificate != "" && (
                <>
                  <h2
                    className={classes.textCenter}
                    style={{ margin: 0, paddingTop: 10 }}
                  >
                    Certificate Info
                  </h2>
                  <List>
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
                            {certificate.issuerName}
                          </Typography>
                        }
                      />
                    </ListItem>
                    <ListItem alignItems="flex-start">
                      <ListItemText
                        primary="Subject:"
                        secondary={
                          <Typography
                            component="span"
                            variant="body2"
                            className={classes.inline}
                            color="textPrimary"
                          >
                            {certificate.name}
                          </Typography>
                        }
                      />
                    </ListItem>
                    <Divider style={{ margin: 17 }} component="li" />
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
                        primary="Email:"
                        secondary={
                          <Typography
                            component="span"
                            variant="body2"
                            className={classes.inline}
                            color="textPrimary"
                          >
                            {certificate.email}
                          </Typography>
                        }
                      />
                    </ListItem>
                    <ListItem alignItems="flex-start">
                      <ListItemText
                        primary="Serial number:"
                        secondary={
                          <Typography
                            component="span"
                            variant="body2"
                            className={classes.inline}
                            color="textPrimary"
                          >
                            {certificate.serialNumber}
                          </Typography>
                        }
                      />
                    </ListItem>
                  </List>
                </>
              )}
            </Paper>
          </Grid>
          <Grid item xs={3}>
            <Paper className={classes.paperSelect}>
              <MenuList>
                {issuerCerts.map((cert) => {
                  return (
                    <MenuItem
                      selected={certificate.serialNumber == cert.serialNumber}
                      key={cert.serialNumber}
                      onClick={() => handleChange(cert)}
                    >
                      {`${cert.name} - ${cert.serialNumber}`}
                    </MenuItem>
                  );
                })}
              </MenuList>
            </Paper>
          </Grid>
        </Grid>
      </div>
    </>
  );
}
