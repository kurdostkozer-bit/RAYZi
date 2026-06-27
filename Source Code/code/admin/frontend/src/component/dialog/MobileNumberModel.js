import React, { useEffect, useState } from "react";
import { connect, useDispatch, useSelector } from "react-redux";
import { ADD_MOBILE_CLOSE_DIALOGUE, ADD_MONEY_CLOSE_DIALOGUE } from "../../store/coinSeller/type";

//MUI
import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
import { Cancel } from "@mui/icons-material";
import { MobileNumberByAdmin } from "../../store/coinSeller/action";

const MobileNumbeModel = (props) => {
  const { mobileDialogOpen: open, mobileDialogData } = useSelector(
    (state) => state.coinSeller
  );
  const [mobileNumber, setMobileNumber] = useState("");
  const [countryCode, setCountryCode] = useState("");
  const [error, setError] = useState({
    countryCode: "",
    mobileNumber: ""
  });

  useEffect(() => {
    setMobileNumber(mobileDialogData?.mobileNumber);
    setCountryCode(mobileDialogData?.countryCode);
    setError("");
  }, [open, mobileDialogData]);

  const dispatch = useDispatch();

  const handleSubmit = () => {
    const mobileRegex = /^[0-9]{10}$/;
    if (!mobileNumber || !countryCode) {
      let error = {};
      if (!mobileNumber) error.mobileNumber = "Mobile Number Is Required!";
      if (!countryCode) error.countryCode = "Country Code Is Required!";
      return setError({ ...error });
    } else if (!mobileRegex.test(mobileNumber)) {
      return setError({ mobileNumber: "Invalid Mobile Number !" });
    } else {
      let data = {
        countryCode: countryCode,
        mobileNumber: mobileNumber,
      };

      props.MobileNumberByAdmin(data, mobileDialogData?._id);
      dispatch({ type: ADD_MOBILE_CLOSE_DIALOGUE });
    }
  };


  const closePopup = () => {
    dispatch({ type: ADD_MOBILE_CLOSE_DIALOGUE });
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
        maxWidth="xs"
      >
        <DialogTitle id="responsive-dialog-title">
          <span className="text-danger font-weight-bold h4"> Coin Seller </span>
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
          <div className="modal-body pt-1 px-1 pb-3">
            <div className="d-flex flex-column">
              <form>
                <div className="form-group" style={{ display: "flex", alignItems: "center", width: "100%" }}>
                  <div style={{ width: "100px", marginRight: "10px" }}>
                    <label className="mb-2 text-gray">Code</label>
                    <input
                      type="number"

                      className="form-control"
                      required=""
                      placeholder="+91"
                      value={countryCode}
                      onChange={(e) => {
                        setCountryCode(e.target.value);
                        if (!e.target.value) {
                          return setError({
                            ...error,
                            countryCode: "Code is Required!",
                          });
                        } else {
                          return setError({
                            ...error,
                            countryCode: "",
                          });
                        }
                      }}
                    />
                  </div>
                  <div style={{width:"100%"}}>
                    <label className="mb-2 text-gray">Mobile Number</label>
                    <input
                      type="number"
                      className="form-control"
                      required=""
                      placeholder="Mobile Number"
                      value={mobileNumber}
                      onChange={(e) => {
                        setMobileNumber(e.target.value);
                        if (!e.target.value) {
                          return setError({
                            ...error,
                            mobileNumber: "Mobile Number is Required!",
                          });
                        } else {
                          return setError({
                            ...error,
                            mobileNumber: "",
                          });
                        }
                      }}
                    />
                  </div>
                </div>
                  {error.mobileNumber && (
                    <div className="ml-2 mt-1">
                      {error.mobileNumber && (
                        <div className="pl-1 text__left">
                          <span className="text-red">{error.mobileNumber}</span>
                        </div>
                      )}
                    </div>
                  )}
                  {error.countryCode && (
                    <div className="ml-2 mt-1">
                      {error.countryCode && (
                        <div className="pl-1 text__left">
                          <span className="text-red">{error.countryCode}</span>
                        </div>
                      )}
                    </div>
                  )}
                <div className="mt-3">
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
              </form>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default connect(null, { MobileNumberByAdmin })(MobileNumbeModel);
