import React from "react";
import DimondIcon from "../assets/image/dimondIcon.png";

export default function SelectCricle(props) {
  const {
    styles,
    image,
    lineCricleStlye,
    cricleMainStlye,
    onClick,
    framNo,
    selectFrame,
    timeText,
  } = props;

  return (
    <div
      className="cricle-wheel"
      style={{
        left: cricleMainStlye?.left,
        top: cricleMainStlye?.top,
        bottom: cricleMainStlye?.bottom,
        transform: cricleMainStlye?.transform,
        right: cricleMainStlye?.right,
      }}
      onClick={onClick}
    >
      <img src={image} />
      <>
        {(selectFrame?.Bit > 0 ? selectFrame?.selectFrame === framNo : "") ? (
          <div className="details-win details-win-selecetd">
            <h6>Win</h6>
            <span>{timeText + "Times"}</span>
          </div>
        ) : (
          <div className="details-win">
            <h6>Win</h6>
            <span>{timeText + "Times"}</span>
          </div>
        )}
      </>
      <div class="gray"></div>
      <div class="winnerFram"></div>
      {(selectFrame?.Bit > 0 ? selectFrame?.selectFrame === framNo : "") && (
        <div className="selected-fram">
          <div className="bet-details">
            <img src={DimondIcon} />
            <h6>{selectFrame?.Bit}</h6>
          </div>
        </div>
      )}
    </div>
  );
}
