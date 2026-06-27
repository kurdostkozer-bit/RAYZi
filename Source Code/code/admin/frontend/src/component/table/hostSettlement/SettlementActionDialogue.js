import React, { useState } from "react";
// import input from "../../extra/Input";
// import Button from "../../extra/Button";
import { connect, useDispatch, useSelector } from "react-redux";
import { useEffect } from "react";
import { CLOSE_DIALOGUE_SETTLEMENT } from "../../../store/settlement/settlement.type";
import { actionUpdatePending } from "../../../store/hostSettlement/hostSettlement.action";
import { useLocation } from "react-router-dom";

const SettlementActionDialogue = (props) => {
  const { dialogueData, dialogueType } = useSelector((state) => state.dialogue);
  const [coin, setCoin] = useState(0);
  const [panelty, setPanelty] = useState(0);
  const [note, setNote] = useState();
  const [mongoId, setMongoId] = useState("");
  const [error, setError] = useState({
    coin: "",
    note: "",
  });

  const dispatch = useDispatch();

  useEffect(() => {
    // setCoin(dialogueData?.bonusOrPenltyAmount);
    setMongoId(dialogueData._id);
    // setNote(dialogueData?.note);
  }, [dialogueData]);


  const handleSubmit = (e, id) => {
    ;
    if (
      (!coin && !panelty) ||
      -Math.abs(dialogueData?.amount) > coin ||
      !note
    ) {
      const error = {};
      if (!note) error.note = "note is required!";
      if (-Math.abs(dialogueData?.amount) > coin)
        error.coin = `No penalty can be imposed more than the ${dialogueData?.amount} `;

      if (!coin && !panelty) error.coin = "Penalty/Bonus is required!";
      if (panelty > dialogueData?.totalCoinEarned)
        error.panelty = "Coin cannot be greater than total coin earned";
      return setError({ ...error });
    } else {
      const data = {
        bonusOrPenltyAmount: coin > 0 ? parseInt(coin) : parseInt(-panelty),
        note: note,
      };

      props.actionUpdatePending(mongoId, data, dialogueType);

      dispatch({ type: CLOSE_DIALOGUE_SETTLEMENT });
    }
  };

  return (
    <>
      <div className="mainDialogue fade-in">
        <div
          className="Dialogue"
          style={{ overflow: "auto", maxHeight: "100vh" }}
        >
          <div className="dialogueHeader">
            <div className="headerTitle fw-bold">
              Apply Penalty/Bonus & Note
            </div>
            <div
              className="closeBtn boxCenter"
              onClick={() => {
                dispatch({ type: CLOSE_DIALOGUE_SETTLEMENT });
              }}
            >
              <i className="fa-solid fa-xmark"></i>
            </div>
          </div>
          <div className="dialogueMain">
            <div
              className={`col-12 ${
                panelty !== null && panelty.length > 0 > 0 ? "opacity-50" : ""
              }`}
            >
              <input
                label={`Bonus`}
                id={`coin`}
                type={`number`}
                value={coin}
                disabled={panelty !== null && panelty.length > 0}
                onFocus={(e) => (e.target.value == 0 ? setCoin("") : "")}
                placeholder={`Enter coin`}
                errorMessage={error.coin && error.coin}
                onChange={(e) => {
                  setCoin(e.target.value);
                  if (!e.target.value) {
                    return setError({
                      ...error,
                      coin: `penalty/Bonus Is Required`,
                    });
                  } else {
                    return setError({
                      ...error,
                      coin: "",
                    });
                  }
                }}
              />
            </div>

            <div
              className={`col-12 ${
                coin !== null && coin.length > 0 ? "opacity-50" : ""
              }`}
            >
              <input
                label={`Panelty`}
                id={`coin`}
                type={`number`}
                value={panelty}
                disabled={coin !== null && coin.length > 0}
                onFocus={(e) => (e.target.value == 0 ? setCoin("") : "")}
                placeholder={`Enter coin`}
                errorMessage={error.panelty && error.panelty}
                onChange={(e) => {
                  setPanelty(e.target.value);
                  if (!e.target.value) {
                    return setError({
                      ...error,
                      panelty: `penalty/Bonus Is Required`,
                    });
                  } else {
                    return setError({
                      ...error,
                      panelty: "",
                    });
                  }
                }}
              />
            </div>
            <div className={`col-12`}>
              <input
                label={`note`}
                id={`note`}
                type={`text`}
                value={note}
                errorMessage={error.note && error.note}
                onChange={(e) => {
                  setNote(e.target.value);
                  if (!e.target.value) {
                    return setError({
                      ...error,
                      note: `note Is Required`,
                    });
                  } else {
                    return setError({
                      ...error,
                      note: "",
                    });
                  }
                }}
              />
            </div>
            <div className="text-danger text-capitalize">
              Note : you Can either give bonus or penalty.
            </div>

            <div className="dialogueFooter">
              <div className="dialogueBtn">
                <button
                  btnName={`Submit`}
                  btnColor={`btnBlackPrime`}
                  style={{ borderRadius: "5px", width: "80px" }}
                  newClass={`me-2`}
                  onClick={() => handleSubmit()}
                />
                <button
                  btnName={`Close`}
                  btnColor={`bg-danger text-white`}
                  style={{ borderRadius: "5px", width: "80px" }}
                  onClick={() => {
                    dispatch({ type: CLOSE_DIALOGUE_SETTLEMENT });
                  }}
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default connect(null, { actionUpdatePending })(SettlementActionDialogue);
