/* eslint-disable no-mixed-operators */
import React, { useEffect, useState } from "react";

// jqury

import $ from "jquery";

// routing
import { Link, useNavigate  } from "react-router-dom";

// redux
import { connect, useSelector } from "react-redux";

//action
import { getCountry, insertUser, editUser } from "../../store/FakeUser/Action";


import { baseURL } from "../../util/Config";
import axios from "axios";
import Select from "react-select";

const colourStyles = {
  option: (styles, { data, isDisabled, isFocused, isSelected }) => {
    return {
      ...styles,

      backgroundColor: isSelected ? "#181821" : "#1f1f2b",
      ":active": {
        ...styles[":active"],
        backgroundColor: !isDisabled
          ? isSelected
            ? "#181821"
            : "#1f1f2b"
          : undefined,
      },
      placeholder: (styles) => ({
        ...styles,
        color: "#fdfdfd",
      }),
    };
  },
};

const FakeAudioUserPage = (props) => {
  const navigate = useNavigate();


  const detail = JSON.parse(sessionStorage.getItem("fakeUser"));

  const [name, setName] = useState("");
  const [userName, setuserName] = useState("");
  const [gender, setGender] = useState("");
  const [age, setAge] = useState(0);
  const [image, setImage] = useState([]);
  const [imagePath, setImagePath] = useState("");
  const [imageType, setImageType] = useState("0");
  const [country, setCountry] = useState({
    value: "",
    label: "",
  });
  const [bio, setBio] = useState("");
  const [mongoId, setMongoId] = useState("");
  const [video, setVideo] = useState([]);

  const [pkVideoType, setPkVideoType] = useState("0");
  const [videoType, setVideoType] = useState("0");
  const [videoPath, setVideoPath] = useState("");
  const [showImage, setShowImage] = useState("");
  const [showVideo, setShowVideo] = useState("");
  const [countryData, setCountryData] = useState([]);
  const [roomName , setRoomName] = useState("");
  const [roomWelcome , setRoomWelcome] = useState("");
  const [roomImage  , setRoomImage] = useState([]);
  const [roomImagePath , setRoomImagePath] = useState("")

  useEffect(() => {
    axios.get("https://restcountries.com/v3.1/all").then((res) => {
      setCountryData(res.data);
    });
  }, []);

  const options = countryData.map((countryData) => ({
    label: countryData?.name?.common,
    value: countryData?.name?.common,
  }));

  const [errors, setError] = useState({
    name: "",
    userName: "",
    gender: "",
    age: "",
    imagePath: "",
    country: "",
    bio: "",
    videoPath: "",
    video: "",
    image: "",
    roomName : "",
    roomWelcome : "",
    roomImage : "",
    roomImagePath : ""
  });
  // const countries = useSelector((state) => state.fakeUser.country);

  useEffect(() => {
    if (detail) {
      setName(detail.name);
      setuserName(detail.username);
      setGender(detail.gender);
      setImageType("1");
      setShowImage(detail.image);
      setImagePath(detail.image);
      setAge(detail.age);
      setCountry(detail.country);
      setBio(detail.bio);
      setMongoId(detail._id);
      setVideoType("1");
      setShowVideo(detail.link);
      setCountry(detail?.country);
      setCountry({
        value: detail?.country,
        label: detail?.country,
      });

      setRoomName(detail?.roomName);
      setRoomImage(detail?.roomImage);
      setRoomWelcome(detail?.roomWelcome)
      setRoomImagePath(detail?.roomImage);
    }
  }, []);

  const HandleInputImage = (e) => {
    setError((prevErrors) => ({
      ...prevErrors,
      image: "",
    }));
    if (e.target.files[0]) {
      setImage(e.target.files[0]);
      setImagePath(URL.createObjectURL(e.target.files[0]));
      setShowImage(URL.createObjectURL(e.target.files[0]));
    }
  };

  const HandleRoomImage = (e) => {
    if (e.target.files[0]) {
      setRoomImage(e.target.files[0]);
      setRoomImagePath(URL.createObjectURL(e.target.files[0]));
    }
  };

  const HandleInputVideo = (e) => {
    setError((prevErrors) => ({
      ...prevErrors,
      video: "",
    }));
    if (e.target.files[0]) {
      setVideo(e.target.files[0]);
      const reader = new FileReader();
      reader.addEventListener("load", () => {
        setVideoPath(reader.result);
        setShowVideo(reader.result);
      });
      reader.readAsDataURL(e.target.files[0]);
    }
  };

  // set default image

  $(document).ready(function () {
    $("img").bind("error", function () {
      // Set the default image
      $(this).attr("src", `${baseURL}` + "storage/male.png");
    });
  });

  const handleSubmit = (e) => {
    
    if (
      !name ||
      !userName ||
      !gender ||
      !age ||
      !bio ||
      !country ||
      !image ||
      !roomName ||
      !roomWelcome ||
      !roomImage
      
    ) {
      const errors = {};
      if (!name) errors.name = "Name is Required!";
      if (!userName) errors.userName = "Username is Required!";
      if (!gender) errors.gender = "Gender is Required!";
      if (!age) errors.age = "Age is Required!";
      if (!bio) errors.bio = "Bio is Required!";
      if (!country) errors.country = "Country is Required!";
      if (image?.length === 0 || !image)
        errors.image = "User Image is Required!";
      
      if(!roomName) errors.roomName = "Room Name is Required!";
      if(!roomImage) errors.roomImage  = "Room Image is Required!";
      if(!roomWelcome) errors.roomWelcome = "Room Welcome is Required!"
     

      setError({ ...errors });
    } else {
      const urlRegex = /^(ftp|http|https):\/\/[^\s"]+$/;
      if (imageType === "0" && !urlRegex.test(imagePath)) {
        const errors = { image: "Invalid URL!" };
        return setError({ ...errors });
      }
      if (videoType === "0" && !urlRegex.test(videoPath)) {
        const errors = { video: "Invalid URL!" };
        return setError({ ...errors });
      }

      const countries = country?.value;

  
      const formData = new FormData();
      formData.append("imageType", imageType);
      formData.append("linkType", videoType);

      formData.append("name", name);
      formData.append("username", userName);
      formData.append("gender", gender);
      formData.append("country", countries);
      formData.append("bio", bio);
      formData.append("age", age);
      formData.append("roomName" , roomName)
      formData.append("roomWelcome" , roomWelcome);
      formData.append("roomImage" , roomImage);

      if (typeof videoPath === "string" && videoType === "0") {
        formData.append("link", videoPath);
      } else {
        formData.append("link", video);
      }

      if (typeof imagePath === "string" && imageType === "0") {
        formData.append("image", imagePath);
      } else {
        formData.append("image", image);
      }

      if (mongoId) {
        props.editUser(mongoId, formData,2);
      } else {
        props.insertUser(formData,2);
      }

      setTimeout(() => {
        navigate("/admin/fakeUser");
      }, 3000);
    }
  };

  return (
    <>
      <div className="page-title">
        <div className="row">
          <div className="col-12 col-md-6 order-md-1 order-last">
            <h3 className="mb-3 text-muted">Fake Audio User Dialog</h3>
          </div>
          <div className="col-12 col-md-6 order-md-2 order-first">
            <nav
              aria-label="breadcrumb"
              className="breadcrumb-header float-start float-lg-end"
            >
              <ol className="breadcrumb">
                <li className="breadcrumb-item">
                  <Link to="/admin/dashboard" className="text-danger">
                    Dashboard
                  </Link>
                </li>
                <li className="breadcrumb-item">
                  <Link to="/admin/fake" className="text-danger">
                    Fake User
                  </Link>
                </li>
                <li className="breadcrumb-item active" aria-current="page">
                  Dialog
                </li>
              </ol>
            </nav>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col">
          <div className="card">
            <div className="card-body card-overflow">
              <div className="d-sm-flex align-items-center justify-content-between mb-4"></div>

              <form>
                <div className="row">
                <div className="col-md-6">
                    <div className="form-group">
                      <label className="mb-2 text-gray">Room Name</label>
                      <input
                        type="text"
                        className="form-control"
                        required=""
                        placeholder="Room Name"
                        value={roomName}
                        onChange={(e) => {
                          setRoomName(e.target.value);
                          if (!e.target.value) {
                            return setError({
                              ...errors,
                              roomName: "Room Name is Required!",
                            });
                          } else {
                            return setError({
                              ...errors,
                              roomName: "",
                            });
                          }
                        }}
                      />
                      {errors.roomName && (
                        <div className="ml-2 mt-1">
                          {errors.roomName && (
                            <div className="pl-1 text__left">
                              <span className="text-red">{errors.roomName}</span>
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>

                  <div className="col-md-6">
                    <div className="form-group">
                      <label className="mb-2 text-gray">Room Welcome</label>
                      <input
                        type="text"
                        className="form-control"
                        required=""
                        placeholder="Room Welcome"
                        value={roomWelcome}
                        onChange={(e) => {
                          setRoomWelcome(e.target.value);
                          if (!e.target.value) {
                            return setError({
                              ...errors,
                              roomWelcome: "Room Welcome is Required!",
                            });
                          } else {
                            return setError({
                              ...errors,
                              roomWelcome: "",
                            });
                          }
                        }}
                      />
                      {errors.roomWelcome && (
                        <div className="ml-2 mt-1">
                          {errors.roomWelcome && (
                            <div className="pl-1 text__left">
                              <span className="text-red">{errors.roomWelcome}</span>
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>

                  <div className="col-md-6">
                  <div className="form-group mt-4">
                  <label className="mb-2 text-gray">Room Image</label>
                  <input  
                    type="file"
                    className="form-control form-control-sm"
                    accept="image/jpg ,image/jpeg ,image/png"
                    required=""
                    onChange={HandleRoomImage}
                  />
                  {errors.roomImage && (
                    <div className="ml-2 mt-1">
                      {errors.roomImage && (
                        <div className="pl-1 text__left">
                          <span className="text-red">{errors.roomImage}</span>
                        </div>
                      )}
                    </div>
                  )}
                  {roomImagePath && (
                    <>
                      <img
                        height="70px"
                        width="70px"
                        alt="app"
                        src={roomImagePath}
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
                  </div>

                  <div className="col-md-6"></div>
                  <div className="col-md-6">
                    <div className="form-group">
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
                              name: "name is Required!",
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
                  <div className="col-md-6">
                    <div className="form-group">
                      <label className="text-gray mb-2">Country</label>

                      <>
                       
                        <Select
                          value={country}
                          options={options}
                          className=""
                          styles={colourStyles}
                          onChange={(selectedOption) => {
                            setCountry(selectedOption);
                            // Handle error
                          }}
                        />

                        {errors.country && (
                          <div className="ml-2 mt-1">
                            {errors.country && (
                              <div className="pl-1 text__left">
                                <span className="text-red">
                                  {errors.country}
                                </span>
                              </div>
                            )}
                          </div>
                        )}
                      </>
                    </div>
                  </div>
                </div>

                <div className="row mt-3">
                  <div className="col-md-6">
                    <div className="form-group">
                      <label className="mb-2 text-gray">userName</label>
                      <input
                        type="text"
                        className="form-control"
                        required=""
                        placeholder="userName"
                        value={userName}
                        onChange={(e) => {
                          setuserName(e.target.value);
                          if (!e.target.value) {
                            return setError({
                              ...errors,
                              userName: "userName is Required!",
                            });
                          } else {
                            return setError({
                              ...errors,
                              userName: "",
                            });
                          }
                        }}
                      />
                      {errors.userName && (
                        <div className="ml-2 mt-1">
                          {errors.name && (
                            <div className="pl-1 text__left">
                              <span className="text-red">
                                {errors.userName}
                              </span>
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                  <div className="col-md-6">
                    <div className="form-group">
                      <label className="mb-2 text-gray">Age</label>
                      <input
                        type="number"
                        className="form-control"
                        required=""
                        placeholder="18"
                        value={age}
                        onChange={(e) => {
                          setAge(e.target.value);
                          if (!e.target.value) {
                            return setError({
                              ...errors,
                              age: "Age is Required!",
                            });
                          } else {
                            return setError({
                              ...errors,
                              age: "",
                            });
                          }
                        }}
                      />
                      {errors.age && (
                        <div className="ml-2 mt-1">
                          {errors.age && (
                            <div className="pl-1 text__left">
                              <span className="text-red">{errors.age}</span>
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                </div>

                <div className="row mt-4">
                  <div className="col-md-6 d-flex justify-content-start">
                    <label className="mb-2 text-gray">Image Type : </label>
                    <div className="form-check">
                      <input
                        className="form-check-input mx-2"
                        type="radio"
                        name="imageType"
                        id="image"
                        value="1"
                        onClick={(e) => {
                          setImageType(e.target.value);
                        }}
                        checked={imageType === "1" ? true : false}
                      />
                      <label className="form-check-label" htmlFor="image">
                        Image
                      </label>
                    </div>
                    <div className="form-check">
                      <input
                        className="form-check-input mx-2"
                        type="radio"
                        name="imageType"
                        id="link"
                        value="0"
                        checked={imageType === "0" ? true : false}
                        onClick={(e) => {
                          setImageType(e.target.value);
                        }}
                      />
                      <label className="form-check-label" htmlFor="link">
                        Link
                      </label>
                    </div>
                  </div>
                  <div className="col-md-6 justify-content-start">
                    <div className="d-flex">
                      <label className="mb-2 text-gray">Gender : </label>
                      <div className="d-flex">
                        <div className="form-check">
                          <input
                            className="form-check-input mx-2"
                            type="radio"
                            name="gender"
                            id="male"
                            value="male"
                            checked={gender === "male" ? true : false}
                            onChange={(e) => {
                              setGender(e.target.value);
                              if (!e.target.value) {
                                return setError({
                                  ...errors,
                                  gender: "Gender is Required!",
                                });
                              } else {
                                return setError({
                                  ...errors,
                                  gender: "",
                                });
                              }
                            }}
                          />
                          <label className="form-check-label" htmlFor="male">
                            Male
                          </label>
                        </div>
                        <div className="form-check">
                          <input
                            className="form-check-input mx-2"
                            type="radio"
                            name="gender"
                            id="female"
                            value="female"
                            checked={gender === "female" ? true : false}
                            onChange={(e) => {
                              setGender(e.target.value);
                              if (!e.target.value) {
                                return setError({
                                  ...errors,
                                  gender: "Gender is Required!",
                                });
                              } else {
                                return setError({
                                  ...errors,
                                  gender: "",
                                });
                              }
                            }}
                          />
                          <label className="form-check-label" htmlFor="female">
                            Female
                          </label>
                        </div>
                      </div>
                    </div>

                    {errors.gender && (
                      <div className="ml-2 mt-1">
                        {errors.gender && (
                          <div className="pl-1 text__left">
                            <span className="text-red">{errors.gender}</span>
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                </div>

                <div className="row mt-4">
                  <div className={imageType === "1" ? "col-md-6" : "d-none"}>
                    <div className="form-group ">
                      <label className="mb-2 text-gray">Image</label>
                      <input
                        type="file"
                        className="form-control form-control-sm"
                        accept="image/jpg ,image/jpeg ,image/png"
                        required=""
                        onChange={HandleInputImage}
                      />
                      {errors.image && imageType === "1" && (
                        <div className="ml-2 mt-1">
                          {errors.image && (
                            <div className="pl-1 text__left">
                              <p className="text-red">{errors.image}</p>
                            </div>
                          )}
                        </div>
                      )}
                      {showImage && (
                        <>
                          <img
                            height="60px"
                            width="60px"
                            alt="app"
                            src={showImage}
                            style={{
                              boxShadow: "0 5px 15px 0 rgb(105 103 103 / 00%)",
                              border: "2px solid #fff",
                              borderRadius: 10,
                              marginTop: 10,
                              float: "left",
                              objectFit: "contain",
                              marginRight: 15,
                            }}
                          />
                        </>
                      )}
                    </div>
                  </div>
                  <div className={imageType === "0" ? "col-md-6" : "d-none"}>
                    <div className="form-group">
                      <label className="mb-2 text-gray">Link</label>
                      <input
                        type="text"
                        className="form-control"
                        required=""
                        placeholder="Image link"
                        value={imagePath}
                        onChange={(e) => {
                          setImagePath(e.target.value);
                          setShowImage(e.target.value);
                          if (!e.target.value) {
                            return setError({
                              ...errors,
                              image: "Image is Required!",
                            });
                          } else {
                            return setError({
                              ...errors,
                              image: "",
                            });
                          }
                        }}
                      />
                      {showImage && (
                        <>
                          <img
                            height="60px"
                            width="60px"
                            alt="app"
                            src={showImage}
                            style={{
                              boxShadow: "0 5px 15px 0 rgb(105 103 103 / 00%)",
                              border: "2px solid #fff",
                              borderRadius: 10,
                              marginTop: 10,
                              float: "left",
                              objectFit: "contain",
                              marginRight: 15,
                            }}
                          />
                        </>
                      )}
                      {errors.image && imageType === "0" && (
                        <div className="ml-2 mt-1">
                          {errors.image && (
                            <div className="pl-1 text__left">
                              <span className="text-red">{errors.image}</span>
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                  <div className="col-md-6">
                    <div className="form-group ">
                      <label className="mb-2 text-gray">Bio</label>
                      <input
                        type="text"
                        className="form-control"
                        required=""
                        placeholder="Bio"
                        value={bio}
                        onChange={(e) => {
                          setBio(e.target.value);
                          if (!e.target.value) {
                            return setError({
                              ...errors,
                              bio: "Bio is Required!",
                            });
                          } else {
                            return setError({
                              ...errors,
                              bio: "",
                            });
                          }
                        }}
                      />
                      {errors.bio && (
                        <div className="ml-2 mt-1">
                          {errors.bio && (
                            <div className="pl-1 text__left">
                              <span className="text-red">{errors.bio}</span>
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                </div>

                <div className="row mt-4">
                  <div className="col-md-6 d-flex justify-content-start">
                    <label className="mb-2 text-gray">Video Type : </label>
                    <div className="form-check">
                      <input
                        className="form-check-input mx-2"
                        type="radio"
                        name="videoType"
                        id="video"
                        value="1"
                        onClick={(e) => {
                          setVideoType(e.target.value);
                        }}
                        checked={videoType === "1" ? true : false}
                      />
                      <label className="form-check-label" htmlFor="video">
                        Video
                      </label>
                    </div>
                    <div className="form-check">
                      <input
                        className="form-check-input mx-2"
                        type="radio"
                        name="videoType"
                        id="linkVideo"
                        value="0"
                        checked={videoType === "0" ? true : false}
                        onClick={(e) => {
                          setVideoType(e.target.value);
                        }}
                      />
                      <label className="form-check-label" htmlFor="linkVideo">
                        Link
                      </label>
                    </div>
                  </div>
                </div>
                <div className="row mt-4">
                  <div className={videoType === "1" ? "col-md-6" : "d-none"}>
                    <div className="form-group ">
                      <label className="mb-2 text-gray">video</label>
                      <input
                        type="file"
                        className="form-control form-control-sm"
                        accept="video/*"
                        required=""
                        controls
                        // value={}
                        onChange={HandleInputVideo}
                      />
                      {errors.video && videoType === "1" && (
                        <div className="ml-2 mt-1">
                          {errors.video && (
                            <div className="pl-1 text__left">
                              <p className="text-red">{errors.video}</p>
                            </div>
                          )}
                        </div>
                      )}
                      {showVideo && (
                        <>
                          <video
                            height="60px"
                            width="60px"
                            alt="app"
                            src={showVideo}
                            style={{
                              boxShadow: "0 5px 15px 0 rgb(105 103 103 / 00%)",
                              border: "2px solid #fff",
                              borderRadius: 10,
                              marginTop: 10,
                              float: "left",
                              objectFit: "contain",
                              marginRight: 15,
                            }}
                          />
                        </>
                      )}
                    </div>
                  </div>
                  <div className={videoType === "0" ? "col-md-6" : "d-none"}>
                    <div className="form-group">
                      <label className="mb-2 text-gray">Link</label>
                      <input
                        type="text"
                        className="form-control"
                        required=""
                        placeholder="Video link"
                        value={videoPath}
                        onChange={(e) => {
                          setVideoPath(e.target.value);
                          setShowVideo(e.target.value);
                          if (!e.target.value) {
                            return setError({
                              ...errors,
                              video: "Video is Required!",
                            });
                          } else {
                            return setError({
                              ...errors,
                              video: "",
                            });
                          }
                        }}
                      />
                     
                      {showVideo && (
                        <>
                          <video
                            height="60px"
                            width="60px"
                            alt="app"
                            src={showVideo}
                            style={{
                              boxShadow: "0 5px 15px 0 rgb(105 103 103 / 00%)",
                              border: "2px solid #fff",
                              borderRadius: 10,
                              marginTop: 10,
                              float: "left",
                              objectFit: "contain",
                              marginRight: 15,
                            }}
                          />
                        </>
                      )}
                    </div>
                  </div>
                </div>

                <div className="row">
                  <div className={imagePath ? "mt-5 pt-5" : "mt-5"}>
                    <button
                      type="button"
                      className="btn btn-outline-info ml-2 btn-round float__right icon_margin"
                      onClick={() => {
                        navigate("/admin/fakeUser");
                      }}
                    >
                      Close
                    </button>
                    {!mongoId ? (
                      <button
                        type="button"
                        className="btn btn-round float__right btn-danger"
                        onClick={handleSubmit}
                      >
                        Submit
                      </button>
                    ) : (
                      <button
                        type="button"
                        className="btn btn-round float__right btn-danger"
                        onClick={handleSubmit}
                      >
                        Update
                      </button>
                    )}
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default connect(null, { getCountry, insertUser, editUser })(
  FakeAudioUserPage
);
