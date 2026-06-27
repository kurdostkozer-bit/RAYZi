import React, { useEffect, useState , useRef } from "react";
import $ from "jquery";

//redux
import { connect, useDispatch, useSelector } from "react-redux";
import ReactSelect from "react-select";
//MUI
import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
import { Cancel } from "@mui/icons-material";

//types

//action
import {
  addCoinSeller,
  editCoinSeller,
  getCoinSellerUniqueId,
} from "../../store/coinSeller/action";
import { CLOSE_DIALOGUE } from "../../store/coinSeller/type";
import InfiniteScroll from "react-infinite-scroll-component";

const CoinSellerAdd = (props) => {
  const dispatch = useDispatch();

  const { dialogOpen: open, dialogData } = useSelector(
    (state) => state.coinSeller
  );
  const { coinSellerId } = useSelector((state) => state.coinSeller);
  const [uniqueId, setUniqueId] = useState("");
  const [coin, setCoin] = useState("");
  const [search, setSearch] = useState("");
  const [data, setData] = useState([]);
  const [countryCode, setCountryCode] = useState("");
  const [mobileNumber, setMobileNumber] = useState("");
    const [hasMore, setHasMore] = useState(true);
    const scrollPosition = useRef(0);
    const scrollRef = useRef(null);
    const [loading, setLoading] = useState(false);
    const [start, setStart] = useState(1);
    const [limit] = useState(10);

  const [errors, setError] = useState({
    uniqueId: "",
    coin: "",
    mobileNumber: "",
  });
  useEffect(() => {
  dispatch(getCoinSellerUniqueId(1, limit, search));
  }, [dispatch, search]);

  useEffect(() => {
    setData(coinSellerId);
  }, [coinSellerId]);

    useEffect(() => {
      const fetchInitialData = () => {
        setLoading(true);
        setStart(1);
        setData([]);
        dispatch(getCoinSellerUniqueId(1, limit, search));
        setLoading(false);
      };
  
      fetchInitialData();
    }, [search, limit]);

  const handleChange = (e) => {};

  // const filteredData = data.filter(item => {
  //   return item?.uniqueId.toLowerCase().includes(search.toLowerCase());
  // });

  useEffect(() => {
    setUniqueId("");
    setCoin("");
    setMobileNumber("");
    setError({
      name: "",
      coin: "",
      countryCode: "",
      mobileNumber: "",
    });
  }, [open]);

  useEffect(() => {
    if (dialogData) {
      setUniqueId(dialogData?.uniqueId);
      setCoin(dialogData?.coin);
      setCountryCode(dialogData?.countryCode);
      setMobileNumber(dialogData?.mobileNumber);
    }
  }, [dialogData]);

   useEffect(() => {
      if (coinSellerId?.length) {
        if (start === 1) {
          setData(coinSellerId);
        } else {
          setData((prevData) => {
            // Save scroll position before updating data
            if (scrollRef.current) {
              scrollPosition.current = scrollRef.current.scrollTop;
            }
            return [...prevData, ...coinSellerId];
          });
        }
        setHasMore(coinSellerId.length === limit);
      } else {
        setHasMore(false);
      }
      setLoading(false);
  
      // Restore scroll position after data update
      if (start > 1 && scrollRef.current) {
        requestAnimationFrame(() => {
          if (scrollRef.current) {
            scrollRef.current.scrollTop = scrollPosition.current;
          }
        });
      }
    }, [coinSellerId, start, limit]);
  
    const fetchMoreData = () => {
      if (!loading && hasMore) {
        // Save scroll position before fetching
        if (scrollRef.current) {
          scrollPosition.current = scrollRef.current.scrollTop;
        }
        setLoading(true);
        const nextPage = start + 1;
        setStart(nextPage);
        dispatch(getCoinSellerUniqueId(nextPage, limit, search));
      }
    };

  const closePopup = () => {
    dispatch({ type: CLOSE_DIALOGUE });
  };

  const handleSubmit = () => {
    if (!uniqueId || !coin || coin < 0 || !mobileNumber || !countryCode) {
      const error = {};
      if (!uniqueId) error.uniqueId = "UniqueId Is Required !";
      if (!coin) error.coin = "Coin Is Required !";
      if (coin < 0) error.coin = "Enter Correct Coin !";
      if (!countryCode) error.countryCode = "Country Code Is Required!";
      if (!mobileNumber) error.mobileNumber = "Mobile Number Is Required !";
      return setError({ ...error });
    } else {
      const data = {
        uniqueId: uniqueId,
        coin: coin,
        countryCode: countryCode,
        mobileNumber: mobileNumber,
      };
      // if (dialogData) {
      //   props.editCoinSeller(dialogData._id,data);
      // } else {
      props.addCoinSeller(data);
      // }
      dispatch({ type: CLOSE_DIALOGUE });
    }
  };
  const [uniqueIdSearch, setUniqueIdSearch] = useState("");

  const handleChangeSearch = (event) => {
    setUniqueIdSearch(event.target.value);
  };

  const filteredData = data.filter((item) =>
    (item?.uniqueId || "")
      .toString()
      .toLowerCase()
      .includes(uniqueIdSearch.toLowerCase())
  );

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
          <span className="text-danger font-weight-bold h4"> Coin Seller </span>
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
                <div className="col-12 mt-3">
                      <div className="form-group">
                        <label className="mb-2 text-gray">
                          Unique Id of User
                        </label>

                        <ReactSelect
                          value={data.find(
                            (option) => option.uniqueId === uniqueId
                          )}
                          options={data}
                          getOptionLabel={(option) => option.uniqueId}
                          formatOptionLabel={(option) => (
                            <div className="country-option">
                              <img
                                src={option.image}
                                alt="country"
                                style={{
                                  height: "30px",
                                  width: "30px",
                                  borderRadius: "50%",
                                }}
                              />
                              <span className="ms-3">{option.uniqueId}</span>
                            </div>
                          )}
                          onChange={(selectedOption) =>
                            setUniqueId(selectedOption.uniqueId)
                          }
                          onInputChange={(inputValue) => setSearch(inputValue)}
                          styles={{
                            menu: (provided) => ({
                              ...provided,
                              maxHeight: "250px",
                              overflowY: "auto",
                            }),
                          }}
                          components={{
                            MenuList: (props) => (
                              <div
                                id="scrollable-container"
                                ref={scrollRef}
                                style={{
                                  maxHeight: "250px",
                                  overflowY: "auto",
                                  scrollBehavior: "smooth",
                                }}
                              >
                                <InfiniteScroll
                                  dataLength={data.length || 0}
                                  next={fetchMoreData}
                                  hasMore={hasMore}
                                  loader={<p>Loading more data...</p>}
                                  scrollableTarget="scrollable-container"
                                  scrollThreshold={0.9}
                                  style={{ overflow: "hidden" }}
                                  endMessage={
                                    !loading &&
                                    data.length > 0 && (
                                      <p
                                        style={{
                                          textAlign: "center",
                                          padding: "8px",
                                        }}
                                      >
                                        No more data to load.
                                      </p>
                                    )
                                  }
                                >
                                  {props.children}
                                </InfiniteScroll>
                              </div>
                            ),
                          }}
                        />
                        {errors.uniqueId && (
                          <div className="ml-2 mt-1">
                            {errors.uniqueId && (
                              <div className="pl-1 text__left">
                                <span className="text-red">
                                  {errors.uniqueId}
                                </span>
                              </div>
                            )}
                          </div>
                        )}
                      </div>
                    </div>
                  <div className="col-12 ">
                    <div className=" form-group">
                      <label className="mb-2 mt-3 text-gray">Coin</label>
                      <input
                        type="number"
                        className="form-control"
                        required=""
                        placeholder="Coin"
                        value={coin}
                        onChange={(e) => {
                          setCoin(e.target.value);
                          if (!e.target.value) {
                            return setError({
                              ...errors,
                              coin: "Coin is Required!",
                            });
                          } else if (e.target.value < 0) {
                            return setError({
                              ...errors,
                              coin: "Enter Correct Coin !",
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
                  <div className="col-12 mt-3">
                    <div
                      className="form-group"
                      style={{
                        display: "flex",
                        alignItems: "center",
                        width: "100%",
                      }}
                    >
                      <div style={{ width: "150px", marginRight: "10px" }}>
                        <label className="mb-2 text-gray">Country Code</label>
                        <input
                          type="number"
                          className="form-control"
                          required=""
                          placeholder="91"
                          value={countryCode}
                          onChange={(e) => {
                            setCountryCode(e.target.value);
                            if (!e.target.value) {
                              return setError({
                                ...errors,
                                countryCode: "Code is Required!",
                              });
                            } else {
                              return setError({
                                ...errors,
                                countryCode: "",
                              });
                            }
                          }}
                        />
                      </div>
                      <div style={{ width: "100%" }}>
                        <label className="mb-2 text-gray">Mobile Number</label>
                        <input
                          type="number"
                          className="form-control"
                          required=""
                          placeholder="Mobile Number"
                          value={mobileNumber}
                          onChange={(e) => {
                            setMobileNumber(e.target.value);
                            if (!e.target.value) {
                              return setError({
                                ...errors,
                                mobileNumber: "Mobile Number is Required!",
                              });
                            } else {
                              return setError({
                                ...errors,
                                mobileNumber: "",
                              });
                            }
                          }}
                        />
                      </div>
                    </div>
                    {errors.countryCode && (
                      <div className="ml-2 mt-1">
                        {errors.countryCode && (
                          <div className="pl-1 text__left">
                            <span className="text-red">
                              {errors.countryCode}
                            </span>
                          </div>
                        )}
                      </div>
                    )}
                    {errors.mobileNumber && (
                      <div className="ml-2 mt-1">
                        {errors.mobileNumber && (
                          <div className="pl-1 text__left">
                            <span className="text-red">
                              {errors.mobileNumber}
                            </span>
                          </div>
                        )}
                      </div>
                    )}
                  </div>
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

export default connect(null, { addCoinSeller, editCoinSeller })(CoinSellerAdd);
