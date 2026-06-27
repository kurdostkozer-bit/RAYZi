const Setting = require("./setting.model");
const Config = require("../../config");

exports.store = async (req, res) => {
  try {
    const setting = new Setting();
    setting.referralBonus = 20;
    await setting.save();

    return res.status(200).json({ status: true, message: "Success", setting });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, error: error.message || "Server Error" });
  }
};

// get setting
exports.getSetting = async (req, res) => {
  try {
    const data = global.settingJSON ? global.settingJSON : null;

    return res.status(200).send({ status: true, message: "Success", setting: data });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" || error });
  }
};

// update the setting data
exports.update = async (req, res) => {
  try {
    const setting = await Setting.findById(req.params.settingId);
    if (!setting) return res.status(200).json({ status: false, message: "Setting data does not Exist!" });

    if (req.body.rCoinForDiamond && req.body?.rCoinForDiamond !== "0") {
      //setting.rCoinForDiamond = Math.floor(req.body.rCoinForDiamond);
      setting.rCoinForDiamond = req.body.rCoinForDiamond;
    }

    setting.referralBonus = req.body?.referralBonus ? Math.floor(req.body?.referralBonus) : setting.referralBonus;
    setting.referralCoinBonus = req.body?.referralCoinBonus ? Math.floor(req.body?.referralCoinBonus) : setting.referralCoinBonus;
    setting.agoraKey = req.body?.agoraKey ? req.body?.agoraKey : setting.agoraKey;
    setting.agoraCertificate = req.body.agoraCertificate ? req.body.agoraCertificate : setting.agoraCertificate;
    setting.maxSecondForVideo = req.body.maxSecondForVideo ? Math.floor(req.body.maxSecondForVideo) : setting.maxSecondForVideo;
    setting.privacyPolicyLink = req.body.privacyPolicyLink ? req.body.privacyPolicyLink : setting.privacyPolicyLink;
    setting.privacyPolicyText = req.body.privacyPolicyText ? req.body.privacyPolicyText : setting.privacyPolicyText;
    setting.chatCharge = req.body.chatCharge ? Math.floor(req.body.chatCharge) : setting.chatCharge;
    setting.maleCallCharge = req.body.maleCallCharge ? Math.floor(req.body.maleCallCharge) : setting.maleCallCharge;
    setting.femaleCallCharge = req.body.femaleCallCharge ? Math.floor(req.body.femaleCallCharge) : setting.femaleCallCharge;
    setting.googlePlayEmail = req.body.googlePlayEmail ? req.body.googlePlayEmail : setting.googlePlayEmail;
    setting.googlePlayKey = req.body.googlePlayKey ? req.body.googlePlayKey : setting.googlePlayKey;
    setting.stripePublishableKey = req.body.stripePublishableKey ? req.body.stripePublishableKey : setting.stripePublishableKey;
    setting.stripeSecretKey = req.body.stripeSecretKey ? req.body.stripeSecretKey : setting.stripeSecretKey;
    setting.version = req.body.version ? req.body.version : setting.version;

    setting.currency = req.body.currency ? req.body.currency : setting.currency; // only currency
    //setting.diamond = req.body.diamond ? Math.floor(req.body.diamond) : setting.diamond;

    setting.minRcoinForCashOut = req.body.minRcoinForCaseOut ? req.body.minRcoinForCaseOut : setting.minRcoinForCashOut;
    setting.rCoinForCashOut = req.body?.rCoinForCaseOut ? req.body?.rCoinForCaseOut : setting.rCoinForCashOut;

    setting.vipDiamond = req.body.vipDiamond ? Math.floor(req.body.vipDiamond) : setting.vipDiamond;
    setting.paymentGateway = req.body.paymentGateway ? req.body.paymentGateway : setting.paymentGateway;
    setting.loginBonus = req.body.loginBonus ? Math.floor(req.body.loginBonus) : setting.loginBonus;
    setting.gameCoin = req.body.gameCoin ? req.body.gameCoin : setting.gameCoin;
    setting.gameRule = req.body.gameRule ? req.body.gameRule : setting.gameRule;
    setting.roulette_gameRule = req.body.roulette_gameRule ? req.body.roulette_gameRule : setting.roulette_gameRule;
    setting.liveDurationTime = req.body?.liveDurationTime ? parseInt(req.body?.liveDurationTime) : 0;
    setting.pkEndTime = req.body.pkEndTime ? req.body.pkEndTime : setting.pkEndTime;
    setting.privateKey = req.body.privateKey ? JSON.parse(req.body.privateKey.trim()) : setting.privateKey;

    setting.locationApiKey = req.body.locationApiKey ? req.body.locationApiKey : setting.locationApiKey;
    setting.callReceiverPercent = req.body.callReceiverPercent ? req.body.callReceiverPercent : setting.callReceiverPercent;

    setting.agencyCommission = req.body.agencyCommission ? req.body.agencyCommission : setting.agencyCommission;
    setting.minRcoinForCashOutAgency = req.body?.minRcoinForCashOutAgency ? parseInt(req.body?.minRcoinForCashOutAgency) : setting.minRcoinForCashOutAgency;

    setting.femaleRandomCallRate = req.body?.femaleRandomCallRate ? parseInt(req.body?.femaleRandomCallRate) : setting.femaleRandomCallRate;
    setting.maleRandomCallRate = req.body?.maleRandomCallRate ? parseInt(req.body?.maleRandomCallRate) : setting.maleRandomCallRate;
    setting.bothRandomCallRate = req.body?.bothRandomCallRate ? parseInt(req.body?.bothRandomCallRate) : setting.bothRandomCallRate;

    await setting.save();

    updateSettingFile(setting);

    return res.status(200).json({ status: true, message: "Success!!", setting });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, error: error.message || "Server Error" });
  }
};

