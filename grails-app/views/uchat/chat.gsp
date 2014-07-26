<!DOCTYPE html>
<html>
<head>
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="chat"/>
    </g:if>
    <g:else>
    	<link rel="stylesheet" href="${resource(dir: 'css', file: 'chat.css')}" type="text/css">
    </g:else>
   <title>Grails WebSocket Chat</title>
   <g:javascript library="jquery"/>
</head>
<body>
<div id="chatterBox">
	<div class="message-container">
		<div class="message-north" >
		<ul class="message-user-list"  >
		<textarea  id="onlineUsers" rows="30" cols="25" readonly=readonly></textarea>
		</ul>
		<div class="message-thread" >
		<textarea  id="chatMessages" readonly=readonly rows="30" cols="85%"></textarea>
		</div>
		</div>	
		<div class="message-south" >
			<div id="contact-area">
			<textarea cols="20" rows="3" id="messageBox"  name="message"  ></textarea>
			</div>
			<input type="button" value="send" class="sendbtn" onClick="sendMessage();">
		</div>
	</div>						
</div>

<g:javascript>
        var webSocket=new WebSocket("ws://localhost:8080/${meta(name:'app.name')}/wschat");
        var chatMessages=document.getElementById("chatMessages");
        var onlineUsers=document.getElementById("onlineUsers");
        var user="${chatuser}";
        webSocket.onopen=function(message) {processOpen(message);};
        webSocket.onmessage=function(message) {
            var jsonData=JSON.parse(message.data);
            if (jsonData.message!=null) {chatMessages.value +=jsonData.message+"\n";}
            if (jsonData.users!=null) {onlineUsers.value=jsonData.users+"\n";}
        }   
        webSocket.onclose=function(message) {processClose(message);};
        webSocket.onerror=function(message) {processError(message);};

        function processOpen(message) {
        	<g:if test="${!chatuser}">
        		chatMessages.value +="Chat denied no username"+"\n";
        		webSocket.send("DISCO:-"+user);
        	 	websocket.close();
        	</g:if>
        	<g:else>
        		webSocket.send("CONN:-"+user);
            	chatMessages.value +=user+" connected to chat.... "+"\n";
            	scrollToBottom();
            </g:else>
        }

        function sendMessage() {
            if (messageBox.value!="/disco") {
                webSocket.send(messageBox.value);
                scrollToBottom();
                messageBox.value="";
            }else {
            	webSocket.send("DISCO:-"+user);
            	chatMessages.value +=user+" disconnecting from server... "+"\n";
                websocket.close();
            }   
        }
        
        function processClose(message) {
        	webSocket.send("DISCO:-"+user);
         	chatMessages.value +=user+" disconnecting from server... "+"\n";
         	websocket.close();
        }
        
        function processError(message) {
            chatMessages.value +=" Error.... \n";
        }
        
        function scrollToBottom() {
   			$('#chatMessages').scrollTop($('#chatMessages')[0].scrollHeight);
		}
		
        window.onbeforeunload = function() {
          	webSocket.send("DISCO:-"+user);
        	websocket.onclose = function() { }
        	websocket.close();
        }
</g:javascript>

</body>
</html>
