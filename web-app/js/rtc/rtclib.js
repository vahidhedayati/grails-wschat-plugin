/*
 * Taken from WebRTC BluePrints
 * https://github.com/fycth/WebRTC-Blueprints
 * 
 */

    var RTCPeerConnection = null;
    var webrtcDetectedBrowser = null;

    var room = null;
    var initiator;

    var pc = null;
    var signalingURL;
    var isSender;

    var sendDChannel = null;
    var recvDChannel = null;

    var channelReady;
    var channel;

    var pc_constraints = {"optional": [{RtpDataChannels: true}]};
    var data_constraint = {reliable :false};

    var pc_config = {"iceServers":
       [{url:'stun:23.21.150.121'},
        {url:'stun:stun.l.google.com:19302'}]};

    function myrtclibinit(sURL, sender) {
        signalingURL = sURL;
        isSender = sender;
        initWebRTCAdapter();
        if (webrtcDetectedBrowser === 'firefox' ||
            (webrtcDetectedBrowser === 'chrome' && webrtcDetectedVersion >= 31)) {
            pc_constraints = null;
            data_constraint = null;
        }

        openChannel();
    };

    function openChannel() {
        channelReady = false;
        channel = new WebSocket(signalingURL);
        channel.onopen = onChannelOpened;
        channel.onmessage = onChannelMessage;
        channel.onclose = onChannelClosed;
    };

    function onChannelOpened() {
        channelReady = true;
        createPeerConnection();
        if (isSender=='false') {        	
            sendMessage({"type" : "ENTERROOM", "value" : getUser1()});
            initiator = true;
            doCall();
        } else {
            sendMessage({"type" : "GETROOM", "value" : ""});
            initiator = false;
        }
    };

    function onChannelMessage(message) {
        processSignalingMessage(message.data);
    };

    function onChannelClosed() {
        channelReady = false;
    };

    function sendMessage(message) {
        var msgString = JSON.stringify(message);
        channel.send(msgString);
    };

    function processSignalingMessage(message) {
        var msg = JSON.parse(message);
        if (msg.type === 'offer') {
            pc.setRemoteDescription(new RTCSessionDescription(msg));
            doAnswer();
        } else if (msg.type === 'answer') {
            pc.setRemoteDescription(new RTCSessionDescription(msg));
        } else if (msg.type === 'candidate') {
            var candidate = new RTCIceCandidate({sdpMLineIndex:msg.label, candidate:msg.candidate});
            pc.addIceCandidate(candidate);
        } else if (msg.type === 'GETROOM') {
            room = msg.value;
            OnRoomReceived(room);
        } else if (msg.type === 'WRONGROOM') {
            window.location.href = "/";
        }
    };

    function createPeerConnection() {
        try {
            pc = new RTCPeerConnection(pc_config, pc_constraints);
            pc.onicecandidate = onIceCandidate;
            pc.ondatachannel = recvChannelCallback;
        } catch (e) {
            console.log(e);
            pc = null;
            return;
        }
    };

    function createDataChannel(role) {
        try {
            sendDChannel = pc.createDataChannel("datachannel_"+room+role, data_constraint);
        } catch (e) {
            console.log('error creating data channel ' + e);
            return;
        }
        sendDChannel.onopen = onSendChannelStateChange;
        sendDChannel.onclose = onSendChannelStateChange;
    }

    function onIceCandidate(event) {
        if (event.candidate)
            sendMessage({type: 'candidate', label: event.candidate.sdpMLineIndex, id: event.candidate.sdpMid,
                candidate: event.candidate.candidate});
    };

    function failureCallback(e) {
        console.log("failure callback "+ e.message);
    }

    function doCall() {
        createDataChannel("caller");
        pc.createOffer(setLocalAndSendMessage, failureCallback, null);
    };

    function doAnswer() {
        pc.createAnswer(setLocalAndSendMessage, failureCallback, null);
    };

    function setLocalAndSendMessage(sessionDescription) {
        sessionDescription.sdp = bandwidthHack(sessionDescription.sdp);
        pc.setLocalDescription(sessionDescription);
        sendMessage(sessionDescription);
    };

    function bandwidthHack(sdp) {
        // FireFox doesn't support this
        if (webrtcDetectedBrowser === 'firefox') return sdp;

        sdp = sdp.replace( /b=AS([^\r\n]+\r\n)/g , '');
        sdp = sdp.replace( /a=mid:data\r\n/g , 'a=mid:data\r\nb=AS:1638400\r\n');

        return sdp;
    };

    function sendDataMessage(data) {
        sendDChannel.send(data);
    };

    function onSendChannelStateChange() {
        console.log('Send channel state is: ' + sendDChannel.readyState);
        if (sendDChannel.readyState === 'open') sendDChannel.onmessage = onReceiveMessageCallback;
    }

    function recvChannelCallback(evt) {
        console.log('Receive Channel Callback');
        recvDChannel = evt.channel;
        recvDChannel.onmessage = onReceiveMessageCallback;
        recvDChannel.onopen = onReceiveChannelStateChange;
        recvDChannel.onclose = onReceiveChannelStateChange;
    }

    function onReceiveChannelStateChange() {
        console.log('Receive channel state is: ' + recvDChannel.readyState);
        if (recvDChannel.readyState === 'open') sendDChannel = recvDChannel;
    }

    function onReceiveMessageCallback(event) {
        try {
            var msg = JSON.parse(event.data);
            if (msg.type === 'file')
            {
                onFileReceived(msg.name, msg.size, msg.data);
            }
        }
        catch (e) {}
    };
