import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
} from "@mui/material";
import React, { Fragment, useEffect, useRef, useState } from "react";
import { CLOSE_SVGA_DIALOG } from "../../store/gift/types";
import { connect, useDispatch, useSelector } from "react-redux";
import { createNewGiftSvga } from "../../store/gift/action";
import SVGA from "svgaplayerweb";

import { baseURL } from "../../util/Config";
import { Cancel } from "@mui/icons-material";
import { getCategory } from "../../store/giftCategory/action";
import { Cropper } from "react-advanced-cropper";
import html2canvas from "html2canvas";

const AddSvgaDialogue = (props) => {
  const dispatch = useDispatch();
  const {
    dialog1: open,
    dialogData1,
    gift,
  } = useSelector((state) => state.gift);

  const categories = useSelector((state) => state.giftCategory.giftCategory);
  const [images, setImages] = useState([]);
  const [imageData, setImageData] = useState(null);
  const [imagePath, setImagePath] = useState(null);
  const [mongoId, setMongoId] = useState("");
  const [coin, setCoin] = useState(0);
  const [category, setCategory] = useState("");
  const [isSvga, setIsSvga] = useState(false);
  const [image, setImage] = useState();
  const [cropper, setCropper] = useState(null);
  const [isSubmit, setIsSubmit] = useState(true);
  const imageRef = useRef(null);

  const [errors, setError] = useState({
    coin: "",
    image: "",
    category: "",
  });

  useEffect(() => {
    dispatch(getCategory());
  }, [dispatch]);

  useEffect(() => {
    setIsSubmit(true);
  }, [open]);

  useEffect(() => {
    if (dialogData1) {
      setMongoId(dialogData1._id);
      setCoin(dialogData1.coin);
      setCategory(dialogData1.category._id);
      setImagePath(baseURL + "/" + dialogData1.icon);
      if (dialogData1?.icon?.split(".")?.pop() === "svga") {
        setIsSvga(true);
      }
    }
  }, [dialogData1]);

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
          parser.load(baseURL + dialogData1?.image, function (videoItem) {
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


  useEffect(
    () => () => {
      setError({
        coin: "",
        image: "",
        category: "",
      });
      setMongoId("");
      setCoin(0);
      setCategory("");
      setImages([]);
      setImageData(null);
      setImagePath(null);
      setIsSvga(false);
    },
    [open]
  );

  const handleInputImage = (e) => {
    setImage("");
    if (e.target.files[0]) {
      Object.assign(e.target.files[0], {
        preview: URL.createObjectURL(e.target.files[0]),
      });
      setImageData(e.target.files[0]);
      setImages([e.target.files[0]]);
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
  const imageSvg = useRef();
  const handleSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();

    if (!coin || !category) {
      const errors = {};

      if (!coin) {
        errors.coin = "Coin can't be a blank!";
      }
      if (!category) {
        errors.category = "Category can't be a blank!";
      }
      if (images.length === 0) {
        errors.image = "Please select an Image!";
      }

      return setError({ ...errors });
    }

    if (mongoId) {
      if (!imageData && !imagePath) {
        return setError({ ...errors, image: "Please select an Image!" });
      }
    } else {
      if (images.length === 0) {
        return setError({ ...errors, image: "Please select an Image!" });
      }
    }

    if (image) {
      formData.append("svgaImage", image);
    }

    formData.append("coin", coin);
    formData.append("category", category);

    if (mongoId) {
      formData.append("image", imageData);
    } else {
      for (let i = 0; i < images.length; i++) {
        formData.append("image", images[i]);
      }
    }

    if (mongoId) {
      props.editGift(formData, mongoId);
      closePopup();
    } else {
      closePopup();
      props.createNewGiftSvga(formData, category);
    }
  };

  const closePopup = () => {
    dispatch({ type: CLOSE_SVGA_DIALOG });
  };

  // Payload data and url to upload files
  const getUploadParams = ({ meta }) => {
    return { url: "https://httpbin.org/post" };
  };

  // Return the current status of files being uploaded
  const handleChangeStatus = ({ meta, file }, status) => {
    if (status === "removed") {
      const filteredItems = images.filter((item) => item !== file);
      setImages(filteredItems);
    }

    if (status === "done") {
      images.push(file);
    }
  };

  const onChange = (cropperRef) => {
    setCropper(cropperRef);
  };



  const captureAndSendImage = (player, index) => {
    return new Promise((resolve) => {
      player.pauseAnimation();
      const canvasElement = document.querySelector(
        `div[attr="${index}"] canvas`
      );

      if (!canvasElement) {
        return;
      }
      html2canvas(canvasElement, {
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
          <span className="text-danger font-weight-bold h4"> SVGA </span>
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
              <div
                style={{ position: "absolute", opacity: "0", zIndex: "-111" }}
              >
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
              <form>
                <div className="form-group">
                  <label className="mb-2 text-gray">Coin</label>
                  <input
                    type="number"
                    className="form-control"
                    placeholder="Magic"
                    required
                    value={coin}
                    onChange={(e) => {
                      setCoin(e.target.value);

                      if (!e.target.value) {
                        return setError({
                          ...errors,
                          coin: "coin can't be a blank!",
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

                  <label className="mb-2 mt-2 text-gray">Category</label>

                  <select
                    className="form-select form-control"
                    aria-label="Default select example"
                    value={category}
                    onChange={(e) => {
                      setCategory(e.target.value);

                      if (!e.target.value) {
                        return setError({
                          ...errors,
                          category: "Please select a Category!",
                        });
                      } else if (e.target.value == "Category") {
                        return setError({
                          ...errors,
                          category: "Please select a Category!",
                        });
                      } else {
                        return setError({
                          ...errors,
                          category: "",
                        });
                      }
                    }}
                  >
                    <option selected>Category</option>
                    {categories.map((category) => {
                      return (
                        <option value={category._id}>{category.name}</option>
                      );
                    })}
                  </select>
                  {errors.category && (
                    <div className="ml-2 mt-1">
                      {errors.category && (
                        <div className="pl-1 text__left">
                          <span className="text-red">{errors.category}</span>
                        </div>
                      )}
                    </div>
                  )}

                  <div className="form-group">
                    <label className="mb-2 mt-2 text-gray">Icon</label>

                
                    <>
                      <input
                        className="form-control"
                        type="file"
                        required=""
                        accept=".svga, .gift"
                        onChange={handleInputImage}
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
                        <Fragment>
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
                                  marginRight: 15,
                                  width: "350px",
                                  marginBottom: "10px",
                                  height: "350px",
                                }}
                              ></div>
                            </>
                          )}
                        </Fragment>
                      )}
                    </>
                    {/* )} */}
                  </div>
                </div>
                <div className="mt-3">
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
              </form>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default connect(null, { getCategory, createNewGiftSvga })(
  AddSvgaDialogue
);
