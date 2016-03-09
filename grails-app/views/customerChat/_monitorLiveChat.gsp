 <g:if test="${enduser?.verifyAppVersion().equals('assets')}">
<asset:javascript src="jquery.min.js" />
<asset:javascript src="jquery-ui.min.js" />
<asset:stylesheet src="jquery-ui.min.css" />
<asset:stylesheet href="customer-chat.css" id="chat_theme" />
<asset:javascript src="wschat.js" />
<asset:javascript src="livechatMonitor.js" />
</g:if>
<g:else>
<script type="text/javascript"	src="${resource(dir: 'js', file: 'jquery.min.js')}"></script>
<script type="text/javascript"	src="${resource(dir: 'js', file: 'jquery-ui.min.js')}"></script>
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'jquery-ui.min.css')}" type="text/css" media="screen" />
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'customer-chat.css')}" type="text/css" media="screen" />
<script type="text/javascript" src="${resource(dir: 'js', file: 'wschat.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'usermenu.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'livechatMonitor.js')}"></script>
</g:else>	
	<div id="colourthemes"  style="display:none;">
		<button id="themeChanger" class="btn btn-danger btn-xs"/>
		<button id="themeChanger2" class="btn btn-primary btn-xs"/>
		<button id="themeChanger3" class="btn btn-inverse btn-xs"/>
		<button id="themeChanger4" class="btn btn-default btn-xs"/>
	</div>
	<g:render template="/il8n"/>
	<span id="liveChatUsersList" />
	<div id="cmessage" style="display:none;"/>
		
<g:javascript>



 	var baseapp="${bean.appName}";
	function getApp() {
		return baseapp;
	}

	if (!window.WebSocket) {
		var msg = "Your browser does not have WebSocket support";
		$("#pageHeader").html(msg);
		$("#chatterBox").html('');
	}

	// Convert grails variable values to javascript format
	var user="${bean.user}";
	var hostname="${bean.hostname}";
	var room = "${bean.roomName}";
	function getHostName() {
		return hostname;
	}
	
	function getUser() {
		return user;
	}
	function getRoom() { 
	 return room;
	}
	var currentRoom;
	var idList = new Array();
	;
	var debug = "${bean.debug}";
	
	var uri="${bean.uri}${bean.roomName}";
	if (debug == "on") {
		console.log('Connecting to '+uri);
	}
	var webSocket=new WebSocket(uri);
    var chatMessages=document.getElementById("chatMessages");
    var onlineUsers=document.getElementById("onlineUsers");
    var messageBox=document.getElementById("messageBox");
    webSocket.onopen=function(message) {processOpen(message);};
    webSocket.onclose=function(message) {processClose(message);};
    webSocket.onerror=function(message) {processError(message);};
    webSocket.onmessage=function(message) {processMessage(message);	};
		
   function processOpen(message) {
   		if (debug == "on") {
			console.log('Opening  connection for to ${bean.user}');
		}
    	<g:if test="${!bean.user}">
       		$('#chatMessages').append("Chat denied no username \n");
       		webSocket.send("LIVEDISCO:-"+user);
       	 	webSocket.close();
       	</g:if>
        <g:else>
       		webSocket.send("LIVECONN:-"+user+",monitorLiveChat");
           	scrollToBottom();
       </g:else>
 	}
     window.onbeforeunload = function() {
       webSocket.send("LIVEDISCO:-"+user);
     }
</g:javascript>

