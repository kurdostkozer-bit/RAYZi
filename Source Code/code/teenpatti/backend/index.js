const path = require("path");
const express = require("express");
const app = express();
const http = require("http");
const cors = require("cors");
const { Server } = require("socket.io");
const mongoose = require("mongoose");
const { on } = require("nodemon");
var ObjectID = require("mongodb").ObjectID;
const { cardUpdateSplice, win, allUserHistory } = require("./service.js");
const User = require("./model/userSchema.js");
const config = require('./config.js')
global.highBitValue = 0;
global.midBitValue = 0;
global.lowBitValue = 0;

app.use(cors());
const server = http.createServer(app);

//Setup for server and socket.io
const LISTER_PORT = config.PORT 
const io = new Server(server, {
  cors: {
    methods: ["GET", "POST"],
  },
});
var MongoClient = require("mongodb").MongoClient;

const url = config.mongoDbConnectionString



mongoose
  .connect(url, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  })
  .then(async () => {
    console.log("MongoDB connected successfully");

    await refreshSettings();

    const db = mongoose.connection.db;
    const isReplicaSet = await db.admin().command({ isMaster: 1 });

    if (isReplicaSet.setName) {
      const changeStream = db.collection("settings").watch(); // Replace 'settings' with your actual collection name
      changeStream.on("change", async (change) => {
        console.log("Settings updated in DB, refreshing global settings...");
        await refreshSettings();
      });
    } else {
      console.log("Change streams are not supported (MongoDB is not in a replica set).");
    }
  })
  .catch((err) => console.error("Database connection error:", err));


const refreshSettings = async () => {
  try {
    const settings = await db.collection("settings").findOne({}); // Replace 'settings' with your actual collection name

    if (settings) {
      global.setting = settings;
    } else {
      global.setting = {}
      console.log("No settings found in DB!");
    }
  } catch (error) {
    console.error("Error refreshing settings:", error);
  }
};

const db = mongoose.connection;
db.on("error", console.error.bind(console, "Connection error: "));
db.once("open", function () {
  console.log("MONGO1: Connected successfully");
});

const indexRoute = require("./routes/routeIndex.js");
app.use("/", indexRoute);


global.currentGame = {}


let activeUsers = [];
let time = 21;
 currentGame = {
  Combinations: [
    { combination: [40, 28, 7, "HighCard"], value: 59 },
    { combination: [15, 29, 4, "Sequence"], value: 36 },
    { combination: [20, 21, 19, "Pure"], value: 20 },
  ], // default combination
  UsersBits: [],
  cardBitCoin: [
    { selectFrame: 1, bit: 0, card: "", winner: false },
    { selectFrame: 2, bit: 0, card: "", winner: false },
    { selectFrame: 3, bit: 0, card: "", winner: false },
  ],
};

//Game Loop For Timer
setInterval(() => {
  updateTime();
}, 1000);

//initializing the socket io connection
io.on("connection", async (socket) => {
  var user;
  const userId = socket.handshake.query.userId;

  if (userId && userId != "null" && ObjectID.isValid(userId)) {
    try {
      MongoClient.connect(url, async (err, db) => {
        if (err) throw err;
        var dbo = db.db();

        dbo
          .collection("users")
          .findOne(
            { _id: mongoose.Types.ObjectId(userId) },
            function (err, result) {
              user = result;
              console.log(
                "user in connection socket ========================= "
              );
              db.close();
            }
          );
      });
    } catch (err) {
      console.log(err);
    }
  }

  //Start game
  socket.on("startGame", async (obj) => {
    if (user && user.gameBlock) {
      console.log(".......................Block..................", user?.name);
      socket.emit("block", user);
      user = {};
    } else {
      if (user) {
        console.log(
          "------------------------------user in game start-------------------------------",
          user?.name
        );

        socket.emit("start", user);
        // activeUsers.push({ User: user, SocketId: socket.id });
        getTop5Users();
        io.emit("game", currentGame);
      } else {
        socket.emit("start", null);
      }
    }
  });

  //Get User
  socket.on("user", async (obj) => {
    // emit -5 sec
    console.log("time", time);
    console.log(
      "----- FINAL UPDATE USER ---------obj in user socket------------------",
      obj.User?._id,
      obj.User?.diamond,
      "obj.winCoin : ",
      obj?.winCoin
    );
    if (obj.User?._id) {
      const finalUser = await finalUpdateUser(obj);
      if (finalUser?.diamond !== obj.User?.diamond) {
        socket.emit("start", finalUser);
      }
    }
  });

  //Bit
  socket.on("bit", async (obj) => {
    await Promise.resolve(updateUser(obj));



    const userDb = await User.findById(obj.User._id);
    if (userDb.diamond < 0) {
      userDb.diamond = 0;
      await userDb.save();
      io.emit("start", userDb);
    }
    var objToModify = currentGame.cardBitCoin.find(
      (val) => val.selectFrame === obj.SelectedFrame
    );

    if (objToModify) {
      objToModify.bit += obj.Bit;
    }

    // how much coin add in bit [user wise bit]
    currentGame.UsersBits.push({
      UserId: obj.User._id,
      Bit: obj.Bit,
      SelectedFrame: obj.SelectedFrame,
      Left: Math.random() * 55 + 5,
      Top: Math.random() * 45 + 0,
    });
    addBet(obj.Bit, obj.User._id);
    io.emit("game", currentGame);
    io.emit("bit");

    MongoClient.connect(url, async (err, db) => {
      if (err) throw err;
      var dbo = db.db();

      dbo
        .collection("gameadmincoins")
        .updateOne(
          {},
          { $inc: { coin: parseInt(obj.Bit) } },
          function (err, result) {
            if (err) {
              console.log(err);
            }
            db.close();
          }
        );
    });
  });



  //when the user exits the room
  socket.on("disconnect", () => {
    console.log(
      "disconnect one socket >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",
      userId
    );
    getTop5Users();
    io.emit("game", currentGame);
    activeUsers = activeUsers.filter((obj) => obj.SocketId !== socket.id);
  });
});

