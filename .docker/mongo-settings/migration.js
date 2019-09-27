db.testersInfo.drop();
db.testers.aggregate([
    {
        $lookup: {
            from: "tester_device",
            localField: "testerId",
            foreignField: "testerId",
            as: "deviceId"
        }
    },
    {
        $project: {
            testerId: 1,
            firstName: 1,
            lastName: 1,
            country: 1,
            lastLogin: 1,
            deviceId: "$deviceId.deviceId"
        }
    },
    {
        $unwind: "$deviceId"
    },
    {
        $lookup: {
            from: "devices",
            localField: "deviceId",
            foreignField: "deviceId",
            as: "deviceName"
        }
    },
    {
        $project: {
            testerId: 1,
            firstName: 1,
            lastName: 1,
            country: 1,
            lastLogin: 1,
            deviceId: 1,
            deviceName: "$deviceName.description"
        }
    },
    {
        $unwind: "$deviceName"
    },
    {
        $lookup: {
            from: "bugs",
            let: {tester_from_result: "$testerId", device_from_result: "$deviceId"},
            pipeline: [
                {
                    $match: {
                        $expr: {
                            $and: [
                                {$eq: ["$testerId", "$$tester_from_result"]},
                                {$eq: ["$deviceId", "$$device_from_result"]}
                            ]
                        }
                    }
                }
            ],
            as: "bugs"
        }
    },
    {
        $project: {
            testerId: 1,
            firstName: 1,
            lastName: 1,
            country: 1,
            lastLogin: 1,
            deviceId: 1,
            deviceName: 1,
            bugs: {$size: "$bugs"}
        }
    },
    {
        $group: {
            _id: "$testerId",
            firstName: {$first: "$firstName"},
            lastName: {$first: "$lastName"},
            country: {$first: "$country"},
            lastLogin: {$first: "$lastLogin"},
            devices: {
                $push: {
                    "id": "$deviceId",
                    "name": "$deviceName",
                    "quantity": "$bugs"
                }
            }
        }
    },
    {
        $project: {
            _id: 0,
            testerId: "$_id",
            firstName: 1,
            lastName: 1,
            country: 1,
            lastLogin: {
                $dateFromString: {
                    dateString: "$lastLogin"
                }
            },
            devices: 1
        }
    },
    {
        $sort: { "testerId": 1 }
    },
    {
        $out: "testerInfos"
    }
]);
db.bugs.drop();
db.testers.drop();
db.tester_device.drop();
db.devices.drop();