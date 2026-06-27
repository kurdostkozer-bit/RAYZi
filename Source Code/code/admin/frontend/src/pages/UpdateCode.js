import React, { useState } from "react";
import { connect } from "react-redux";

import { updateCode } from "../store/admin/action";

const UpdateCode = (props) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [code, setCode] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = () => {
    if (!email || !password || !code) {
      let error = {};
      if (!email) error.email = "Email Is Required !";
      if (!password) error.password = "password is required !";
      if (!code) error.code = "purchase code is required !";

      return setError({ ...error });
    } else {
      let login = {
        email,
        password,
        code,
      };

      props.updateCode(login);
    }
  };
  return (
    <>
      <div className="login-page back__style">
        <div className="container">
          <div className="row justify-content-md-center">
            <div className="col-md-12 col-lg-4">
              <div className="card login-box-container">
                <div className="card-body">
                  <div className="authent-logo mb-4">
                    <span className="text-danger h1">ALL-IN_ONE </span>
                  </div>
                  <div className="authent-text">
                  
                    <p>
                      Enter your email address and password to access admin
                      panel.
                    </p>
                  </div>

                  <form autoComplete="off">
                    <div className="mb-3">
                      <div className="form-floating">
                        <input
                          type="email"
                          className="form-control"
                          id="floatingInput"
                          placeholder="name@example.com"
                          required
                          value={email}
                          onChange={(e) => {
                            setEmail(e.target.value);
                            if (!e.target.value) {
                              return setError({
                                ...error,
                                email: "Email is Required !",
                              });
                            } else {
                              return setError({
                                ...error,
                                email: "",
                              });
                            }
                          }}
                        />
                        <label htmlFor="floatingInput">Email address</label>
                      </div>
                      <div className="mt-2 ml-2 mb-3">
                        {error.email && (
                          <div className="pl-1 text-left pb-1">
                            <span className="text-red font-size-lg">
                              {error.email}
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
                          id="floatingPassword"
                          placeholder="Password"
                          value={password}
                          onChange={(e) => {
                            setPassword(e.target.value);
                            if (!e.target.value) {
                              return setError({
                                ...error,
                                password: "Password is Required !",
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
                            <span className="text-red">{error.password}</span>
                          </div>
                        )}
                      </div>
                    </div>

                    <div className="mb-3">
                      <div className="form-floating">
                        <input
                          type="text"
                          className="form-control"
                          id="code"
                          name="code"
                          placeholder="Purchase code"
                          value={code}
                          onChange={(e) => {
                            setCode(e.target.value);
                            if (!e.target.value) {
                              return setError({
                                ...error,
                                code: "purchase Code  is Required !",
                              });
                            } else {
                              return setError({
                                ...error,
                                code: "",
                              });
                            }
                          }}
                        />
                        <label htmlFor="floatingPassword"> Purchase Code</label>
                      </div>
                      <div className="mt-2 ml-2 mb-3">
                        {error.code && (
                          <div className="pl-1 text-left pb-1">
                            <span className="text-red">{error.code}</span>
                          </div>
                        )}
                      </div>
                    </div>

                    <div className="d-grid">
                      <button
                        type="button"
                        className="btn btn-danger m-b-xs"
                        onClick={handleSubmit}
                      >
                        Sign In
                      </button>
                    </div>
                  </form>
                  <div className="authent-reg">
                    <p>
                      {/* <Link to="/forgot" className="text-info">
                        Forgot password?
                      </Link> */}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default connect(null, { updateCode })(UpdateCode);
