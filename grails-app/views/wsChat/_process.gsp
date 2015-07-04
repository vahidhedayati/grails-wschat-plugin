
<div id="task_1" style="font-size: 4em; font-weight: bold;"></div>

<div id="flipflop" style="font-size: 4em; font-weight: bold;"></div>

<g:javascript>
	var loggedInUsers=[];
	var user="${bean.user}";
	var receivers="${bean.receivers}"
	var arrayLength = receivers.length;
	// Connect websocket and set up processes 
	console.log('${bean.uri}${bean.room}  @@ ${bean.user}  >> ${bean.receivers} ${uri}');
	var webSocket=new WebSocket('${uri}');
 	webSocket.onopen=function(message) {processOpen(message);};
 	webSocket.onclose=function(message) {processClose(message);};
    webSocket.onerror=function(message) {processError(message);};
	webSocket.onmessage=function(message) {processMessage(message);	};
	
	// Global variables
	var userList=[];
	
	function processMessage( message) {
	console.log('Message '+message)
		//Create internal users list
		// and log out front end user when backend - real user has logged out
		var jsonData = JSON.parse(message.data);
		if (jsonData.flatusers!=null) {
			loggedInUsers=[];
			updateFlatUserList(jsonData.users);
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
				$('#task_1').html('Example: Doing task 1');
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
	    		console.log('--'+content+'---'+content.arguments)
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
	
	function updateFlatUserList(users) {
		if (users!=null) {
			users.forEach(function(entry) {
				if (entry.user!=null) {
					addUser(entry.user);
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
		console.log('closing');
		webSocket.send("DISCO:-"+user);
			 webSocket.send("DISCO:-${bean.user}${bean.frontUser}");
		//$('#chatMessages').append(user+" disconnecting from server... \n");
		webSocket.close();
	}


	// Open connection only if we have frontuser variable    
 	function processOpen(message) {
 		//webSocket.send("CONN:-${bean.user}");
    	<g:if test="${bean.frontUser}">
    		console.log('connecting');
    		webSocket.send("CONN:-${bean.user}${bean.frontUser}");
       	</g:if>
		<g:else>
			console.log('denied');
       		$('#chatMessages').append("Chat denied no username \n");
       		webSocket.send("DISCO:-");
       	 	webSocket.close();
       </g:else>
 	}
 	


     window.onbeforeunload = function() {
    	 webSocket.send('/pm '+user+',close_connection');
    	 webSocket.send("DISCO:-${bean.user}");
    	 webSocket.send("DISCO:-${bean.user}${bean.frontUser}");
       	webSocket.onclose = function() { }
       	webSocket.close();
     }
</g:javascript>