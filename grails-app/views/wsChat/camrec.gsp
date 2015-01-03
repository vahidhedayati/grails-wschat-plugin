<!DOCTYPE html>
<html>
<head>

<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<g:render template="/assets" />
</g:if>
<g:else>
	<g:render template="/resources" />
</g:else>

<title>
	${chatTitle }
</title>
</head>
<body>


	<div id="pageHeader"></div>
	<div style="visibility: hidden; width: 0; height: 0;">
		<canvas width="320" id="canvas" height="240"></canvas>
	</div>

	<div>
		<img id="target" style="display: inline;" />
	</div>

	<g:javascript>
	if (!window.WebSocket) {
		var msg = "Your browser does not have WebSocket support";
		$("#pageHeader").html(msg);
	}
	
	/*
	 *	Open Websocket-Connection
	 */
	// create new WebRTC-Object
	var WebCamRec = new WebCamRec();

	// connect to websocket server
	
	
		<g:if test="${addAppName=='no'}">
			var uri="ws://"+getHostName()+"/WsCamEndpoint/${user}/"+getUser();
		</g:if>
		<g:else>
			var uri="ws://"+getHostName()+"/"+getApp()+"/WsCamEndpoint/${user}/"+getUser();
		</g:else>

	
		//console.log(uri);
		WebCamRec.getFromSocket(uri);
	

		window.onbeforeunload = function() {
		//WebCam.send("DISCO:-");
		WebCamRec.onclose = function() { }
		WebCamRec.close();
	}
</g:javascript>

</body>
</html>