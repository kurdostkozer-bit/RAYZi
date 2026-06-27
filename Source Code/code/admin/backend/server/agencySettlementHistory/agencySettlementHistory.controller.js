const Agency = require("../agency/agency.model");
const AgencySettlementHistory = require("./agencySettlementHistory.model");
const Setting = require("../setting/setting.model");

const moment = require("moment");

// update agency settlement by successful paid
exports.updatePaidHistroy = async (req, res) => {
  try {
    if (!req?.params?.id) {
      return res.status(200).send({ status: false, message: "Invalid details" });
    }

    const history = await AgencySettlementHistory.findById(req?.params?.id);
    if (!history) {
      return res.status(200).send({ status: false, message: "data not found" });
    }

    const agency = await Agency.findById(history?.agencyId);
    if (!agency) {
      return res.status(200).send({ status: false, message: "data not found" });
    }

    history.statusOfTransaction = 2;
    history.payoutDate = moment().format("YYYY-MM-DD HH:mm");

    await Promise.all([agency.save(), history.save()]);

    return res.status(200).send({ status: true, message: "success!!", history });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

// get agency settlement pending or solved in admin penal
exports.getPendingOrSolvedHistory = async (req, res) => {
  try {
    const start = parseInt(req?.query?.start) || 1;
    const limit = parseInt(req?.query?.limit) || 20;

    let dateFilterQuery = {};

    
    let searchingQuery = {};

    if (req?.query?.search !== "ALL") {
      searchingQuery = {
        $or: [
          {
            $expr: {
              $regexMatch: {
                input: { $toString: "$coinEarned" },
                regex: req?.query?.search,
                options: "i",
              },
            },
          },
          
          {
            $expr: {
              $regexMatch: {
                input: { $toString: "$bonusOrPenaltyAmount" },
                regex: req?.query?.search,
                options: "i",
              },
            },
          },
          {
            $expr: {
              $regexMatch: {
                input: { $toString: "$commissionCoinEarned" },
                regex: req?.query?.search,
                options: "i",
              },
            },
          },
          {
            $expr: {
              $regexMatch: {
                input: { $toString: "$totalCoinEarned" },
                regex: req?.query?.search,
                options: "i",
              },
            },
          },
          {
            $expr: {
              $regexMatch: {
                input: { $toString: "$agencyCommissionPercentage" },
                regex: req?.query?.search,
                options: "i",
              },
            },
          },
          {
            $expr: {
              $regexMatch: {
                input: { $toString: "$totalCoinEarned" },
                regex: req?.query?.search,
                options: "i",
              },
            },
          },
          {
            $expr: {
              $regexMatch: {
                input: { $toString: "$agency.agencyCode" },
                regex: req?.query?.search,
                options: "i",
              },
            },
          },
          {
            $expr: {
              $regexMatch: {
                input: { $toString: "$agency.name" },
                regex: req?.query?.search,
                options: "i",
              },
            },
          },
          {
            $expr: {
              $regexMatch: {
                input: { $toString: "$dollar" },
                regex: req?.query?.search,
                options: "i",
              },
            },
          },
          {
            $expr: {
              $regexMatch: {
                input: { $toString: "$amount" },
                regex: req?.query?.search,
                options: "i",
              },
            },
          },
        ],
      };
    }

    if (req?.query?.startDate != "ALL" || req?.query?.endDate != "ALL") {
      const startDate = new Date(req?.query?.startDate);
      const endDate = new Date(req?.query?.endDate);

      endDate.setDate(23, 59, 59, 999);

      dateFilterQuery = {
        analytic: {
          $gte: startDate,
          $lte: endDate,
        },
      };
    }

    const [historyCount, history] = await Promise.all([
      AgencySettlementHistory.countDocuments({
        statusOfTransaction: parseInt(req?.query?.type),
      }),

      AgencySettlementHistory.aggregate([
        {
          $match: {
            statusOfTransaction: parseInt(req?.query?.type),
          },
        },
        {
          $addFields: {
            analytic: { $toDate: "$createdAt" },
          },
        },
        {
          $match: dateFilterQuery,
        },
        {
          $lookup: {
            from: "agencies",
            localField: "agencyId",
            foreignField: "_id",
            as: "agency",
          },
        },
        {
          $unwind: {
            path: "$agency",
            preserveNullAndEmptyArrays: false,
          },
        },
        {
          $match: searchingQuery,
        },
        {
          $skip: (start - 1) * limit,
        },
        {
          $limit: limit,
        },
        {
          $sort: { createdAt: -1 },
        },
      ]),
    ]);

    return res.status(200).send({ status: true, message: "Success!", historyCount, history });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

// get all settlement for admin penal
exports.getAllSettlementHistory = async (req, res) => {
  try {
    const start = parseInt(req?.query?.start, 10) || 1;
    const limit = parseInt(req?.query?.limit, 10) || 20;

    const [historyCount, history] = await Promise.all([
      AgencySettlementHistory.aggregate([
        {
          $group: {
            _id: "$startDate",
          },
        },
      ]),

      AgencySettlementHistory.aggregate([
        {
          $lookup: {
            from: "agencies",
            as: "agencyId",
            let: { agencyId: "$agencyId" },
            pipeline: [
              {
                $match: {
                  $expr: {
                    $eq: ["$_id", "$$agencyId"],
                  },
                },
              },
              {
                $project: {
                  _id: 0,
                  agencyCode: 1,
                  name: 1,
                },
              },
            ],
          },
        },
        {
          $unwind: {
            path: "$agencyId",
            preserveNullAndEmptyArrays: false,
          },
        },
        {
          $group: {
            _id: "$startDate",
            totalAgency: { $sum: 1 },
            totalrevenue: { $sum: "$coinEarned" },
            totalCommission: { $sum: "$commissionCoinEarned" },
            penltyOrBonus: { $sum: "$bonusOrPenltyAmount" },
            total: { $sum: "$amount" },
            dollar: { $sum: "$dollar" },
            agencyCommissionPercentage: { $first: "$agencyCommissionPercentage" },
            startDate: { $first: "$startDate" },
            endDate: { $first: "$endDate" },
            agency: { $push: "$$ROOT" },
          },
        },
        {
          $project: {
            _id: 1,
            totalAgency: 1,
            totalrevenue: 1,
            totalCommission: {
              $round: ["$totalCommission", 2],
            },
            penltyOrBonus: 1,
            total: { $round: ["$total", 2] },
            dollar: { $round: ["$dollar", 2] },
            totalCoinEarned: { $round: ["$totalCoinEarned", 2] },
            agencyCommissionPercentage: {
              $round: ["$agencyCommissionPercentage", 2],
            },
            startDate: 1,
            endDate: 1,
            agency: 1,
          },
        },
        {
          $skip: (start - 1) * limit,
        },
        {
          $limit: limit,
        },
      ]),
    ]);

    return res.status(200).send({
      status: true,
      message: "success!!",
      historyCount: historyCount.length,
      history,
    });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

// action for agency settlement
exports.actionForAgencySettlement = async (req, res) => {
  try {
    if (!req?.params?.id) {
      return res.status(200).send({ status: false, message: "Invalid details" });
    }

    if (!req?.body?.bonusOrPenltyAmount) {
      return res.status(200).send({ status: false, message: "penltyOrBonus us require" });
    }

    const [historyData, data] = await Promise.all([
      AgencySettlementHistory.findById(req?.params?.id),
      AgencySettlementHistory.findOneAndUpdate(
        {
          _id: req?.params?.id,
        },
        {
          $set: {
            note: req?.body?.note,
          },
          $inc: {
            bonusOrPenltyAmount: parseInt(req?.body?.bonusOrPenltyAmount),
            finalAmountTotal: parseInt(req?.body?.bonusOrPenltyAmount),
          },
        },
        {
          new: true,
        }
      ),
    ]);

    if (!settingJSON) {
      return res.status(200).json({ status: false, message: "Setting does not found." });
    }

    if (historyData.amount <= 0 && req?.body?.bonusOrPenltyAmount < 0) {
      return res.status(200).send({
        status: false,
        message: "You can not give penalty with 0 amount agency",
      });
    }

    const history = await AgencySettlementHistory.findById(data._id);

    history.dollar = parseFloat(history.finalAmountTotal / settingJSON?.rCoinForCashOut).toFixed(2);
    await history.save();

    return res.status(200).send({ status: true, message: "Success", history });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

// get agency for all settelment info
exports.getAllAgencyInfo = async (req, res) => {
  try {
    if (!req?.query?.date) {
      return res.status(200).send({ status: false, message: "Invaild details" });
    }

    const history = await AgencySettlementHistory.find({
      startDate: req?.query?.date,
    }).populate("agencyId", "code name");

    return res.status(200).send({ status: true, message: "success!!", history });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

// get agency wise settlement for agency penal
exports.agencySettlementForAgency = async (req, res) => {
  try {
    if (!req?.query?.agencyId) {
      return res
        .status(200)
        .send({ status: false, message: "Invalid details" });
    }

    const agency = await Agency.findById(req?.query?.agencyId);
    if (!agency) {
      return res
        .status(200)
        .send({ status: false, message: "agency not found" });
    }

    const startDate = req.query.startDate
    const endDate = req.query.endDate

    console.log(startDate, endDate);
    const history = await AgencySettlementHistory.aggregate([
      {
        $match: {
          agencyId: agency._id,
          $and: [
            { startDate: { $gte: startDate } },
            { startDate: { $lte: endDate } },
          ],
        },
      },
      {
        $sort: {
          createdAt: -1,
        },
      },
    ]);

    let summary = {
      totalCoin: 0,
      hostCoin: 0,
    };

    history.forEach((record) => {
      summary.totalCoin += record.coin || 0;
      summary.hostCoin += record.hostCoin || 0;
    });

    return res.status(200).send({
      status: true,
      message: "success!!",
      summary,
      history: history,
    });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

// get all agecy settlent for new module in agency penal
exports.getAllAgencySettlemtforPayOuts = async (req, res) => {
  try {
    if (!req?.query?.agencyId) {
      return res.status(200).send({ status: false, message: "Invalid details" });
    }

    const start = parseInt(req?.query?.start) || 1;
    const limit = parseInt(req?.query?.limit) || 20;

    const agency = await Agency.findById(req?.query?.agencyId);
    if (!agency) {
      return res.status(200).send({ status: false, message: "agency not found" });
    }

    let dateFilterQuery = {};
    if (req?.query?.startDate != "ALL" || req?.query?.endDate != "ALL") {
      const startDate = new Date(req?.query?.startDate);
      const endDate = new Date(req?.query?.endDate);

      endDate.setDate(23, 59, 59, 999);

      dateFilterQuery = {
        analytic: {
          $gte: startDate,
          $lte: endDate,
        },
      };
    }

    const [total, history] = await Promise.all([
      AgencySettlementHistory.find({ agencyId: agency._id }).countDocuments(),
      AgencySettlementHistory.aggregate([
        {
          $match: {
            agencyId: agency._id,
          },
        },
        {
          $addFields: {
            analytic: { $toDate: "$startDate" },
          },
        },
        {
          $match: dateFilterQuery,
        },
        {
          $sort: {
            createdAt: -1,
          },
        },
        {
          $lookup: {
            from: "hostsettlementhistories",
            as: "hostHistory",
            let: {
              agencyId: "$agencyId",
              sDate: "$startDate",
              eDate: "$endDate",
            },
            pipeline: [
              {
                $match: {
                  $expr: {
                    $and: [{ $eq: ["$agencyId", "$$agencyId"] }, { $gte: ["$startDate", "$$sDate"] }, { $lte: ["$startDate", "$$eDate"] }],
                  },
                },
              },
              {
                $group: {
                  _id: null,
                  count: { $sum: 1 },
                },
              },
            ],
          },
        },
        {
          $unwind: {
            path: "$hostHistory",
            preserveNullAndEmptyArrays: false,
          },
        },
        // {
        //   $project: {
        //     _id: 1,
        //     agencyId: 1,
        //     agencyCommissionPercentage: 1,
        //     statusOfTransaction: 1,
        //     bonusOrPenltyAmount: 1,
        //     coinEarned: 1,
        //     commissionCoinEarned: 1,
        //     totalCoinEarned: 1,
        //     startDate: 1,
        //     endDate: 1,
        //     amount: 1,
        //     dollar: 1,
        //     note: 1,
        //     finalAmountTotal: 1,
        //     payoutDate: 1,
        //     totalHost: { $ifNull: ['$hostHistory.count', 0] },
        //   },
        // },
        {
          $skip: (start - 1) * limit,
        },
        {
          $limit: limit,
        },
      ]),
    ]);

    return res.status(200).send({ status: true, message: "success", total, history });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};
