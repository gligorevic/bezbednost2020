import React, { useEffect } from "react";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import Divider from "@material-ui/core/Divider";
import Paper from "@material-ui/core/Paper";
import Container from "@material-ui/core/Container";

import FaceIcon from "@material-ui/icons/Face";
import EmailIcon from "@material-ui/icons/Email";
import InfoIcon from "@material-ui/icons/Info";
import BlockIcon from "@material-ui/icons/Block";
import ContactsIcon from "@material-ui/icons/Contacts";

import Grid from "@material-ui/core/Grid";

import { getProfile } from "../../store/actions/profile";

import { connect } from "react-redux";
import { Typography } from "@material-ui/core";

const Profile = ({ profile, getProfile }) => {
  useEffect(() => {
    getProfile();
  }, []);

  return (
    <Container maxWidth="xl">
      <Grid container spacing={3}>
        <Grid item sm={12} lg={6}>
          <Paper style={{ padding: "30px 10px" }}>
            <Typography variant="h3" style={{ textAlign: "center" }}>
              User info
            </Typography>
            <List component="div" aria-label="profile">
              <ListItem>
                <ListItemIcon>
                  <FaceIcon />
                </ListItemIcon>
                <ListItemText>
                  <Typography variant="h6" style={{ display: "inline-block" }}>
                    First name:
                  </Typography>
                  <span style={{ float: "right" }}>
                    {profile && profile.firstName}
                  </span>
                </ListItemText>
              </ListItem>
              <Divider style={{ marginLeft: 70 }} />
              <ListItem>
                <ListItemIcon></ListItemIcon>
                <ListItemText>
                  <Typography variant="h6" style={{ display: "inline-block" }}>
                    Last name:
                  </Typography>
                  <span style={{ float: "right" }}>
                    {profile && profile.lastName}
                  </span>
                </ListItemText>
              </ListItem>
              <Divider style={{ marginLeft: 70 }} />
              <ListItem>
                <ListItemIcon>
                  <EmailIcon />
                </ListItemIcon>
                <ListItemText>
                  <Typography variant="h6" style={{ display: "inline-block" }}>
                    Email:
                  </Typography>
                  <span style={{ float: "right" }}>
                    {profile && profile.email}
                  </span>
                </ListItemText>
              </ListItem>
              <Divider style={{ marginLeft: 70 }} />
              {profile &&
                profile.roles.map((role, index) => (
                  <ListItem>
                    <ListItemIcon>
                      {index === 0 && <ContactsIcon />}
                    </ListItemIcon>
                    <ListItemText>
                      <Typography
                        variant="h6"
                        style={{ display: "inline-block" }}
                      >
                        {`Role ${index + 1}:`}
                      </Typography>
                      <span style={{ float: "right" }}>
                        {role.name.substring(5)}
                      </span>
                    </ListItemText>
                  </ListItem>
                ))}
            </List>
          </Paper>
        </Grid>
        <Grid item sm={6}>
          <Paper style={{ padding: "30px 10px" }}>
            <Typography variant="h3" style={{ textAlign: "center" }}>
              Blocked Privileges
            </Typography>
            <List component="div" aria-label="profile">
              {profile && profile.blockedPrivileges.length > 0 ? (
                profile.blockedPrivileges.map((r) => (
                  <ListItem>
                    <ListItemIcon>
                      <BlockIcon />
                    </ListItemIcon>
                    <ListItemText>{r.name}</ListItemText>
                  </ListItem>
                ))
              ) : (
                <div
                  style={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    marginTop: 30,
                    color: "#a1a1a1",
                  }}
                >
                  <InfoIcon />
                  No blocked privileges.
                </div>
              )}
            </List>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

const mapStateToProps = (state) => ({
  profile: state.profile.profile,
});

export default connect(mapStateToProps, { getProfile })(Profile);
