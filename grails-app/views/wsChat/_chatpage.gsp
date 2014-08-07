<div id="pageHeader" class="page-header2">
<h2>${chatHeader }</h2>
<small>  
${now}
</small>
</div>

    <div id="chat_div">
    </div>
    <hr />
    <div id="log">
    </div>
          
          <div id="userList">
          </div>

<div id="chatterBox">
	<div class="message-container">
	
		<div class="message-north" >

		
      		<div class="message-user-list" >
      	 <div id="fixyflow">
      		 	<div id="fixflow">
     
		<ul class="nav nav-tabs nav-stacked"  >
		 <ul class="dropdown-menu" id='onlineUsers' style="display: block; position: static; margin-bottom: 5px; *">
		<span  id="onlineUsers" />
		</ul>
		</ul>
		</div>
		</div>
		</div>
		<div class="message-thread" id="cmessage" >
		<div  id="chatMessages" ></div>
		</div>
		</div>

		<div class="message-south" >

			<textarea cols="20" rows="1" id="messageBox"  name="message"></textarea>
			
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
    webSocket.onclose=function(message) {processClose(message);};
    webSocket.onerror=function(message) {processError(message);};
	
    webSocket.onmessage=function(message) {
    	var jsonData=JSON.parse(message.data);
    	if (jsonData.message!=null) {$('#chatMessages').append(jsonData.message+"\n");}
    	if (jsonData.users!=null) {
         $('#onlineUsers').html(jsonData.users);
       	}
       	if (jsonData.genDiv!=null) {
    
         $('#userList').html(jsonData.genDiv);
       	}
    }
    
    function pmuser(suser,sender) {
	    $(function(event, ui) {
			var box = null;
	         if(box) {
	        	box.chatbox("option", "boxManager").toggleBox();
	         }else {
	         	box = $("#"+suser).chatbox({id:sender, 
	            	user:{key : "value"},
	                title : "PM: "+user,
	                messageSent : function(id, user, msg) {
	                //$("#log").append(id + " said: " + msg + "<br/>");
	                $("#"+suser).chatbox("option", "boxManager").addMsg(id, msg);
	                 webSocket.send("/pm "+suser+","+msg);
	        		}})
	        		
	        }
	     });
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
   	
   function processOpen(message) {
    	<g:if test="${!chatuser}">
       		$('#chatMessages').append("Chat denied no username \n");
       		webSocket.send("DISCO:-"+user);
       	 	webSocket.close();
       	</g:if>
       	<g:else>
       		webSocket.send("CONN:-"+user);
           	$('#chatMessages').append(user+" connected to chat.... \n");
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
           	$('#chatMessages').append(user+" disconnecting from server... \n");
           	messageBox.value="";
               webSocket.close();
           }   
     }
       
     function processClose(message) {
       	webSocket.send("DISCO:-"+user);
        	$('#chatMessages').append(user+" disconnecting from server... \n");
        	webSocket.close();
     }
       
     function processError(message) {
           $('#chatMessages').append("Error.... \n");
     }
       
     function scrollToBottom() {
  			$('#cmessage').scrollTop($('#cmessage')[0].scrollHeight);
  			
	 }
	
     window.onbeforeunload = function() {
       	webSocket.send("DISCO:-"+user);
       	webSocket.onclose = function() { }
       	webSocket.close();
     }
</g:javascript>
