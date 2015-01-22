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
	<div class="container">


		<g:if test="${showtitle.equals('yes') }">
			<div class="page-header">
				<h2>
					${chatHeader }
				</h2>
				<small> ${now}
				</small>
			</div>
		</g:if>

		<div id="chat_div"></div>

		<div id="userList"></div>

		<div id="camcom"></div>
		<div id="bannedconfirmation"></div>

		<div id="profileconfirmation"></div>

		<div id="banuser" style="display: none;">
			<g:render template="/banuser" />
		</div>

		<div id="userprofile" style="display: none;">
			<g:render template="/profile/profile" />
		</div>

		<div id="userphoto" style="display: none;">
			<g:render template="/profile/photomb" />
		</div>
		<div id="roomcontainer" style="display: none;">
			<g:render template="/room/room" />
		</div>

		<div id="admincontainer" style="display: none;">
			<g:render template="/admin/master" />
		</div>


		<div id="chatterBox">
			<div class='row' id="themeChoice">

				<nav class="container navbar nav">
					<ul class="nav-pills pull-left">
						<li class="btn btn-success"
							style="margin-top: 10px; margin-right: 2px;">
							<g:message code="wschat.change.room.label" default="CHANGE ROOM:" />
							 <span
							class="glyphicon glyphicon-hand-right" title="Chatrooms"
							alt="choose a different room"></span>
						</li>
								<li class="btn" style="margin-top: 0.4em;"><a  onclick="javascript:closeChatPMs()" title="${message(code: 'wschat.close.PM.boxes.label', default: 'Attempt to close any stuck PM Boxes')}">
					<span class="glyphicon glyphicon-export"></span>
					</a></li>
					</ul>
					<div id="chatRooms"></div>
					<div id="adminRooms"></div>
					<div id="colourthemes">
						<button id="themeChanger" class="btn btn-danger btn-xs">
						</button>
						<button id="themeChanger2" class="btn btn-primary btn-xs">
						</button>
						<button id="themeChanger3" class="btn btn-inverse btn-xs">
						</button>
						<button id="themeChanger4" class="btn btn-default btn-xs">
						</button>
				
					
					</div>
				</nav>

				
	<div class='col-sm-2'>
					<div id="friendsBlock">



						<div class="btn btn-warning btn-xs btn-block">
							<b><g:message code="wschat.friends.label" default="FRIENDS" /></b>
						</div>
						<ul class="nav nav-stacked" >
							<ul class="dropdown-menu" id='friendsList'
								style="display: inline-block; position: relative; padding:0px; ">

								<span id="friendsList1" />
							</ul>
						</ul>

						<div class="clearfix"></div>
					</div>
					<div class="clearall"></div>



					<div id="roomBlock">
						<div class="btn btn-success btn-xs btn-block">
							<b><g:message code="wschat.room.users.label" default="ROOM USERS" /></b>
						</div>
						<ul class="nav nav-stacked" >
							<ul class="dropdown-menu" id='onlineUsers'
								style="display: inline-block; position: relative; padding:0px;">
								<span id="onlineUsers1" />
							</ul>
						</ul>

						<div class="clearfix"></div>
					</div>

				</div>


				<div class='col-sm-10'>

					<div id="cmessage">
						<div id="fixyflow">
							<div id="fixflow">
								<div id="chatMessages"></div>

							</div>
						</div>
					</div>

					<div class="message-thread">
						<div id="sendMessage">
							<textarea id="messageBox" name="message"></textarea>
							<input type="button" id="sendBtn" value="${message(code: 'wschat.send.label', default: 'SEND')}"
								onClick="sendMessage();">
						</div>
					</div>
				</div>

			</div>
		</div>

	</div>


	<g:javascript>
	$( "#roomBlock" ).resizable();
 	$( "#friendsBlock" ).resizable();
 	
 	var baseapp="${meta(name:'app.name')}";
 	
	function getApp() {
		return baseapp;
	}
 	
 	<g:if test="${addAppName=='no'}">
		var themeuri="/assets/"
	</g:if>
	<g:else>
		var themeuri="/"+baseapp+"/assets/"
	</g:else>
	
	
	document.getElementById('themeChanger2').onclick = function () { 
   	 	document.getElementById('chat_theme').href = themeuri+'chat-blue.css';
	};
	document.getElementById('themeChanger').onclick = function () { 
    	document.getElementById('chat_theme').href = themeuri+'chat-red.css';
	};
	document.getElementById('themeChanger3').onclick = function () { 
    	document.getElementById('chat_theme').href = themeuri+'chat-dark.css';
	};
	document.getElementById('themeChanger4').onclick = function () { 
   	 	document.getElementById('chat_theme').href = themeuri+'/chat.css';
	};


	if (!window.WebSocket) {
		var msg = "Your browser does not have WebSocket support";
		$("#pageHeader").html(msg);
		$("#chatterBox").html('');
	}

	// Convert grails variable values to javascript format
	var user="${chatuser}";
	var hostname="${hostname}";

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
	var rtcOn = new Array();
	var isAdmin="false";
	
	var debug = "${debug }";
		
		
		
	var video = $("#live").get()[0];
	var canvas = $("#canvas");
	var ctx
	if (!canvas) {
		ctx = canvas.get()[0].getContext('2d');
	}
	var options = {
		"video" : true,
		audio:true
	};
	<g:if test="${addAppName=='no'}">
		var uri="ws://${hostname}/WsChatEndpoint/${room}";
	</g:if>
		<g:else>
		var uri="ws://${hostname}/${meta(name:'app.name')}/WsChatEndpoint/${room}"
	</g:else>

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
			console.log('Openning  connection for to ${chatuser}');
		}
	
    	<g:if test="${!chatuser}">
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
       	webSocket.send("DISCO:-"+user);
       	//webSocket.onclose = function() { }
       	//webSocket.close();
       	
     }

</g:javascript>


</body>
</html>
