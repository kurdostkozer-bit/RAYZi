var createError = require("http-errors");
var express = require("express");
var path = require("path");
const mongoose = require("mongoose");
const FerryWheelLastHistory = require("./model/ferryWheelLastHistory.model");
const GameAdminCoin = require("./model/gameAdminCoin.model");
const config = require("./config");
var app = express();
const port = config.PORT || 5001;
const http = require("http");
const cors = require("cors");
const { Server } = require("socket.io");
const User = require("./model/user.model");

const {
  selectFrame,
  RandomResult,
  allUserHistory,
  createHistoryWinnerNumber,
  addBet,
  addUserBits,
  updateUser,
  createAdminCoin,
  historyRecord,
  todayProfit,
  addGameHistory,
} = require("./service");
const { getDefaultSettings } = require("http2");

app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, "public")));

// catch 404 and forward to error handler
app.use(function (req, res, next) {
  next(createError(404));
});

app.get("/*", function (req, res) {
  res.status(200).sendFile(path.join(__dirname, "public", "index.html"));
});

const server = http.createServer(app);

//Setup for server and socket.io

global.io = new Server(server, {
  cors: {
    methods: ["GET", "POST"],
  },
});

const url = config.mongoDbConnectionString;
mongoose.connect(url).then(() => {
  console.log(`Mongodb connected Successfully`);
});

const db = mongoose.connection;

db.on("unhandleRejection", (err) => {
  console.log(err.name, err.message);
  console.log("UNHANDLE REJECTION  🔥 Shuting Down ...");
  db.exit(1);
});
db.once("open", () => {
  console.log(`Mongo Connect successfully on localhost port:${port}`);
});

const refreshSettings = async () => {
  try {
  
    const settings = await db.collection("settings").findOne({});
    if (settings) {
      global.setting = settings;
    } else {
      console.log("No documents found in the settings collection.");
    }
  } catch (error) {
    console.error("Error fetching the settings:", error);
  }
};

