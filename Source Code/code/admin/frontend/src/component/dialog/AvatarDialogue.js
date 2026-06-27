import React, { useEffect, useState } from "react";
import { connect, useDispatch, useSelector } from "react-redux";

import {
  updateAvatarFrame,
  crateAvatarFrame,
} from "../../store/AvatarFrame/action";

//MUI
import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
import { Cancel } from "@mui/icons-material";
import { CLOSE_DIALOGUE_AVATAR_FRAME } from "../../store/AvatarFrame/type";

// react dropzone
import ReactDropzone from "react-dropzone";
import { baseURL } from "../../util/Config";



const AvatarDialogue = (props) => {
  const { Dialogue, DialogueData } = useSelector((state) => state.avatarFrame);

  const [mongoId, setMongoId] = useState("");
  const [validity, setValidity] = useState("");
  const [validityType, setValidityType] = useState("");
  const [diamond, setDiamond] = useState("");
  const [name, setName] = useState("");
  const [images, setImages] = useState([]);
  const [errors, setError] = useState("");
  const [imageData, setImageData] = useState("");
  const [imagePath, setImagePath] = useState("");

  useEffect(() => {
    setError("");
    setName("");
    setDiamond("");
    setImageData(null);
    setImages([]);
  }, [Dialogue]);

  useEffect(() => {
    if (DialogueData) {
      setValidity(DialogueData?.validity);
      setValidityType(DialogueData?.validityType);
      setMongoId(DialogueData?._id);
      setDiamond(DialogueData?.diamond);
      setName(DialogueData?.name);
      setImagePath(baseURL + DialogueData?.image);
    }
  }, [DialogueData]);

  useEffect(
    () => () => {
      setValidity("");
      setValidityType("");
      setError("");
      setName("");
      setDiamond("");
      setImageData(null);
      setImagePath(null);
    },
    []
  );

  const dispatch = useDispatch();

  const closePopup = () => {
    dispatch({ type: CLOSE_DIALOGUE_AVATAR_FRAME });
  };

  const handleSubmit = () => {
    if (
      !name || !diamond || diamond < 0 || !mongoId
        ? images.length === 0
        : !imagePath || !validity
    ) {
      const errors = {};
      if (!name) errors.name = "Name is Required";
      if (validity < 0) errors.validity = "invalid value of validity";
      if (!validity) errors.validity = "Validity is required!";
      if (!diamond) errors.diamond = "Diamond is required";
      if (diamond < 0) errors.diamond = "Invalid Diamond ";
      if (images.length === 0) errors.images = "Please select an Image!";

      return setError({ ...errors });
    } else {
  
      const formData = new FormData();
      formData.append("diamond", diamond);
      formData.append("validity", validity);
      formData.append("validityType", validityType ? validityType : "day");
      if (DialogueData) {
        formData.append("image", imageData);
      } else {
        for (let i = 0; i < images.length; i++) {
          formData.append("imageVideo", images[i]);
        }
      }
      formData.append("name", name);
      if (DialogueData) {
        props.updateAvatarFrame(mongoId, formData);
      } else {
        props.crateAvatarFrame(formData);
      }
    }
    closePopup();
  };

  const onPreviewDrop = (files) => {
    setError({ ...errors, image: "" });
    files.map((file) =>
      Object.assign(file, { preview: URL.createObjectURL(file) })
    );
    setImages(images.concat(files));
  };

  const removeImage = (file) => {
    if (file.preview) {
      const image = images.filter((ele) => {
        return ele.preview !== file.preview;
      });
      setImages(image);
    }
  };

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

  return (
    <>
      <Dialog
        open={Dialogue}
        aria-labelledby="responsive-dialog-title"
        onClose={closePopup}
        disableBackdropClick
        disableEscapeKeyDown
        fullWidth
        maxWidth="xs"
      >
        <DialogTitle id="responsive-dialog-title">
          <span className="text-danger font-weight-bold h4">Avatar Frame</span>
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
                  <div className="col-md-6">
                    <div className="form-group">
                      <label className="text-gray mb-2">Validity</label>
                      <input
                        type="number"
                        className="form-control"
                        required=""
                        placeholder="1"
                        min="0"
                        value={validity}
                        onChange={(e) => {
                          setValidity(e.target.value);
                          if (!e.target.value) {
                            return setError({
                              ...errors,
                              validity: "Validity is Required!",
                            });
                          } else {
                            return setError({
                              ...errors,
                              validity: "",
                            });
                          }
                        }}
                      />
                      {errors.validity && (
                        <div className="ml-2 mt-1">
                          {errors.validity && (
                            <div className="pl-1 text__left">
                              <span className="text-red">
                                {errors.validity}
                              </span>
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                  <div className="col-md-6">
                    <div className="form-group">
                      <label className="text-gray mb-2">Validity Type</label>
                      <select
                        className="form-select form-control"
                        aria-label="Default select example"
                        value={validityType}
                        onChange={(e) => {
                          setValidityType(e.target.value);
                        }}
                      >
                        <option value="day">Day</option>
                        <option value="Month">Month</option>
                        <option value="year">Year</option>
                      </select>
                    </div>
                  </div>
                </div>
                <div className="form-group">
                  <label className="mb-2 text-gray">diamond</label>
                  <input
                    type="number"
                    className="form-control"
                    required=""
                    min="0"
                    placeholder="20"
                    value={diamond}
                    onChange={(e) => {
                      setDiamond(e.target.value);
                      if (!e.target.value) {
                        return setError({
                          ...errors,
                          diamond: "diamond is Required!",
                        });
                      } else {
                        return setError({
                          ...errors,
                          diamond: "",
                        });
                      }
                    }}
                  />
                  {errors.diamond && (
                    <div className="ml-2 mt-1">
                      {errors.diamond && (
                        <div className="pl-1 text__left">
                          <span className="text-red">{errors.diamond}</span>
                        </div>
                      )}
                    </div>
                  )}
                </div>
                <div className="form-group mt-4">
                  <label className="text-gray mb-2">Name</label>

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
                </div>

                {DialogueData ? (
                  <div className="form-group mt-4">
                    <label className="mb-2 text-gray">Select Image</label>
                    <input
                      type="file"
                      className="form-control form-control-sm"
                      accept=".gif"
                      required=""
                      onChange={HandleInputImage}
                    />
                    {errors.image && (
                      <div className="ml-2 mt-1">
                        {errors.image && (
                          <div className="pl-1 text__left">
                            <span className="text-red">{errors.image}</span>
                          </div>
                        )}
                      </div>
                    )}
                    {imagePath && (
                      <>
                        <img
                          height="70px"
                          width="70px"
                          alt="app"
                          src={imagePath}
                          style={{
                            boxShadow: "0 5px 15px 0 rgb(105 103 103 / 0%)",
                            // border: "2px solid #fff",
                            borderRadius: 10,
                            marginTop: 10,
                            float: "left",
                            objectFit: "cover",
                          }}
                        />
                      </>
                    )}
                  </div>
                ) : (
                  <div className="row mt-4">
                    <div className="col-lg-6">
                      <label
                        className="form-control-label"
                        htmlFor="input-username"
                      >
                        Select (Multiple) Images
                      </label>

                      <>
                        <ReactDropzone
                          onDrop={(acceptedFiles) =>
                            onPreviewDrop(acceptedFiles)
                          }
                          accept="image/*"
                        >
                          {({ getRootProps, getInputProps }) => (
                            <section>
                              <div {...getRootProps()}>
                                <input {...getInputProps()} />
                                <div
                                  style={{
                                    height: 130,
                                    width: 130,
                                    border: "2px dashed gray",
                                    textAlign: "center",
                                    marginTop: "10px",
                                  }}
                                >
                                  <i
                                    className="fas fa-plus"
                                    style={{ paddingTop: 30, fontSize: 70 }}
                                  ></i>
                                </div>
                              </div>
                            </section>
                          )}
                        </ReactDropzone>

                        {errors.images && (
                          <div className="ml-2 mt-1">
                            {errors.images && (
                              <div className="pl-1 text__left">
                                <span className="text-red">
                                  {errors.images}
                                </span>
                              </div>
                            )}
                          </div>
                        )}
                      </>
                    </div>
                    <div className="col-lg-6 mt-4">
                      {images.length > 0 && (
                        <>
                          {images.map((file, index) => {
                            return (
                              file.type?.split("image")[0] === "" && (
                                <>
                                  <img
                                    height="60px"
                                    width="60px"
                                    alt="app"
                                    src={file.preview}
                                    style={{
                                      boxShadow:
                                        "0 5px 15px 0 rgb(105 103 103 / 00%)",
                                      border: "2px solid #fff",
                                      borderRadius: 10,
                                      marginTop: 10,
                                      float: "left",
                                      objectFit: "contain",
                                      marginRight: 15,
                                    }}
                                  />
                                  <div
                                    className="img-container"
                                    style={{
                                      display: "inline",
                                      position: "relative",
                                      float: "left",
                                    }}
                                  >
                                    <i
                                      className="fas fa-times-circle text-danger"
                                      style={{
                                        position: "absolute",
                                        right: "10px",
                                        top: "4px",
                                        cursor: "pointer",
                                      }}
                                      onClick={() => removeImage(file)}
                                    ></i>
                                  </div>
                                </>
                              )
                            );
                          })}
                        </>
                      )}
                    </div>
                  </div>
                )}

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

export default connect(null, { updateAvatarFrame, crateAvatarFrame })(
  AvatarDialogue
);
