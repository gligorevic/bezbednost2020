import React from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import { withRouter } from "react-router-dom";

const GenerateNewCertificateDialog = ({
  open,
  setOpen,
  generateNew,
  history,
}) => {
  const handleClose = () => {
    history.push("/");
  };

  return (
    <div>
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          Generate new certificate?
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            You have been successfully generated new certificate. Click
            "Generate New" to generate one more.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">
            Close
          </Button>
          <Button onClick={generateNew} color="primary" autoFocus>
            Generate New
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default withRouter(GenerateNewCertificateDialog);
