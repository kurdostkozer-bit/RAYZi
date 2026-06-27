import React, { Suspense, useEffect } from "react";

// routing
import { Switch, BrowserRouter, Route, Redirect, Routes, Navigate } from "react-router-dom";
//redux
import { useDispatch, useSelector } from "react-redux";
//types
import { SET_ADMIN } from "./store/admin/types";

import Admin from "./pages/Admin";
import HostHistory from "./pages/HostHistory";
import HostHistoryInfo from "./pages/HostHistoryInfo";

function App() {
  const dispatch = useDispatch();
  const { isAuth } = useSelector((state) => state.admin);
  const token = localStorage.getItem("TOKEN");
  const key = localStorage.getItem("KEY");

  const params = new URLSearchParams(window.location.search);

  const hostId = params.get("id") || localStorage.getItem("hostId");


  localStorage.setItem("hostId", hostId);

  useEffect(() => {
    if (!token && !key) return;
    dispatch({ type: SET_ADMIN, payload: token });
  }, [token, key, dispatch]);

  return (
    <div className="App">
      <Suspense fallback={""}>
        <BrowserRouter>
          <Routes>
            <Route path="/host/homePage" element={<Admin />} />
            <Route path="/host/hosthistory" element={<HostHistory />} />
            <Route path="/host/historyInfo" element={<HostHistoryInfo />} />
            <Route path="*" element={<Navigate to="/host/homePage" />} />
          </Routes>

        </BrowserRouter>
      </Suspense>
    </div>
  );
}

export default App;
