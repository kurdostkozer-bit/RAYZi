import React, { useEffect, useState } from "react";
import DimondIcon from "../assets/image/dimondIcon.png";
import Winner1 from "../assets/image/winner1.png";
import Winner2 from "../assets/image/winner2.png";
import Winner3 from "../assets/image/winner3.png";
import { imageShowData } from "../GiftImage";

export default function WinnerDialog(props) {
  const { open, setOpen, winnerShow, userData, gameRound, currentGame } = props;
  const [getUserData, setGetUserData] = useState();
  const [lossBet, setLossBet] = useState(false);

  useEffect(() => {
    const handleOutsideClick = (event) => {
      if (open && !event.target.closest(".winner-dialog")) {
        setOpen(false);
      }
    };
    if (open) {
      document.addEventListener("click", handleOutsideClick);
    }
    return () => {
      document.removeEventListener("click", handleOutsideClick);
    };
  }, [open, setOpen]);

  useEffect(() => {
    const findWinnerUser = winnerShow?.winnerUserArray?.find(
      (user) => user?._id === userData?._id
    );
    setGetUserData(findWinnerUser);
  }, [userData, winnerShow]);

  useEffect(() => {
    const filterData = currentGame?.some(
      (item) => item?.userId === userData?._id
    );
    setLossBet(filterData);
    console.log("currentGame",currentGame);
  }, [userData, currentGame, winnerShow]);

  return (
    <>
      {open&& (
        <div className="winner-dialog" style={{height:`${winnerShow?.winnerUserArray?.length === 0 ? "232px" : "300px"}`}}>
          <div className="dialogBox">
            {(winnerShow?.winnerUserArray?.length === 0 ? true : false) ? (
              <div className="dialog winnerNot-dialog">
                <div className="winner-not">
                  <div className="winner-result">
                    <h6>The Result of {gameRound} round: </h6>
                    {winnerShow?.winnerNumber && (
                      <img src={imageShowData[winnerShow?.winnerNumber - 1]} />
                    )}
                  </div>
                  <h5>Unfortunately, No one win this game</h5>
                </div>
              </div>
            ) : (
              <div className="dialog">
                {getUserData && Object?.values(getUserData)?.length > 0 ? (
                  <>
                    <div className="dialog-head ">
                      <div className="user-image">
                        {getUserData?.image && <img src={getUserData?.image} />}
                      </div>
                      <div className="winner-number">
                        <h6>Congratulations you get</h6>
                        <img src={DimondIcon} />
                        <h6>
                          {getUserData?.userWinCoin
                            ? getUserData?.userWinCoin
                            : 0}
                        </h6>
                      </div>
                      <div className="winner-result">
                        <h6>The Result of {gameRound} round: </h6>
                        {winnerShow?.winnerNumber && (
                          <img
                            src={imageShowData[winnerShow?.winnerNumber - 1]}
                          />
                        )}
                      </div>
                      <div className="body-head">
                        <div className="left-border"></div>
                        <h6>And they get</h6>
                        <div className="right-border"></div>
                      </div>
                    </div>
                  </>
                ) : lossBet === true ? (
                  <>
                    <div className="dialog-head lossGame-winner">
                      <div className="user-image">
                        {getUserData?.image && <img src={getUserData?.image} />}
                      </div>

                      <div className="winner-result">
                        <h6>The Result of {gameRound} round: </h6>
                        {winnerShow?.winnerNumber && (
                          <img
                            src={imageShowData[winnerShow?.winnerNumber - 1]}
                          />
                        )}
                      </div>
                      <div className="winner-number">
                        <h6>You loss this round</h6>
                      </div>
                      <div className="body-head">
                        <div className="left-border"></div>
                        <h6>Congratulations they get</h6>
                        <div className="right-border"></div>
                      </div>
                    </div>
                  </>
                ) : (
                  <div className="dialog-head lossGame-winner">
                    <div className="winner-result">
                      <h6>The Result of {gameRound} round: </h6>
                      {winnerShow?.winnerNumber && (
                        <img
                          src={imageShowData[winnerShow?.winnerNumber - 1]}
                        />
                      )}
                    </div>
                    <div className="winner-number">
                      <h6>You didn't place any bet this round</h6>
                    </div>
                    <div className="body-head">
                      <div className="left-border"></div>
                      <h6>Congratulations they get</h6>
                      <div className="right-border"></div>
                    </div>
                  </div>
                )}
                <div className="dialog-body">
                  <div className="show-result">
                    {winnerShow?.winnerUserArray?.map((item) => {
                      return (
                        <div className="user-show-winner">
                          <div className="user-image">
                            <img
                              src={item?.image}
                              className="user-image-fram"
                            />
                            <div className="winner-fram">
                              {item?.number === 1 ? (
                                <img src={Winner1} />
                              ) : item?.number === 2 ? (
                                <img src={Winner2} />
                              ) : (
                                <img src={Winner3} />
                              )}
                            </div>
                          </div>
                          <h5>{item?.name}</h5>
                          <div className="winner-coin">
                            <img src={DimondIcon} />
                            <h6>{item?.userWinCoin}</h6>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </>
  );
}
