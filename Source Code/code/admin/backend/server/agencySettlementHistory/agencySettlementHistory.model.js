const mongoose = require("mongoose");

const agencySettlementHistorySchema = new mongoose.Schema(
  {
    agencyId: {
      type: mongoose.Schema.Types.ObjectId,
      default: null,
      ref: "Agency",
    },
    agencyCommissionPercentage: { type: Number, default: 0 },
    statusOfTransaction: {
      type: Number,
      enum: [1, 2], //(INITIATED = 1), (SUCCESSFULLY_PAID = 2)
      default: 1,
    },
    bonusOrPenltyAmount: { type: Number, default: 0 },
    coinEarned: { type: Number, default: 0 },
    commissionCoinEarned: { type: Number, default: 0 },
    totalCoinEarned: { type: Number, default: 0 },
    startDate: String,
    endDate: String,
    amount: { type: Number, default: 0 },
    dollar: { type: Number, default: 0 },
    note: { type: String, default: "" },
    finalAmountTotal: { type: Number, default: 0 },
    availableCoinAfterPaid: { type: Number, default: 0 },
    payoutDate: String,
    bankDetails: { type: String, default: "" },
  },
  {
    timestamps: true,
    versionKey: false,
  }
);

agencySettlementHistorySchema.index({ agencyId: 1 });
agencySettlementHistorySchema.index({ statusOfTransaction: 1 });
agencySettlementHistorySchema.index({ startDate: 1 });
agencySettlementHistorySchema.index({ endDate: 1 });
agencySettlementHistorySchema.index({ createdAt: -1 });

module.exports = new mongoose.model("agencySettlementHistory", agencySettlementHistorySchema);
