import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { CLOSE_AGENCY_CODE_DIALOGUE } from "../../../store/hostRequest/type";
import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
import { Cancel } from "@mui/icons-material";
import { acceptHostRequest, acceptHostRequestWithAgecyCode } from "../../../store/hostRequest/action";
import { getAgency } from "../../../store/agency/action";
import { warning } from "../../../util/Alert";

const AddAgencyCodeDialogue = () => {
  const dispatch = useDispatch();
  const { dialog1: open, dialogData1 } = useSelector(
    (state) => state.hostRequest
  );
  const { agency } = useSelector((state) => state?.agency);

  const [agencyCode, setAgencyCode] = useState();
  const [addAgency, setAddAgency] = useState();
  const [errors, setErrors] = useState({
    agencyCode: "",
  });


  useEffect(() => {
    dispatch(getAgency(1, 1000));
  }, [1, 1000]);

  useEffect(
    () => () => {
      setErrors({
        agencyCode: "",
      });
      setAgencyCode("");
    },
    [open]
  );

  const closePopup = () => {
    dispatch({ type: CLOSE_AGENCY_CODE_DIALOGUE });
  };

  const handleSubmit = () => {
    if (!agencyCode || agencyCode === "Select Code") {
      let error = {};
      if (!agencyCode) error.agencyCode = "AgencyCode can't be a blank!";

      if (agencyCode === "Select Code")
        error.agencyCode = "AgencyCode can't be a blank!";

      return setErrors({ ...error });
    } else {
      const data = warning("Are you sure?");
      data
        .then((isDeleted) => {
          if (isDeleted) {
            dispatch(
              acceptHostRequestWithAgecyCode(
                dialogData1?.data?._id,
                "accept",
                agencyCode
              )
            );
          }
        })
        .catch((err) => console.log(err));
    }

    closePopup();
  };

  return (
    <>
      <Dialog
        open={open}
        aria-labelledby="responsive-dialog-title"
        onClose={closePopup}
        disableBackdropClick
        disableEscapeKeyDown
        fullWidth
        sx={{ maxWidth: "100%", margin: "0 auto" }}
      >
        <DialogTitle id="responsive-dialog-title">
          <span className="text-danger font-weight-bold h4">
            Add AgencyCode Dialog
          </span>
        </DialogTitle>

        <IconButton
          style={{
            position: "absolute",
            right: 0,
          }}
        >
          <Tooltip title="Close">
            <Cancel className="text-danger" onClick={closePopup} />
          </Tooltip>
        </IconButton>
        <DialogContent>
          <div className="form-group col-12 my-3">
            <label className="mb-2 mt-2 text-gray">Select Agency Code and Name</label>

            <select
              className="form-select form-control"
              aria-label="Default select example"
              value={agencyCode}
              onChange={(e) => {
                setAgencyCode(e.target.value);

                if (!e.target.value) {
                  return setErrors({
                    ...errors,
                    agencyCode: "Please select a AgencyCode!",
                  });
                } else if (e.target.value === "Select Code") {
                  return setErrors({
                    ...errors,
                    agencyCode: "Please select a AgencyCode!",
                  });
                } else {
                  return setErrors({
                    ...errors,
                    agencyCode: "",
                  });
                }
              }}
            >
              <option selected value="Select Code">
                Select Agency Code and Name
              </option>
              {Array.isArray(agency) &&
                agency.map((agencyItem) => {
                  return (
                    <>
                      <option
                        key={agencyItem?.agencyCode}
                        value={agencyItem?.agencyCode}
                      >
                        Code: {agencyItem?.agencyCode} and  Name: {agencyItem?.name}


                      </option>
                     
                    </>
                  );
                })}
            </select>
            {errors.agencyCode && (
              <div className="ml-2 mt-1">
                {errors.agencyCode && (
                  <div className="pl-1 text__left">
                    <span className="text-red">{errors.agencyCode}</span>
                  </div>
                )}
              </div>
            )}
          </div>
          <div className="form-group col-12 my-3">
            <label className="mb-2 mt-2 text-gray">Bank Details</label>

            <textarea
              name="bank"
              id="bank"
              className="form-control"
              rows={5}
              readOnly
            >
              {dialogData1?.data.bankDetails !== ""
                ? dialogData1?.data.bankDetails
                : "-"}
            </textarea>
          </div>
          <div className={" pt-3"}>
            <button
              type="button"
              className="btn btn-outline-info ml-2 my-3 btn-round float__right icon_margin"
              onClick={closePopup}
            >
              Close
            </button>
            <button
              type="button"
              className="btn btn-round float__right my-3 btn-danger"
              onClick={handleSubmit}
            >
              Accept
            </button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default AddAgencyCodeDialogue;
