#!/usr/bin/env bash

mongoimport --host task-mongo --db applause --collection bugs --file /mongo-settings/bugs.csv --type csv --headerline
mongoimport --host task-mongo --db applause --collection testers --file /mongo-settings/testers.csv --type csv --headerline
mongoimport --host task-mongo --db applause --collection tester_device --file /mongo-settings/tester_device.csv --type csv --headerline
mongoimport --host task-mongo --db applause --collection devices --file /mongo-settings/devices.csv --type csv --headerline
mongo task-mongo:27017/applause /mongo-settings/migration.js
