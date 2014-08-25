<!DOCTYPE html>
<html>
<head>
<g:if test="${enduser?.verifyAppVersion().equals('resources')}">
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="chat"/>
    </g:if>
    <g:else>
   	    <link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery-ui.min.css')}" type="text/css" media="screen" />
   		<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.min.js')}"></script>
   		<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery-ui.min.js')}"></script>
     	<link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery.ui.chatbox.css')}" type="text/css" >
    	<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.ui.chatbox.js')}"></script>
   		<script type="text/javascript" src="${resource(dir: 'js', file: 'chatboxManager.js')}"></script>
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'chat.css')}" type="text/css">
    	<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css">
    
    </g:else>
</g:if>
<g:else>
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="achat"/>
    </g:if>
    <g:else>
    	<asset:stylesheet href="chat.css" />
     	<asset:stylesheet href="bootstrap.min.css" />
	  	<asset:stylesheet href="jquery.ui.chatbox.css" />
    	<asset:javascript src="jquery.min.js"/>
    	<asset:javascript src="jquery-ui.min.js"/>
    	<asset:javascript src="jquery.min.js"/>
    	<asset:javascript src="jquery.min.js"/>
    	<asset:javascript src="jquery.ui.chatbox.js"/>
    	<asset:javascript src="chatboxManager.js"/>
    </g:else>
</g:else>    
   <title>${chatTitle }</title>
</head>
<body>
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
      	 			<div id="fixyflow"><div id="fixflow">
						<ul class="nav nav-tabs nav-stacked"  >
		 				<ul class="dropdown-menu" id='onlineUsers' style="display: block; position: static; margin-bottom: 5px; *">
						<span  id="onlineUsers" />
						</ul>
						</ul>
					</div></div>
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
	
	var config = {
		width : 155, //px
		gap : 2,
		maxBoxes : 5,
		messageSent : function(dest, msg) {
	   	// override this
	    	$("#" + dest).chatbox("option", "boxManager").addMsg(dest, msg);
		}
    };
	var idList = new Array();
	
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
    	
    	if (jsonData.message!=null) {
    		$('#chatMessages').append(htmlEncode(jsonData.message)+"<br/>");
    	}
    	
    	if (jsonData.users!=null) {
    	 $('#onlineUsers').html("");
    		jsonData.users.forEach(function(entry) {
    			if (entry.owner!=null) {
    	 			$('#onlineUsers').append('\n<li class="dropdown-submenu active">\n<a tabindex="-1" class="user-title" href="#">'+entry.owner+'</a>\n\
    	 			<ul class="dropdown-menu">\n<li><a>'+entry.owner+'s profile</a>\n</li>\n</ul>\n</li>\n\n\n');
    			 }
    			 if (entry.user!=null) {
    				$('#onlineUsers').append('\n<li class="dropdown-submenu"><a tabindex="-1" class="user-title" href="#">'+entry.user+'</a>\n\
    				<ul class="dropdown-menu">\n<li>\
    				<a onclick="javascript:pmuser('+wrapIt(entry.user)+', '+wrapIt(user)+');">PM  '+entry.user+'</a>\
    				</li>\n</ul>\n</li>\n\n\n');
    			 }
    	 	});
       	}
      
       	
       	if (jsonData.privateMessage!=null) {
       		var receiver
       		var sender
       		if (jsonData.msgFrom!=null) {
       			sender=jsonData.msgFrom
       		}
       		if (jsonData.msgTo!=null) {
       			receiver=jsonData.msgTo
       		}
       		
       		$('#chatMessages').append("PM("+sender+"): "+jsonData.privateMessage+"<br/>");
       		sendPM(receiver,sender,jsonData.privateMessage);
       		
       	}   	
    }

	function verifyAdded(uid) {
		var added="false";
	  	var idx = idList.indexOf(uid);
		if (idx != -1) {
			added="true";
		}
		return added;
	}		
			
		
    function verifyPosition(uid) {
    	var idx = idList.indexOf(uid);
		if(idx == -1) {
		   	idList.push(uid);
			if (idList.length>1) { 
       			var getNextOffset = function() {
					return (config.width + config.gap) * idList.length;
	 			};	
       			$("#"+uid).chatbox("option", "offset", getNextOffset());
       			
       		}
      	} 		
      } 		 			
       		 				
    function sendPM(receiver,sender,pm) {
	     $(function(event, ui) {
	    	var box = null;
		         if(box) {
		        	box.chatbox("option", "boxManager").toggleBox();
		         }else {
		         	var added=verifyAdded(sender);
		         	var el="#"+sender
		          	if (added=="false") {
		           		var el = document.createElement('div');
	    				el.setAttribute('id', sender);
	    		  	}	
		   			box =  $(el).chatbox({id:sender, 
		            	user:{key : "value"},
		                title : "PM from: "+sender,
		                messageSent : function(id, user, msg) {
		               		verifyPosition(sender);
       		 				$("#"+sender).chatbox("option", "boxManager").addMsg(receiver, msg);
		                	webSocket.send("/pm "+sender+","+msg);
		        		}
		        	});
                 }                 
           });
           
           verifyPosition(sender);
		   $("#"+sender).chatbox("option", "boxManager").addMsg(sender, pm);         
    }
    
    function pmuser(suser,sender) {
      $(function(event, ui) {
	    	var box = null;
		         if(box) {
		        	box.chatbox("option", "boxManager").toggleBox();
		         }else {
		         	var added=verifyAdded(suser);
		         	var el="#"+suser
		          	if (added=="false") {
		           		var el = document.createElement('div');
	    				el.setAttribute('id', suser);
	    		  	}	
		       		box = $(el).chatbox({id:sender, 
	            		user:{key : "value"},
	                	title : "PM: "+suser,
	                	messageSent : function(id, user, msg) {
	               			verifyPosition(suser);
	                		$("#"+suser).chatbox("option", "boxManager").addMsg(id, msg);
	                 		webSocket.send("/pm "+suser+","+msg);
	        			}
	       			});
          	 box.chatbox("option", "show",1); 
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
   	
   	function wrapIt(value) {
   		return "'"+value+"'"
   	}
   	function htmlEncode(value){
 	 return $('<div/>').text(value).html();
	}
	
   function processOpen(message) {
    	<g:if test="${!chatuser}">
       		$('#chatMessages').append("Chat denied no username <br/>");
       		webSocket.send("DISCO:-"+user);
       	 	webSocket.close();
       	</g:if>
       	<g:else>
       		webSocket.send("CONN:-"+user);
           	//$('#chatMessages').append(user+" connected to chat.... <br/>");
           	scrollToBottom();
       </g:else>
 	}

    function sendMessage() {
           if (messageBox.value!="/disco") {
           	 if (messageBox.value!="") {
               webSocket.send(messageBox.value);
               messageBox.value="";
               messageBox.value.replace(/^\s*[\r\n]/gm, "");
               scrollToBottom();
             }  
           }else {
           		webSocket.send("DISCO:-"+user);
           		$('#chatMessages').append(user+" disconnecting from server... <br/>");
           		messageBox.value="";
               	webSocket.close();
           }   
     }
       
     function processClose(message) {
       	webSocket.send("DISCO:-"+user);
        	$('#chatMessages').append(user+" disconnecting from server... <br/>");
        	webSocket.close();
     }
       
     function processError(message) {
           $('#chatMessages').append("Error.... <br/>");
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


</body>
</html>
