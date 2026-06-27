import React from "react";
import DimondIcon from "../assets/image/dimondIcon.png";

function convertToShortForm(number) {
  if (number >= 1000) {
    const suffixes = ["", "k", "M", "B", "T"];
    const magnitude = Math.floor(Math.log10(number) / 3);
    const shortNumber = (number / Math.pow(1000, magnitude)).toFixed(0);
    return shortNumber + suffixes[magnitude];
  }
  return number?.toString();
}

export default function CoinButton(props) {
  const { funcClick, selectedClass, disabledClass, number } = props;

  const shortForm = convertToShortForm(number);
  return (
    <button
      className={`${disabledClass + ""} ${selectedClass} dimond-box`}
      style={{
        color: "#ffff",
        fontWeight: "700",
      }}
      onClick={funcClick}
    >
      <img src={DimondIcon} />
      <h6>{shortForm}</h6>
    </button>
  );
}
