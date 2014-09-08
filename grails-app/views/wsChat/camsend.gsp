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
 <div>
     <video id="live" width="320" height="240" autoplay="autoplay"  style="display: inline;"></video>
         <canvas width="320" id="canvas" height="240" style="display: inline;"></canvas>
       </div>
<input type=hidden id="streamtype" value="stream">
<g:javascript>
$(document).ready(function() {

	if (!window.WebSocket) {
		var msg = "Your browser does not have WebSocket support";
		$("#pageHeader").html(msg);
		$("#chatterBox").html('');
	}
	var	ws = new WebSocket("ws://${hostname}/${meta(name:'app.name')}/WsCamEndpoint/${user}/${user}");
	
	var video = $("#live").get()[0];
	var canvas = $("#canvas");
	var ctx = canvas.get()[0].getContext('2d');
	var options = {
			"video" : true,
			audio:true
	};

	function recordStream() {
		//send(video, ctx, 320, 240);
		timer = setInterval(
				function() {
					send(video, ctx, 320, 240);
				}, 15);
	}

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

	function streamVideo() {
		// Open websocket
	

		timer = setInterval(
				function () {
					ctx.drawImage(video, 0, 0, 320, 240);
					var data = canvas.get()[0].toDataURL('image/jpeg', 1.0);
					newblob = convertToBinary(data);
					ws.send(newblob);
				}, 250);

		/*
		ws.onopen = StreamMethod;

        ws.onmessage = function(msg) {
            var data = msg.data;
            canvas = $('#canvas');
            ctx = canvas[0].getContext('2d');

            var image = new Image();
            image.src = data;
            ctx.drawImage(image, 0, 0);
        }
		 */

		ws.onclose = function() {
			console.log("closed the connection");
		}
	}

	function send(stream, ctx, w, h) {
		ctx.drawImage(video, 0, 0,320, 240);
		var data = canvas.get()[0].toDataURL('image/jpeg', 1.0);
		newblob = convertToBinary(data);
		ws.send(newblob);

		/*  
		ctx.drawImage(stream, 0, 0, w, h);
        var el = $("canvas").get(0);
        var d = el.toDataURL();
        var msg = {
            event: "Onstream",
            data: {
                url: d

            }
        };
        ws.send(JSON.stringify(msg));
		 */
	}

	var streamType = $('#streamtype').val();
	if (streamType === "stream") {
		$('canvas').hide();
		if (hasGetUserMedia()) {
			canvas = $('#canvas');
			ctx = canvas[0].getContext('2d');

			video = $("#live").get()[0];

			var streamRecorder;

			if (typeof video !== 'undefined') {
				window.URL = window.URL || window.webkitURL;
				navigator.getUserMedia = (navigator.getUserMedia ||
						navigator.webkitGetUserMedia ||
						navigator.mozGetUserMedia ||
						navigator.msGetUserMedia);

				if (navigator.webkitGetUserMedia) {                
					// use the chrome specific GetUserMedia function
					navigator.webkitGetUserMedia(options, function(stream) {
						video.src = webkitURL.createObjectURL(stream);
						webcamstream = stream;

						StreamMethod = recordStream();
						streamVideo();
					}, function(err) {
						console.log("Unable to get video stream!")
					});
					//var ws = new WebSocket("ws://localhost:8080/${meta(name:'app.name')}/WsCamEndpoint/${user}");
					ws.onopen = function () {
						console.log("Openened connection to websocket");
					}

					timer = setInterval(
							function () {
								ctx.drawImage(video, 0, 0, 320, 240);
								var data = canvas.get()[0].toDataURL('image/jpeg', 1.0);
								newblob = convertToBinary(data);
								ws.send(newblob);
							}, 250);
				}else{     


					if (navigator.getUserMedia) {
						navigator.getUserMedia(
								{
									video:true,
									audio:true
								},        
								function(stream) { 
									video.src = window.URL.createObjectURL(stream);
									webcamstream = stream;

									StreamMethod = recordStream();
									streamVideo();
								},
								function(error) { /* do something */ }
						);
					}
					else {
						alert('Sorry, the browser you are using doesn\'t support getUserMedia');
						return;
					}

				}
			}
		}
	} else {
		video = $("#videostream").get()[0];
		if (typeof video !== 'undefined') {
			$('videostream').hide();
			StreamMethod = null;
			streamVideo();
		}
	}
	
	  window.onbeforeunload = function() {
       	ws.send("DISCO:-"+user);
       	ws.onclose = function() { }
       	ws.close();
     }
});
</g:javascript>
</body>
</html>