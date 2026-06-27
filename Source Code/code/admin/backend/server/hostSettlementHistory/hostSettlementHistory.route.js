const express = require("express");
var checkAccess = require("../../checkAccess");
const hostSettlementController = require("../hostSettlementHistory/hostSettlementHistory.controller");

const route = express.Router();

// update paid host settlement
route.put("/updatePaidSettlement/:id", checkAccess(), hostSettlementController.updatePaidSettlement);

// get agency wise host settlement history
route.get("/agencyWiseHostSettlement", checkAccess(), hostSettlementController.agencyWiseHostSettlement);

// action for host settlement
route.patch("/actionForHostSettlement/:id", checkAccess(), hostSettlementController.actionForHostSettlement);

// pending or solved settlement
route.get("/pendingOrSolvedSettlement", checkAccess(), hostSettlementController.pendingOrSolvedSettlement);

route.get("/getPendingOrSolvedAll", checkAccess(), hostSettlementController.getPendingOrSolvedAll);

route.get("/hostSettlementForHost", checkAccess(), hostSettlementController.hostSettlementForHost);

module.exports = route;
