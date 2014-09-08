<!DOCTYPE html>
<html>
<head>

<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<g:render template="/assets"/>
</g:if>
<g:else>
	<g:render template="/resources"/>
</g:else>
   <title>${chatTitle }</title>
</head>
<body>

 <div id="pageHeader">
     <video id="live" width="320" height="240" autoplay="autoplay"  style="display: inline;"></video>
         <canvas width="320" id="canvas" height="240" style="display: inline;"></canvas>
       </div>

<g:javascript>
$(document).ready(function() {

	if (!window.WebSocket) {
		var msg = "Your browser does not have WebSocket support";
		$("#pageHeader").html(msg);
	}
	
	var	webSocket = new WebSocket("ws://${hostname}/${meta(name:'app.name')}/WsCamEndpoint/${user}/${user}");
	 webSocket.onclose=function(message) {processClose(message);};
	 //webSocket.onmessage=function(message) {processChatMessage(message);	};
	var video = $("#live").get()[0];
	var canvas = $("#canvas");
	var ctx = canvas.get()[0].getContext('2d');
	var options = {
			"video" : true,
			audio:true
	};


	function convertToBinary (dataURI) {
		// convert base64 to raw binary data held in a string
		// doesn't handle URLEncoded DataURIs
		var byteString = atob(dataURI.split(',')[1]);
		// separate out the mime component
		var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0]
		// write the bytes of the string to an ArrayBuffer
		var ab = new ArrayBuffer(byteString.length);
		var ia = new Uint8Array(ab);
		for (var i = 0; i < byteString.length; i++) {
			ia[i] = byteString.charCodeAt(i);
		}

		// write the ArrayBuffer to a blob, and you're done
		var bb = new Blob([ab]);
		return bb;
	}   

	function hasGetUserMedia() {
		// Note: Opera is unprefixed.
		return !!(navigator.getUserMedia || navigator.webkitGetUserMedia ||
				navigator.mozGetUserMedia || navigator.msGetUserMedia);
	}


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
						
					}, function(err) {
						console.log("Unable to get video stream!")
					});
					webSocket.onopen = function () {
						console.log("Openened connection to websocket");
					}

					timer = setInterval(
							function () {
								ctx.drawImage(video, 0, 0, 320, 240);
								var data = canvas.get()[0].toDataURL('image/jpeg', 1.0);
								newblob = convertToBinary(data);
								webSocket.send(newblob);
							}, 250);
			}
		}
	
	
	  window.onbeforeunload = function() {
       	webSocket.send("DISCO:-"+user);
       	webSocket.onclose = function() { }
       	webSocket.close();
     }
});
</g:javascript>
</body>
</html>