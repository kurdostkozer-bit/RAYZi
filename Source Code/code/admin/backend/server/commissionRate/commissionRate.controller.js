const Commission = require("./commissionRate.model");

//add commission
exports.store = async (req, res) => {
  try {
    if (!req?.body?.amountPercentage || !req?.body?.upperCoin || !req?.body?.type) {
      return res.status(200).send({ status: false, message: "Invalid details" });
    }

    const type = parseInt(req.body.type);
    if (type != 1 && type != 2) {
      return res.status(200).send({ status: false, message: "Invalid type" });
    }

    if (req.body.amountPercentage >= 100) {
      return res.status(200).send({
        status: false,
        message: "amountPercentage must be less than 100",
      });
    }

    const [existingCommissionWithSameCoin, existingCommissionWithPercent] = await Promise.all([
      Commission.findOne({ upperCoin: req.body.upperCoin, type: req.body.type }),
      Commission.findOne({ amountPercentage: req.body.amountPercentage, type: req.body.type }),
    ]);

    if (existingCommissionWithSameCoin) {
      return res.status(200).send({ status: false, message: "upperCoin already exists" });
    }

    if (existingCommissionWithPercent) {
      return res.status(200).send({ status: false, message: "Same percent already exists" });
    }

    const commission = await Commission.create({
      amountPercentage: req.body.amountPercentage,
      upperCoin: req.body.upperCoin,
      type,
    });

    return res.status(200).send({ status: true, message: "Success", commission });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

//update commission
exports.update = async (req, res) => {
  try {
    if (!req.query.commissionRateId) {
      return res.status(200).send({ status: false, message: "Invalid details" });
    }

    const commission = await Commission.findById(req.query.commissionRateId);
    if (!commission) {
      return res.status(200).send({ status: false, message: "data not found" });
    }

    commission.amountPercentage = req?.body?.amountPercentage || commission.amountPercentage;
    commission.upperCoin = req?.body?.upperCoin || commission.upperCoin;
    await commission.save();

    return res.status(200).send({ status: true, message: "Success", commission });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

//get commission
exports.get = async (req, res) => {
  try {
    const type = parseInt(req.query.type);

    if (type != 1 && type != 2) {
      return res.status(200).send({ status: false, message: "Invalid type" });
    }

    if (!type) {
      return res.status(200).send({ status: false, message: "Type is required" });
    }
    const commission = await Commission.find({ type: type }).sort({ amountPercentage: 1 });

    return res.status(200).send({ status: true, message: "success", commission });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

//delete commission
exports.delete = async (req, res) => {
  try {
    if (!req.query.commissionRateId) {
      return res.status(200).send({ status: false, message: "Invalid details" });
    }

    const [commission, totalCommission] = await Promise.all([Commission.findById(req.query.commissionRateId), Commission.countDocuments()]);

    if (!commission) {
      return res.status(200).json({ status: false, message: "data not found" });
    }

    if (totalCommission > 1) {
      await commission.deleteOne();
    } else {
      return res.status(200).json({
        status: false,
        message: "last one record cannot be deleted.",
      });
    }

    return res.status(200).json({ status: true, message: "Success" });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, message: "Internal server error" });
  }
};
