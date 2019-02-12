/**
 * Created by nidhi on 20/12/13.
 */


var DBService = require('../services/DBService');
var db = DBService.db;
var userm = require("../models/user");
var pendingm  =  require("../models/pending");
var companym = require("../models/company");
var completedm  = require("../models/completed");
var historym = require("../models/history");

var userc = db.get("user");
var pendingc = db.get("pending");
var historyc = db.get("history");
var companyc = db.get("company");
var completedc = db.get("completed");

var companies , pending_trans , users , completeds;


exports.sessionend = function () {
    console.log("Called Session End ");

    // console.log("Calling this function to end a session and continue another");
  //  console.log ("Getting all users");

    userc.find({},function(err,docs){
        if(err)
            console.log(err);
        else {
            if(docs != null) {
                users = docs;
                for(i = 0 ;i < users.length ; i++){
                    users[i].og = users[i].networth;
                }
                getallcompanies();
            }
        }
    });
}


function getallcompanies() {
  //  console.log("Getting company data");
    companyc.find({},function (err,docs) {
        if(err)
            console.log(err);
        else {
            if(docs != null) {
                companies = docs;
                getpendings();
            }
        }
    });
}

function getpendings() {
   // console.log("Getting pending data");
    pendingc.find({},function (err,docs) {
        if(err)
            console.log(err);
        else {
            if(docs != null) {
                pending_trans = docs;
                getcompleted();
            }
        }
    });
}

function getcompleted() {
   // console.log("Getting completed data");
    completedc.find({},function (err,docs) {
        if(err)
            console.log(err);
        else {
                completeds = docs;
                completepending();
            }
    });
}




function completepending () {


    for (i = 0; i < companies.length; i++) {

        //    console.log("Completing Transactions of Company :");
        //    console.log(companies[i]);
        supply = parseInt(companies[i].supply);
        demand = 0;

        for (j = 0; j < pending_trans.length; j++) {
            if (companies[i].shortname == pending_trans[j].name) {
                if (pending_trans[j].type == "1") {
                    demand = demand + parseInt(pending_trans[j].quantity);
                }
                else {
                    supply = supply + parseInt(pending_trans[j].quantity);
                }

            }
        }

      //  console.log("Comapny Name :" + companies[i].shortname + "demand : " + demand + " supply :" + supply);

        oldprice = parseInt(companies[i].rate);

        newprice = oldprice + (0.05 * (demand - supply ));

        change = Math.abs(oldprice - newprice);
        percentage = (change / oldprice )*100;
        if (change / oldprice > 0.15) {
            if (demand > supply){
                newprice = 1.15 * oldprice;
                percentage = 15;
                }
            else {
                newprice = 0.85 * oldprice;
                percentage = 15;
            }
        }

       // console.log("Oldprice : " + oldprice + " Newprice :" + newprice);

        companies[i].rate = newprice;
        companies[i].previous = oldprice;
        companies[i].change = percentage;
       // console.log("Oldprice : " + companies[i].previous + " Newprice :" + companies[i].rate);
        inserthistory(companies[i]);

        for(j = 0 ; j < pending_trans.length ; j ++){
            if(pending_trans[j].name == companies[i].shortname){
                console.log("PENDING " + pending_trans[j]._id +" of company"+ pending_trans[j].name);
                for (k = 0 ; k < users.length ; k++){
                    if(pending_trans[j].userid == users[k]._id){
                        curruser = k;
                        break;
                    }
                }
                currcomp = -1;
                for(k = 0 ; k < completeds.length ; k ++){
                    if(pending_trans[j].userid == completeds[k].userid  && pending_trans[j].name == completeds[k].name){
                        currcomp = k;
                        break;
                    }
                }
                if(pending_trans[j].type == "1"){
                  //  console.log("BUY");
                    usercapacity = Math.min (pending_trans[j].quantity , users[curruser].balance / companies[i].previous );
                    if(currcomp != -1) {
                        completeds[currcomp].quantity += usercapacity;
                        completeds[currcomp].price = companies[i].rate;
                    }
                    else{
                        console.log ("Creating new");
                        completedt = completedm.create();
                        completedt.userid = pending_trans[j].userid;
                        completedt.quantity = usercapacity;
                        completedt.price = companies[i].rate;
                        completedt.name = companies[i].shortname;
                        completedt.longname = companies[i].longname;
                        completeds.push(completedt);
                    }
                    users[curruser].previous = users[curruser].balance;
                    users[curruser].balance -= usercapacity * companies[i].previous;
                }
                else {
                 //   console.log("SELL");
                    usercapacity =pending_trans[j].quantity;
                    if(currcomp != completeds.length) {
                        completeds[currcomp].quantity -= usercapacity;
                        completeds[currcomp].price = companies[i].rate;
                    }
                    users[curruser].previous = users[curruser].balance;
                    users[curruser].balance += usercapacity * companies[i].previous;
                }
            }
        }
    }
    updateallcomps();
    updateallcompd();
    updateallusers();
    removepending();
}

