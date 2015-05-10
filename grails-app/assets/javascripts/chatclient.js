function WebChat() {
	/*
	* 	Private Attributes
	*/
	
	var that = this;
	var connection = false;
	var currentRoom;
	
    var chatMessages=document.getElementById("chatMessages");
    var onlineUsers=document.getElementById("onlineUsers");
    var messageBox=document.getElementById("messageBox");
	
	/*var video = $("#live").get()[0];
	var canvas = $("#canvas");
	var ctx
	if (!canvas) {
		ctx = canvas.get()[0].getContext('2d');
	}
	var options = {
		"video" : true,
		audio:true
	};
	 */
	



	this.close=function() {
		connection.close();
	}
	
	this.send = function(msg){
		connection.send(msg);
	}
	// this function handles all the websocket-stuff
	this.connectToSocket = function(wsUrl){
		// open the websocket
		connection = new WebSocket(wsUrl);
	    
	    connection.onopen=function(message) {
	    	console.log((new Date())+' Connection successfully established');
	    	processOpen(message);
	    };
	    
	    connection.onclose=function(message) {
	    	console.log((new Date())+' Connection was closed');
	    	processClose(message);
	    };
	    
	    connection.onerror=function(message) {
	    	console.log((new Date())+' WebSocket connection error: ');
	    	processError(message);
	    };
	    connection.onmessage=function(message) {processMessage(message);	};
			
	   function processOpen(message) {
	    	if (getUser()=="") {
	       		$('#chatMessages').append("Chat denied no username \n");
	       		connection.send("DISCO:-"+user);
	       		connection.close();
	    	}else{
	    		connection.send("CONN:-"+user);
	           	scrollToBottom();
	    	}
	 	}

	}
}