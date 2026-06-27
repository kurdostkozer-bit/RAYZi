import React, { useEffect, useRef, useState } from "react";
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
// react dropzone
import { baseURL } from "../../util/Config";
import {
  updateAdmissionSVGA,
  crateAdmissionSVGA,
} from "../../store/AdmissionCar/action";
import { CLOSE_DIALOGUE_ADMISSION_CAR } from "../../store/AdmissionCar/type";

import SVGA from "svgaplayerweb";
import { Typography } from "antd";
import html2canvas from "html2canvas";
import {
  CropperRef,
  Cropper,
  CropperPreviewWrapper,
} from "react-advanced-cropper";


const AdmissionCarDialogue = (props) => {
  const { Dialogue, DialogueData, DialogueType } = useSelector(
    (state) => state.admissionSVGA
  );

  const [validity, setValidity] = useState("");
  const [validityType, setValidityType] = useState("");
  const [mongoId, setMongoId] = useState("");
  const [diamond, setDiamond] = useState("");
  const [name, setName] = useState("");
  const [images, setImages] = useState("");
  const [errors, setError] = useState("");
  const [imagePath, setImagePath] = useState("");
  const [imageData, setImageData] = useState("");
  const [isSvga, setIsSvga] = useState(true);
  const [image, setImage] = useState();
  const [isSubmit, setIsSubmit] = useState(true);
  const [cropper, setCropper] = useState();
  const imageRef = useRef();

  const dispatch = useDispatch();

  useEffect(
    () => () => {
      setValidity("");
      setValidityType("");
      setError("");
      setName("");
      setDiamond("");
      setIsSubmit(true);
      setImages("");
      setIsSvga(true);
      setImageData(null);
      setImagePath(null);
    },
    [Dialogue]
  );

  useEffect(() => {
    if (DialogueData) {
      setValidity(DialogueData?.validity);
      setValidityType(DialogueData?.validityType);
      setMongoId(DialogueData?._id);
      setDiamond(DialogueData?.diamond);
      setName(DialogueData?.name);
      setImagePath(baseURL + DialogueData?.image);
      setImageData(baseURL + DialogueData?.image);
      if (DialogueData?.imageVideo?.split(".")?.pop() === "svga") {
        setIsSvga(true);
      }
    }
  }, [DialogueData]);

  const handleInputImage = (e) => {
    setImage("");
    if (e.target.files[0]) {
      Object.assign(e.target.files[0], {
        preview: URL.createObjectURL(e.target.files[0]),
      });
      setImageData(e.target.files[0]);
      setImages(e.target.files[0]);

      const reader = new FileReader();
      reader.addEventListener("load", () => {
        setImagePath(reader.result);
        setError({
          image: "",
        });
      });
      reader.readAsDataURL(e.target.files[0]);
      if (e.target.files[0].name.split(".").pop() === "svga") {
        setIsSvga(true);
      } else {
        setIsSvga(false);
      }
    }
  };

  // // for .svga file
  useEffect(() => {
    if (isSvga) {
      if (!!document.getElementById("svga") && imagePath) {
        var player = new SVGA.Player(`div[attr="${mongoId}"]`);
        var parser = new SVGA.Parser(`div[attr="${mongoId}"]`);
        if (imageData?.preview) {
          parser.load(imageData.preview, function (videoItem) {
            player.setVideoItem(videoItem);
            player.startAnimation();
            setTimeout(() => {
              captureAndSendImage(player, mongoId);
            }, 3000);
          });
        } else {
          parser.load(baseURL + DialogueData?.image, function (videoItem) {
            player.setVideoItem(videoItem);
            player.startAnimation();
            setTimeout(() => {
              captureAndSendImage(player, mongoId);
            }, 3000);
          });
        }
      }
    } else {
      setIsSubmit(false);
    }
  }, [imageData, isSvga, imagePath]);

  const captureAndSendImage = (player, index) => {
    return new Promise((resolve) => {
      player.pauseAnimation();

      const container = document.querySelector(`div[attr="${index}"]`);
      const canvas = document.createElement("canvas");

      // Set the desired width and height for the canvas
      const width = container?.offsetWidth;
      const height = container?.offsetHeight;

      canvas.width = width;
      canvas.height = height;

      html2canvas(container, {
        scale: 1,
        useCORS: true,
        backgroundColor: "rgba(0, 0, 0, 0)",
        onclone: (cloneDoc) => {
          const clonedCanvas = cloneDoc.querySelector(
            `div[attr="${index}"] canvas`
          );
          clonedCanvas.style.backgroundColor = "transparent";
        },
      }).then((canvas) => {
        const data = canvas.toDataURL("image/png");
        canvas.toBlob((blob) => {
          resolve(blob);
          setImage(blob);
          setIsSubmit(false);
        }, "image/png");
      });
    });
  };

  const closePopup = () => {
    dispatch({
      type: CLOSE_DIALOGUE_ADMISSION_CAR,
      payload: {
        DialogueType: "",
        Dialog: false,
        DialogueData: "",
      },
    });
  };

  const onChange = (cropperRef) => {
    setCropper(cropperRef);
  };

  const handleSubmit = async () => {
    if (!name || !diamond || diamond < 0 || !validity || validity < 0) {
      const errors = {};
      if (!validity) errors.validity = "Validity is required!";
      if (validity < 0) errors.validity = "invalid value of validity";
      if (!name) errors.name = "Name is Required";
      if (!diamond) errors.diamond = "Diamond is required";
      if (diamond < 0) errors.diamond = "Invalid Diamond ";
      if (images.length === 0) errors.images = "Please select an Image!";

      return setError({ ...errors });
    } else {
  
      const formData = new FormData();
      formData.append("thumbnail", image);
      formData.append("validity", validity);
      formData.append("validityType", validityType ? validityType : "day");
      formData.append("diamond", diamond);
      formData.append("imageVideo", images);
      formData.append("name", name);
      if (DialogueData) {
        props.updateAdmissionSVGA(mongoId, formData);
      } else {
        props.crateAdmissionSVGA(formData);
      }
      closePopup();
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
        <div>
          <div
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              width: "100%",
            }}
          >
            <DialogTitle id="responsive-dialog-title w-100">
              <span className="text-danger font-weight-bold h4">
                Entry Effect
              </span>
            </DialogTitle>

            <IconButton onClick={closePopup}>
              <Tooltip title="Close">
                <Cancel className="text-danger" />
              </Tooltip>
            </IconButton>
          </div>
          <DialogContent style={{ overflowY: "hidden" }}>
            <div style={{ display: "none" }}>
              <Cropper
                defaultCoordinates={{
                  height: 221,
                  left: 77,
                  top: 128,
                  width: 192,
                }}
                src={image}
                onChange={onChange}
                className={"cropper"}
              />
              <img
                ref={imageRef}
                src={image}
                alt="Original"
                style={{ display: "none" }}
              />
            </div>
            <div className="modal-body pt-1 px-1 pb-3">
              <div className="d-flex flex-column">
                <form>
                  <div className="row form-data-body">
                    <div className="col-md-6 col-12">
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
                    <div className="col-md-6 col-12">
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
                    <div className="col-md-6 col-12">
                      <div className="form-group mt-2">
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
                    </div>
                    <div className="col-md-6 col-12">
                      <div className="form-group mt-2">
                        <label className="text-gray mb-2">Diamond</label>
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
                                <span className="text-red">
                                  {errors.diamond}
                                </span>
                              </div>
                            )}
                          </div>
                        )}
                      </div>
                    </div>
                    <div className="col-12">
                      <div className="form-group mt-2">
                        <label className="text-gray mb-2">Image Upload</label>
                        <input
                          className="form-control"
                          type="file"
                          required=""
                          accept=".svga"
                          onChange={handleInputImage}
                        />
                        {errors.image && (
                          <div className="pl-1 text-left">
                            <Typography variant="caption" color="error">
                              {errors.image}
                            </Typography>
                          </div>
                        )}
                      </div>
                    </div>
                    <div className="col-12">
                      {imagePath && (
                        <>
                          {!isSvga ? (
                            <img
                              src={imagePath}
                              className="mt-3 rounded float-left mb-2"
                              height="100px"
                              width="100px"
                            />
                          ) : (
                            <>
                              <div
                                id="svga"
                                attr={mongoId}
                                style={{
                                  boxShadow:
                                    "0 5px 15px 0 rgb(105 103 103 / 00%)",
                                  float: "left",
                                  objectFit: "contain",
                                  marginBottom: "28px",
                                  overflow: "hidden",
                                  marginRight: 15,
                                  width: "350px",
                                  height: "350px",
                                }}
                              ></div>
                            </>
                          )}
                        </>
                      )}
                    </div>
                  </div>

                  <div className="modal-footer ">
                    <div>
                      <button
                        type="button"
                        className="btn btn-outline-info ml-2 btn-round float__right icon_margin"
                        onClick={closePopup}
                      >
                        Close
                      </button>
                      <button
                        type="button"
                        disabled={isSubmit}
                        className="btn btn-round float__right btn-danger"
                        onClick={handleSubmit}
                      >
                        Submit
                      </button>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </DialogContent>{" "}
        </div>
      </Dialog>
    </>
  );
};

export default connect(null, { crateAdmissionSVGA, updateAdmissionSVGA })(
  AdmissionCarDialogue
);
