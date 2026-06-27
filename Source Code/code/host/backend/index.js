const express = require("express");
const app = express();
const path = require("path");
const cors = require("cors");

app.use(cors());
app.use(express.json());

app.use(express.static(path.join(__dirname, "public")));

//public index.html file
app.get("/*", function (req, res) {
  res.status(200).sendFile(path.join(__dirname, "public", "index.html"));
});

const PORT = 5005;

//start the server
app.listen(PORT, () => {
  console.log("Magic happens on port: " + PORT);
});
