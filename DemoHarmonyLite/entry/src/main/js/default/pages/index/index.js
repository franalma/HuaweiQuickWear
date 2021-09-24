import { P2pClient, Message, Builder } from '../wearengine/wearengine.js'

var peerFinger = "32046831FF092BE587724346B280A8651E1C242E424CEF60F50F912CCD85004A";
var p2pClientSend = new P2pClient();
var p2pClientRecv= new P2pClient();
var peerPkgName = "com.fam.wear.service";
var isInit = false;

initInternal();

export default {
    data: {
        title: 'DTSEs',
        info: "",
    },
    findPhone() {
        console.log("Find phone");
        findPhoneInternal();
    },
    foundPhone() {
        console.log("Stop phone");
        foundPhoneInternal();
    },
    startReception() {
        var flash = this;
        p2pClientRecv.setPeerPkgName(peerPkgName);
        p2pClientRecv.setPeerFingerPrint(peerFinger);

        var receiver = {
            onSuccess: function () {},
            onFailure: function () {},
            onReceiveMessage: function (input) {
                flash.in
                flash.info = input;
                console.log("Data received: " + input);
            },
        }
        p2pClientRecv.registerReceiver(receiver)
    },
}

function initInternal(){

    p2pClientSend.setPeerPkgName(peerPkgName);
    p2pClientSend.setPeerFingerPrint(peerFinger);

    // Step 3: Check that your app has been installed on the phone.
    p2pClientSend.ping({
        onSuccess: function() {
            console.log('ping success.');
        },
        onFailure: function() {
            console.log('ping failed');
        },
        onPingResult: function(resultCode) {
            console.log("Ping result: "+resultCode.data + resultCode.code);
            isInit = true;
        },
    });
}

function sendMessage(data) {
    if (!isInit){
        console.log("Not possible to send message. Not init");
        return;
    }

    var builderClient = new Builder();
    var messageStr = data;
    builderClient.setDescription(messageStr);
    // Build a Message object
    var message = new Message();
    message.builder = builderClient;
    // Define the callback function.
    var sendCallback = {
        onSuccess: function () {
            // Related processing logic for your app after the send task is successfully executed.
            console.log("Send data success");
        },
        onFailure: function () {
            // Related processing logic for your app after the send task fails.
            console.log("send data failed");
        },
        onSendResult: function (resultCode) {
            // Process the result code and information returned after the send task is complete.
            console.log("Send data result. "+"Data: "+resultCode.data +" code:"+ resultCode.code);
        }
    }
    p2pClientSend.send(message, sendCallback);
}

function findPhoneInternal(){

    sendMessage("1")
}

function foundPhoneInternal(){
    sendMessage("0");
}


