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
import DescriptionIcon from "@material-ui/icons/Description";

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
  changeMaxDate,
  changeMinDate,
  handleNext,
  setUsage,
}) {
  const classes = useStyles();

  const [issuerCerts, setIssuerCerts] = useState([]);

  useEffect(() => {
    (async () => {
      const resp = await axios.get("/api/admin/getAllIssuerCerts");
      setIssuerCerts(resp.data);
    })();
  }, []);

  const handleChange = (cert) => {
    setCertificate(cert);
    changeMaxDate(cert.notAfter);
    changeMinDate(cert.notBefore);
  };

  const handleRoot = () => {
    setCertificate(-1);
    setUsage(["CRL_SIGN", "DIGITAL_SIGNATURE", "KEY_CERT_SIGN"]);
    handleNext();
  };

  return (
    <>
      <h1 className={classes.textCenter}>Select Signing Certificate</h1>
      <div>
        <Grid container spacing={3}>
          <Grid item xs={9}>
            <Paper style={{ height: certificate === "" && 477 }}>
              {certificate !== "" ? (
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
              ) : (
                <div
                  style={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    flexDirection: "column",
                    height: "100%",
                  }}
                >
                  <DescriptionIcon
                    style={{
                      fontSize: 105,
                      color: "#e1e1d1",
                      // position: "relative",
                      // top: "50%",
                      // left: "50%",
                      // transform: "translate(-50%, -80%)",
                      display: "inline-block",
                    }}
                  />
                  <p
                    style={{
                      textAlign: "center",
                      color: "#a1a1a1",
                      // position: "relative",
                    }}
                  >
                    {
                      'NOTE: If you select "Root certificate" key usages will be set to: "CRL_SIGN","DIGITAL_SIGNATURE" and "KEY_CERT_SIGN"'
                    }
                  </p>
                </div>
              )}
            </Paper>
          </Grid>
          <Grid item xs={3}>
            <Paper className={classes.paperSelect}>
              <MenuList>
                <MenuItem key={-1} onClick={() => handleRoot()}>
                  Root certificate
                </MenuItem>
                <Divider style={{ margin: 17 }} component="li" />
                {issuerCerts.map((cert) => {
                  return (
                    <MenuItem
                      selected={certificate.serialNumber === cert.serialNumber}
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
