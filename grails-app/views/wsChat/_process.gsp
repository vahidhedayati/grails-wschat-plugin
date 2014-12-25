
<div id="task_1" style="font-size: 4em; font-weight: bold;">
</div>

<div id="flipflop" style="font-size: 4em; font-weight: bold;">
</div>

<g:javascript>
	var loggedInUsers=[];
	var user="${user }";
	var receivers="${receivers}"
	var arrayLength = receivers.length;
	// Connect websocket and set up processes 
	
	<g:if test="${addAppName=='no'}">
		var uri="ws://${hostname}/${chatApp }/${room}";
	</g:if>
	<g:else>
		var uri="ws://${hostname}/${appName}/${chatApp }/${room}";
	</g:else>
	
	var webSocket=new WebSocket(uri);
 	webSocket.onopen=function(message) {processOpen(message);};
 	webSocket.onclose=function(message) {processClose(message);};
    webSocket.onerror=function(message) {processError(message);};
	webSocket.onmessage=function(message) {processMessage(message);	};
	
	// Global variables
	var userList=[];
	
	function processMessage( message) {
	
		//Create internal users list
		// and log out front end user when backend - real user has logged out
		var jsonData = JSON.parse(message.data);
		if (jsonData.users!=null) {
			loggedInUsers=[];
			updateUserList(jsonData.users);
			var backendLoggedin=verifyIsOn(user);
			if (backendLoggedin=="false") {
				//console.log('Backend user left, logging out now');
				webSocket.send("DISCO:-");
       	 		webSocket.close();
			}
		}
	
		// Log out user if system tells it to	
		if (jsonData.system!=null) {
			if (jsonData.system=="disconnect") { 
				webSocket.send("DISCO:-"+user);
				webSocket.close();
			}
		}
		
		
		//Process private Messages:
		if (jsonData.privateMessage!=null) {
			var receiver
			var sender
			if (jsonData.msgFrom!=null) {
				sender=jsonData.msgFrom
			}
			if (jsonData.msgTo!=null) {
				receiver=jsonData.msgTo
			}
			
			
			
			//Important segment
			// Figure out JSON OR STANDARD OBJECT
			var itJson=isJson(jsonData.privateMessage);
			if (itJson==false	) {
				processMapAction(jsonData.privateMessage);
			}else{
				processEventAction(jsonData.privateMessage)
			}
		}   	
		
	}


	//Example copy this template into your app
	//use template="/path/to/new/template/template"
	// inside the taglib calls to override and set custom 
	// actions in this example
	// if do_task_1 update a div above and show it
	// you could override and based upon action client/server
	// set different pages doing different things
	// remember the tag lib is being called within your master app
	//so this is template within your main page but being loaded by plugin
	
	function processMapAction(currentact) {
			if (currentact=="do_task_1") {
				$('#task_1').html('Doing task 1 for this broken video');
			}

	}
	
	
	function processEventAction(message) {
	
		var jsonActions = JSON.parse(message);
		var command = jsonActions.command
		var event,context,data='';
		jsonActions.arguments.forEach(function(entry) {
			if (entry.event!=null) {
				event=entry.event;
			}
			if (entry.context!=null) {
				context=entry.context;
			}
			if (entry.data!=null) {
				data=entry.data;
			}
		});
		
		var jsonCommands=JSON.stringify(data)
		
		//console.log(event+'--'+context+"-------"+jsonCommands);
		
		// In our demo background Class:
		// WsClientProcessService 
		// we defined _receieved to be appended to any event received
		// now we ensure only front end of msgs that don't have this get executed
		// go wild do what you like
		var msgType=isReceivedMsg(event);
		if (msgType==false) {
			processCommands(jsonCommands);
		}
		
	}
	
	
	
	// Example process commands - 
	// override as template and set up own actions
	
	function processCommands(jsonCommand) {
		var jsonCommands=JSON.parse(jsonCommand)
		if(jsonCommands !== undefined){
			jsonCommands.forEach(function(entry) {
	   		 if(entry.content !== undefined){
	    		var content=entry.content;
		        switch(content.command){
	        	case "flipflop":
	        		$('#flipflop').html(content.arguments);
	            //case "javascript":
	           //     executeJavascript(content.arguments);
	            //    break;
	           // case "closeTab":
	             //   var args = getParametersFromMessage(content.arguments);
	             //   var oFunction = tabRemoveActiveInMain || window.parent.tabRemoveActiveInMain;
	             //   oFunction(args[0], args[1]);
	             //   break;
				 default:
				 	     break;             
			};
	    }
	    });
	    }
    }
	
	
	function updateUserList(users) {
		if (users!=null) {
			users.forEach(function(entry) {
				if (entry.owner_rtc!=null) {
					addUser(entry.owner_rtc);
				}
				if (entry.owner_av!=null) {
					addUser(entry.owner_av);
				}
				if (entry.owner!=null) {
					addUser(entry.owner);
				}			
				if (entry.friends_rtc!=null) {
					addUser(entry.friends_rtc);
				}	
				if (entry.friends_av!=null) {
					addUser(entry.friends_av);
				}
				if (entry.friends!=null) {
					addUser(entry.friends);
				}
				if (entry.user_rtc!=null) {
					addUser(entry.user_rtc);
				}
				if (entry.user_av!=null) {
					addUser(entry.user_av);
				}
				if (entry.user!=null) {
					addUser(entry.user);
				}
				if (entry.blocked!=null) {
				}
			});
			
		}	
	}

	function isJson(message) {
 		var input='{';
 		return new RegExp('^' + input).test(message);
	}

	function isReceivedMsg(message) {
		var input='_received';
 		return new RegExp( input +'$').test(message);
	}
	
	function processError(message) {
		console.log(message);
	}

	function verifyIsOn(uid) {
		var ison="false";
		var idx = loggedInUsers.indexOf(uid);
		if (idx != -1) {
			ison="true";
		}
		return ison;
	}	
	
	function addUser(uid) {
		var idx = loggedInUsers.indexOf(uid);
		if(idx == -1) {
			loggedInUsers.push(uid);
		}	
	}
	
	function processClose(message) {
		webSocket.send("DISCO:-"+user);
		$('#chatMessages').append(user+" disconnecting from server... \n");
		webSocket.close();
	}


	// Open connection only if we have frontuser variable    
 	function processOpen(message) {
    	<g:if test="${frontuser}">
    		webSocket.send("CONN:-${frontuser}");
       	</g:if>
       	<g:else>
       		$('#chatMessages').append("Chat denied no username \n");
       		webSocket.send("DISCO:-");
       	 	webSocket.close();
       </g:else>
 	}
 	


     window.onbeforeunload = function() {
    	 webSocket.send('/pm '+user+',close_connection');
    	 webSocket.send("DISCO:-");
       	webSocket.onclose = function() { }
       	webSocket.close();
     }
</g:javascript>