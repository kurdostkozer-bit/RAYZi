const mongoose = require("mongoose");

const hostSettlementHistory = new mongoose.Schema(
  {
    hostId: { type: mongoose.Schema.Types.ObjectId, default: null, ref: "User" },
    agencyId: { type: mongoose.Schema.Types.ObjectId, default: null, ref: "Agency" },
    coinEarned: { type: Number, default: 0 },
    bonusOrPenaltyAmount: { type: Number, default: 0 },
    statusOfTransaction: {
      type: Number,
      enum: [1, 2], //1-initiated // 2-success
      default: 1,
    },
    totalCoinEarned: { type: Number, default: 0 },
    amount: { type: Number, default: 0 },
    startDate: String,
    endDate: String,
    dollar: { type: Number, default: 0 },
    note: { type: String, default: "" },
    finalTotalAmount: { type: Number, default: 0 },
    payoutDate: String,
    bankDetails: { type: String, default: "" },
  },
  {
    timestamps: true,
    versionKey: false,
  }
);

hostSettlementHistory.index({ hostId: 1 });
hostSettlementHistory.index({ agencyId: 1 });
hostSettlementHistory.index({ statusOfTransaction: 1 });
hostSettlementHistory.index({ createdAt: -1 });

module.exports = new mongoose.model("hostSettlementHistory", hostSettlementHistory);