function removepending() {
    var pendingc = db.get('pending');
    pendingc.remove({},function (err,docs) {
    });
}
function updateallcompd(){
    var completedc = db.get("completed");
    for ( i = 0 ; i < completeds.length ; i ++){
        if(completeds[i]._id == null){
            completedc.insert(completeds[i],function (err,docs) {
                //console.log(docs);
            });
        }
        else
            completedc.update({"_id":completeds[i]._id},{$set:{"quantity":completeds[i].quantity,"price":completeds[i].price}});
    }
}

function updateallusers(){
    var userc = db.get("user");
    for ( i = 0 ; i < users.length ; i ++){
        userc.update({"_id":users[i]._id},{$set:{"previous":users[i].previous,"balance":users[i].balance}});
    }
}

function updateallcomps(){
    var companyc = db.get("company");
    for ( i = 0 ; i < companies.length ; i ++){
        console.log("Oldprice : " + companies[i].previous + " Newprice :" + companies[i].rate);
        companyc.update({"_id":companies[i]._id},{$set:{"rate":companies[i].rate,"previous":companies[i].previous,"change":companies[i].change}});
    }
}

function inserthistory(company) {
    var historyc = db.get('history');
    var history = historym.create();
    var d = new Date();
    history.time = d.getTime();;
    history.name = company.name;
    history.shortname = company.shortname;
    history.price = company.rate;
    historyc.insert(history,function (err,docs) {
    });
}


exports.recalculatenet = function () {
   // console.log("Called Networth Cal")
    
    var userc  = db.get("user");
    userc.find({},function (err,docs) {
       for(i=0;i<docs.length;i++){
        //   console.log("id:"+docs[i]._id);
            recal(docs[i]._id,docs[i].balance,users[i].og);
           var fcm = require("./fcmMessaging");
            fcm.sendmessage(docs[i].fcm);
       }
    });


    function recal(id1,bal,og){

        var net = bal;
        companyc.find({},function (err,comp) {
            completedc.find({},function (err,compl) {



//                console.log("recall");

                for(i=0;i<compl.length;i++){
                    for(j=0;j<comp.length;j++){
                        if(comp[j].shortname == compl[i].name && compl[i].userid == id1){
                         //   console.log("matchfound");
                         //   console.log("Completed ");
                         //   console.log(compl[i]);
                         //   console.log(comp[j]);
                            console.log(":::"+parseInt(compl[i].quantity) + ":::::"+ parseFloat(comp[j].rate));
                            console.log(":::"+parseInt(compl[i].quantity) * parseInt(comp[j].rate));
                            net  = net +( parseInt(compl[i].quantity) * parseFloat(comp[j].rate));
                            console.log("Net "+net);
                        }
                    }
                }
                console.log("Final Net"+net);
                userc.update({"_id": id1}, {$set: {"previous":og,"networth": net}}, function (err, docs) {});
            });
        })
    }
    
};