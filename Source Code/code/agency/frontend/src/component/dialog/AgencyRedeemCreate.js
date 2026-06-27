import React, { useEffect, useState } from "react";

//redux
import { connect, useDispatch, useSelector } from "react-redux";

//MUI
import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
import { Cancel } from "@mui/icons-material"

//types
import { CLOSE_NEW_REDEEM_DIALOG } from "../../store/redeem/types";

//action
import { createAgencyRedeem, getSetting } from "../../store/redeem/action";
import { getProfile } from "../../store/admin/action";

const AgencyRedeemCreate = (props) => {
  const dispatch = useDispatch();

  const {
    dialog: open,
    dialogData,
    setting,
  } = useSelector((state) => state.redeem);

  console.log("dialog" , open)
  const { seller } = useSelector((state) => state.admin);


  const agencyId = localStorage.getItem("agencyId");
  const [coin, setCoin] = useState();
  const [description, setDescription] = useState("");
  const [paymentGateway, setPaymentGateway] = useState("");
  const [errors, setError] = useState({
    description: "",
    coin: "",
  });

  useEffect(() => {
    if (dialogData) {
      setDescription(dialogData.description);
      setPaymentGateway(dialogData.paymentGateway);
    }
  }, [dialogData]);

  // useEffect(() => {
  //   props.getSetting();
  //   props.getProfile();
  // }, []);

  useEffect(
    () => () => {
      setError({
        description: "",
        paymentGateway: "",
        coin: "",
      });
      setDescription("");
      setPaymentGateway("");
      setCoin("");
    },
    [open]
  );

  useEffect(() => {
    window.onbeforeunload = closePopup();
  }, []);

  const closePopup = () => {
    dispatch({ type: CLOSE_NEW_REDEEM_DIALOG });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    let error = {};

    if (!description) error.description = "Description is required!";
    if (!coin) error.coin = "Coin is required!";
    if (coin < setting?.minRcoinForCashOutAgency)
      error.coin = `Minimum cashout amount is ${setting?.minRcoinForCashOutAgency}`;

    if (Object.keys(error).length > 0) {
      setError(error);
      return;
    }

    const data = { agencyId: dialogData?._id, description,coin : parseInt(coin) , agencyId  };
    console.log("Submitting Data:", data);

    dispatch(createAgencyRedeem(data));

    // Add your submit action here
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
        maxWidth="sm"
      >
        <DialogTitle id="responsive-dialog-title">
          <span className="text-white font-weight-bold h4">Agency Redeem</span>
        </DialogTitle>

        <IconButton
          style={{
            position: "absolute",
            right: 0,
          }}
        >
          {/* <Tooltip title="Close">
            <Cancel className="text-dark" onClick={closePopup} />
          </Tooltip> */}
        </IconButton>
        <DialogContent>
          <div className="modal-body px-1 pb-3">
           
            <div className="d-flex flex-column pt-0">
              <form>
                <div className="row">
                  <div className="col-md-12">
                    <div className="form-group">
                      <label className="text-white mb-2">coin</label>
                      <input
                        type="number"
                        className="form-controlredeem"
                        placeholder="coin"
                        value={coin}
                        onChange={(e) => {
                          setCoin(e.target.value);
                          if (!e.target.value) {
                            return setError({
                              ...errors,
                              coin: "coin is Required!",
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
                              <span className="text-red">{errors.coin}</span>
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                  <div className="col-md-12 mt-2">
                    <div className="form-group">
                      <label className="text-white mb-2">Description</label>
                      <input
                        type="text"
                        className="form-controlredeem"
                        placeholder="Description"
                        value={description}
                        onChange={(e) => {
                          setDescription(e.target.value);
                          if (!e.target.value) {
                            return setError({
                              ...errors,
                              description: "Description is Required!",
                            });
                          } else {
                            return setError({
                              ...errors,
                              description: "",
                            });
                          }
                        }}
                      />
                      {errors.description && (
                        <div className="ml-2 mt-1">
                          {errors.description && (
                            <div className="pl-1 text__left">
                              <span className="text-red">
                                {errors.description}
                              </span>
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                  
                </div>
                <div className="mt-5">
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

export default connect(null, {
  createAgencyRedeem,
  getSetting,
  getProfile,
})(AgencyRedeemCreate);
