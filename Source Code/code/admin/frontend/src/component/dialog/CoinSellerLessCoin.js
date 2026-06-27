import React, { useEffect, useState } from "react";
import { connect, useDispatch, useSelector } from "react-redux";
import {
  LESS,
  LESS_MONEY_CLOSE_DIALOGUE,
  LESS_MONEY_CLOSE_DIALOGUE_MONEY_CLOSE_DIALOGUE,
} from "../../store/coinSeller/type";

//MUI
import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
import { Cancel, Money } from "@mui/icons-material";
import { lessCoinSellerCoin } from "../../store/coinSeller/action";

const CoinSellerLessCoin = (props) => {
  const { lessMonyDialogOpen: open, lessMoneyDialogData } = useSelector(
    (state) => state.coinSeller
  );
  const [money, setMoney] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    setMoney("");
    setError("");
  }, [open]);

  const dispatch = useDispatch();

  const handleSubmit = () => {
    if (!money || money < 0) {
      let error = {};
      if (!money) error.money = "Coin Is Required!";
      if (money < 0) error.money = "Enter Correct Coin !";
      return setError({ ...error });
    } else {
      let data = {
        coin: parseInt(money),
      };

      props.lessCoinSellerCoin(data, lessMoneyDialogData);
    }
    dispatch({ type: LESS_MONEY_CLOSE_DIALOGUE });
  };

  const closePopup = () => {
    dispatch({ type: LESS_MONEY_CLOSE_DIALOGUE });
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
          <span className="text-danger font-weight-bold h4"> Less Coin </span>
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
                  <label className="mb-2 text-gray">Coin</label>
                  <input
                    type="number"
                    className="form-control"
                    required=""
                    placeholder=""
                    value={money}
                    onChange={(e) => {
                      setMoney(e.target.value);
                      if (!e.target.value) {
                        return setError({
                          ...error,
                          money: "money is Required!",
                        });
                      } else if (e.target.value < 0) {
                        return setError({
                          ...error,
                          money: "Enter Correct Coin !",
                        });
                      } else {
                        return setError({
                          ...error,
                          money: "",
                        });
                      }
                    }}
                  />
                  {error.money && (
                    <div className="ml-2 mt-1">
                      {error.money && (
                        <div className="pl-1 text__left">
                          <span className="text-red">{error.money}</span>
                        </div>
                      )}
                    </div>
                  )}
                </div>

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

export default connect(null, { lessCoinSellerCoin })(CoinSellerLessCoin);
