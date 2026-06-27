const express = require("express");
const router = express.Router();

const NotificationController = require("./notification.controller");

const checkAccessWithKey = require("../../checkAccess");

// handle user notification
router.post("/", checkAccessWithKey(), NotificationController.handleNotification);

module.exports = router;
