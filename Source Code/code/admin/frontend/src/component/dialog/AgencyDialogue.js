import React, { useEffect, useRef, useState } from "react";

//redux
import { connect, useDispatch, useSelector } from "react-redux";
import Select from "react-select";
//MUI
import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
// import { Cancel } from "@mui/icons-material";

//action
import { createNewAgency, editAgency } from "../../store/agency/action";
import Male from "../../assets/images/male.png";
import { CLOSE_AGENCY_DIALOG } from "../../store/agency/type";
import $ from "jquery";
import { getCoinSellerUniqueId } from "../../store/coinSeller/action";
import { Cancel } from "@mui/icons-material";
import InfiniteScroll from "react-infinite-scroll-component";
import ReactSelect from "react-select";
// import { getBd } from "../../store/bd/action";
// import { getCoinSellerUniqueId } from "../../store/coinSeller/action";

const AgencyDialogue = (props) => {
  const dispatch = useDispatch();

  //   const { bd } = useSelector((state) => state.bd);
  const { coinSellerId } = useSelector((state) => state.coinSeller);

  const {
    dialog: open,
    dialogData,
    agency,
  } = useSelector((state) => state.agency);

  const [imageData, setImageData] = useState([]);
  const [data, setData] = useState([]);
  const [uniqueId, setUniqueId] = useState("");
  const [search, setSearch] = useState("");
  const [imagePath, setImagePath] = useState(null);
  const [mongoId, setMongoId] = useState("");
  const [name, setName] = useState("");
  const [bdId, setBdId] = useState("");
  const [mobileNumber, setMobileNumber] = useState("");
  const [code, setCode] = useState("");
  const [bankDetails, setbankDetails] = useState();
  const [start, setStart] = useState(1);
  const [limit] = useState(10);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const scrollRef = useRef(null);
  const scrollPosition = useRef(0);
  const [errors, setError] = useState({
    name: "",
    uniqueId: "",
    mobileNumber: "",
    code: "",
    image: "",
    bankDetails: "",
  });

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

  useEffect(() => {
    setData(coinSellerId);
  }, [coinSellerId]);

  useEffect(() => {
    if (dialogData) {
      setMongoId(dialogData?._id);
      setName(dialogData?.name);
      setImagePath(dialogData?.image);
      setUniqueId(dialogData?.uniqueId);
      setMobileNumber(dialogData?.mobile);
      setCode(dialogData?.agencyCode);
      setbankDetails(dialogData?.bankDetails);
    }
  }, [dialogData]);

  $(document).ready(function () {
    $("img").bind("error", function () {
      $(this).attr("src", Male);
    });
  });

  useEffect(
    () => () => {
      setError({
        name: "",
        mobileNumber: "",
        password: "",
        code: "",
        image: "",
        uniqueId: "",
        bankDetails: "",
      });
      setMongoId("");
      setName("");
      setUniqueId("");
      setBdId("");
      setCode("");
      setMobileNumber("");
      setImageData([]);
      setbankDetails("");
      setImagePath(null);
    },
    [open]
  );

  const handleInputImage = (e) => {
    if (e.target.files[0]) {
      setImageData(e.target.files[0]);
      const reader = new FileReader();

      reader.addEventListener("load", () => {
        setImagePath(reader.result);
      });
      reader.readAsDataURL(e.target.files[0]);
    }
    if (!e.target.files[0]) {
      return setError({
        ...errors,
        image: "Please select an Image!",
      });
    } else {
      return setError({
        ...errors,
        image: "",
      });
    }
  };

  const createCode = () => {
    const randomChars = "0123456789";
    let code_ = "";
    for (let i = 0; i < 5; i++) {
      code_ += randomChars.charAt(
        Math.floor(Math.random() * randomChars.length)
      );
      setCode(code_);
    }
    if (!code_) {
      return setError({
        ...errors,
        code: "Code can't be a blank!",
      });
    } else {
      return setError({
        ...errors,
        code: "",
      });
    }
  };

  const handleKeyPress = (event) => {
    if (event.key === "Enter") {
      handleSubmit();
    }
  };

  const handleSubmit = (e) => {
    if (!name || !code || !mobileNumber || !uniqueId || !bankDetails) {
      const errors = {};
      if (!name) {
        errors.name = "Name can't be a blank!";
      }
      if (!uniqueId) errors.uniqueId = "UniqueId Is Required !";
      if (!mobileNumber) errors.mobileNumber = "MobileNumber is Required !";
      if (!code) {
        errors.code = "Code can't be a blank!";
      }

      if (!bankDetails) errors.bankDetails = "Bank Details can't be a blank!";

      return setError({ ...errors });
    }
    if (code?.length > 10) {
      return setError({
        ...errors,
        code: "Maximum 6 Digits are Allowed!",
      });
    }

    if (code?.length < 5) {
      return setError({
        ...errors,
        code: "Minimum 5 Digits are Allowed!",
      });
    }

    if (!mongoId) {
      const index = agency?.findIndex(
        (agency) => agency?.code?.toString() === code
      );
      if (index > -1) {
        return setError({ ...errors, code: "Code already exist." });
      }
    } else {
      const index = agency?.find((agency) => agency?.code?.toString() === code);
      if (index !== undefined) {
        if (index?._id === mongoId) {
        } else {
          return setError({ ...errors, code: "Code already exist." });
        }
      }
    }

    const formData = new FormData();
    formData.append("image", imageData);
    formData.append("name", name);
    formData.append("uniqueId", uniqueId);
    formData.append("agencyCode", code);
    formData.append("mobile", mobileNumber);
    formData.append("bankDetails", bankDetails);

    if (mongoId) {
      props.editAgency(formData, mongoId);
    } else {
      props.createNewAgency(formData);
    }
    closePopup();
  };

  const closePopup = () => {
    dispatch({ type: CLOSE_AGENCY_DIALOG });
  };

  const customStyles = {
    option: (provided, state) => ({
      ...provided,
      ":hover": {
        backgroundColor: "#BBDEFB",
        color: "#000",
      },
    }),
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
          <span className="text-danger font-weight-bold h4"> Agency </span>
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
                  {!dialogData && (
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
                  )}
                  <div className="form-group col-12 mt-3">
                    <label className="mb-2 text-gray">Name</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Enter Name"
                      required
                      value={name}
                      onKeyPress={handleKeyPress}
                      onChange={(e) => {
                        setName(e.target.value.trim());

                        if (!e.target.value) {
                          return setError({
                            ...errors,
                            name: "Name can't be a blank!",
                          });
                        } else {
                          return setError({
                            ...errors,
                            name: "",
                          });
                        }
                      }}
                    />
                    {errors.name && (
                      <div className="ml-2 mt-1">
                        {errors.name && (
                          <div className="pl-1 text__left">
                            <span className="text-red">{errors.name}</span>
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                </div>

                <div className={`${mongoId ? "col-12" : "col-md-12"}`}>
                  <div className="form-group mt-2">
                    <label className="mb-2 text-gray">Mobile Number</label>
                    <input
                      type="number"
                      className="form-control"
                      placeholder="Enter Mobile Number"
                      required
                      value={mobileNumber}
                      onKeyPress={handleKeyPress}
                      onChange={(e) => {
                        setMobileNumber(e.target.value);

                        if (!e.target.value) {
                          return setError({
                            ...errors,
                            mobileNumber: "mobileNumber can't be a blank!",
                          });
                        } else {
                          return setError({
                            ...errors,
                            mobileNumber: "",
                          });
                        }
                      }}
                    />
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
                <div className="col-12 mt-2">
                  <div className="form-group">
                    <label className="mb-2 text-gray">Bank Details</label>
                    <textarea
                      className="form-control"
                      placeholder="Enter Bank Details"
                      required
                      rows={4}
                      value={bankDetails}
                      onKeyPress={handleKeyPress}
                      onChange={(e) => {
                        setbankDetails(e.target.value);

                        if (!e.target.value) {
                          return setError({
                            ...errors,
                            bankDetails: "bankDetails can't be a blank!",
                          });
                        } else {
                          return setError({
                            ...errors,
                            bankDetails: "",
                          });
                        }
                      }}
                    />
                    {errors.bankDetails && (
                      <div className="ml-2 mt-1">
                        {errors.bankDetails && (
                          <div className="pl-1 text__left">
                            <span className="text-red">
                              {errors.bankDetails}
                            </span>
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                </div>

                <div className="row d-flex mt-3">
                  <div className={`${mongoId ? "col-12" : "col-md-9"}`}>
                    <div className="form-group">
                      <label className="mb-2 text-gray">Agency Code</label>
                      <input
                        readOnly
                        type="number"
                        className="form-control"
                        placeholder="Enter Code"
                        required
                        value={code}
                        onKeyPress={handleKeyPress}
                        onChange={(e) => {
                          setCode(e.target.value);

                          if (!e.target.value) {
                            return setError({
                              ...errors,
                              code: "Code can't be a blank!",
                            });
                          } else {
                            return setError({
                              ...errors,
                              code: "",
                            });
                          }
                        }}
                      />
                      {errors.code && (
                        <div className="ml-2 mt-1">
                          {errors.code && (
                            <div className="pl-1 text__left">
                              <span className="text-red">{errors.code}</span>
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>

                  {!mongoId && (
                    <div
                      className="col-md-3 pl-0 d-flex justify-content-end align-items-center"
                      style={{ marginTop: "22.01px" }}
                    >
                      <button
                        type="button"
                        className="btn btn-info"
                        style={{
                          borderRadius: 5,
                          fontSize: "13px",
                          padding: "8px",
                          marginTop : "5px"
                        }}
                        onClick={createCode}
                      >
                        Auto Generate
                      </button>
                    </div>
                  )}
                </div>
                <div className={imagePath ? "mt-3 pt-3" : "mt-5"}>
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

export default connect(null, { createNewAgency, editAgency })(AgencyDialogue);
