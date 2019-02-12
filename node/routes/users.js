var express = require('express');
var router = express.Router();
var DBService = require('../services/DBService');
var db = DBService.db;
var userm = require("../models/user");
var pendingm  =  require("../models/pending");

router.get('/', function(req, res, next) {
  res.send('Welcome To Ecell VSM');
});

//verification done
router.post('/register' , function (req,res,next){
  console.log("Registing User");
  var user = userm.create();
  docs = "Empty";
  user.username = req.body.username;
  user.password = req.body.password;
  user.networth = 5000;
  user.balance = 5000;
  user.verify = 1;    //change during event
  user.previous = 5000;
  var userc = db.get('user');
  userc.findOne({"username":req.body.username},function (err,docs) {
    if(docs == null){ //if docs is null that means user not presented
      userc.insert(user,function (err,docs) {
        if(err)
        {
          res.send({success:"0",error:"1"});//some db error
        }
        else {
          res.send({success: "1"}); //user created
        }
      });
    }
    else{
      res.send({success:"0",error:"2"});//user already present
    }
  }) ;
});

//verification done
router.post('/getuser',function (req,res,next) {
  id = req.body.id;
  var userc = db.get('user');
  userc.findOne({"_id":id},{fields:{password:0}}, function (err,docs) {
    if(docs == null)
    {
      res.send({success:"0",error:"1"});//some db error
    }
    else
    {
        res.send({success: "1", user: docs}); //got the user just return it to the app
    }

  })

});


//verification done
router.post('/login',function (req,res,next) {
  username = req.body.username; 
  password = req.body.password; 
  var userc = db.get('user');
  userc.findOne({"username":username,"password":password},{fields:{password:0}}, function (err,docs) {
    if(docs == null)
    {
      res.send({success:"0",error:"1"});//some db error
    }
    else
    {
      if(docs.verify == "0"){ 
        res.send({success:"0",error:"2"});//user not verified
      }
      else {
        res.send({success: "1", user: docs});//user was logged in
      }
    }

  })

});


//verfication done
router.post('/getallcompany',function (req,res,next) {
  var companyc = db.get('company');
  companyc.find({}, function (err,docs) {
    if(docs == null)
    {
      res.send({success:"0",error:"1"});//some db error
    }
    else
    {
        res.send({success: "1", company: docs});
    }
  })

});

//Verification Done
router.post('/leaderboard',function (req,res,next) {
  id = req.body.id;
  var userc = db.get('user');
  userc.find({"_id":id},function (err,docs1) {
    var networth = docs1[0].networth;
    console.log(docs1[0].networth);
    userc.find({networth:{$gt:networth},verify:1},function (err,docs2) {
      var rank = docs2.length + 1;
      userc.find({verify:1},{sort: {networth: -1},fields:{password:0,_id:0,verify:0},limit: 10}, function (err,docs) {
        if(docs == null)
        {
          res.send({success:"0",error:"1"});//some db error
        }
        else {
          res.send({success:"1","rank":rank,"networth":networth, user: docs});
        }
      });
    });
  });
  


});


//Verification done
router.post('/companydetails',function (req,res,next) {
  shortname = req.body.shortname;
  id = req.body.id;
  
  
  var hirtoryc = db.get('history');
  hirtoryc.find({"shortname":shortname},{limit: 6, sort : {time : -1 }}, function (err,docs) {
    if(docs == null)
    {
      res.send({success:"0",error:"11"});//some db error
    }
    else
    {
      docs.reverse();
      var completed = db.get('completed');
      completed.findOne({"name":shortname,"userid":id}, function (err,docs2) {
        if(err) {
          console.log(err);
          res.send({success: "0", error: "12"});//some db error
        }

           var companyc = db.get("company");
           companyc.findOne({"shortname":shortname},function (err,docs3) {
             res.send({success: "1",history: docs, completed: docs2,company:docs3});
           });
      });
    }

  });

});

//partially tested
router.post('/transactionload',function (req,res,next) {
  shortname = req.body.shortname;
  id = req.body.id;
  type = req.body.type;console.log("type"+ type);
  var userc = db.get("user");
  var completedc = db.get("completed");

    userc.findOne({"_id":id},function (err,docs){
    //  console.log(docs);
        if(err){
          res.send({success:"0",error:"11"});//some db error
        }
        else{ // we got the user
          var companyc = db.get("company"); // company for which the stocks are required
          companyc.find({},function (err,docs2) {
            if(err){
              console.log(err);
              res.send({success:"0",error:"12"});//some db error
            }
            else{
             // console.log(docs2);
              var historyc = db.get("history"); // 
              historyc.find({"name":shortname},{sort:{time:-1},limit:6},function(err,docs3){
                if(err){
                  console.log(err);
                  res.send({success:"0",error:"13"});//some db error
                }
                else{
                 // console.log(docs3);
                  completedc.findOne({"userid":id,"name":shortname},function (err,docs4) {
                    console.log(req.body);
                      var pendingc = db.get("pending");
                    pendingc.find({"userid":id,"type":type},function(err,docs5){
                      res.send({success:"1",company:docs2,user:docs,history:docs3,completed:docs4,pending:docs5});
                    });

                  });
                }

              });

            }
          });
        }
    });
});



