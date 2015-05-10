/*
 * Comment added by Vahid Hedayati to:
 * 
 * Highlight that all thanks should go to Felix Hagspiel
 * https://github.com/felixhagspiel/webrtc-tutorial/tree/development
 * http://blog.felixhagspiel.de/index.php/posts/create-your-own-videochat-application-with-html-and-javascript
 * Amazing work dude.
 * Only change to this was to add firefox calls which are currently still not working under firefox.
 */
function WebRTC() {

	/*
	* 	Private Attributes
	*/
	
	var that = this;
	var connection = false;
	var roomId = false; // here is the room-ID stored
	var myStream= false; // my media-stream
	var otherStream= false; // other guy`s media-stream
	var peerConnection = false; // RTCPeerconnection
	//var peerConfig= JSON.stringify(getIcServers());
	
	// VH: Hack to call getIceServers function exists in webrtc gsps.
	var peerConfig=JSON.parse(getIceServers().replace(/&quot;/g,'"'));
	//console.log(peerConfig);
	// if nothing found default to googles stun server
	//console.log(peerConfig);
	if (!peerConfig.iceServers) {
		peerConfig =   {iceServers: [{url: 'stun:stun.l.google.com:19302'}] };  // set Google Stunserver
		//console.log(peerConfig);
	}	
	
	var peerConstraints = {"optional": [{"DtlsSrtpKeyAgreement": true}]}; // set DTLS encrpytion
    var otherSDP = false;
    var othersCandidates = []; // other guy's icecandidates

	// via this element we will send events to the view
	var socketEvent = document.createEvent('Event');
    	socketEvent.initEvent('socketEvent',true,true);

    // VH Hack to disable webrtc	
    this.close=function() {
    	myStream.stop();
    	connection.close();
    }	
    	
    	
	/*
	* 	Private Methods
	*/

    	
    	
	// encode to JSON and send data to server
	var sendToServer = function(data) {
		try {
			connection.send(JSON.stringify(data));
			return true;
		} catch(e) {
			console.log('There is no connection to the websocket server');
			return false;
		}
	};
	

    // create ice-candidate
    var createRTCIceCandidate = function(candidate){
            var ice;
            if( typeof(webkitRTCIceCandidate) === 'function') {
                ice = new webkitRTCIceCandidate(candidate);
            }
            else if( typeof(RTCIceCandidate) === 'function') {
                ice = new RTCIceCandidate(candidate);
            }
            else if( typeof(mozRTCIceCandidate) === 'function') {
                ice = new mozRTCIceCandidate(candidate);
            }
            return ice;
    };

    // create an session description object
	var createRTCSessionDescription = function(sdp){
	    var newSdp;
		console.log(sdp);
	    if( typeof(RTCSessionDescription) === 'function') {
	        newSdp = new RTCSessionDescription(sdp);
	    }
	    else if( typeof(webkitRTCSessionDescription) === 'function') {
	        newSdp = new webkitRTCSessionDescription(sdp);
	    }
	    else if (typeof(mozRTCSessionDescription) === 'function') {
	    	newSdp = new mozRTCSessionDescription(sdp);
	    }
	    return newSdp;
	};
    // set or save the icecandidates
    var setIceCandidates = function(iceCandidate) {
    	// push icecandidate to array if no SDP of other guys is available
        if(!otherSDP) {
            othersCandidates.push(iceCandidate);
        }
        // add icecandidates immediately if not Firefox & if remoteDescription is set
        if(otherSDP &&
                iceCandidate.candidate &&
                iceCandidate.candidate !== null ) {
            peerConnection.addIceCandidate(createRTCIceCandidate(iceCandidate.candidate));
        }
    };

    // exchange of connection info is done, set SDP and ice-candidates
    var handshakeDone = function(){
        peerConnection.setRemoteDescription(createRTCSessionDescription(otherSDP));
		// add other guy's ice-candidates to connection
        for (var i = 0; i < othersCandidates.length; i++) {
            if (othersCandidates[i].candidate) {
                peerConnection.addIceCandidate(ceateRTCIceCandidate(othersCandidates[i].candidate));
            }
        }
		// fire event
		socketEvent.eventType = 'p2pConnectionReady';
		document.dispatchEvent(socketEvent);
    };

    // create an offer for an peerconnection
    var createOffer = function() {

    	// create new peer-object
        if( typeof(RTCPeerConnection) === 'function') {
            peerConnection = new RTCPeerConnection(peerConfig);
        }
        else if( typeof(webkitRTCPeerConnection) === 'function') {
            peerConnection = new webkitRTCPeerConnection(peerConfig);
        }
        else if( typeof(mozRTCPeerConnection) === 'function') {
      	  peerConnection = new mozRTCPeerConnection(peerConfig);
      	}

    	// add media-stream to peerconnection
    	peerConnection.addStream(myStream);

    	// other side added stream to peerconnection
    	peerConnection.onaddstream = function(e) {
    		console.log('other guys stream added');
    		otherStream = e.stream;
			// fire event
			socketEvent.eventType = 'streamAdded';
			document.dispatchEvent(socketEvent);
    	};

    	// we receive our icecandidates and send them to the other guy
    	peerConnection.onicecandidate = function(icecandidate) {
    		console.log('icecandidate send to room '+roomId);
    		// send candidates to other guy
			var data = {
				type: 'iceCandidate',
				roomId: roomId,
				payload: icecandidate
			};
			sendToServer(data);
    	};

    	// we actually create the offer
    	peerConnection.createOffer(function(SDP){
    		// set our SDP as local description
    		peerConnection.setLocalDescription(SDP);
    		console.log('sending offer to: '+roomId);
    		// send SDP to other guy
    		
			var data = {
				type: 'offer',
				roomId: roomId,
				payload: SDP
			};
			sendToServer(data);
    	});
    };

    // create an answer for an received offer
    var createAnswer = function() {
    	// create new peer-object
        if( typeof(RTCPeerConnection) === 'function') {
            peerConnection = new RTCPeerConnection(peerConfig);
        }
        else if( typeof(webkitRTCPeerConnection) === 'function') {
            peerConnection = new webkitRTCPeerConnection(peerConfig);
        }
        else if( typeof(mozRTCPeerConnection) === 'function') {
        	peerConnection = new mozRTCPeerConnection(peerConfig);
        }

    	// add media-stream to peerconnection
    	peerConnection.addStream(myStream);

    	// set remote-description
    	peerConnection.setRemoteDescription(createRTCSessionDescription(otherSDP));

    	// other side added stream to peerconnection
    	peerConnection.onaddstream = function(e) {
    		console.log('stream added');
    		otherStream = e.stream;
			// fire event
			socketEvent.eventType = 'streamAdded';
			document.dispatchEvent(socketEvent);
    	};

    	// we receive our icecandidates and send them to the other guy
    	peerConnection.onicecandidate = function(icecandidate) {
    		console.log('icecandidate send to room '+roomId);
    		// send candidates to other guy
			var data = {
				type: 'iceCandidate',
				roomId: roomId,
				payload: icecandidate
			};
			sendToServer(data);
    	};

    	// we create the answer
    	peerConnection.createAnswer(function(SDP){
    		// set our SDP as local description
    		peerConnection.setLocalDescription(SDP);

    		// add other guy's ice-candidates to connection
            for (var i = 0; i < othersCandidates.length; i++) {
                if (othersCandidates[i].candidate) {
                    peerConnection.addIceCandidate(ceateRTCIceCandidate(othersCandidates[i].candidate));
                }
            }

    		// send SDP to other guy
			var data = {
				type: 'answer',
				roomId: roomId,
				payload: SDP
			};
			sendToServer(data);
    	});
    };

	/*
	* 	Public Methods
	*/

	// this function handles all the websocket-stuff
	this.connectToSocket = function(wsUrl){
		// open the websocket
		connection = new WebSocket(wsUrl);

		// connection was successful
		connection.onopen = function(event){
			console.log((new Date())+' Connection successfully established');
		};

		// connection couldn't be established
		connection.onerror = function(error){
			console.log((new Date())+' WebSocket connection error: ');
			console.log(error);
		};

		// connection was closed
		connection.onclose = function(event){
			console.log((new Date())+' Connection was closed');
		};

		// this function is called whenever the server sends some data
		connection.onmessage = function(message){
            try {
                var data = JSON.parse(message.data);
            } catch (e) {
                console.log('This doesn\'t look like a valid JSON or something else went wrong.');
                console.log(message);
                return;
            }
            switch( data.type ) {
            	// the server has created a room and returns the room-ID
            	case 'roomCreated':
            		// set room
            		roomId = data.payload;

            		// fire event
            		socketEvent.eventType = 'roomCreated';
            		document.dispatchEvent(socketEvent);
            	break;
            	// other guy wants to join our room
            	case 'offer':
            		console.log('offer received, answer will be created');
            		otherSDP = data.payload;
            		createAnswer();
            	break;
            	// we receive the answer
            	case 'answer':
            		console.log('answer received, connection will be established');
            		otherSDP = data.payload;
            		handshakeDone();
            	break;
            	// we receive icecandidates from the other guy
            	case 'iceCandidate':
            		setIceCandidates(data.payload);
            	break;
            }
		};
	};

	this.getRoomId = function(){
		return roomId;
	};

	// this function tells the server to create a new room
	this.createRoom = function() {
		// create data-object
		var data = {
			type: 'createRoom',
			payload: false
		};
		// send data-object to server
		return sendToServer(data);
	};
	// connect to a room
	this.joinRoom = function(id){
		roomId = id;
		createOffer();
	};
	
	
	
	// get the video & audio-stream
	this.getMedia = function(constraints,success) {
		// set default constraints if none passed
		if(!constraints) {
			constraints = {audio: true, video: true};
		}
		// check prefix
        if(navigator.getUserMedia) {
        	console.log('prefix-less');
            getUserMedia = navigator.getUserMedia.bind(navigator);
        }
        else if(navigator.webkitGetUserMedia) {
        	console.log('webkit');
            getUserMedia = navigator.webkitGetUserMedia.bind(navigator);
        }
        else if(navigator.mozGetUserMedia) {
        	console.log('mozilla');
        	getUserMedia = navigator.mozGetUserMedia.bind(navigator);
        }
        
        // call getUserMedia
        getUserMedia(constraints, function (stream) {
        	// set stream
            myStream = stream;
            // call success-callback
            if(success) {
            	success(myStream);
            }
        }, function(e){
            console.log("GetUserMediaFailed: "+e);
           // if(fail) {
             //   fail();
            //}
        });
	};

	// get the other guys media stream
	this.getOtherStream = function(){
		return otherStream;
	};
}
