const express = require("express");
const router = express.Router();

const multer = require("multer");
const { storage } = require("../../util/multer");
const upload = multer({ storage });

const SettingController = require("./setting.controller");

const checkAccessWithKey = require("../../checkAccess");

//store setting data
//router.post("/", checkAccessWithKey(), SettingController.store);

router.get("/", checkAccessWithKey(), SettingController.getSetting);

router.patch("/:settingId", checkAccessWithKey(), SettingController.update);

router.put("/:settingId", checkAccessWithKey(), SettingController.handleSwitch);

router.patch("/addGame/:settingId", checkAccessWithKey(), upload.single("image"), SettingController.addGame);

router.patch("/updateGame/:settingId", checkAccessWithKey(), upload.single("image"), SettingController.updateGame);

router.delete("/deleteGame/:settingId", checkAccessWithKey(), SettingController.deleteGame);

module.exports = router;
