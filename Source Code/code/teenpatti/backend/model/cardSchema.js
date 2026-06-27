const mongoose = require("mongoose");

const CardSchema = new mongoose.Schema(
  {
    cardArray: { type: Array, required: true },
  },
  { versionKey: false } // Disables Mongoose's internal versioning
);

const Card = mongoose.model("Card", CardSchema);

module.exports = Card;
