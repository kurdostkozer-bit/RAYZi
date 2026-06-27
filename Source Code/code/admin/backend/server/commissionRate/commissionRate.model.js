const mongoose = require("mongoose");

const CommissionRateSchema = new mongoose.Schema(
  {
    amountPercentage: { type: Number, default: 0 }, // commission percentage for agency
    upperCoin: { type: Number, default: 0 }, // amount  wise commission
    type: { type: Number, enum: [1, 2] }, // amount  wise commission 1:agency 2:host
  },
  {
    timestamps: true,
    versionKey: false,
  }
);

CommissionRateSchema.index({ createdAt: -1 });

module.exports = new mongoose.model("CommissionRate", CommissionRateSchema);
