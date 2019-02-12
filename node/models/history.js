function history() {
    this.time = null;
    this.name = null;
    this.shortname = null;
    this.price = null;
}

exports.create =  function () {
    var instance = new history();
    return instance;
}