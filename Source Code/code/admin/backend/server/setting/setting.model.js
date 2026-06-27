const mongoose = require("mongoose");

const gameItemSchema = new mongoose.Schema(
  {
    name: { type: String, default: "NAME" },
    image: { type: String, default: "IMAGE" },
    link: { type: String, default: "LINK" },
    minWinPercent: { type: Number, default: 25 },
    maxWinPercent: { type: Number, default: 120 },
  },
  {
    timestamps: true,
    versionKey: false,
  }
);

const settingSchema = new mongoose.Schema(
  {
    referralBonus: { type: Number, default: 50 }, //diamond
    referralCoinBonus: { type: Number, default: 50 }, //coin

    loginBonus: { type: Number, default: 50 },
    maxSecondForVideo: { type: Number, default: 30 },

    agoraKey: { type: String, default: "AGORA KEY" },
    agoraCertificate: { type: String, default: "AGORA CERTIFICATE" },

    privacyPolicyLink: { type: String, default: "PRIVACY POLICY LINK" },
    privacyPolicyText: { type: String, default: "PRIVACY POLICY TEXT" },

    chatCharge: { type: Number, default: 10 },

    //private call charge
    maleCallCharge: { type: Number, default: 10 },
    femaleCallCharge: { type: Number, default: 10 },

    //random call charge
    femaleRandomCallRate: { type: Number, default: 10 },
    maleRandomCallRate: { type: Number, default: 10 },
    bothRandomCallRate: { type: Number, default: 10 },

    googlePlayEmail: { type: String, default: "GOOGLE PLAY EMAIL" },
    googlePlayKey: { type: String, default: "GOOGLE PLAY KEY" },
    googlePlaySwitch: { type: Boolean, default: false },

    stripeSwitch: { type: Boolean, default: false },
    stripePublishableKey: { type: String, default: "STRIPE PUBLISHABLE KEY" },
    stripeSecretKey: { type: String, default: "STRIPE SECRET KEY" },

    currency: { type: String, default: "$" },
    vipDiamond: { type: Number, default: 20 },

    minRcoinForCashOut: { type: Number, default: 200 }, // minimum rCoin for withdraw [user redeem]

    rCoinForCashOut: { type: Number, default: 20 }, // coin trf to cash
    rCoinForDiamond: { type: Number, default: 20 }, // diamond trf to coin

    isFake: { type: Boolean, default: false },
    isAppActive: { type: Boolean, default: true },
    version: { type: Number, default: 1 },
    paymentGateway: { type: Array, default: [] },
    freeDiamondForAd: { type: Number, default: 20 },
    maxAdPerDay: { type: Number, default: 3 },
    liveDurationTime: { type: Number, default: 0 },
    gameCoin: { type: Array, default: [100, 200, 500, 1000, 2000] },
    gameRule: { type: String, default: "GAME RULE" },
    roulette_gameRule: { type: String, default: "roulette_gameRule" },
    pkEndTime: { type: Number, default: 0 },
    game: [gameItemSchema],
    privateKey: { type: Object, default: {} }, //firebase.json handle notification

    agencyCommission: { type: Number, default: 15 },
    minRcoinForCashOutAgency: { type: Number, default: 500 },
    locationApiKey: { type: String, default: "LOCATION API KEY" },
    callReceiverPercent: { type: Number, default: 0 }, //coin earn by call receiver
  },
  {
    timestamps: false,
    versionKey: false,
  }
);

module.exports = mongoose.model("Setting", settingSchema);
