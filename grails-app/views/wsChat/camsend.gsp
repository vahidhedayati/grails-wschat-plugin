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

  <script type="text/javascript">

 
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
		var uri="ws://"+getHostName()+"/"+getApp()+"/WsCamEndpoint/"+getUser()+"/"+getUser()
			WebCam.connectToSocket(uri);
		
	
		window.onbeforeunload = function() {
			WebCam.send("DISCO:-");
			WebCam.onclose = function() { }
			WebCam.close();
		}
</script>
</body>
</html>