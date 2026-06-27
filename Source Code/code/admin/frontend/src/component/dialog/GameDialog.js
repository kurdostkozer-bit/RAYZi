import React, { useEffect, useRef, useState } from "react";

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
import { Cancel } from "@mui/icons-material";

//types
import { CLOSE_CATEGORY_DIALOG } from "../../store/giftCategory/types";


//action
import { createNewGame, editGame } from "../../store/game/action";
import { baseURL } from "../../util/Config";
import { CLOSE_GAME_DIALOG } from "../../store/game/types";
import Select from "react-select";
import InfiniteScroll from "react-infinite-scroll-component";

const options = [
  { uniqueId: "casino", label: "Casino" },
  { uniqueId: "ferrywheel", label: "Ferry Wheel" },
  { uniqueId: "teenpattin", label: "Teenpattin" },
];

const GameDialog = (props) => {
  const dispatch = useDispatch();

  const { dialog: open, dialogData } = useSelector((state) => state.game);

  const settingId = useSelector((state) => state.game.settingId);

  const [mongoId, setMongoId] = useState("");
  const [name, setName] = useState("");
  const [gameLink, setGameLink] = useState("");
  const [imageData, setImageData] = useState(null);
  const [imagePath, setImagePath] = useState(null);
  const [minWinPercent, setMinWinPercent] = useState("");
  const [maxWinPercent, setMaxWinPercent] = useState("");


  const [search, setSearch] = useState("");
  const [hasMore, setHasMore] = useState(false);
  const scrollRef = useRef(null);
  const fetchMoreData = () => {
  };


  const [errors, setError] = useState({
    image: "",
    name: "",
    gameLink: "",
  });

  useEffect(() => {
    if (dialogData) {
      setMongoId(dialogData._id);
      setName(dialogData.name);
      setImagePath(dialogData.image);
      setGameLink(dialogData?.link);
      setMinWinPercent(dialogData.minWinPercent);
      setMaxWinPercent(dialogData.maxWinPercent);
    }
  }, [dialogData]);

  useEffect(
    () => () => {
      setError({
        image: "",
        name: "",
        gameLink: "",
        minWinPercent: "",
        maxWinPercent: "",
      });
      setMongoId("");
      setName("");
      setGameLink("");
      setMinWinPercent("");
      setMaxWinPercent("");
      setImageData(null);
      setImagePath(null);
    },
    []
  );

  const HandleInputImage = (e) => {
    if (e.target.files[0]) {
      setImageData(e.target.files[0]);
      const reader = new FileReader();
      reader.addEventListener("load", () => {
        setImagePath(reader.result);
      });
      reader.readAsDataURL(e.target.files[0]);
    }
  };

  const closePopup = () => {
    dispatch({ type: CLOSE_GAME_DIALOG });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const error = {};
    if (mongoId && (!name || !gameLink || !imagePath || !minWinPercent || !maxWinPercent)) {
      if (!name) error.name = "Name is Required!";
      if (!gameLink) error.gameLink = "Game Link is Required!";
      if (!imageData && !imagePath) error.image = "Image is Required!";
      if (!minWinPercent) error.minWinPercent = "Min Win Percent is Required!";
      if (!maxWinPercent) error.maxWinPercent = "Max Win Percent is Required!";
      return setError({ ...error });
    }

    const formData = new FormData();
    formData.append("image", imageData);
    formData.append("name", name);
    formData.append("link", gameLink);
    formData.append("minWinPercent", minWinPercent);
    formData.append("maxWinPercent", maxWinPercent);
    if (mongoId) {
      formData.append("gameId", mongoId);
    }

    if (mongoId) {
      props.editGame(mongoId, settingId, formData);
    } else {
      props.createNewGame(formData, settingId);
    }

    setMongoId("");
    setName("");
    setGameLink("");
    setMinWinPercent("");
    setMaxWinPercent("");
    setImageData(null);
    setImagePath(null);
    closePopup()
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
          <span className="text-danger font-weight-bold h4">Game </span>
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
                <div className="col-12 mt-3">
                  <div className="form-group">
                    <label className="mb-2 text-gray">Name</label>

                    <Select
                      value={options.find((option) => option.uniqueId === name)}
                      options={options}
                      getOptionLabel={(option) => option.label}
                      formatOptionLabel={(option) => (
                        <div className="country-option">
                          <span className="ms-3">{option.label}</span>
                        </div>
                      )}
                      onChange={(selectedOption) => setName(selectedOption.uniqueId)}
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
                              dataLength={options.length}
                              next={fetchMoreData}
                              hasMore={hasMore}
                              loader={<p>Loading more data...</p>}
                              scrollableTarget="scrollable-container"
                              scrollThreshold={0.9}
                              style={{ overflow: "hidden" }}
                              endMessage={
                                options.length > 0 && (
                                  <p style={{ textAlign: "center", padding: "8px" }}>
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
                  </div>
                </div>
                {/* <div className="form-group">
                  <label className="mb-2 text-gray">Name</label>
                  <input
                    type="text"
                    className="form-control"
                    required=""
                    placeholder="Name"
                    value={name}
                    onChange={(e) => {
                      setName(e.target.value);
                      if (!e.target.value) {
                        return setError({
                          ...errors,
                          name: "Name is Required!",
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
                </div> */}
                <div className="form-group mt-4">
                  <label className="mb-2 text-gray">Game Link</label>
                  <input
                    type="text"
                    className="form-control"
                    required=""
                    placeholder="Game Link"
                    value={gameLink}
                    onChange={(e) => {
                      setGameLink(e.target.value);
                      if (!e.target.value) {
                        return setError({
                          ...errors,
                          gameLink: "Game Link is Required!",
                        });
                      } else {
                        return setError({
                          ...errors,
                          gameLink: "",
                        });
                      }
                    }}
                  />
                  {errors.gameLink && (
                    <div className="ml-2 mt-1">
                      {errors.gameLink && (
                        <div className="pl-1 text__left">
                          <span className="text-red">{errors.gameLink}</span>
                        </div>
                      )}
                    </div>
                  )}
                </div>
                <div className="row">
  {/* Image Upload Section */}
  <div className="col-md-12">
    <div className="form-group mt-4">
      <label className="mb-2 text-gray">Image</label>
      <input
        type="file"
        className="form-control form-control-sm"
        accept="image/jpg ,image/jpeg ,image/png"
        required
        onChange={HandleInputImage}
      />
      {errors.image && (
        <div className="text-danger mt-1">
          <small>{errors.image}</small>
        </div>
      )}
      {imagePath && (
        <div className="mt-3">
          <img
            height="80"
            width="80"
            alt="app"
            src={imagePath}
            className="rounded shadow-sm border"
          />
        </div>
      )}
    </div>
  </div>

  {/* Win Percent Section */}
  <div className="col-md-12">
    <div className="form-group mt-4">
      <label className="mb-2 text-gray">Win Percent (Minimum)</label>
      <input
        type="number"
        className="form-control"
        required
        placeholder="Enter Win Percent"
        value={minWinPercent}
        onChange={(e) => {
          setMinWinPercent(e.target.value);
          setError({
            ...errors,
            minWinPercent: e.target.value ? "" : "Win Percent is required!",
          });
        }}
      />
      {errors.minWinPercent && (
        <div className="text-danger mt-1">
          <small>{errors.minWinPercent}</small>
        </div>
      )}
    </div>
  </div>
</div>

                <div className="form-group mt-4">
                  <label className="mb-2 text-gray">Win Percent (Maximum)</label>
                  <input
                    type="number"
                    className="form-control"
                    required=""
                    placeholder="Game Link"
                    value={maxWinPercent}
                    onChange={(e) => {
                      setMaxWinPercent(e.target.value);
                      if (!e.target.value) {
                        return setError({
                          ...errors,
                          maxWinPercent: "Game Link is Required!",
                        });
                      } else {
                        return setError({
                          ...errors,
                          maxWinPercent: "",
                        });
                      }
                    }}
                  />
                  {errors.maxWinPercent && (
                    <div className="ml-2 mt-1">
                      {errors.maxWinPercent && (
                        <div className="pl-1 text__left">
                          <span className="text-red">{errors.maxWinPercent}</span>
                        </div>
                      )}
                    </div>
                  )}
                </div>

                <div className={imagePath ? "mt-5 pt-5" : "mt-5"}>
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

export default connect(null, { createNewGame, editGame })(GameDialog);
