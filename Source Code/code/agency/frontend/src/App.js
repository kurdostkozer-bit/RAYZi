import React, { Suspense, useEffect } from "react";
// routing
import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
  useLocation,
  useNavigate
} from "react-router-dom";
// redux
import { useDispatch, useSelector } from "react-redux";
// types
import { SET_ADMIN } from "./store/admin/types";

import Admin from "./pages/Admin";
import TotalIncome from "./pages/TotalIncome";
import Creators from "./pages/Creators";
import HostHistory from "./pages/HostHistory";
import { AgencyRedeem } from "../src/pages/AgencyRedeem";
import HostRequest from "./pages/hostRequest/HostRequest";

function App() {
  const dispatch = useDispatch();
  const { isAuth } = useSelector((state) => state.admin);
  const token = localStorage.getItem("TOKEN");
  const key = localStorage.getItem("KEY");
  const navigate = useNavigate();
  const location = useLocation();


  // When Open In Local :-
  
  // const params = new URLSearchParams(window.location.search);
  // const agencyId = params.get("id") || localStorage.getItem("agencyId");
  // localStorage.setItem("agencyId", agencyId);

  useEffect(() => {
    if (!token && !key) return;
    dispatch({ type: SET_ADMIN, payload: token });
  }, [token, key, dispatch]);

  // When Open in Server :-

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const id = queryParams.get("id");
    if (id) {
      console.log(id)
      localStorage.setItem("agencyId", id);
      navigate("/agencypanel/homePage", { replace: true });
    } else if (localStorage.getItem("agencyId")) {
      navigate("/agencypanel/homePage", { replace: true });
    }
}, []);

  return (
    <div className="App">
      <Suspense fallback={""}>
        <Routes>
          <Route path="/agencypanel/homePage" element={<Admin />} />
          <Route path="/agencypanel/Income" element={<TotalIncome />} />
          <Route path="/agencypanel/creators" element={<Creators />} />
          <Route path="/agencypanel/creatorRequest" element={<HostRequest />} />
          <Route path="/agencypanel/hosthistory" element={<HostHistory />} />
          <Route
              path={"/agencypanel/agencyredeem"}
              element={<AgencyRedeem />} 
            />
          <Route
            path="/"
            element={<Navigate to="/agencypanel/homePage" replace />}
          />
        </Routes>
      </Suspense>
    </div>
  );
}

export default App;
