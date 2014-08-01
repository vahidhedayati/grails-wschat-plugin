<div id="pageHeader" class="page-header uppercase">
<h3>${chatHeader }</h3>
<small>  
<div id="clock" data-time="${now.time}">
    <h5>${now}</h5>
</div> 
</b></small>
</div>

<div id="chatterBox">
	<div class="message-container">
		<div class="message-north" >
		<ul class='message-user-list'  id='onlineUsers'>
		<div  id="onlineUsers" ></div>
		</ul>
		<div class="message-thread" >
		<textarea  id="chatMessages" readonly=readonly rows="30" cols="85%"></textarea>
		</div>
		</div>	
		<div class="message-south" >
			<div id="contact-area">
			<textarea cols="20" rows="3" id="messageBox"  name="message"></textarea>
			</div>
			<input type="button" value="send" class="sendbtn" onClick="sendMessage();">
		</div>
	</div>						
</div>
<g:javascript>
	if (!window.WebSocket) {
		var msg = "Your browser does not have WebSocket support";
		$("#pageHeader").html(msg);
		$("#chatterBox").html('');
	}
        var webSocket=new WebSocket("ws://${hostname}/${meta(name:'app.name')}/WsChatEndpoint");
        
        var chatMessages=document.getElementById("chatMessages");
        var onlineUsers=document.getElementById("onlineUsers");
        var messageBox=document.getElementById("messageBox");
        var user="${chatuser}";
        webSocket.onopen=function(message) {processOpen(message);};
        webSocket.onmessage=function(message) {
            var jsonData=JSON.parse(message.data);
            
            if (jsonData.message!=null) {chatMessages.value +=jsonData.message+"\n";}
            if (jsonData.users!=null) {
            //onlineUsers.value=jsonData.users+"\n";
            $('#onlineUsers').html(jsonData.users);
            }
        }   
        webSocket.onclose=function(message) {processClose(message);};
        webSocket.onerror=function(message) {processError(message);};
		
	
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
    	
        function processOpen(message) {
        	<g:if test="${!chatuser}">
        		chatMessages.value +="Chat denied no username"+"\n";
        		webSocket.send("DISCO:-"+user);
        	 	webSocket.close();
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
                messageBox.value="";
                messageBox.value.replace(/^\s*[\r\n]/gm, "");
                scrollToBottom();
            }else {
            	webSocket.send("DISCO:-"+user);
            	chatMessages.value +=user+" disconnecting from server... "+"\n";
            	messageBox.value="";
                webSocket.close();
            }   
        }
        
        function processClose(message) {
        	webSocket.send("DISCO:-"+user);
         	chatMessages.value +=user+" disconnecting from server... "+"\n";
         	webSocket.close();
        }
        
        function processError(message) {
            chatMessages.value +=" Error.... \n";
        }
        
        function scrollToBottom() {
   			$('#chatMessages').scrollTop($('#chatMessages')[0].scrollHeight);
		}
		
        window.onbeforeunload = function() {
          	webSocket.send("DISCO:-"+user);
        	webSocket.onclose = function() { }
        	webSocket.close();
        }
 
      
</g:javascript>
