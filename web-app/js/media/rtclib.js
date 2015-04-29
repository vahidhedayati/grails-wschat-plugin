/*
 * Taken from WebRTC BluePrints
 * https://github.com/fycth/WebRTC-Blueprints
 * used for media streaming aspect of this plugin
 * 
 */
    var RTCPeerConnection = null;
    var webrtcDetectedBrowser = null;

    var getUserMedia = null;
    var attachMediaStream = null;
    var reattachMediaStream = null;
    var localStream;
    var remoteStream;
    var remoteVideo;

    var chunkSize = 1200;

    var room = null;
    var initiator;

    var pc = null;
    var signalingURL;
    var isSender;
    var sendDChannel = null;
    var recvDChannel = null;

    var channelReady;
    var channel;

    var videoScreen;

    var pc_constraints = {"optional": [{RtpDataChannels: true}]};
    var data_constraint = {reliable :false};

    var pc_config = {"iceServers":
       [{url:'stun:23.21.150.121'},
        {url:'stun:stun.l.google.com:19302'}]};

    var receiverBuffer = null;
    var recvMediaSource = null;

    function myrtclibinit(sURL, rv, sender) {
        signalingURL = sURL;
        initWebRTCAdapter();
        isSender = sender;
        if (webrtcDetectedBrowser === 'firefox' ||
            (webrtcDetectedBrowser === 'chrome' && webrtcDetectedVersion >= 31)) {
            pc_constraints = null;
            data_constraint = null;
        }

        remoteVideo = rv;

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
    //    if(location.search.substring(1,5) == "room") {
      //      room = location.search.substring(6);
            sendMessage({"type" : "ENTERROOM", "value" : getUser1()});
            initiator = true;
        } else {
            sendMessage({"type" : "GETROOM", "value" : ""});
            initiator = false;
        }
        doGetUserMedia();
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

    // for screen casting audio HAVE TO be false
    function doGetUserMedia() {
        var constraints = {"audio": false, "video": {"mandatory": {chromeMediaSource: 'screen'}, "optional": []}};
        try {
            getUserMedia(constraints, onUserMediaSuccess,
                function(e) {
                    console.log("getUserMedia error "+ e.toString());
                });
        } catch (e) {
            console.log(e.toString());
        }
    };

    function onUserMediaSuccess(stream) {
        localStream = stream;
        pc.addStream(localStream);

        if (initiator) doCall();
    };

    function createPeerConnection() {
        try {
            pc = new RTCPeerConnection(pc_config, pc_constraints);
            pc.onicecandidate = onIceCandidate;
            pc.ondatachannel = recvChannelCallback;
            pc.onaddstream = onRemoteStreamAdded;
        } catch (e) {
            console.log(e);
            pc = null;
            return;
        }
    };

    function onRemoteStreamAdded(event) {requestAnimationFrame
        attachMediaStream(remoteVideo, event.stream);
        remoteStream = event.stream;
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
        pc.createOffer(setLocalAndSendMessage, failureCallback, errorCallBack);
    };

    function doAnswer() {
        pc.createAnswer(setLocalAndSendMessage, failureCallback, errorCallBack);
    };

    function errorCallBack(e) {
        console.log("Something is wrong: " + e.toString());
    };

    function setLocalAndSendMessage(sessionDescription) {
        pc.setLocalDescription(sessionDescription);
        sendMessage(sessionDescription);
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
           // console.log("Msg is "+msg);
            if (msg.type === 'streaming_proposed') {
                doReceiveStreaming();
            }
            else if (msg.type === 'chunk') {
                onChunk(msg.data);
            }
        }
        catch (e) {}
    };

    function doStreamMedia(fileName) {
        var msg = JSON.stringify({"type" : "streaming_proposed"});
        sendDataMessage(msg);

        var fileReader = new window.FileReader();
        fileReader.onload = function (e) {
            startStreaming(new window.Blob([new window.Uint8Array(e.target.result)]));
        };
        fileReader.readAsArrayBuffer(fileName);

        function startStreaming(blob) {
            if(!blob) return;
            var size = blob.size;
            var startIndex = 0;
            var addition = chunkSize;

            function netStreamer() {

                fileReader = new window.FileReader();
                fileReader.onload = function (e) {
                    var chunk = new window.Uint8Array(e.target.result);
                    pushChunk(chunk);

                    startIndex += addition;
                    if (startIndex <= size) window.requestAnimationFrame(netStreamer);
                    else pushChunk({end: true});
                };
                fileReader.readAsArrayBuffer(blob.slice(startIndex, startIndex + addition));
            }

            netStreamer();
        }
    };

    function doReceiveStreaming() {
        recvMediaSource = new MediaSource();
        recvMediaSource.addEventListener('sourceopen', function (e) {
            receiverBuffer = recvMediaSource.addSourceBuffer('video/webm; codecs="vorbis,vp8"');
        	//receiverBuffer = recvMediaSource.addSourceBuffer('video/mp4;codecs="avc1.4d001e,mp4a.40.2"');
            console.log('media source state: ', this.readyState);
        }, false);

        recvMediaSource.addEventListener('sourceended', function (e) {
            console.log('media source state: ', this.readyState);
        }, false);

        videoScreen.src = window.URL.createObjectURL(recvMediaSource);
    };

    function doAppendStreamingData(data) {
        var uint8array = new window.Uint8Array(data);
        receiverBuffer.appendBuffer(uint8array);

        if (videoScreen.paused) videoScreen.play();
    };

    function doEndStreamingData() {
        recvMediaSource.endOfStream();
    };

    function pushChunk(data) {
        var msg = JSON.stringify({"type" : "chunk", "data" : Array.apply(null, data)});
        sendDataMessage(msg);
    };

    function onChunk(data) {
        if (data.end) doEndStreamingData();
        else doAppendStreamingData(new Uint8Array(data));
    };


    // firefox
    // about:config
    // media.mediasource.enabled = true

    // screen casting
    // for Chrome
    // chrome://flags
    // enable screen capture support in getusermedia()
    // HTTPS is MANDATORY for screen casting
    // Chrome will ask 'Do you want <web site name> to share your screen? - say YES
    // firefox show what shared by chrome but FF doesn't share crseen itself (transmits just video from cam)



