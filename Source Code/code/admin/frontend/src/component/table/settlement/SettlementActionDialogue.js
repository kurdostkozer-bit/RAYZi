import React, { useState, useEffect } from "react";
import { connect, useDispatch, useSelector } from "react-redux";
import { CLOSE_DIALOGUE_SETTLEMENT } from "../../../store/settlement/settlement.type";
import { actionUpdatePending } from "../../../store/settlement/settlement.action";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Input,
  IconButton,
  Tooltip,
} from "@mui/material";
import { CLOSE_AGENCY_DIALOG } from "../../../store/agency/type";
import { Cancel } from "@mui/icons-material";

const SettlementActionDialogue = (props) => {
  const { dialog: open, dialogData } = useSelector((state) => state.settlement);
  const [coin, setCoin] = useState(0);
  const [penalty, setPenalty] = useState(0);
  const [note, setNote] = useState("");
  const [mongoId, setMongoId] = useState("");
  const [errors, setError] = useState({ coin: "", note: "" });

  const dispatch = useDispatch();

  useEffect(() => {
    setMongoId(dialogData?.id);
  }, [dialogData]);

  const handleSubmit = () => {
    if ((!coin && !penalty) || -Math.abs(dialogData?.amount) > coin) {
      const error = {};
      if (!coin || !penalty) error.coin = "Coin/Penalty is required";
      if (coin > Math.abs(dialogData?.amount))
        error.coin = "Coin must not be greater than amount";
      setError(error);
      return;
    }
    const data = {
      bonusOrPenltyAmount: penalty || coin,
    };
    dispatch(actionUpdatePending(data, mongoId));
  };

  const closePopup = () => {
    dispatch({ type: CLOSE_DIALOGUE_SETTLEMENT });
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
            {" "}
            Agency Penalty/Bonus{" "}
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
          <div className="modal-body pt-1 px-1 pb-3">
            <div className="d-flex flex-column">
              <form>
                <div className="row">
                  <div className="col-12">
                    <div className="form-group">
                      {errors.bd && (
                        <div className="ml-2 mt-1">
                          {errors.bd && (
                            <div className="pl-1 text__left">
                              <span className="text-red">{errors.bd}</span>
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>

                  <div className="form-group col-12 mt-3">
                    <label className="mb-2 text-gray">Bonus</label>
                    <input
                      label={`Bonus`}
                      id={`coin`}
                      type={`number`}
                      className="form-control"
                      value={coin}
                      disabled={penalty !== null && penalty.length > 0}
                      onFocus={(e) => (e.target.value == 0 ? setCoin("") : "")}
                      placeholder={`Enter coin`}
                      errorMessage={errors.coin && errors.coin}
                      onChange={(e) => {
                        setCoin(e.target.value);
                        if (!e.target.value && !penalty) {
                          return setError({
                            ...errors,
                            coin: `penalty/Bonus Is Required`,
                          });
                        } else {
                          return setError({
                            ...errors,
                            coin: "",
                          });
                        }
                      }}  
                    />
                    {errors.coin && (
                      <div className="ml-2 mt-1">
                        {errors.coin && (
                          <div className="pl-1 text__left">
                            <span className="text-red">{!penalty && !coin ? errors.coin : ''}</span>
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                </div>

                <div className={`${mongoId ? "col-12" : "col-md-12"}`}>
                  <div className="form-group">
                    <label className="mb-2 text-gray">Penalty</label>
                    <input
                      label={`Panelty`}
                      className="form-control"
                      id={`coin`}
                      type={`number`}
                      value={penalty}
                      disabled={coin !== null && coin.length > 0}
                      onFocus={(e) => (e.target.value == 0 ? setCoin("") : "")}
                      placeholder={`Enter coin`}
                      errorMessage={errors.penalty && errors.penalty}
                      onChange={(e) => {
                        setPenalty(e.target.value);
                        if (!e.target.value && !coin) {
                          return setError({
                            ...errors,
                            penalty: `penalty/Bonus Is Required`,
                          });
                        } else {
                          return setError({
                            ...errors,
                            penalty: "",
                          });
                        }
                      }}
                    />
                    {errors.penalty && (
                      <div className="ml-2 mt-1">
                        {errors.penalty && (
                          <div className="pl-1 text__left">
                            <span className="text-red">{!penalty && !coin ? errors.penalty : ''}</span>
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                </div>

                <div className={"mt-3 pt-3"}>
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

export default connect()(SettlementActionDialogue);