// handle setting switch
exports.handleSwitch = async (req, res) => {
  try {
    const setting = await Setting.findById(req.params.settingId);
    if (!setting) return res.status(200).json({ status: false, message: "Setting data does not Exist!" });

    if (req.query.type === "googlePlay") {
      setting.googlePlaySwitch = !setting.googlePlaySwitch;
    } else if (req.query.type === "stripe") {
      setting.stripeSwitch = !setting.stripeSwitch;
    } else if (req.query.type === "fake") {
      setting.isFake = !setting.isFake;
    } else {
      setting.isAppActive = !setting.isAppActive;
    }

    await setting.save();

    updateSettingFile(setting);

    return res.status(200).json({ status: true, message: "Success", setting });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, error: error.message || "Server Error" });
  }
};

// add game
exports.addGame = async (req, res) => {
  try {
    const setting = await Setting.findById(req.params.settingId);
    if (!setting) return res.status(200).json({ status: false, message: "Setting data does not Exist!" });

    if (!req.body.name || !req.file || !req.body.link) {
      return res.status(200).json({ status: false, message: "Invalid Details" });
    }

    const isGameExists = setting.game.some((game) => game.name.toLowerCase() === req.body.name.toLowerCase());
    if (isGameExists) {
      return res.status(200).json({ status: false, message: "Game with the same name already exists!" });
    }

    setting.game.push({
      name: req.body.name,
      image: Config.baseURL + req.file.path,
      link: req.body.link,
    });
    await setting.save();

    updateSettingFile(setting);

    return res.status(200).json({ status: true, message: "Success!!", setting });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, error: error.message || "Server Error" });
  }
};

// delete game
exports.deleteGame = async (req, res) => {
  try {
    const setting = await Setting.findById(req.params.settingId);
    if (!setting) return res.status(200).json({ status: false, message: "Setting data does not Exist!" });

    if (!req.query.gameId) {
      return res.status(200).json({ status: false, message: "Invalid Details !!" });
    }

    const index = setting.game.findIndex((item) => item._id.toString() === req.query.gameId);
    if (index !== -1) setting.game.splice(index, 1);
    await setting.save();

    updateSettingFile(setting);

    return res.status(200).json({ status: true, message: "Success!!", setting });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, error: error.message || "Server Error" });
  }
};

// update game
exports.updateGame = async (req, res) => {
  try {
    const setting = await Setting.findById(req.params.settingId);
    if (!setting) return res.status(200).json({ status: false, message: "Setting data does not Exist!" });

    if (!req.body.gameId) {
      return res.status(200).json({ status: false, message: "Invalid Details !!" });
    }

    const index = setting.game.findIndex((item) => item._id.toString() === req.body.gameId);
    if (index !== -1) {
      setting.game[index].name = req.body.name || setting.game[index].name;
      setting.game[index].image = req.file ? Config.baseURL + req.file.path : setting.game[index].image;
      setting.game[index].link = req.body.link ? req.body.link : setting.game[index].link;
      await setting.save();
    }

    updateSettingFile(setting);

    return res.status(200).json({ status: true, message: "Success!!", setting });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, error: error.message || "Server Error" });
  }
};
