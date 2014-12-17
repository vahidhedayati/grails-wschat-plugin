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
<div class="container">

<g:if test="${showtitle.equals('yes') }">
	<div  class="page-header">
	<h2>${chatHeader }</h2>
	<small>  
		${now}
	</small>
	</div>
</g:if>
    <div id="chat_div">
    </div>
    
    <div id="userList">
    </div>

	<div id="camcom">
	</div>
	<div id="bannedconfirmation">
	</div>
	
	<div id="profileconfirmation">
	</div>

	<div id="banuser" style="display:none;">
		<g:render template="/banuser" />
	</div>
	
	<div id="userprofile" style="display:none;">
		<g:render template="/profile/profile" />
	</div>

	<div id="userphoto" style="display:none;">
		<g:render template="/profile/photomb" />
	</div>
	<div id="roomcontainer" style="display:none;">
		<g:render template="/room/room" />
	</div>
	

	
	<div id="chatterBox">
		
			
		<div class="container navbar nav navbar-inverse">
	
				<ul class="nav-pills pull-left">
				<li class="btn">
					<a  class="btn btn-success glyphicon glyphicon-hand-right" title="Chatrooms" alt="choose a different room"></a>
				</li>
				</ul>
				<div id="chatRooms" >
				</div>
				
			
			
				<div id="adminRooms"></div>
			
		</div>
		
		<div class='row'>
				
		<div class='col-sm-2'>
		
			<div id="fixyflow"><div id="fixflow">
			<ul class="nav nav-tabs nav-stacked"  >
				<ul class="dropdown-menu" id='onlineUsers' style="display: block; position: static; margin-bottom: 5px; *">
					<span  id="onlineUsers" />
				</ul>
			</ul>
			</div></div>
			
		</div>
			
		<div class='col-sm-10' >
		
		<div id="cmessage">
				<div id="fixyflow"><div id="fixflow">
					<div  id="chatMessages" ></div>
					
				</div>	</div>	</div>
			
			<div class="message-thread" >
				<div id="sendMessage" >
				<textarea cols="20" rows="1" id="messageBox"  name="message"></textarea>
				<input type="button" id="sendBtn" value="send" class="btn btn-danger btn-lg" onClick="sendMessage();">
		</div></div>
		</div>
				
	</div>
	</div>
	
</div>
<script>

	if (!window.WebSocket) {
		var msg = "Your browser does not have WebSocket support";
		$("#pageHeader").html(msg);
		$("#chatterBox").html('');
	}

	// Convert grails variable values to javascript format
	var user="${chatuser}";
	var hostname="${hostname}";
	var baseapp="${meta(name:'app.name')}";
	function getApp() {
		return baseapp;
	}
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
    var webSocket=new WebSocket("ws://${hostname}/${meta(name:'app.name')}/WsChatEndpoint/${room}");
     
    var chatMessages=document.getElementById("chatMessages");
    var onlineUsers=document.getElementById("onlineUsers");
    var messageBox=document.getElementById("messageBox");
   
    webSocket.onopen=function(message) {processOpen(message);};
    webSocket.onclose=function(message) {processClose(message);};
    webSocket.onerror=function(message) {processError(message);};
    webSocket.onmessage=function(message) {processMessage(message);	};
		
   function processOpen(message) {
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

 
</script>


</body>
</html>
