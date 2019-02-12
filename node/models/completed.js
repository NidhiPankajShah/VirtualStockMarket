/**
 * Created by nidhi on 16/12/13.
 */
function completed(){
    this.userid = null;
    this.name = null;
    this.longname = null;
    this.quantity = null;
    this.price = null;
}

exports.create =  function () {
    var instance = new completed();
    return instance;
}