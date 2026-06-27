const mongoose = require("mongoose");
const Schema = mongoose.Schema;
const bcrypt = require("bcryptjs");

const adminSchema = new Schema(
  {
    name: { type: String, default: "Admin" },
    email: String,
    password: String,
    image: { type: String, default: null },
    purchaseCode: { type: String, default: null },
  },
  {
    timestamps: true,
    versionKey: false,
  }
);

//hash password before the admin is saved
adminSchema.pre("save", function (next) {
  const admin = this;
  if (!admin.isModified("password")) return next();
  bcrypt.hash(admin.password, 10, (err, hash) => {
    if (err) return next(err);
    admin.password = hash;
    next();
  });
});

module.exports = mongoose.model("Admin", adminSchema);