// Connect to MongoDB
mongoose.connect(url, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(async (client) => {
    console.log("MONGO1: Connected successfully to the database");

    const db = mongoose.connection.db;

    // Load initial settings
    await refreshSettings(db);

    // Poll for changes every 10 seconds
    setInterval(async () => {
      console.log("Checking for settings updates...");
      await refreshSettings(db);
    }, 10000); // Adjust polling interval as needed
  })
  .catch((error) => {
    console.error("Connection error:", error);
  });


// when first time run this code unComment this two lines and after again comment
createAdminCoin();
createHistoryWinnerNumber();

//Game Loop For Timer
setInterval(() => {
  updateTime();
}, 1000);

global.betG = [];
global.winnerUserArrayG = [];
global.highOrLowWinResult = [];
let time = 25;
let randomWinnerNumber, RandomResWithSelectedFrame;
global.currentGame = {
  UsersBits: [],
  frameBitCoin: [
    { selectFrame: 1, bit: 0, winner: false },
    { selectFrame: 2, bit: 0, winner: false },
    { selectFrame: 3, bit: 0, winner: false },
    { selectFrame: 4, bit: 0, winner: false },
    { selectFrame: 5, bit: 0, winner: false },
    { selectFrame: 6, bit: 0, winner: false },
    { selectFrame: 7, bit: 0, winner: false },
    { selectFrame: 8, bit: 0, winner: false },
  ],
};

io.on("connection", async (socket) => {
  const { globalRoom } = socket?.handshake?.query;

  console.log("globalRoom connect =====", globalRoom);
  let todayProfitCoin = 0;
  const user = await User.findById(globalRoom);
  todayProfitCoin = await todayProfit(globalRoom);
  //Start game
  socket.on("startGame", async (obj) => {
    if (user) {
      console.log(
        "------------------------------user in game start-------------------------------",
        user?.diamond
      );
      socket.emit("start", user);
      console.log("--user--", user.name);
      io.emit("game", currentGame);
      const ferryWheelLastHistory = await FerryWheelLastHistory.findOne();
      socket.emit("lastHistories", ferryWheelLastHistory.winnerNumber); // last 5 number history
      socket.emit("gameRound", ferryWheelLastHistory.totalRound); // last some histories
      socket.emit(
        "todayProfit",
        parseInt(todayProfitCoin) ? parseInt(todayProfitCoin) : 0
      );

      todayProfit;
      if (time < 2) {
        io.emit("randomWinnerNumber", randomWinnerNumber); // 3 or 6
      }
    } else {
      socket.emit("start", null);
    }
  });
  socket.on("bit", async (obj) => {
    const userDb = await updateUser(obj);
    console.log("BIT ==============  obj.SelectedFrame", obj.SelectedFrame);
    if (userDb?.diamond < 0) {
      userDb.diamond = 0;
      await userDb.save(); // @toDo
      io.emit("start", userDb);
    }
    var objToModify = currentGame.frameBitCoin.find(
      (val) => val.selectFrame === obj.SelectedFrame
    );

    if (objToModify) {
      objToModify.bit += obj.Bit;
    }
    addUserBits(obj.Bit, obj.SelectedFrame, obj.User._id);
    addBet(obj.Bit, obj.User._id);
    await GameAdminCoin.updateOne(
      {},
      { $inc: { coin: obj.Bit } },
      { new: true }
    );
    // io.emit('bit', bitData);
  });
  socket.on("user", async () => {
    console.log("user listen ============== ", time, winnerUserArrayG);
    const user = await User.findById(globalRoom);
    socket.emit("user", user);
    todayProfitCoin = await todayProfit(globalRoom);
    socket.emit(
      "todayProfit",
      parseInt(todayProfitCoin) ? parseInt(todayProfitCoin) : 0
    );
  });
  socket.on("historyRecord", async (data) => {
    console.log("historyRecord 111===========", data._id);
    const record = await historyRecord(data._id);
    socket.emit("historyRecord", record);
  });
  socket.on("disconnect", () => {
    console.log(
      "disconnect one socket >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
    );
  });
});

const updateTime = async () => {
  io.emit("time", time);
  time--;

  if (time == -7) {
    RandomResWithSelectedFrame = await RandomResult();
    console.log(
      " RandomResWithSelectedFrame  : ======== ",
      RandomResWithSelectedFrame
    );

    randomWinnerNumber = RandomResWithSelectedFrame?.number;
    console.log(" randomWinnerNumber  : ======== ", RandomResWithSelectedFrame);
  }

  if (time == -9) {
    // make a random winner
    console.log(" time  : ======== ", time);
    let sum = 0;
    for (const item of currentGame.frameBitCoin) {
      sum += item.bit;
    }
    await addGameHistory(
      RandomResWithSelectedFrame.number,
      sum,
      RandomResWithSelectedFrame.winCoinPercent,
      RandomResWithSelectedFrame.winnerNumberTimes
    );
    io.emit("randomWinnerNumber", randomWinnerNumber); // 3 or 6
  }
  // -12 result declare in frontend Side
  //-14 new game start in frontend Side
  if (time == -11) {
    allUserHistory(currentGame, randomWinnerNumber);
  }
  if (time == 2) {
    io.emit("game", currentGame);
  }
  if (time == -13) {
    const sortedArray = winnerUserArrayG.sort(
      (a, b) => b.userWinCoin - a.userWinCoin
    );
    const resultTopUserArray = sortedArray.map((element, index) => ({
      ...element,
      number: index + 1,
    }));
    io.emit("winnerUserArray", {
      winnerUserArray: resultTopUserArray,
      winnerNumber: randomWinnerNumber,
    });
  }
  if (time == -14) {
    let ferryWheelLastHistory = await FerryWheelLastHistory.findOne();
    ferryWheelLastHistory.winnerNumber.unshift(randomWinnerNumber);
    if (ferryWheelLastHistory.winnerNumber.length > 15) {
      ferryWheelLastHistory.winnerNumber.pop();
    }
    ferryWheelLastHistory.totalRound += 1;
    await ferryWheelLastHistory.save();
    io.emit("lastHistories", ferryWheelLastHistory.winnerNumber); // last some histories
  }

  if (time == -15) {
    currentGame.UsersBits = [];
    bitData = [];
    winnerUserArrayG = [];
    currentGame.frameBitCoin = [
      { selectFrame: 1, bit: 0, winner: false },
      { selectFrame: 2, bit: 0, winner: false },
      { selectFrame: 3, bit: 0, winner: false },
      { selectFrame: 4, bit: 0, winner: false },
      { selectFrame: 5, bit: 0, winner: false },
      { selectFrame: 6, bit: 0, winner: false },
      { selectFrame: 7, bit: 0, winner: false },
      { selectFrame: 8, bit: 0, winner: false },
    ];
    io.emit("game", currentGame);
    time = 25;
    betG?.splice(0, betG?.length);
    let ferryWheelLastHistory = await FerryWheelLastHistory.findOne();
    io.emit("gameRound", ferryWheelLastHistory.totalRound); // last some histories
    await refreshSettings();
  }
};
server.listen(port, () => {
  console.log(`magic happen on ${port}`);
});
