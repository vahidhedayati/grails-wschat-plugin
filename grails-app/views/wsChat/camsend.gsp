<!DOCTYPE html>
<html>
<head>

<g:render template="/assets" />
<title>
	${chatTitle }
</title>
</head>
<body>
	<div id="pageHeader">
		<video id="live" width="300" height="240" autoplay="autoplay"
			style="display: inline;"></video>
		<div style="visibility: hidden; width: 0; height: 0;">
			<canvas width="300" id="canvas" height="240" style="display: inline;"></canvas>
		</div>
	</div>

	<g:javascript>
 
		if (!window.WebSocket) {
			var msg = "Your browser does not have WebSocket support";
			$("#pageHeader").html(msg);
		}

		/*	Open Websocket-Connection */
		// create new WebRTC-Object
		var WebCam = new WebCam();
		var uri="${bean.camEndpoint}/"+getUser()+"/"+getUser();
		if (debug == "on") {
			console.log('Connecting to '+uri);
		}
		WebCam.connectToSocket(uri);
		window.onbeforeunload = function() {
			WebCam.send("DISCO:-");
			WebCam.onclose = function() { }
			WebCam.close();
		}
</g:javascript>
</body>
</html>