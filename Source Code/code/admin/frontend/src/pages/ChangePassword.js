//react
import React, { useEffect, useState } from "react";

// toast
import { Toast } from "../util/Toast";

// axios
import axios from "axios";

const ChangePassword = (props) => {

  const [id, setId] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [error, setError] = useState({
    password: "",
    confirmPassword: "",
  });

  useEffect(() => {
    setId(props.match.params.id);
  }, [props.match.params.id]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!password || !confirmPassword || password !== confirmPassword) {
      const error = {};

      if (!password) {
        error.password = "Please enter password!";
      }
      if (!confirmPassword) {
        error.confirmPassword = "Please enter confirm password!";
      }
      if (password !== confirmPassword) {
        error.confirmPassword = "Password & Confirm Password does not match";
      }

      return setError({ ...error });
    }
    axios
      .post(`admin/setPassword/${id}`, {
        newPass: password,
        confirmPass: confirmPassword,
      })
      .then((res) => {
        if (res.data.status) {
          Toast("success", "password changed successfully!");
          setTimeout(() => {
            props.navigate("/");
          }, 3000);
        } else {
          Toast("error", res.data.message);
        }
      })
      .catch((error) => {
        Toast("error", error.message);
      });
  };
  return (
    <>
      <div className="login-page back__style">
        <div className="container">
          <div className="row justify-content-md-center">
            <div className="col-md-12 col-lg-4">
              <div className="card login-box-container">
                <div className="card-body">
                  <div className="authent-text">
                    <p className="text-danger mb-4 h5">Change Password</p>
                    <p>If you have forgotten your password you can reset it here!</p>
                  </div>
                  <form autoComplete="off">
                    <div className="mb-3">
                      <div className="form-floating">
                        <input
                          type="password"
                          className="form-control"
                          id="floatingInput"
                          placeholder="password"
                          required
                          value={password}
                          onChange={(e) => {
                            setPassword(e.target.value);
                            if (!e.target.value) {
                              return setError({
                                ...error,
                                password: "Please enter password!",
                              });
                            } else {
                              return setError({
                                ...error,
                                password: "",
                              });
                            }
                          }}
                        />
                        <label htmlFor="floatingPassword">Password</label>
                      </div>
                      <div className="mt-2 ml-2 mb-3">
                        {error.password && (
                          <div className="pl-1 text-left pb-1">
                            <span className="text-red font-size-lg">
                              {error.password}
                            </span>
                          </div>
                        )}
                      </div>
                    </div>
                    <div className="mb-3">
                      <div className="form-floating">
                        <input
                          type="password"
                          className="form-control"
                          id="floatingInput"
                          placeholder="password"
                          required
                          value={confirmPassword}
                          onChange={(e) => {
                            setConfirmPassword(e.target.value);
                            if (!e.target.value) {
                              return setError({
                                ...error,
                                confirmPassword: "Please enter confirm password!",
                              });
                            } else {
                              return setError({
                                ...error,
                                confirmPassword: "",
                              });
                            }
                          }}
                        />
                        <label htmlFor="floatingPassword">Confirm Password</label>
                      </div>
                      <div className="mt-2 ml-2 mb-3">
                        {error.confirmPassword && (
                          <div className="pl-1 text-left pb-1">
                            <span className="text-red font-size-lg">
                              {error.confirmPassword}
                            </span>
                          </div>
                        )}
                      </div>
                    </div>
                    <div className="d-grid">
                      <button type="submit" className="btn btn-danger m-b-xs" onClick={handleSubmit}>
                        Submit
                      </button>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default ChangePassword;
