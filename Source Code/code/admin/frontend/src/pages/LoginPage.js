import React, { useEffect, useState } from 'react';

//routing
import { Link } from "react-router-dom";

//redux
import { connect } from "react-redux";

//action
import { login } from "../store/admin/action";
import { projectName } from "../util/Config";

const LoginPage = (props) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState({
    email: "",
    password: "",
  });



  const handleSubmit = (e) => {
    e.preventDefault();
    if (!email || !password) {
      const error = {};

      if (!email) error.email = "Email is Required!";
      if (!password) error.password = "Password is Required!";

      return setError({ ...error });
    }
    const data = {
      email,
      password,
    };
    sessionStorage.removeItem("isAuth1");
    sessionStorage.removeItem("isAuth2");
    props.login(data);
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
                    <span className="text-danger h1 text-capitalize">
                      {projectName}
                    </span>
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
                    <div className="d-grid">
                      <button
                        type="submit"
                        className="btn btn-danger m-b-xs"
                        onClick={handleSubmit}
                      >
                        Sign In
                      </button>
                    </div>
                  </form>
                  <div className="authent-reg">
                    <p>
                      <Link to="/forgot" className="text-info">
                        Forgot password?
                      </Link>
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

export default connect(null, { login })(LoginPage);
