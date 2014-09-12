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
     <video id="live" width="300" height="240" autoplay="autoplay"  style="display: none;"></video>
      <canvas width="300" id="canvas" height="240" style="display: inline;"></canvas>
 </div>

  <script type="text/javascript">
  $(document).ready(function() {
		if (!window.WebSocket) {
			var msg = "Your browser does not have WebSocket support";
			$("#pageHeader").html(msg);
		}
		var	webSocketCam = new WebSocket("ws://${hostname}/${meta(name:'app.name')}/WsCamEndpoint/${user}/${user}");
		webSocketCam.onclose=function(message) {processCamClose(message);};
		// webSocketCam.onmessage=function(message) {processChatMessage(message);	};
		var video = $("#live").get()[0];
		var canvas = $("#canvas");
		var ctx = canvas.get()[0].getContext('2d');
		var options = {
				"video" : true,
				audio:true
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
				}, function(err) {
					console.log("Unable to get video stream!")
				});
				webSocketCam.onopen = function () {
					//console.log("Open connection to websocket");
				}
				timer = setInterval(
						function () {
							if (ctx) {
								ctx.drawImage(video, 0, 0, 320, 240);
								var data = canvas.get()[0].toDataURL('image/jpeg', 1.0);
								newblob = convertToBinary(data);
								webSocketCam.send(newblob);
							}
						}, 250);
			} 
		}
	});

	window.onbeforeunload = function() {
		webSocketCam.send("DISCO:-");
		webSocketCam.onclose = function() { }
		webSocketCam.close();
	}
</script>
</body>
</html>