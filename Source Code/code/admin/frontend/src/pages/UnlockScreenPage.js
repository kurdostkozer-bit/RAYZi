//react
import React from "react";

//Image
import ProfilePic from "../assets/images/profile-image.png";

const UnlockScreenPage = () => {
  return (
    <>
      <div className="login-page">
        <div className="container">
          <div className="row justify-content-md-center">
            <div className="col-md-12 col-lg-4">
              <div className="card login-box-container">
                <div className="card-body">
                  <div className="authent-logo">
                    <img src={ProfilePic} width="60" alt="" />
                  </div>
                  <div className="authent-text">
                    <p>Welcome back!</p>
                    <p>Enter your password to unlock.</p>
                  </div>
                  <form>
                    <div className="mb-3">
                      <div className="form-floating">
                        <input
                          type="password"
                          className="form-control"
                          id="floatingPassword"
                          placeholder="Password"
                        />
                        <label htmlFor="floatingPassword">Password</label>
                      </div>
                    </div>
                    <div className="d-grid">
                      <button type="submit" className="btn btn-secondary m-b-xs">
                        Unlock
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

export default UnlockScreenPage;
