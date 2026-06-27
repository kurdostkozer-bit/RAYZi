import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { CLOSE_DIALOG_SETTLEMENT_BANK_DETAILS } from "../../../store/settlement/settlement.type";
import { warning } from "../../../util/Alert";
import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
import { Cancel } from "@mui/icons-material";
import { paidSettlement } from "../../../store/settlement/settlement.action";

const SettlementBankDetailsDialogue = () => {
  const dispatch = useDispatch();
  const { dialog1: open, dialogData1 } = useSelector(
    (state) => state.settlement
  );


  const closePopup = () => {
    dispatch({ type: CLOSE_DIALOG_SETTLEMENT_BANK_DETAILS });
  };
  const handleSubmit = () => {
    const data = warning("Are you sure?");
    data
      .then((isDeleted) => {
        if (isDeleted) {
          dispatch(paidSettlement(dialogData1?._id));
        }
      })
      .catch((err) => console.log(err));
    dispatch({ type: CLOSE_DIALOG_SETTLEMENT_BANK_DETAILS });
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
          <span className="text-danger font-weight-bold h4">Bank Details</span>
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
              {dialogData1?.bankDetails ? dialogData1?.bankDetails : "-"}
            </textarea>
          </div>
          <p className="text-danger">
            Note :- "By clicking the 'Pay' button, you are authorizing a
            non-reversible payment to the agency. Once confirmed, this action
            cannot be undone."
          </p>
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
              Pay
            </button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default SettlementBankDetailsDialogue;
