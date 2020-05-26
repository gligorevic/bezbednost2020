import React, { useState, useEffect } from "react";
import Avatar from "@material-ui/core/Avatar";
import Button from "@material-ui/core/Button";
import CssBaseline from "@material-ui/core/CssBaseline";
import TextField from "@material-ui/core/TextField";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import Link from "@material-ui/core/Link";
import Grid from "@material-ui/core/Grid";
import Box from "@material-ui/core/Box";
import LockOutlinedIcon from "@material-ui/icons/LockOutlined";
import Typography from "@material-ui/core/Typography";
import { makeStyles } from "@material-ui/core/styles";
import Container from "@material-ui/core/Container";
import MainNavbar from "../layouts/Navbar/MainNavbar";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import { connect } from "react-redux";
import { registrate } from "../../store/actions/auth";
import Backdrop from "@material-ui/core/Backdrop";
import CircularProgress from "@material-ui/core/CircularProgress";
import InfoDialog from "../Dialogs/InfoDialog";

function Copyright() {
  return (
    <Typography variant="body2" color="textSecondary" align="center">
      {"Copyright © "}
      Rentaj care
      {new Date().getFullYear()}
      {"."}
    </Typography>
  );
}

const useStyles = makeStyles((theme) => ({
  paper: {
    marginTop: theme.spacing(8),
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: theme.palette.secondary.main,
  },
  form: {
    width: "100%", // Fix IE 11 issue.
    marginTop: theme.spacing(3),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
  backdrop: {
    zIndex: theme.zIndex.drawer + 1,
    color: "#fff",
  },
}));

const SignUp = ({ registrate, user, history }) => {
  const classes = useStyles();

  const [loading, setLoading] = useState(false);
  const [role, setRole] = useState("ROLE_ENDUSER");
  const [state, setState] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    passwordRepeated: "",
  });
  const [submitedEmail, setSubmitedEmail] = useState("");

  const [responseStatus, setResponseStatus] = useState(0);

  useEffect(() => {
    user.isAuthenticated && history.push("/");
  }, []);

  const handleChange = (e) => {
    setState({ ...state, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    const res = await registrate({
      firstName: state.firstName,
      lastName: state.lastName,
      email: state.email,
      password: state.password,
      roleName: role,
    });
    setResponseStatus(res.status);
    setSubmitedEmail(state.email);
    setLoading(false);
  };

  return (
    <>
      <MainNavbar />
      <Container component="main" maxWidth="xs">
        <CssBaseline />
        <div className={classes.paper}>
          <Avatar className={classes.avatar}>
            <LockOutlinedIcon />
          </Avatar>
          <Typography component="h1" variant="h5">
            Sign up
          </Typography>
          <form className={classes.form} noValidate onSubmit={handleSubmit}>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  autoComplete="fname"
                  name="firstName"
                  variant="outlined"
                  required
                  fullWidth
                  onChange={handleChange}
                  value={state.firstName}
                  id="firstName"
                  label="First Name"
                  autoFocus={responseStatus === 0}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  variant="outlined"
                  required
                  fullWidth
                  id="lastName"
                  label="Last Name"
                  name="lastName"
                  autoComplete="lname"
                  onChange={handleChange}
                  value={state.lastName}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  variant="outlined"
                  required
                  fullWidth
                  id="email"
                  label="Email Address"
                  name="email"
                  autoComplete="email"
                  onChange={(e) => handleChange(e)}
                  value={state.email}
                  error={
                    (submitedEmail === state.email && responseStatus === 400) ||
                    (state.email.length > 2 &&
                      state.email.match(/^\S+@\S+\.\S+$/) === null)
                  }
                  helperText={
                    submitedEmail === state.email && responseStatus === 400
                      ? "User Already Exists"
                      : state.email.length > 2 &&
                        state.email.match(/^\S+@\S+\.\S+$/) === null &&
                        "Email format is example@gmail.com"
                  }
                  autoFocus={responseStatus === 400}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  variant="outlined"
                  required
                  fullWidth
                  name="password"
                  label="Password"
                  type="password"
                  id="password"
                  onChange={handleChange}
                  value={state.password}
                  error={
                    state.password.length > 0 &&
                    state.password.match(
                      /^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])[a-zA-Z0-9!@#$%^&*]{6,25}$/
                    ) === null
                  }
                  helperText={
                    state.password.length < 6 && state.password.length > 0
                      ? "Password must have at least 6 characters."
                      : state.password.length > 6 &&
                        state.password.match(
                          /^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])[a-zA-Z0-9!@#$%^&*]{6,25}$/
                        ) === null &&
                        "Password must have one number, one upper-case letter and one lower-case letter."
                  }
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  variant="outlined"
                  required
                  fullWidth
                  name="passwordRepeated"
                  label="Repeat Password"
                  type="password"
                  id="passwordRepeated"
                  onChange={handleChange}
                  value={state.passwordRepeated}
                  error={
                    state.password !== state.passwordRepeated &&
                    state.passwordRepeated.length > 0
                  }
                  helperText={
                    state.password !== state.passwordRepeated &&
                    state.passwordRepeated.length > 0 &&
                    "Repeated password must be same."
                  }
                />
              </Grid>
              <Grid item xs={6}>
                <Typography variant="h6">Uloga korisnika:</Typography>
              </Grid>
              <Grid item xs={6}>
                <ButtonGroup
                  variant="contained"
                  color="primary"
                  aria-label="contained primary button group"
                  style={{ float: "right" }}
                  fullWidth
                >
                  <Button
                    variant={role === "ROLE_ENDUSER" ? "contained" : "outlined"}
                    onClick={() => setRole("ROLE_ENDUSER")}
                  >
                    ENDUSER
                  </Button>
                  <Button
                    variant={role !== "ROLE_ENDUSER" ? "contained" : "outlined"}
                    onClick={() => setRole("ROLE_AGENT")}
                  >
                    AGENT
                  </Button>
                </ButtonGroup>
              </Grid>
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Checkbox value="allowExtraEmails" color="primary" />
                  }
                  label="I want to receive inspiration, marketing promotions and updates via email."
                />
              </Grid>
            </Grid>
            <Button
              type="submit"
              fullWidth
              variant="contained"
              color="primary"
              className={classes.submit}
              disabled={
                state.email.length < 5 ||
                state.firstName.length < 2 ||
                state.lastName.length < 2 ||
                state.password.length < 6 ||
                state.password !== state.passwordRepeated ||
                state.email.match(/^\S+@\S+\.\S+$/) === null ||
                state.email === submitedEmail ||
                state.password.match(
                  /^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])[a-zA-Z0-9!@#$%^&*]{6,25}$/
                ) === null
              }
            >
              Sign Up
            </Button>
            <Grid container justify="flex-end">
              <Grid item>
                <Link href="#" variant="body2">
                  Already have an account? Sign in
                </Link>
              </Grid>
            </Grid>
          </form>
        </div>
        <Box mt={5}>
          <Copyright />
        </Box>
      </Container>
      <Backdrop className={classes.backdrop} open={loading}>
        <CircularProgress color="inherit" />
      </Backdrop>
      <InfoDialog
        open={responseStatus === 200}
        title={"Successfull registration"}
        text={
          "You are successfully registrated. You can login with given username and password."
        }
        routeToGo={"/login"}
      />
    </>
  );
};

function mapStateToProps(state) {
  return {
    user: state.user,
  };
}

export default connect(mapStateToProps, { registrate })(SignUp);
