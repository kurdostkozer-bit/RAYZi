import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
import { Cancel } from "@mui/icons-material";
import { useDispatch, useSelector } from "react-redux";
import { CLOSE_REASON_DIALOGUE } from "../../store/hostRequest/types";
import { acceptHostRequest } from "../../store/hostRequest/action";

const ReasonDialogue = () => {
  const dispatch = useDispatch();
  const {
    dialog: open,
    dialogData,
    agency,
  } = useSelector((state) => state.hostRequest);

  const [reason, setReason] = useState();
  const [errors, setErrors] = useState({
    reason: "",
    dialogueData: "",
  });

  useEffect(
    () => () => {
      setErrors({
        reason: "",
      });
      setReason("");
    },
    [open]
  );

  const closePopup = () => {
    dispatch({ type: CLOSE_REASON_DIALOGUE });
  };
  const handleSubmit = () => {
    if (!reason || !dialogData.id) {
      let error = {};
      if (!reason) error.reason = "Reason can't be a blank!";
      if (!dialogData) error.dialogData = "Id not found!";

      return setErrors({ ...error });
    } else {
      dispatch(acceptHostRequest(dialogData?.id, "decline", reason, ""));
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
        sx={{ maxWidth: "400px" }}
      >
        <DialogTitle id="responsive-dialog-title">
          <span className="text-dark font-weight-bold h4">Reason Dialog</span>
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
        <DialogContent className="mb-2">
          <div class="form-group col-12 mY-3">
            <label class="mb-2 text-dark">Reason</label>
            <input
              type="text"
              class="form-control"
              placeholder="Enter Reason"
              required
              value={reason}
              onChange={(e) => {
                setReason(e.target.value);

                if (!e.target.value) {
                  return setErrors({
                    ...errors,
                    reason: "Reason can't be a blank!",
                  });
                } else {
                  return setErrors({
                    ...errors,
                    reason: "",
                  });
                }
              }}
            />
            {errors.reason && (
              <div className="ml-2 mt-1">
                {errors.reason && (
                  <div className="pl-1 text__left">
                    <span className="text-red">{errors.reason}</span>
                  </div>
                )}
              </div>
            )}

            <div className={"pt-3"}>
              <button
                type="button"
                className="btn btn-outline-info ml-2 btn-round float__right icon_margin"
                onClick={closePopup}
              >
                Close
              </button>
              <button
                type="button"
                className="btn btn-round float__right btn-danger"
                onClick={handleSubmit}
              >
                Submit
              </button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default ReasonDialogue;
