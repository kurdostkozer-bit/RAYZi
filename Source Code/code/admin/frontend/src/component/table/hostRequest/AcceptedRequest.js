import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getHostRequest } from "../../../store/hostRequest/action";
import dayjs from "dayjs";
import Pagination from "../../../pages/Pagination";
import { useNavigate } from "react-router-dom";

const AcceptedRequest = () => {
  const dispatch = useDispatch();
  const { request, total } = useSelector((state) => state.hostRequest);
    const navigate = useNavigate();

  const [coinSort, setCoinSort] = useState(true);
  const [data, setData] = useState([]);
  const [type, setType] = useState("Pending");
  const [activePage, setActivePage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
    const [search, setSearch] = useState("");

  useEffect(() => {
    dispatch(getHostRequest(activePage, rowsPerPage, 2));
  }, [activePage, rowsPerPage, 2]);

  useEffect(() => {
    setData(request);
  }, [request]);

  //   pagination

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  const handleRowsPerPage = (value) => {
    setActivePage(1);
    setRowsPerPage(value);
  };

  const handleUserInfo = (user) => {
    

    sessionStorage.setItem("user", JSON.stringify(user));
    navigate("/admin/user/detail", {
      state: {
        id: user?.user?._id,
      },
    })
  };

  const handleSearch = () => {
    const value = search.trim().toLowerCase();

    if (value) {
      const filteredData = data.filter((data) => {
        return (
          data?.user?.name?.toLowerCase().includes(value) ||
          data?.agencyCode?.toString().includes(value)
        );
      });
      setData(filteredData);
    } else {
      setSearch("")
      setData(request);
    }
  };

  return (
    <>
      <div className="row">
        <div className="col">
          <div className="card">
            <div className="card-header pb-0">
              <div className="row my-3">
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-8 float-left"></div>
                <div className="col-xs-12 col-sm-12 col-md-6 col-lg-4 float-right">
                <div className="input-group mb-3 border rounded-pill">
                      <input
                        type="search"
                        id="searchBar"
                        autoComplete="off"
                        placeholder="What're you searching for?"
                        aria-describedby="button-addon4"
                        className="form-control bg-none border-0 rounded-pill searchBar"
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        onKeyDown={(e) => {
                          if (e.key === "Enter") {
                            handleSearch(); // Trigger search on Enter key
                          }
                        }}
                      />
                      <div className="input-group-prepend border-0">
                        <div
                          id="button-addon4"
                          className="btn text-danger"
                          onClick={handleSearch}
                        >
                          <i className="fas fa-search mt-2"></i>
                        </div>
                      </div>
                    </div>
                </div>
              </div>
            </div>
            <div className="card-body card-overflow">
              <div className="d-sm-flex align-items-center justify-content-between mb-4"></div>

              <table className="table table-striped">
                <thead className="text-white">
                  <tr>
                    <th>No.</th>
                    <th>Image</th>
                    <th>User Name</th>
                    <th>Agency Code</th>
                    <th>CreatedAt</th>
                  </tr>
                </thead>
                <tbody className="t">
                  {data?.length > 0 ? (
                    data?.map((data, index) => {
                      return (
                        <tr key={index}>
                          <td>{index + 1}</td>
                          <td>
                            <img
                              height="50px"
                              width="50px"
                              alt="app"
                              onClick={() => handleUserInfo(data)}
                              src={
                                data?.user?.image
                                  ? data?.user?.image
                                  : data?.profileImage
                              }
                              style={{
                                boxShadow: "0 5px 15px 0 rgb(105 103 103 / 0%)",
                                border: "2px solid #fff",
                                borderRadius: 10,
                                objectFit: "cover",
                                display: "block",
                              }}
                              className="mx-auto"
                            />
                          </td>
                          <td>
                            {data?.user?.name ? data?.user?.name : data?.name}
                          </td>

                          <td>{data?.agencyCode ? data?.agencyCode : "-"}</td>

                          <td>
                            {dayjs(data?.createdAt).format("DD MMM, YYYY")}
                          </td>
                        </tr>
                      );
                    })
                  ) : (
                    <tr>
                      <td colSpan="10" align="center">
                        Nothing to show!!
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
              <Pagination
                activePage={activePage}
                rowsPerPage={rowsPerPage}
                userTotal={total}
                handleRowsPerPage={handleRowsPerPage}
                handlePageChange={handlePageChange}
              />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default AcceptedRequest;
