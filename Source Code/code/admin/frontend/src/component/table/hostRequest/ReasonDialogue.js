import React, { useEffect, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
import { Cancel } from "@mui/icons-material";
import { useDispatch, useSelector } from "react-redux";
import { CLOSE_REASON_DIALOGUE } from "../../../store/hostRequest/type";
import { acceptHostRequest } from "../../../store/hostRequest/action";


const ReasonDialogue = () => {
  const dispatch = useDispatch();
  const { dialog: open, dialogData } = useSelector((state) => state.hostRequest);


  const [reason, setReason] = useState("");
  const [errors, setErrors] = useState({ reason: "" });

  useEffect(() => {
    setReason("");
    setErrors({ reason: "" });
  }, [open]);

  const closePopup = () => {
    dispatch({ type: CLOSE_REASON_DIALOGUE });
    setReason("");
    setErrors({ reason: "" });
  };

  const validate = () => {
    const newErrors = {};
    if (!reason.trim()) {
      newErrors.reason = "Reason can't be blank!";
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = () => {


    if (validate()) {
      // Dispatch action if validation passes
      // dispatch(acceptHostRequest(dialogData.id?._id, "decline", reason));
      // closePopup();
    }
  };

  return (
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
        <span className="text-danger font-weight-bold h4">Reason Dialog</span>
      </DialogTitle>

      <IconButton
        style={{
          position: "absolute",
          right: 0,
        }}
        onClick={closePopup}
      >
        <Tooltip title="Close">
          <Cancel className="text-danger" />
        </Tooltip>
      </IconButton>

      <DialogContent>
        <div className="form-group col-12 my-3">
          <label className="mb-2 text-gray">Reason</label>
          <input
            type="text"
            className={`form-control ${errors.reason ? "is-invalid" : ""}`}
            placeholder="Enter Reason"
            value={reason}
            onChange={(e) => {
              setReason(e.target.value);
              if (e.target.value.trim()) {
                setErrors({ reason: "" });
              }
            }}
          />
          {errors.reason && (
            <div className="invalid-feedback">{errors.reason}</div>
          )}

          <div className="my-3 pt-3 d-flex justify-content-end">
            <button
              type="button"
              className="btn btn-outline-info ml-2 btn-round float-right"
              onClick={closePopup}
              style={{ marginRight: "10px" }}
            >
              Close
            </button>
            <button
              type="button"
              className="btn btn-round float-right btn-danger"
              onClick={handleSubmit}
            >
              Submit
            </button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default ReasonDialogue;
