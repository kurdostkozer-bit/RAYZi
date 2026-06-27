import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
import { Cancel } from "@mui/icons-material";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { CLOSE_BANK_DETAILS_DIALOGUE } from "../../../store/hostRequest/type";
import {  warning } from "../../../util/Alert";
import { acceptHostReq } from "../../../store/hostRequest/action";

const BankDetailsDialogue = () => {
  const dispatch = useDispatch();
  const { dialog2: open, dialogData2 } = useSelector(
    (state) => state.hostRequest
  );



  const closePopup = () => {
    dispatch({ type: CLOSE_BANK_DETAILS_DIALOGUE });
  };

  const handleSubmit = () => {

    const data = warning();
    data
      .then((isDeleted) => {
        if (isDeleted) {
          dispatch(acceptHostReq(dialogData2?.data?._id, dialogData2?.type));
        }
      })
      .catch((err) => console.log(err));
    dispatch({ type: CLOSE_BANK_DETAILS_DIALOGUE });
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
        sx={{ maxWidth: "100%" , margin : "0 auto" }}
      >
        <DialogTitle id="responsive-dialog-title">
          <span className="text-danger font-weight-bold h4">
            Add Agency Code Dialog
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
            <label className="mb-2 mt-2 text-gray">Bank Details</label>

            <textarea name="bank" id="bank" className="form-control" rows={5}>
              {dialogData2?.data?.bankDetails}
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
              {dialogData2?.type === "accept" ? "Accept" : "Decline"}
            </button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default BankDetailsDialogue;