var bet = [];

const updateTime = async () => {

  io.emit("time", time);
  time--;
  if (time == 1) {
    let winResult = await win(currentGame); //mid , [0,200, 4000]

    console.log("winResult  : ", winResult);
    currentGame.Combinations = winResult.combination;

    allUserHistory(currentGame, winResult.winnerIndex);
  }


  if(time == 3){
    console.log("currentGame.Combinations==1  : ");
    currentGame.Combinations = await cardUpdateSplice(currentGame,time);
  }
  if (time == -9) {
    currentGame.UsersBits = [];
    currentGame.cardBitCoin = [
      { selectFrame: 1, bit: 0, card: "", winner: false },
      { selectFrame: 2, bit: 0, card: "", winner: false },
      { selectFrame: 3, bit: 0, card: "", winner: false },
    ];
  }
  if (time == -11) {
    // new game start with time 20
    console.log(
      time,
      "  ...........  TIME ------------------- -11 -------------  NEW GAME START"
    );

    time = 21;

    bet.splice(0, bet.length);
    await refreshSettings();

    currentGame.UsersBits = [];
    currentGame.cardBitCoin = [
      { selectFrame: 1, bit: 0, card: "", winner: false },
      { selectFrame: 2, bit: 0, card: "", winner: false },
      { selectFrame: 3, bit: 0, card: "", winner: false },
    ];
  }
  getTop5Users();
  io.emit("game", currentGame);
};

const getTop5Users = () => {
  let topUsers = [];
  activeUsers.sort((obj1, obj2) => obj2.User.diamond - obj1.User.diamond);
  topUsers = activeUsers.slice(0, 5);
  io.emit("topUsers", topUsers);
};

const updateUser = async (obj) => {
  try {
    console.log(
      "UPDATE user Function Call =====================> ",
      obj.User._id,
      -parseInt(obj.Bit)
    );

    await User.updateOne(
      { _id: obj.User._id },
      {
        $inc: { diamond: -parseInt(obj.Bit) },
      },
      { new: true }
    );
    return 1;
  } catch (err) {
    console.log(err);
  }
};

const finalUpdateUser = async (obj) => {
  const userDb = await User.findById(obj.User._id);
  return userDb;
};

const addBet = (diamond, id) => {
  console.log(
    "id in bet function.................. .............",
    diamond,
    id
  );
  if (bet.length) {
    const index = bet.findIndex((element) => {
      if (element.userId == id) return true;
      return false;
    });
    if (index != -1) {
      bet[index].amount += diamond;
    } else {
      bet.push({
        amount: diamond,
        userId: id,
      });
    }
  } else {
    bet.push({
      amount: diamond,
      userId: id,
    });
  }
};

server.listen(
  LISTER_PORT,
  console.log(`Server is running on the port no: ${LISTER_PORT}`)
);

// Have Node serve the files for our built React app
app.use(express.static(path.resolve(__dirname, "./public")));

// All other GET requests not handled before will return our React app
app.get("*", (req, res) => {
  res.sendFile(path.resolve(__dirname, "./public", "index.html"));
});
