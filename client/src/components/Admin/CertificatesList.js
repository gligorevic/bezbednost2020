import React, { useEffect } from "react";
import Axios from "axios";
import { makeStyles } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableHead from "@material-ui/core/TableHead";
import TablePagination from "@material-ui/core/TablePagination";
import TableRow from "@material-ui/core/TableRow";
import TableSortLabel from "@material-ui/core/TableSortLabel";
import Fab from "@material-ui/core/Fab";
import AddIcon from "@material-ui/icons/Add";
import Paper from "@material-ui/core/Paper";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { getAllCertificates } from "../../store/actions/certificates";
import { Button } from "@material-ui/core";
import moment from "moment";
import Backdrop from "@material-ui/core/Backdrop";
import CircularProgress from "@material-ui/core/CircularProgress";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import Alert from "@material-ui/lab/Alert";
import Slide from "@material-ui/core/Slide";

const Transition = React.forwardRef(function Transition(props, ref) {
  return <Slide direction="up" ref={ref} {...props} />;
});

const useStyles = makeStyles((theme) => ({
  root: {
    width: "100%",
    marginTop: theme.spacing(3),
  },
  paper: {
    width: "100%",
    marginBottom: theme.spacing(2),
  },
  table: {
    minWidth: 750,
  },
  tableWrapper: {
    overflowX: "auto",
  },
  visuallyHidden: {
    border: 0,
    clip: "rect(0 0 0 0)",
    height: 1,
    margin: -1,
    overflow: "hidden",
    padding: 0,
    position: "absolute",
    top: 20,
    width: 1,
  },
  formControl: {
    minWidth: 120,
  },
  margin: {
    margin: theme.spacing(1),
  },
  extendedIcon: {
    marginRight: theme.spacing(1),
  },
  backdrop: {
    zIndex: theme.zIndex.drawer + 1,
    color: "#fff",
  },
}));

