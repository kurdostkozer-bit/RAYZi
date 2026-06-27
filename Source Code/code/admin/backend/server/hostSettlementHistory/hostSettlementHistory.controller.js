const hostSettlementHistory = require("./hostSettlementHistory.model");
const Agency = require("../agency/agency.model");
const Host = require("../user/user.model");
const AgencySettlementHistory = require("../agencySettlementHistory/agencySettlementHistory.model");
const Setting = require("../setting/setting.model");

const moment = require("moment");
const mongoose = require("mongoose");

// update for paid host settlemet
exports.updatePaidSettlement = async (req, res) => {
  try {
    if (!req?.params?.id) {
      return res.status(200).send({ status: false, message: "Invalid details" });
    }

    const history = await hostSettlementHistory.findById(req?.params?.id);
    if (!history) {
      return res.status(200).send({ status: false, message: "data not found" });
    }

    const [agencySettlemet, agency, host] = await Promise.all([
      AgencySettlementHistory.findOne({
        agencyId: history.agencyId,
        startDate: history.startDate,
        endDate: history.endDate,
      }),
      Agency.findById(history.agencyId),
      Host.findById(history.hostId),
    ]);

    // if (agencySettlemet.statusOfTransaction == 1) {
    //   return res
    //     .status(200)
    //     .send({ status: false, message: 'agency amount not paid by admin' });
    // }

    // if (
    //   parseInt(agency.totalBalance) <
    //   parseInt(history.amount)
    // ) {
    //   return res
    //     .status(200)
    //     .send({ status: false, message: 'Insuffecient balance' });
    // }

    // agency.totalBalance -= parseInt(history.amount);
    history.statusOfTransaction = 2;
    history.payoutDate = moment().format("YYYY-MM-DD HH:mm");

    const payload = {
      token: host.fcmToken,
      notification: {
        body: `You have received payment of ${history.dollar} for ${history.startDate} to ${history.endDate}`,
        title: "Payment received from agency",
      },
      data: {
        data: {
          body: `You have received payment of ${history.dollar} for ${history.startDate} to ${history.endDate}`,
          title: "Payment received from agency",
        },
      },
    };

    await Promise.all([agency.save(), history.save()]);

    // await fcm.send(payload, async (error, response) => {
    //   if (error) {
    //     console.log("Something has gone wrong: ", error);
    //   } else {
    //     console.log("Successfully sent with response: ", response);
    //   }
    // });

    return res.status(200).send({ status: true, message: "success!!", history });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

// get agencywise host settlement  history for admin penal
exports.agencyWiseHostSettlement = async (req, res) => {
  try {
    if (!req?.query?.agencyId) {
      return res.status(200).send({ status: false, message: "Invalid details" });
    }

    const start = parseInt(req?.query?.start) || 1;
    const limit = parseInt(req?.query?.limit) || 20;

    let dateFilterQuery = {};
    let sort = { _id: -1 };
    if (req?.query?.startDate !== "ALL" && req?.query?.endDate !== "ALL") {
      const startDate = new Date(req.query.startDate);
      const endDate = new Date(req.query.endDate);

      endDate.setHours(23, 59, 59, 999);
      dateFilterQuery = {
        analytic: {
          $gte: startDate,
          $lte: endDate,
        },
      };
    }
    const agency = await Agency.findById(req?.query?.agencyId);
    if (!agency) {
      return res.status(200).send({ status: false, message: "agency not found" });
    }

    const history = await hostSettlementHistory.aggregate([
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
        $lookup: {
          from: "users",
          localField: "hostId",
          foreignField: "_id",
          as: "host",
        },
      },
      {
        $unwind: {
          path: "$host",
          preserveNullAndEmptyArrays: false,
        },
      },

      {
        $sort: sort,
      },
      {
        $facet: {
          history: [{ $skip: (start - 1) * limit }, { $limit: limit }],
          totalRevenue: [{ $group: { _id: null, total: { $sum: "$amount" } } }],
          historyCount: [{ $group: { _id: null, totalCount: { $sum: 1 } } }],
        },
      },
    ]);

    return res.status(200).send({
      status: true,
      message: "success!!",
      historyCount: history[0].historyCount[0]?.totalCount > 0 ? history[0].historyCount[0]?.totalCount : 0,
      totalRevenue: history[0].totalRevenue[0]?.total > 0 ? history[0].totalRevenue[0]?.total : 0,
      history: history[0].history,
    });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

// action for host settlement
exports.actionForHostSettlement = async (req, res) => {
  try {
    if (!req?.params?.id) {
      return res.status(200).send({ status: false, message: "Invalid details" });
    }

    if (!settingJSON) {
      return res.status(200).send({ status: false, message: "Setting does not found." });
    }

    if (!req?.body?.bonusOrPenaltyAmount) {
      return res.status(200).send({ status: false, message: "penltyOrBonus us require" });
    }

    const [history, data] = await Promise.all([
      hostSettlementHistory.findById(data._id),
      hostSettlementHistory.findOneAndUpdate(
        {
          _id: req?.params?.id,
        },
        {
          $set: {
            note: req?.body?.note,
          },
          $inc: {
            bonusOrPenaltyAmount: parseInt(req?.body?.bonusOrPenaltyAmount),
            amount: parseInt(req?.body?.bonusOrPenaltyAmount),
            finalTotalAmount: parseInt(req?.body?.bonusOrPenaltyAmount),
          },
        },
        {
          new: true,
        }
      ),
    ]);

    history.dollar = parseFloat(history.amount / settingJSON?.coinPerDollar).toFixed(2);
    history.payoutDate = moment().format("YYYY-MM-DD HH:mm");
    await history.save();

    return res.status(200).send({ status: true, message: "success!!", history });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

// get pending or solved settlemet for agency penal
exports.pendingOrSolvedSettlement = async (req, res) => {
  try {
    if (!req?.query?.agencyId) {
      return res.status(200).send({ status: false, message: "Invalid details" });
    }

    const agency = await Agency.findById(req?.query?.agencyId);
    if (!agency) {
      return res.status(200).send({ status: false, message: "agency not found" });
    }

    const start = parseInt(req?.query?.start) || 1;
    const limit = parseInt(req?.query?.limit) || 20;

    let searchingQuery = {};
    let sort = { _id: -1 };

    if (req?.query?.search !== "ALL") {
      searchingQuery = {
        $or: [
          { "hostId.name": { $regex: req?.query?.search, $options: "i" } },
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

    switch (req?.query?.sort) {
      case "coinEarned":
        sort = { coinEarned: parseInt(req.query?.sortType) || 1 };
        break;
      case "commissionCoinEarned":
        sort = { commissionCoinEarned: parseInt(req.query?.sortType) || 1 };
        break;
      case "totalCoinEarned":
        sort = { totalCoinEarned: parseInt(req.query?.sortType) || 1 };
        break;
      case "amount":
        sort = { amount: parseInt(req.query?.sortType) || 1 };
        break;
      case "dollar":
        sort = { dollar: parseInt(req.query?.sortType) || 1 };
        break;
      default:
        break;
    }

    const [total, history] = await Promise.all([
      hostSettlementHistory
        .find({
          agencyId: agency._id,
          statusOfTransaction: parseInt(req?.query?.type),
        })
        .countDocuments(),
      hostSettlementHistory.aggregate([
        {
          $match: {
            agencyId: agency._id,
            statusOfTransaction: parseInt(req?.query?.type),
          },
        },
        {
          $lookup: {
            from: "hosts",
            as: "hostId",
            let: { hostId: "$hostId" },
            pipeline: [
              {
                $match: {
                  $expr: {
                    $eq: ["$_id", "$$hostId"],
                  },
                },
              },
              {
                $project: {
                  name: 1,
                  uniqueId: 1,
                },
              },
            ],
          },
        },
        {
          $unwind: {
            path: "$hostId",
            preserveNullAndEmptyArrays: false,
          },
        },
        {
          $lookup: {
            from: "agencies",
            as: "agency",
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
                  name: 1,
                  agencyCode: 1,
                },
              },
            ],
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
          $sort: sort,
        },
        {
          $skip: (start - 1) * limit,
        },
        {
          $limit: limit,
        },
      ]),
    ]);

    return res.status(200).send({ status: true, message: "Success", total, history });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

exports.getPendingOrSolvedAll = async (req, res) => {
  try {
    const start = parseInt(req?.query?.start) || 1;
    const limit = parseInt(req?.query?.limit) || 20;

    let dateFilterQuery = {};

    const startDate = req.query.startDate || "ALL";
    const endDate = req.query.endDate || "ALL";

    if (!req.query.type) {
      return res.status(200).send({ status: false, message: "Invalid details" });
    }

    if (startDate != "ALL" && endDate != "ALL") {
      dateFilterQuery = {
        date: {
          $gte: req.query.startDate,
          $lte: req.query.endDate,
        },
      };
    }

    let searchingQuery = {};

    if (req?.query?.search !== "ALL") {
      searchingQuery = {
        $or: [
          { "host.name": { $regex: req?.query?.search, $options: "i" } },
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
                input: { $toString: "$host.uniqueId" },
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
                input: { $toString: "$host.uniqueId" },
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

    const [historyCount, history] = await Promise.all([
      hostSettlementHistory.countDocuments({ statusOfTransaction: parseInt(req?.query?.type) }),
      hostSettlementHistory.aggregate([
        {
          $match: {
            statusOfTransaction: parseInt(req?.query?.type),
          },
        },
        {
          $addFields: {
            date: { $substr: ["$createdAt", 0, 10] }, // Add a field with the first 10 characters of createdAt
          },
        },
        {
          $match: dateFilterQuery,
        },

        {
          $lookup: {
            from: "users",
            localField: "hostId",
            foreignField: "_id",
            as: "host",
          },
        },
        {
          $unwind: {
            path: "$host",
            preserveNullAndEmptyArrays: false,
          },
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

    return res.status(200).send({ status: true, message: "Success", historyCount, history });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};

exports.hostSettlementForHost = async (req, res) => {
  try {
    if (!req?.query?.hostId) {
      return res.status(200).send({ status: false, message: "Invalid details" });
    }

    const host = await Host.findById(req?.query?.hostId);
    if (!host) {
      return res.status(200).send({ status: false, message: "host not found" });
    }

    const startDate = req.query.startDate;
    const endDate = req.query.endDate;

    const [total, history] = await Promise.all([
      hostSettlementHistory
        .find({
          hostId: host._id,
          startDate: { $gte: startDate },
          startDate: { $lte: endDate },
        })
        .countDocuments(),
      hostSettlementHistory.aggregate([
        {
          $match: {
            hostId: host._id,
            $and: [{ startDate: { $gte: startDate } }, { startDate: { $lte: endDate } }],
          },
        },
        {
          $sort: {
            createdAt: -1,
          },
        },
        {
          $project: {
            _id: 1,
            statusOfTransaction: 1,
            bonusOrPenltyAmount: 1,
            coinEarned: 1,
            commissionCoinEarned: 1,
            totalCoinEarned: 1,
            startDate: 1,
            endDate: 1,
            amount: 1,
            dollar: 1,
            note: 1,
            finalAmountTotal: 1,
            payoutDate: 1,
          },
        },
      ]),
    ]);

    return res.status(200).send({
      status: true,
      message: "success!!",
      total: total,
      history: history,
    });
  } catch (error) {
    console.log(error);
    return res.status(500).send({ status: false, message: "Internal server error" });
  }
};
