import React from "react";
import { useSelector } from "react-redux";
import { Navigate, Outlet } from "react-router-dom";

const AuthRouter = () => {
  // Fetch auth state from Redux store
  const isAuth = useSelector((state) => state.admin?.isAuth);



  return isAuth ? <Outlet /> : <Navigate to="/login" />;
};

export default AuthRouter;
