import React, { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { createCoinData } from "../../store/seller/seller.action";
import { Dialog, DialogContent, IconButton, Tooltip } from "@mui/material";
import Cancel from "@mui/icons-material/Cancel";
import { createHost, getCoinSellerUniqueId } from "../../store/user/action";
import male from "../../assets/images/male.png";
import cancelButton from "../../assets/images/cancel.png";

function CoinSeller(props) {
  const { dialog } = useSelector((state) => state.sellerCoin);
  const { userId } = useSelector((state) => state.user);
  const dispatch = useDispatch();
  const [data, setData] = useState([]);
  const [search, setSearch] = useState("");
  const [uniqueId, setUniqueId] = useState("");
  const [errors, setError] = useState({
    userId: "",
  });

  useEffect(() => {
    dispatch(getCoinSellerUniqueId(search));
  }, [search]);

  useEffect(() => {
    setData(userId);
  }, [userId]);

  useEffect(() => {
    if (search === "") {
      setData([]);
    }
  }, [search]);

  useEffect(() => {
    setError({
      userId: "",
      coin: "",
    });
    setSearch("");
    setUniqueId("");
  }, [dialog]);

  const handleClose = () => {
    dispatch({ type: "CLOSE_COIN_SELLER_DIALOGUE" });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!uniqueId) {
      setError({
        ...errors,
        userId: "Please select user",
      });
    } else {
      dispatch(createHost(localStorage.getItem("agencyId"), uniqueId));
      handleClose();
    }
  };

  const handleOptionClick = (item) => {
    setUniqueId(item.uniqueId);
    setSearch(item.uniqueId); // Update the search input with the selected value
  };

  const handleSearchChange = (e) => {
    const newSearchValue = e.target.value;
    setSearch(newSearchValue);
    if (newSearchValue === "") {
      setUniqueId("");
      setSearch("");
      setData([]);
    }
  };

  return (
    <>
      <Dialog
        open={dialog}
        onClose={handleClose}
        fullWidth
        maxWidth="xs"
        style={{ zIndex: 9999999 }}
      >
        <div className="row" style={{ borderBottom: "1px solid #43354D" }}>
          <div className="col-10 text-align-center">
            <p
              className="text-center py-3 mb-0"
              style={{ color: "#fff", fontSize: "17px" }}
            >
              Add Host
            </p>
          </div>
          <IconButton
            className="col-2"
            onClick={handleClose}
            style={{ color: "#fff" }}
          >
            <Tooltip
              title="Close"
              placement="right"
              style={{
                color: "#fff",
                borderRadius: "50%",
              }}
            >
              <img src={cancelButton} width={22} height={22} />
            </Tooltip>
          </IconButton>
        </div>
        <DialogContent style={{ height: "400px", overflow: "hidden" }}>
          <div className="modal-body pt-1 px-1 pb-3 mt-4">
            <div className="d-flex flex-column">
              <form>
                <div className="form-group">
                  <div className="col-md-12 mt-2">
                    <label
                      className="float-left dialog__input__title mb-3 text-white"
                      style={{ fontSize: "13px" }}
                    >
                      User ID
                    </label>

                    <input
                      type="number"
                      className="form-control"
                      placeholder="Search or Select User ID..."
                      value={search || uniqueId}
                      onChange={handleSearchChange}
                      style={{
                        backgroundColor: "#422F4F",
                        borderColor: "#4a2f59",
                        padding: "10px",
                        borderRadius: "4px",
                        width: "100%",
                        color: "#fff",
                      }}
                    />

                    {/* Display filtered options */}

                    <div
                      className="options-container mt-2"
                      style={{
                        height: "150px",
                        overflowY: "auto",
                        backgroundColor: "transparent",
                        borderRadius: "4px",
                        marginTop: "5px",
                      }}
                    >
                      {search && data.length === 0 ? (
                        <div
                          className="d-flex align-items-center p-2"
                          style={{
                            borderBottom: "1px solid #443a49",
                            color: "#fff",
                          }}
                        >
                          <p>User Does not found!</p>
                        </div>
                      ) : (
                        data.map((item) =>
                          item?.uniqueId ? (
                            <div
                              key={item.uniqueId}
                              className="d-flex align-items-center justify-content-between p-2"
                              style={{
                                borderBottom: "1px solid #443a49",
                                color: "#fff",
                              }}
                              onClick={() => handleOptionClick(item)}
                            >
                              <div className="d-flex align-items-center">
                                <img
                                  src={item.image || male}
                                  style={{
                                    height: "30px",
                                    width: "30px",
                                    borderRadius: "50%",
                                    marginRight: "10px",
                                  }}
                                  alt="User"
                                />
                                <div>
                                  <p
                                    style={{
                                      marginBottom: "0",
                                      fontSize: "14px",
                                      color: "#fff",
                                    }}
                                  >
                                    {item.uniqueId}
                                  </p>
                                  <span
                                    style={{
                                      fontSize: "12px",
                                      color: "#b2a6bf",
                                    }}
                                  >
                                    ID: {item.uniqueId}
                                  </span>
                                </div>
                              </div>
                              <div>
                                <input
                                  type="radio"
                                  name="radio-select"
                                  checked={uniqueId === item.uniqueId}
                                  onChange={() => setUniqueId(item.uniqueId)}
                                  style={{
                                    accentColor: "#40B405",
                                    cursor: "pointer",
                                  }}
                                />
                              </div>
                            </div>
                          ) : null
                        )
                      )}
                    </div>
                    {errors?.userId && (
                      <p className="text-danger mt-2">{errors?.userId}</p>
                    )}
                  </div>
                </div>
              </form>
            </div>
            <div
              className="d-flex justify-content-end align-items-center"
              style={{
                position: "absolute",
                bottom: "-93px",
                right: "35%",
              }}
            >
              {uniqueId ? (
                <button
                  className={`${uniqueId ? "btn-main" : "btn-main1"} px-4 py-2`}
                  style={{ border: "none" }}
                  onClick={handleSubmit}
                >
                  Submit
                </button>
              ) : (
                <button
                  className="btn-main1 px-4 py-2"
                  style={{ border: "none" }}
                  onClick={handleSubmit}
                >
                  Submit
                </button>
              )}
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
}

export default CoinSeller;
