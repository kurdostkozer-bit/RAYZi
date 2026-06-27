const express = require("express");
const checkAccessWithKey = require("../../checkAccess");

const commissionRateController = require("./commissionRate.controller");

const route = express.Router();

//add Commission
route.post("/store", checkAccessWithKey(), commissionRateController.store);

//update Commission
route.patch("/update", checkAccessWithKey(), commissionRateController.update);

//get Commission
route.get("/get",  commissionRateController.get);

//delete Commission
route.delete("/delete", checkAccessWithKey(), commissionRateController.delete);

module.exports = route;
