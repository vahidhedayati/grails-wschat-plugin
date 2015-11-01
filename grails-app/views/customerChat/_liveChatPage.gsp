<asset:javascript src="jquery.min.js" />
<asset:javascript src="jquery-ui.min.js" />
<asset:stylesheet src="jquery-ui.min.css" />
<asset:stylesheet href="customer-chat.css" id="chat_theme" />
<asset:javascript src="wschat.js" />
<asset:javascript src="usermenu.js" />
<div id="chatDialog" title="${bean.liveChatTitle}">
	<div id="chat_div"></div>
	<div class='col-sm-10'>
		<div id="cmessage">
			<div id="fixyflow">
    			<div id="fixflow">
    				<div id="chatMessages"></div>
				</div>
			</div>
		</div>
	<div class="message-thread" id="waiting" style="display:none;">
		<div id="sendMessage">
			<textarea id="messageBox" name="message"></textarea>
			<input type="button" id="sendBtn"
				value="${message(code: 'wschat.send.label', default: 'SEND')}"
				onClick="sendLiveMessage();" >
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
            height: 440,
            width: 660,
            position: {
                my: "left center",
                at: "left center"
            },
             close: function(ev, ui) {
             	//webSocket.send("deactive_me");
             	//webSocket.send("close_connection");
             	webSocket.send("LIVEDISCO:-"+user);
       	 		webSocket.close();
             	$(this).dialog("close");
             	$('#chatReturn').hide();
             	$(this).dialog("destroy");
             	$(this).hide();
             },
             open: function (event, ui) {
                $('#chatDialog').css('overflow', 'hidden');
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
	var roomName="${bean.roomName}";
	
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
    webSocket.onclose=function(message) {processLiveClose(message);};
    webSocket.onerror=function(message) {processError(message);};
    webSocket.onmessage=function(message) {processMessage(message);	};
		
   function processOpen(message) {
   		if (debug == "on") {
			console.log('Opening  connection for to ${bean.user}');
		}
    	<g:if test="${!bean.user}">
       		$('#chatMessages').append("${message(code: 'wschat.denied.nousername.label', default: 'Chat denied no username provided')} \n");
       		webSocket.send("LIVEDISCO:-"+user);
       	 	webSocket.close();
       	</g:if>
		<g:else>
       		webSocket.send("LIVECONN:-"+user);
       		webSocket.send("/userType liveChat");
           	scrollToBottom();
       </g:else>
       	$('#chatMessages').append("${message(code: 'wschat.please.wait.for.staff.label', default: 'Please wait for member of staff')}\n");
       	$('#messageBox').prop("readonly", true);
 	}
	$('#messageBox').keypress(function(e){
	if (e.keyCode == 13 && !e.shiftKey) {
		e.preventDefault();
	}
	if(e.which == 13){
		var tmb=messageBox.value.replace(/^\s*[\r\n]/gm, "");
		if (tmb!="") {
			sendLiveMessage();
			$("#messageBox").val().trim();
			messageBox.focus();
		}
	}
	});
	
     window.onbeforeunload = function() {
		//webSocket.send("deactive_me");
       // webSocket.send("close_connection");
       	webSocket.send("LIVEDISCO:-"+user);
       webSocket.onclose = function() { }
       webSocket.close();
     }
</g:javascript>