//Completed verification
router.post('/transactionexecute',function (req,res,next) {
  var pendingc = db.get("pending");
  pendingc.findOne({"userid":req.body.userid,"name":req.body.shortname,"type":req.body.type},function (err,docs) {
    if(docs == null){
      pendingex(req,res);
    }
    else{
      res.send({success:"0",error:"Transaction already present with same user,company,type cancel that first"})
    }
  });

});


function pendingex(req,res){
  if(req.body.type == "1"){
    var userc = db.get("user");
    var companyc = db.get("company");
    var pendingc = db.get("pending");
    userc.find({"_id":req.body.id},function(err,user){
      if(err)
        console.log(err);
      if(user[0] == null){
        res.send("User Not Found");
      }
      else{
        companyc.find({"shortname":req.body.shortname},function (err,company) {
          if(err)
            console.log(err);
          pendingc.find({"userid":req.body.id,"name":req.body.shortname},function (err,pending) {
            quantity = 0;
            if(pending != null) {
              for (i = 0; i < pending.length; i++) {
                quantity = parseInt(pending[i].quantity) + quantity;
              }
            }
              console.log(company[0].rate);
              var estcost = parseInt(company[0].rate)*parseInt((quantity + parseInt(req.body.quantity)));
              console.log(user[0].balance + " >= " + estcost);
              if(parseInt(user[0].balance) >=  estcost){//user Allowed
                pendingdo(req,res);
              }
              else{
                 res.send({success:0,error:"User Cannot Buy if he doesnt have enough money"})
              }
          });
        });
      }
    });
  }
  else{

    var completedc = db.get("completed");

    completedc.find({"userid":req.body.id,"name":req.body.shortname},function (err,comp) {
      if(err) {
        console.log(err);
      }
      else {
        console.log(comp);

        var pendingc = db.get("pending");
          pendingc.findOne({"userid":req.body.id,"name":req.body.shortname,"type":"2"},function (err,pend){
              if(pend == null)
                  totalquant = 0;
              else
                  totalquant = parseInt(pend.quantity);
              console.log("Pending "+( totalquant+ parseInt( req.body.quantity)  ));
              if ( comp[0] == null || comp[0].quantity < ( totalquant+  parseInt(req.body.quantity)) ) {
                  res.send({success:0,error:"User cannot sell what he doesn't have"});
              }
              else {
                  pendingdo(req,res)
              }
           

          });

      }
    });


  }
}

function pendingdo(req,res)
{
  var pending = pendingm.create();
  pending.name = req.body.shortname;
  pending.userid = req.body.id;
  pending.type = req.body.type;
  pending.quantity = req.body.quantity;
  pending.longname = req.body.longname;
  var pendingc = db.get("pending");
  pendingc.find({"userid":pending.userid,"name":pending.name,"type":pending.type},function (err,docs) {
    if(err)
    {
      res.send({success:"0",error:"1"});//some db error
    }
    else {
      if(docs[0]==null ){
        console.log("previous  not found");
        pendingc.insert(pending,function (err,docs) {
          if(err)
          {
            res.send({success:"0",error:"1"});//some db error
          }
          else {
            res.send({success: "1"});
          }
        });
      }
      else{
        var temp = parseInt(docs[0].quantity) + parseInt(pending.quantity) ;
        console.log(temp);
        pendingc.update({"userid":pending.userid,"name":pending.name,"type":pending.type}, {$set: {"quantity":temp}}, function (err, docs) {
          if (err) {
            res.send({success: "0",error:"1"});//some db error
          }
          else {
            res.send({success: "1"});
          }
        });
      }
    }
  });
}

//Completed Verification
router.post('/pending',function (req,res,next) {
  //shortname = req.body.shortname;
  id = req.body.id;
  var pendingc = db.get('pending');
  pendingc.find({"userid":id}, function (err,docs) {
    if(docs == null)
    {
      res.send({success:"0",error:"1"});//some db error
    }
    else
    {
        res.send({success: "1", pending: docs});
    }

  })

});

router.post('/cancelpend',function (req,res,next) {
  //shortname = req.body.shortname;
  id = req.body.id;
  var pendingc = db.get('pending');
  pendingc.remove({"_id":id}, function (err,docs) {
    if(err == null)
    {
      res.send({success:"1"});//some db error
    }
    else
    {
      res.send({success: "0"});
    }

  })

});


router.post('/addfcm',function (req,res,next) {
  userid = req.body.id;
  fcm = req.body.token;
  var  userc = db.get("user");
  userc.update({"_id": userid}, {$set: {"fcm":fcm}}, function (err, docs) {
    if(err){
      res.send({"success":"0"});
    }
    res.send({"success":"1"});
  });
});


router.post('/completed',function(req,res,next){
  userid = req.body.id;
  var completedc = db.get('completed');
  var companyc = db.get('company');
  var userc = db.get('user');
  completedc.find({"userid":req.body.id},function (err,docs) {
      if(err)
          console.log(err);
      else{
        companyc.find({},function (err,docs2) {
          userc.findOne({"_id":userid},function (err,docs3) {
            res.send({"company":docs2,"completed":docs,"user":docs3});
          });

        });


      }

  });
});

router.get('/begin',function(res,res,next){
  var beginc = db.get("begin");
  beginc.findOne({},function(err,docs){
    console.log(docs);
    res.send({"begin":docs.begin});
  });
});





module.exports = router;
