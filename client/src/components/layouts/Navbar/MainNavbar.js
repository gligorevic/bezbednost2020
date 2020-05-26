import React from "react";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import IconButton from "@material-ui/core/IconButton";
import MenuIcon from "@material-ui/icons/Menu";

import useMediaQuery from "@material-ui/core/useMediaQuery";

import clsx from "clsx";
import Drawer from "@material-ui/core/Drawer";

import List from "@material-ui/core/List";

import Divider from "@material-ui/core/Divider";
import ChevronLeftIcon from "@material-ui/icons/ChevronLeft";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import InboxIcon from "@material-ui/icons/MoveToInbox";
import { withRouter } from "react-router-dom";
import { connect } from "react-redux";
import { logout } from "../../../store/actions/auth";
import AccountCircleIcon from "@material-ui/icons/AccountCircle";
import ExitToAppIcon from "@material-ui/icons/ExitToApp";
import DirectionsCarIcon from "@material-ui/icons/DirectionsCar";

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  menuButton: {
    marginRight: theme.spacing(2),
  },
  title: {
    flexGrow: 1,
    display: "flex",
    alignItems: "center",
  },
  appBar: {
    transition: theme.transitions.create(["margin", "width"], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
  },
  appBarShift: {
    width: `calc(100% - ${drawerWidth}px)`,
    marginLeft: drawerWidth,
    transition: theme.transitions.create(["margin", "width"], {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    }),
  },
  hide: {
    display: "none",
  },
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
  },
  drawerPaper: {
    width: drawerWidth,
  },
  drawerHeader: {
    display: "flex",
    alignItems: "center",
    padding: theme.spacing(0, 1),
    // necessary for content to be below app bar
    ...theme.mixins.toolbar,
    justifyContent: "flex-end",
  },
  content: {
    flexGrow: 1,
    padding: theme.spacing(3),
    transition: theme.transitions.create("margin", {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    marginLeft: -drawerWidth,
  },
  contentShift: {
    transition: theme.transitions.create("margin", {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    }),
    marginLeft: 0,
  },
  username: {
    fontSize: 15,
    fontWeight: "bold",
    marginLeft: 20,
    marginRight: 5,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
}));

const MainNavbar = ({
  history,
  location,
  logout,
  user: { isAuthenticated, user },
}) => {
  const classes = useStyles();
  const theme = useTheme();
  const tablet = useMediaQuery(theme.breakpoints.up("sm"));

  const [open, setOpen] = React.useState(false);

  const handleDrawerOpen = () => {
    setOpen(true);
  };

  const handleDrawerClose = () => {
    setOpen(false);
  };
  return (
    <div className={classes.root}>
      <AppBar position="static">
        <Toolbar className={classes.navContainer}>
          {!tablet && (
            <IconButton
              color="inherit"
              aria-label="open drawer"
              onClick={handleDrawerOpen}
              edge="start"
              className={clsx(classes.menuButton, open && classes.hide)}
            >
              <MenuIcon />
            </IconButton>
          )}

          <Typography variant="h6" className={classes.title}>
            <DirectionsCarIcon
              style={{ fontSize: 32, paddingBottom: 5, marginRight: 4 }}
            />
            Rentaj care
          </Typography>
          {tablet && (
            <>
              <Button
                color="inherit"
                onClick={() => location.pathname !== "/" && history.push("/")}
              >
                Home
              </Button>

              <Button color="inherit">MenuItem</Button>
              <Button color="inherit">MenuItem</Button>
              {!isAuthenticated ? (
                <>
                  <Button
                    color="inherit"
                    onClick={() =>
                      location.pathname !== "/login" && history.push("/login")
                    }
                  >
                    Login
                  </Button>
                  <Button
                    color="inherit"
                    onClick={() =>
                      location.pathname !== "/registration" &&
                      history.push("/registration")
                    }
                  >
                    Registrate
                  </Button>
                </>
              ) : (
                <>
                  <Button
                    color="inherit"
                    onClick={() => {
                      switch (user.role.length > 0 && user.role[0].name) {
                        case "ROLE_ADMIN":
                          history.push("/admin");
                      }
                    }}
                  >
                    Profile
                  </Button>
                  <div className={classes.username}>
                    <AccountCircleIcon />
                    <p style={{ paddingLeft: 5 }}>{user.username}</p>
                  </div>
                  <Button
                    color="inherit"
                    onClick={() => {
                      logout();
                      history.push("/");
                    }}
                  >
                    <ExitToAppIcon />
                    Logout
                  </Button>
                </>
              )}
            </>
          )}
        </Toolbar>
      </AppBar>
      {!tablet && (
        <Drawer
          className={classes.drawer}
          variant="persistent"
          anchor="left"
          open={open}
          classes={{
            paper: classes.drawerPaper,
          }}
        >
          <div className={classes.drawerHeader}>
            <IconButton onClick={handleDrawerClose}>
              <ChevronLeftIcon />
            </IconButton>
          </div>
          <Divider />
          <List>
            <ListItem button>
              <ListItemIcon>
                <InboxIcon />
              </ListItemIcon>
              <ListItemText primary={"Home"} />
            </ListItem>
            <ListItem button>
              <ListItemIcon>
                <InboxIcon />
              </ListItemIcon>
              <ListItemText primary={"Profile"} />
            </ListItem>
            <ListItem button>
              <ListItemIcon>
                <InboxIcon />
              </ListItemIcon>
              <ListItemText primary={"Menu item"} />
            </ListItem>
            <Divider />
            <ListItem button>
              <ListItemIcon>
                <InboxIcon />
              </ListItemIcon>
              <ListItemText primary={"Login"} />
            </ListItem>
          </List>
        </Drawer>
      )}
    </div>
  );
};

function mapStateToProps(state) {
  return {
    user: state.user,
  };
}

export default withRouter(connect(mapStateToProps, { logout })(MainNavbar));
