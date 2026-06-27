const express = require("express");
const router = express.Router();

const multer = require("multer");
const { storage } = require("../../util/multer");
const upload = multer({ storage });

const checkAccessWithKey = require("../../checkAccess");

const ReactionController = require("./reaction.controller");

//store reaction
router.post("/add", checkAccessWithKey(), upload.single("image"), ReactionController.store);

//update reaction
router.patch("/update", checkAccessWithKey(), upload.single("image"), ReactionController.update);

//get reaction
router.get("/getReaction", checkAccessWithKey(), ReactionController.get);

//delete reaction
router.delete("/delete", checkAccessWithKey(), ReactionController.destroy);

module.exports = router;
