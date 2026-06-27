const Chat = require("./chat.model");
const ChatTopic = require("../chatTopic/chatTopic.model");
const fs = require("fs");
const mongoose = require("mongoose");
const { compressImage } = require("../../util/compressImage");

//private key
const admin = require("../../util/privateKey");

// get old chat
exports.getOldChat = async (req, res) => {
  try {
    const topicId = new mongoose.Types.ObjectId(req.query.topicId);

    const [readMessage, chat] = await Promise.all([
      Chat.updateMany({ topic: topicId, isRead: false }, { isRead: true }),
      Chat.find({ topic: topicId })
        .sort({ createdAt: -1 })
        .skip(req.query.start ? parseInt(req.query.start) : 0)
        .limit(req.query.limit ? parseInt(req.query.limit) : 20)
        .lean(),
    ]);

    if (!chat) return res.status(200).json({ status: false, message: "No data found!" });

    return res.status(200).json({ status: true, message: "Success", chat });
  } catch (error) {
    console.log(error);

    return res.status(500).json({ status: false, error: error.message || "Server Error" });
  }
};

// create chat [only image]
exports.store = async (req, res) => {
  try {
    if (req.body.messageType !== "image" || !req.body.topic || !req.body.messageType || !req.body.senderId) {
      return res.status(200).json({ status: false, message: "Invalid Details!!" });
    }

    const chatTopic = await ChatTopic.findById(req.body.topic).populate("receiverUser senderUser");
    if (!chatTopic) {
      return res.status(200).json({ status: false, message: "Topic not Exist!" });
    }

    //compress image
    compressImage(req.file);

    const chat = new Chat();
    chat.senderId = req.body.senderId;
    chat.messageType = "image";
    chat.message = "📸 Image";
    chat.image = req.file.path;
    chat.topic = chatTopic._id;
    chat.date = new Date().toLocaleString("en-US", { timeZone: "Asia/Kolkata" });

    chatTopic.chat = chat._id;

    await Promise.all([chat.save(), chatTopic.save()]);

    res.status(200).json({ status: true, message: "Success", chat });

    let receiverUser, senderUser;
    if (chatTopic.senderUser && chatTopic.senderUser._id.toString() === req.body.senderId.toString()) {
      receiverUser = chatTopic.receiverUser;
      senderUser = chatTopic.senderUser;
    } else if (chatTopic.receiverUser && chatTopic.receiverUser._id) {
      receiverUser = chatTopic.senderUser;
      senderUser = chatTopic.receiverUser;
    }

    if (receiverUser && !receiverUser.isBlock && receiverUser.notification.message && !receiverUser.fcmToken !== null) {
      const adminPromise = await admin;

      const payload = {
        token: receiverUser.fcmToken,
        notification: {
          body: chat.message,
          title: senderUser.name,
        },
        data: {
          data: JSON.stringify({
            topic: chatTopic._id,
            message: chat.message,
            date: chat.date,
            chatDate: chat.date,
            userId: senderUser._id,
            name: senderUser.name,
            username: senderUser.username,
            image: senderUser.image,
            country: senderUser.country,
            isVIP: senderUser.isVIP,
            time: "Just Now",
          }),
          type: "MESSAGE",
        },
      };

      adminPromise
        .messaging()
        .send(payload)
        .then((response) => {
          console.log("Successfully sent with response: ", response);
        })
        .catch((error) => {
          console.log("Error sending message:      ", error);
        });
    }
  } catch (error) {
    return res.status(500).json({ status: false, error: error.message || "Internal Server Error" });
  }
};

//delete message
exports.deleteMessage = async (req, res, next) => {
  try {
    const chat = await Chat.findById(req.query.chatId);
    if (!chat) return res.status(200).json({ status: false, message: "Chat does not Exist!" });

    const chatTopic = await ChatTopic.findById(chat.topic);

    if (fs.existsSync(chat.image)) {
      fs.unlinkSync(chat.image);
    }

    await chat.deleteOne();

    if (chatTopic && chatTopic.chat.toString() === req.query.chatId.toString()) {
      const newChat = await Chat.findOne({ topic: chatTopic._id }).sort({
        createdAt: -1,
      });

      chatTopic.chat = newChat._id;
      await chatTopic.save();
    }

    return res.status(200).json({ status: true, message: "Success!" });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, error: error.message });
  }
};
