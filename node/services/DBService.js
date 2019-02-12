/**
 * Created by nidhi on 20/12/13.
 */
//DB connection
var mongo = require('mongodb');
var monk = require('monk');
var db = monk('mongodb://localhost:27017/ecellvsm');
exports.db = db;