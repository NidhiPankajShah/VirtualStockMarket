/**
 * Created by nidhi on 19/12/13.
 */
function user(){
    this.username = null;
    this.password = null;
    this.networth = null;
    this.previous = null;
    this.balance = null;
    this.verify = null; 
}

exports.create =  function () {
    var instance = new user();
    return instance;
}