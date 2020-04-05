import React, { useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import Container from "@material-ui/core/Container";
import CertificateSelect from "./CertificateSelect";
import ConfirmIssuing from "./ConfirmIssuing";
import AdminKeyUsageForm from "./AdminCertificateForm";
import Axios from "axios";
import GenerateNewCertificateDialog from "./GenerateNewCertificateDialog";
import Backdrop from "@material-ui/core/Backdrop";
import CircularProgress from "@material-ui/core/CircularProgress";

const useStyles = makeStyles((theme) => ({
  root: {
    width: "100%",
  },
  button: {
    marginRight: theme.spacing(1),
  },
  instructions: {
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(1),
  },
  center: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  backdrop: {
    zIndex: theme.zIndex.drawer + 1,
    color: "#fff",
  },
}));

function getSteps() {
  return [
    "Podaci o sertifikatu",
    "Sertifikat koji potpisuje",
    "Kreiraj sertifikat",
  ];
}

export default function HorizontalLinearStepper() {
  const [usages, setUsage] = React.useState([]);
  const [state, setState] = useState({
    commonName: "",
    organization: "",
    organizationalUnit: "",
    city: "",
    countryOfState: "",
    country: "",
    mail: "",
  });
  const [loading, setLoading] = useState(false);

  const [certificate, setCertificate] = React.useState("");

  const [generated, setGenerated] = React.useState(false);

  const classes = useStyles();
  const [activeStep, setActiveStep] = React.useState(0);
  const steps = getSteps();

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const generateNewCertificate = () => {
    setState({
      commonName: "",
      organization: "",
      organizationalUnit: "",
      city: "",
      countryOfState: "",
      country: "",
      mail: "",
    });
    setCertificate("");
    setActiveStep(0);
    setUsage([]);
    setGenerated(false);
  };

  function getStepContent(step) {
    switch (step) {
      case 0:
        return (
          <AdminKeyUsageForm
            usages={usages}
            setUsage={setUsage}
            state={state}
            setState={setState}
          />
        );
      case 1:
        return (
          <CertificateSelect
            usages={usages}
            certificate={certificate}
            setCertificate={setCertificate}
          />
        );
      case 2:
        return (
          <ConfirmIssuing
            certificate={state}
            issuer={certificate}
            usages={usages}
          />
        );
      default:
        return "Unknown step";
    }
  }

  const handleSubmit = async (e) => {
    setLoading(true);
    const resp = await Axios.post("/api/admin/createCertificate", {
      ...state,
      issuer: certificate.serialNumber,
      keyUsages: usages,
    });
    setLoading(false);
    if (resp.status === 201) {
      setGenerated(true);
    }
  };

  return (
    <Container maxWidth="md" className={classes.root}>
      <Stepper activeStep={activeStep}>
        {steps.map((label, index) => {
          const stepProps = {};
          const labelProps = {};

          return (
            <Step key={label} {...stepProps}>
              <StepLabel {...labelProps}>{label}</StepLabel>
            </Step>
          );
        })}
      </Stepper>
      <div style={{ marginBottom: 130 }}>
        {activeStep < steps.length && (
          <div>
            <Typography className={classes.instructions} component="div">
              {getStepContent(activeStep)}
            </Typography>
            <div className={classes.center}>
              <Button
                disabled={activeStep === 0}
                onClick={handleBack}
                className={classes.button}
              >
                Back
              </Button>

              {activeStep === steps.length - 1 ? (
                <Button
                  variant="contained"
                  color="primary"
                  onClick={handleSubmit}
                  className={classes.button}
                >
                  {" "}
                  Finish
                </Button>
              ) : (
                <Button
                  variant="contained"
                  color="primary"
                  onClick={handleNext}
                  className={classes.button}
                >
                  "Next"
                </Button>
              )}
            </div>
          </div>
        )}
      </div>
      {generated && (
        <GenerateNewCertificateDialog
          open={generated}
          setOpen={setGenerated}
          generateNew={generateNewCertificate}
        />
      )}

      <Backdrop className={classes.backdrop} open={loading}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Container>
  );
}
