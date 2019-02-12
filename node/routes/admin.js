var express = require('express');
var router = express.Router();
var DBService = require('../services/DBService');
var db = DBService.db;
var userm = require("../models/user");
var companym = require("../models/company");

router.get('/', function(req, res, next) {
    res.send('Welcome To Ecell VSM');
});

//verification done
router.post('/verify',function (req,res,next) {
    var username = req.body.username;
    var userc = db.get('user');
    userc.findOne({"username":req.body.username},function (err,docs) {
        if(docs == null){
            res.send({success: "0",error:"2"});//User not found
        }
        else{
        userc.update({"username": username}, {$set: {"verify":1}}, function (err, docs) {
            if (err) {
                res.send({success: "0",error:"1"});//some db error
            }
            else {
                res.send({success: "1"});
            }
        });
    }
    });
});

router.get('/startstop',function (req,res,next) {
   var begin = db.get("begin");
    begin.findOne({},function (err,docs) {
       if(docs.begin == 1){
           begin.update({},{$set:{"begin":"0"}},function (err,docs) {
                res.send("DONE ACTIVITY STOPPED");
           })
       }else {
           begin.update({},{$set:{"begin":"1"}},function (err,docs) {
               res.send("DONE ACTIVITY STARTED");
           })
       }
    });
});

router.get('/completeround',function (req,res,next) {
    var hourservice = require('../services/marketprice');
    hourservice.sessionend();
    setTimeout(function() {     hourservice.recalculatenet(); }, 30000);
    res.send("Started Round Completion");
});

//verification done
router.post('/addCompany',function ( req,res,next){
    var company = companym.create();
    company.name = req.body.name;
    company.shortname = req.body.shortname;
    company.rate = req.body.rate;
    company.previous = req.body.rate;
    company.change = 0;
    company.supply = req.body.supply;
    var companyc = db.get('company');

            companyc.insert(company,function (err,docs) {
                if(err)
                {
                    res.send({success:"0",error:"1"});//some db error
                }
                else {
                    res.send({success: "1"});
                }
            });
});


router.get('/resetcompany',function (req,res,next) {
    var companyc = db.get("company");
    companyc.find({},function (err,docs) {
        for(i=0;i<docs.length;i++){
            companyc.update({"_id":docs[i]._id},{$set:{"rate":100,"previous":100,"change":"0","supply":"0"}})
        }
        res.send("RESETTED");
    })
    var historyc = db.get("history");
    historyc.remove({},function (err,docs){
        resetUser();
    });


});

function resetUser(){
    var userc = db.get("user");
    userc.find({},function (err,docs) {
        for(i=0;i<docs.length;i++){
            userc.update({"_id":docs[i]._id},{$set:{"networth":5000,"previous":5000,"balance":5000}});
        }
    });

    var completec  = db.get("completed");
    completec.remove({},function (err,docs) {
    });

}
module.exports = router;