const CertificatesList = ({ getAllCertificates, certificates, history }) => {
  useEffect(() => {
    getAllCertificates();
    //eslint-disable-next-line
  }, []);
  const classes = useStyles();
  const [loading, setLoading] = React.useState(false);
  const [order, setOrder] = React.useState("asc");
  const [orderBy, setOrderBy] = React.useState("name");
  const [open, setOpen] = React.useState(false);
  const [page, setPage] = React.useState(0);
  const [rowsPerPage, setRowsPerPage] = React.useState(5);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleDownload = async (e, row) => {
    setLoading(true);
    console.log(row);
    const resp = await Axios.put(`/api/admin/download`, row);
    setLoading(false);
    if (resp.status === 200) {
      setOpen(true);
    }
  };

  const handleClose = () => {
    setOpen(false);
  };

  const sort = (certificates) => {
    return certificates.sort((a, b) => {
      if (orderBy === "name") {
        return order === "asc"
          ? a.name.ime < b.name
            ? 1
            : -1
          : b.name < a.name
          ? 1
          : -1;
      } else if (orderBy === "organization") {
        return order === "asc"
          ? a.organization - b.organization
          : b.organization - a.organization;
      } else if (orderBy === "email") {
        return order === "asc" ? a.email - b.email : b.email - a.email;
      } else if (orderBy === "notBefore") {
        return order === "asc"
          ? a.notBefore - b.notBefore
          : b.notBefore - a.notBefore;
      } else if (orderBy === "notAfter") {
        return order === "asc"
          ? a.notAfter - b.notAfter
          : b.notAfter - a.notAfter;
      } else {
        return order === "asc"
          ? a.issuerName < b.issuerName
            ? 1
            : -1
          : b.issuerName < a.issuerName
          ? 1
          : -1;
      }
    });
  };

  const handleRequestSort = (property, event) => {
    const isDesc = orderBy === property && order === "desc";
    setOrder(isDesc ? "asc" : "desc");
    setOrderBy(property);
  };

  return (
    <>
      <div className={classes.root}>
        <Fab
          variant="extended"
          color="primary"
          aria-label="add"
          className={classes.margin}
          onClick={() => {
            history.push({
              pathname: `/admin/issueCertificate`,
            });
          }}
        >
          <AddIcon className={classes.extendedIcon} variant="outlined" />
          New certificate
        </Fab>
      </div>
      <div className={classes.root}>
        {certificates && (
          <Paper className={classes.paper}>
            <div className={classes.tableWrapper}>
              <Table
                className={classes.table}
                aria-labelledby="tableTitle"
                aria-label="enhanced table"
              >
                <TableHead>
                  <TableRow>
                    <TableCell align="left">
                      <TableSortLabel
                        active={orderBy === "name"}
                        direction={order}
                        onClick={() => handleRequestSort("name")}
                      >
                        Common name
                      </TableSortLabel>
                    </TableCell>
                    <TableCell align="left">
                      <TableSortLabel
                        active={orderBy === "organization"}
                        direction={order}
                        onClick={() => handleRequestSort("organization")}
                      >
                        Organization
                      </TableSortLabel>
                    </TableCell>
                    <TableCell align="left">
                      <TableSortLabel
                        active={orderBy === "email"}
                        direction={order}
                        onClick={() => handleRequestSort("email")}
                      >
                        Email
                      </TableSortLabel>
                    </TableCell>
                    <TableCell align="left">
                      <TableSortLabel
                        active={orderBy === "issuerName"}
                        direction={order}
                        onClick={() => handleRequestSort("issuerName")}
                      >
                        Issuer
                      </TableSortLabel>
                    </TableCell>
                    <TableCell align="left">
                      <TableSortLabel
                        active={orderBy === "notBefore"}
                        direction={order}
                        onClick={() => handleRequestSort("notBefore")}
                      >
                        Valid From
                      </TableSortLabel>
                    </TableCell>
                    <TableCell align="left">
                      <TableSortLabel
                        active={orderBy === "notAfter"}
                        direction={order}
                        onClick={() => handleRequestSort("notAfter")}
                      >
                        To
                      </TableSortLabel>
                    </TableCell>
                    <TableCell></TableCell>
                    <TableCell></TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {certificates &&
                    certificates.length > 0 &&
                    sort(certificates)
                      .slice(
                        page * rowsPerPage,
                        page * rowsPerPage + rowsPerPage
                      )
                      .map((row, index) => {
                        return (
                          <>
                            <TableRow
                              hover
                              role="checkbox"
                              tabIndex={-1}
                              key={row.name}
                            >
                              <TableCell component="th" allign="left">
                                {row.name}
                              </TableCell>
                              <TableCell align="left">
                                {row.organization}
                              </TableCell>
                              <TableCell align="left">{row.email}</TableCell>

                              <TableCell align="left">
                                {row.issuerName}
                              </TableCell>
                              <TableCell align="left">
                                {moment(row.notBefore).format("YYYY-MM-DD")}
                              </TableCell>
                              <TableCell align="left">
                                {moment(row.notAfter).format("YYYY-MM-DD")}
                              </TableCell>
                              <TableCell align="right">
                                <Button
                                  variant="contained"
                                  color="primary"
                                  onClick={(event) =>
                                    handleDownload(event, row)
                                  }
                                >
                                  Download
                                </Button>
                              </TableCell>
                              <TableCell align="right">
                                <Button
                                  variant="outlined"
                                  color="secondary"
                                  onClick={() => {}}
                                >
                                  Revocate
                                </Button>
                              </TableCell>
                            </TableRow>
                          </>
                        );
                      })}
                  {rowsPerPage -
                    Math.min(
                      rowsPerPage,
                      certificates.length - page * rowsPerPage
                    ) >
                    0 && (
                    <TableRow
                      style={{
                        height:
                          53 *
                          (rowsPerPage -
                            Math.min(
                              rowsPerPage,
                              certificates.length - page * rowsPerPage
                            )),
                      }}
                    >
                      <TableCell colSpan={8} />
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </div>
            <TablePagination
              rowsPerPageOptions={[5, 10, 25]}
              component="div"
              count={certificates.length}
              rowsPerPage={rowsPerPage}
              page={page}
              backIconButtonProps={{
                "aria-label": "previous page",
              }}
              nextIconButtonProps={{
                "aria-label": "next page",
              }}
              onChangePage={handleChangePage}
              onChangeRowsPerPage={handleChangeRowsPerPage}
            />
          </Paper>
        )}
        <Backdrop className={classes.backdrop} open={loading}>
          <CircularProgress color="inherit" />
        </Backdrop>
        <Dialog
          open={open}
          TransitionComponent={Transition}
          keepMounted
          onClose={handleClose}
          aria-labelledby="alert-dialog-slide-title"
          aria-describedby="alert-dialog-slide-description"
        >
          <DialogContent>
            <Alert severity="success">
              Certificate has been downloaded to Downloads folder.
            </Alert>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose} color="primary">
              Ok
            </Button>
          </DialogActions>
        </Dialog>
      </div>
    </>
  );
};

const mapStateToProps = (state) => ({
  certificates: state.certificates.allCertificates,
});

export default withRouter(
  connect(mapStateToProps, { getAllCertificates })(CertificatesList)
);
