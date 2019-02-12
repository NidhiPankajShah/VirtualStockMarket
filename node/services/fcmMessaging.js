/**
 * Created by nidhi on 20/12/13.
 */
var FCM = require('fcm-node');
exports.sendmessage= function (token) {
    serverKey = "AAAAU77e4Gk:APA91bFgPGlCTxdRoFEeXbEVtKe21n4YvBlBT5V-2iQR1DijYjZk1caxQIwYT-yuGgum0c0DWbZGC7F--tEQp2G-DjxS9s2y6av6oQQfe67sbMrmPXdPwmq1YZgtxCG_9iLbQeN2Gl983LxZfpXILVS4V9gscCS23w";
    var fcm = new FCM(serverKey);
    var message = { //this may vary according to the message type (single recipient, multicast, topic, et cetera)
        to: token,
        notification: {
            title: 'Market Price Updated',
            body: 'All your pending transactions are completed'
        }
    };

    fcm.send(message, function(err, response){
        if (err) {
            console.log("Something has gone wrong!");
        } else {
            console.log("Successfully sent with response: ", response);
        }
    });
};
