import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { CLOSE_HOST_COMMISSION_DIALOG } from "../../../store/hostCommision/type";
import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
import { Cancel } from "@mui/icons-material";
import {
  createNewHostCommission,
  editHostCommission,
} from "../../../store/hostCommision/action";

const HostCommissionDialog = () => {
  const dispatch = useDispatch();

  const { dialog, dialogData } = useSelector((state) => state.hostCommision);

  const [mongoId, setMongoId] = useState("");
  const [upperAmount, setUpperAmount] = useState("");
  const [amountPercentage, setAmountPercentage] = useState("");
  const [imageData, setImageData] = useState(null);
  const [imagePath, setImagePath] = useState(null);

  const [errors, setError] = useState({
    upperAmount: "",
    amountPercentage: "",
  });

  useEffect(() => {
    if (dialogData) {
      setMongoId(dialogData._id);
      setUpperAmount(dialogData?.upperCoin);
      setAmountPercentage(dialogData?.amountPercentage);
    }
  }, [dialogData]);

  useEffect(
    () => () => {
      setError({
        upperAmount: "",
        amountPercentage: "",
      });
      setMongoId("");
      setUpperAmount("");
      setAmountPercentage(null);
    },
    [dialog]
  );

  useEffect(() => {
    window.onbeforeunload = closePopup();
  }, []);

  const closePopup = () => {
    dispatch({ type: CLOSE_HOST_COMMISSION_DIALOG });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!upperAmount || !amountPercentage) {
      const errors = {};
      if (!upperAmount) errors.upperAmount = "Upper Coin is Required!";
      if (!amountPercentage)
        errors.amountPercentage = "Amount Coin is Required!";
      return setError({ ...errors });
    } else {
      if (mongoId) {
        const data = {
          upperCoin: upperAmount,
          amountPercentage: amountPercentage,
          commissionId: mongoId,
        };
        dispatch(editHostCommission(mongoId, data));
      } else {
        const data = {
          amountPercentage: amountPercentage,
          upperCoin: upperAmount,
          type: 2,
        };
        dispatch(createNewHostCommission(data));
      }
    }

    closePopup();
  };

  return (
    <>
      <Dialog
        open={dialog}
        aria-labelledby="responsive-dialog-title"
        onClose={closePopup}
        disableBackdropClick
        disableEscapeKeyDown
        fullWidth
        maxWidth="xs"
      >
        <DialogTitle id="responsive-dialog-title">
          <span className="text-danger font-weight-bold h4"> Commission </span>
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
                <div className="form-group">
                  <label className="mb-2 text-gray">Upper Coin</label>
                  <input
                    type="number"
                    className="form-control"
                    required=""
                    placeholder="Enter Upper Coin"
                    value={upperAmount}
                    onChange={(e) => {
                      setUpperAmount(e.target.value);

                      if (!e.target.value) {
                        return setError({
                          ...errors,
                          upperAmount: "Upper Amount is Required!",
                        });
                      } else {
                        return setError({
                          ...errors,
                          upperAmount: "",
                        });
                      }
                    }}
                  />
                  {errors.upperAmount && (
                    <div className="ml-2 mt-1">
                      {errors.upperAmount && (
                        <div className="pl-1 text__left">
                          <span className="text-red">{errors.upperAmount}</span>
                        </div>
                      )}
                    </div>
                  )}
                </div>

                <div className="form-group mt-3">
                  <label className="mb-2 text-gray">Coin Percentage (%)</label>
                  <input
                    type="number"
                    className="form-control"
                    required=""
                    placeholder="Enter Coin Percentage"
                    value={amountPercentage}
                    onChange={(e) => {
                      setAmountPercentage(e.target.value);

                      if (!e.target.value) {
                        return setError({
                          ...errors,
                          amountPercentage: "Amount Coin is Required!",
                        });
                      } else {
                        return setError({
                          ...errors,
                          amountPercentage: "",
                        });
                      }
                    }}
                  />
                  {errors.amountPercentage && (
                    <div className="ml-2 mt-1">
                      {errors.amountPercentage && (
                        <div className="pl-1 text__left">
                          <span className="text-red">
                            {errors.amountPercentage}
                          </span>
                        </div>
                      )}
                    </div>
                  )}
                </div>

                <div className={"mt-5"}>
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

export default HostCommissionDialog;
