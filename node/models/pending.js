/**
 * Created by nidhi on 18/12/13.
 */
function pending(){
    this.userid = null;
    this.name = null;
    this.longname = null;
    this.type = null;
    this.quantity = null;
}

exports.create =  function () {
    var instance = new pending();
    return instance;
}