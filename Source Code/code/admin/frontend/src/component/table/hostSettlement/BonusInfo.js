import React, { useEffect, useState } from "react";
import { connect, useDispatch, useSelector } from "react-redux";
import { CLOSE_DIALOGUE_SETTLEMENT } from "../../../store/settlement/settlement.type";

const BonusInfo = (props) => {
  const { dialogue, dialogueData } = useSelector((state) => state.settlement);

  const showImage = (openImage) => {
    window.open(openImage, "_blank");
  };

  const dispatch = useDispatch();

  return (
    <div className="mainDialogue fade-in">
      <div
        className="Dialogue"
        style={{ width: "450px", overflow: "auto", maxHeight: "100vh" }}
      >
        <div className="dialogueHeader">
          <div className="headerTitle fw-bold">Bonus/Penalty Info</div>
          <div
            className="closeBtn "
            onClick={() => {
              dispatch({ type: CLOSE_DIALOGUE_SETTLEMENT });
            }}
          >
            <i className="fa-solid fa-xmark ms-2"></i>
          </div>
        </div>
        <div
          className="dialogueMain"
          style={{ overflow: "auto", maxHeight: "100vh" }}
        >
          <div className="feedName d-flex mb-3">
            <div className="fw-bolder" style={{ width: "160px" }}>
              Bonus/Penaly
            </div>
            <div className="">
              :&nbsp;
              {dialogueData?.bonusOrPenltyAmount
                ? dialogueData?.bonusOrPenltyAmount
                : dialogueData?.bonusOrPenaltyAmount
                ? dialogueData?.bonusOrPenaltyAmount
                : dialogueData?.penltyOrBonus
                ? dialogueData?.penltyOrBonus
                : "-"}
            </div>
          </div>

          <div className="d-flex mb-3">
            <div className="fw-bolder" style={{ width: "160px" }}>
              Details
            </div>
            <span>
              :&nbsp;{dialogueData?.note ? dialogueData?.note : "Not Specified"}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BonusInfo;
