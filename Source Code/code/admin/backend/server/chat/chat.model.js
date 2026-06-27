const mongoose = require("mongoose");

const chatSchema = mongoose.Schema(
  {
    topic: { type: mongoose.Schema.Types.ObjectId, ref: "ChatTopic" },
    senderId: String,
    messageType: String,
    message: String,
    image: String,
    isRead: { type: Boolean, default: false },
    date: String,
  },
  {
    timestamps: true,
    versionKey: false,
  }
);

chatSchema.index({ topic: 1 });

module.exports = mongoose.model("Chat", chatSchema);
