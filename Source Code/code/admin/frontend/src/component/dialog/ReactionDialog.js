import React, { useEffect, useState } from "react";

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

import { createReaction, editReaction } from "../../store/reaction/action";
import { CLOSE_REACTION_DIALOG } from "../../store/reaction/type";

const ReactionDialog = (props) => {
  const dispatch = useDispatch();

  const { dialog: open, dialogData } = useSelector((state) => state.reaction);


  const [mongoId, setMongoId] = useState("");
  const [imageData, setImageData] = useState();
  const [imagePath, setImagePath] = useState();
  const [name,setName] = useState("");

  const [error, setError] = useState("");
  const [nameError,setNameError] = useState("")
  // useEffect(() => {
  //   if (dialogData) {
  //     setMongoId(dialogData._id);
  //     setImagePath(dialogData.image);
  //   }
  // }, [dialogData]);

  useEffect(
    () => () => {
      setError("");
      setMongoId("");
      setImageData(null);
      setImagePath(null);
      setName("")
      setNameError("")
    },
    [open]
  );

  const HandleInputImage = (e) => {
    if (e.target.files[0]) {
      setImageData(e.target.files[0]);
      const reader = new FileReader();
      reader.addEventListener("load", () => {
        setImagePath(reader.result);
      });
      reader.readAsDataURL(e.target.files[0]);
      setError("")
    }
  };

  const closePopup = () => {
    dispatch({ type: CLOSE_REACTION_DIALOG });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!imagePath) {
      return setError("Image Required");
    }else if(!name){
      return setNameError("Name Required")
    }
    const formData = new FormData();
    formData.append("image", imageData);
    formData.append("name", name);



    // if (mongoId) {
    //   props.editReaction(mongoId, formData);
    // } else {
        props.createReaction(formData);
    // }
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
          <span className="text-danger font-weight-bold h4">Reaction </span>
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
                <div className="form-group col-12 mt-3">
                  <label className="mb-2 text-gray">Name</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Enter Name"
                    required
                    value={name}
                    onChange={(e) => {
                      setName(e.target.value.trim());

                      if (!e.target.value) {
                        return setNameError("Name can't be a blank!");
                      } else {
                        return setNameError("")
                      }
                    }}
                  />
                  {nameError && (
                    <div className="ml-2 mt-1">
                      {nameError && (
                        <div className="pl-1 text__left">
                          <span className="text-red">{nameError}</span>
                        </div>
                      )}
                    </div>
                  )}
                </div>
                <div className="form-group mt-4">
                  <label className="mb-2 text-gray">Image</label>
                  <input
                    type="file"
                    className="form-control form-control-sm"
                    accept="image/jpg,image/jpeg,image/png,image/gif"
                    required=""
                    onChange={HandleInputImage}
                  />
                  {error && (
                    <div className="ml-2 mt-1">
                      {error && (
                        <div className="pl-1 text__left">
                          <span className="text-red">{error}</span>
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
                          // boxShadow: "0 5px 15px 0 rgb(105 103 103 / 50%)",
                          // border: "2px solid #fff",
                          borderRadius: 10,
                          marginTop: 10,
                          float: "left",
                        }}
                      />
                    </>
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

export default connect(null, { createReaction, editReaction })(ReactionDialog);
