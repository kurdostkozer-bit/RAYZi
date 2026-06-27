const mongoose = require("mongoose");
const seat = require("../../util/defaultSeat");
const config = require("../../config");

const liveUserSchema = new mongoose.Schema(
  {
    name: String,
    country: String,
    image: String,
    avatarFrameImage: String,
    age: Number,
    token: String,
    channel: String,
    diamond: Number,
    username: String,
    isVIP: Boolean,
    uniqueId: Number,
    countryFlagImage: String,

    rCoin: { type: Number, default: 0 }, //liveRoom's coin

    background: { type: String, default: `${config?.baseURL}storage/default_bg_audioroom.png` },
    view: { type: Array, default: [] },
    time: { type: Number, default: 0 },
    liveUserId: { type: mongoose.Schema.Types.ObjectId, ref: "User" },
    liveStreamingId: { type: mongoose.Schema.Types.ObjectId, ref: "livestreaminghistories" },
    continueLive: { type: Boolean, default: false },

    isPublic: { type: Boolean, default: true },
    agoraUID: { type: Number, default: 0 },
    filter: { type: String, default: "" },

    audioConfig: {
      isHostMute: { type: Number, default: 0 },
    },
    isHostExists: { type: Boolean, default: false },
    privateCode: { type: Number, default: 0 }, //max 4 digit
    roomName: { type: String, default: "" },
    roomWelcome: { type: String, default: "" },
    roomImage: { type: String, default: "" },
    audio: { type: Boolean, default: false },
    seat: {
      type: [
        {
          position: { type: Number },
          mute: { type: Number, enum: [0, 1, 2], default: 0 },
          lock: { type: Boolean },
          isSpeaking: { type: Boolean },
          reserved: { type: Boolean },
          invite: { type: Boolean },
          avatarFrameImage: { type: String },
          userId: { type: mongoose.Schema.Types.ObjectId, ref: "User" },
          name: { type: String },
          image: { type: String },
          country: { type: String },
          agoraUid: { type: Number },
        },
      ],
      default: seat.default,
    },

    isPkMode: { type: Boolean, default: false },
    pkEndTime: { type: String, default: null },
    pkIdentity: {
      pkId: { type: String, default: null },
      count: { type: Number, default: 0 },
    },
    pkConfig: {
      host1Id: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
        default: null,
      },
      host2Id: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
        default: null,
      },
      host1Token: { type: String, default: null },
      host2Token: { type: String, default: null },
      host1Name: { type: String, default: null },
      host2Name: { type: String, default: null },
      host1Image: { type: String, default: null },
      host2Image: { type: String, default: null },
      host1Channel: { type: String, default: null },
      host2Channel: { type: String, default: null },
      host1UniqueId: { type: Number, default: null },
      host2UniqueId: { type: Number, default: null },
      host1LiveId: { type: String, default: null },
      host2LiveId: { type: String, default: null },
      host1AgoraUID: { type: Number, default: 0 },
      host2AgoraUID: { type: Number, default: 0 },
      localRank: { type: Number, default: 0 },
      remoteRank: { type: Number, default: 0 },
      isWinner: { type: Number, enum: [0, 1, 2], default: 0 }, //0.draw(tie) 1.loose 2.winner
      giftTopUser: [
        {},
        {
          default: [
            {
              userId: null,
              diamond: 0,
              image: null,
              name: null,
            },
            {
              userId: null,
              diamond: 0,
              image: null,
              name: null,
            },
            {
              userId: null,
              diamond: 0,
              image: null,
              name: null,
            },
          ],
        },
      ],
      host1Details: {
        name: { type: String, default: null },
        rCoin: { type: Number, default: 0 },
        avatarFrameImage: { type: String, default: null },
        image: { type: String, default: null },
        country: { type: String, default: null },
        uniqueId: { type: String, default: null },
        isVIP: { type: Boolean, default: false },
      },
      host2Details: {
        name: { type: String, default: null },
        rCoin: { type: Number, default: 0 },
        avatarFrameImage: { type: String, default: null },
        image: { type: String, default: null },
        country: { type: String, default: null },
        uniqueId: { type: String, default: null },
        isVIP: { type: Boolean, default: false },
      },
    },

    blockedUsers: [
      {
        blockedUserId: { type: mongoose.Schema.Types.ObjectId, ref: "User", default: null },
      },
    ],

    expiration_date: { type: Date, expires: 0 }, //for liveUsers deleted after 30 min of when user is live (except audio live)
  },
  {
    timestamps: true,
    versionKey: false,
  }
);

liveUserSchema.index({ liveUserId: 1 });
liveUserSchema.index({ liveStreamingId: 1 });

module.exports = mongoose.model("LiveUser", liveUserSchema);
