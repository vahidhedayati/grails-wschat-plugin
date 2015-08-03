function WebCam() {

	/*
	* 	Private Attributes
	*/
	
	var that = this;
	var connection = false;
	var localStream;
	var video = $("#live").get()[0];
	var canvas = $("#canvas");
	var ctx = canvas.get()[0].getContext('2d');
	var options = {
			"video" : true,
			"audio":false
	};
	
	this.close=function() {
		video.src="";
		video.pause();
		localStream.stop();
		connection.close();
		
	};
	
	this.send = function(msg){
		connection.send(msg);
	};
	
	var runcam=true;
	// this function handles all the websocket-stuff for receiving cam
	this.getFromSocket = function(wsUrl){
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
		// Message
		connection.onmessage = function(msg) {
			var target = document.getElementById("target");
			url = window.URL.createObjectURL(msg.data);
			target.onload = function() {
			window.URL.revokeObjectURL(url);
			};
			target.src = url;
		}
	};	
	
	// this function handles all the websocket-stuff for outgoing cam
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
			runcam=false;
			connection.send("DISCO:-");
			console.log((new Date())+' Connection was closed');
		};
		
		
		
		if (hasGetUserMedia()) {
			if (typeof video !== 'undefined') {
				window.URL = window.URL || window.webkitURL;
				navigator.getUserMedia = (navigator.getUserMedia ||
						navigator.webkitGetUserMedia ||
						navigator.mozGetUserMedia ||
						navigator.msGetUserMedia);
				// use the chrome specific GetUserMedia function
				navigator.getUserMedia(options, function(stream) {
					video.src = window.URL.createObjectURL(stream);
					localStream=stream;
					if (runcam==true) {
						timer = setInterval(
							function () {
								if (ctx) {
									ctx.drawImage(video, 0, 0, 320, 240);
									var data = canvas.get()[0].toDataURL('image/jpeg', 1.0);
									newblob = convertToBinary(data);
									if (runcam==true) {
										connection.send(newblob);
									}
								}
							}, 250);
					}
				}, function(err) {
					console.log("Unable to get video stream!")
				});
				
				
			} 
		}
	};
}	
	
function WebCamRec() {
	/*
	* 	Private Attributes
	*/
	
	var that = this;
	var connection = false;
	var localStream;
	var video = $("#live").get()[0];
	var canvas = $("#canvas");
	var ctx = canvas.get()[0].getContext('2d');
	var options = {
			"video" : true,
			"audio":false
	};
	
	this.close=function() {
		video.src="";
		video.pause();
		localStream.stop();
		connection.close();
		
	};
	
	this.send = function(msg){
		connection.send(msg);
	};
	
	// this function handles all the websocket-stuff for receiving cam
	this.getFromSocket = function(wsUrl){
		// open the websocket
		connection = new WebSocket(wsUrl);
		// connection was successful
		connection.onopen = function(event){
			console.log((new Date())+' Connection successfully established to '+wsUrl);
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
		// Message
		connection.onmessage = function(msg) {
			console.log('msg'+msg);
			var target = document.getElementById("target");
			url = window.URL.createObjectURL(msg.data);
			target.onload = function() {
			window.URL.revokeObjectURL(url);
			};
			target.src = url;
		}
	};	

}