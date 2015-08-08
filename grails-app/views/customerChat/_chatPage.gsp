<asset:javascript src="jquery.min.js" />
<asset:javascript src="jquery-ui.min.js" />
<asset:stylesheet src="jquery-ui.min.css" />
<asset:stylesheet href="customer-chat.css" id="chat_theme" />
<asset:javascript src="wschat.js" />
<div id="chatDialog" title="${bean.liveChatTitle}">
	<div id="chat_div"></div>
	<div class='col-sm-10'>
		<div id="cmessage">
			<div id="chatMessages"></div>
		</div>
	<div class="message-thread">
		<div id="sendMessage">
			<textarea id="messageBox" name="message"></textarea>
			<input type="button" id="sendBtn"
				value="${message(code: 'wschat.send.label', default: 'SEND')}"
				onClick="sendMessage();">
			</div>
		</div>
	</div>
</div>

<g:javascript>
	$(function() {
	 	$( "#chatDialog" ).dialog({
			autoOpen: true,
            hide: "puff",
            show : "slide",
            height: 435,
            width: 660,
            position: {
                my: "left center",
                at: "left center"
            },
             close: function(ev, ui) {
             	webSocket.send("deactive_me");
             	webSocket.send("close_connection");
             	$(this).dialog("close");
             	$('#chatReturn').hide();
             	$(this).dialog("destroy");
             	$(this).hide();
             }
         });
    });

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

	function getHostName() {
		return hostname;
	}
	
	function getUser() {
		return user;
	}
	
	var currentRoom;
	var idList = new Array();
	var camList = new Array();
	var camOn = new Array();
	var fileOn = new Array();
	var mediaOn = new Array();
	var rtcOn = new Array();
	var gameOn = new Array();
	var isAdmin="${'false' }";
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
       		webSocket.send("DISCO:-"+user);
       	 	webSocket.close();
       	</g:if>
		<g:else>
       		webSocket.send("CONN:-"+user);
           	scrollToBottom();
       </g:else>
 	}
	$('#messageBox').keypress(function(e){
	if (e.keyCode == 13 && !e.shiftKey) {
		e.preventDefault();
	}
	if(e.which == 13){
		var tmb=messageBox.value.replace(/^\s*[\r\n]/gm, "");
		if (tmb!="") {
			sendMessage();
			$("#messageBox").val().trim();
			messageBox.focus();
		}
	}
	});
	
     window.onbeforeunload = function() {
		webSocket.send("deactive_me");
        webSocket.send("close_connection");
       //	webSocket.send("DISCO:-"+user);
       webSocket.onclose = function() { }
       webSocket.close();
     }
</g:javascript>
