/**
 * Created by nidhi on 16/12/13.
 */
function company(){
    this.name = null
    this.shortname = null;
    this.rate = null;
    this.previous = null;
    this.change = null;
    this.supply = null;
}

exports.create =  function () {
    var instance = new company();
    return instance;
}