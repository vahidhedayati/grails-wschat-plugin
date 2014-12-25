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
     <video id="live" width="300" height="240" autoplay="autoplay"  style="display: inline;"></video>
      <div  style="visibility: hidden; width: 0; height: 0;">
      <canvas width="300" id="canvas" height="240" style="display: inline;"></canvas>
      </div>
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
		var WebCam = new WebCam();

		// connect to websocket server
		<g:if test="${addAppName=='no'}">
			var uri="ws://"+getHostName()+"/WsCamEndpoint/"+getUser()+"/"+getUser();
		</g:if>
		<g:else>
			var uri="ws://"+getHostName()+"/"+getApp()+"/WsCamEndpoint/"+getUser()+"/"+getUser();
		</g:else>
		
		WebCam.connectToSocket(uri);
		
	
		window.onbeforeunload = function() {
			WebCam.send("DISCO:-");
			WebCam.onclose = function() { }
			WebCam.close();
		}
</g:javascript>
</body>
</html>