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
       <g:javascript>
       		var ws=new WebSocket("ws://${hostname }/${meta(name:'app.name')}/WsCamEndpoint/${user }/${chatuser}");
   			ws.onmessage = function(msg) {
                     var target = document.getElementById("target");
                     url = window.webkitURL.createObjectURL(msg.data);
                     target.onload = function() {
                           window.webkitURL.revokeObjectURL(url);
                     };
                     target.src = url;
              }
            window.onbeforeunload = function() {
       			ws.send("DISCO:-"+user);
       			ws.onclose = function() { }
       			ws.close();
     		}
       </g:javascript>
       <div style="visibility: hidden; width: 0; height: 0;">
              <canvas width="320" id="canvas" height="240"></canvas>
       </div>

       <div>
              <img id="target" style="display: inline;" />
       </div>
</body>
</html>