/* cut down process message for live chat users only */

function processMessage(message) {
	
	var jsonData=JSON.parse(message.data);

	if (debug == "on") {
		console.log('@onMessage: '+JSON.stringify(message.data));
	}
		
	if (jsonData.message!=null) {
		$('#chatMessages').append('<div id="msgBroadcast">'+jsonData.message.trim()+"<div>");
        scrollToBottom();
	}	
	if (jsonData.isBanned!=null) {
		$('#chatMessages').append(htmlEncode(jsonData.isBanned)+"\n");
		webSocket.send("DISCO:-"+user);
		$('#chatMessages').append(user+' '+disconnectingMessage+"\n");
		$('#onlineUsers').html("");
		messageBox.value="";
		webSocket.close();
	}
		
	if (jsonData.system!=null) {
		if (jsonData.system=="disconnect") { 
			webSocket.send("DISCO:-"+user);
			$('#chatMessages').append(user+' '+disconnectingMessage+"\n");
			$('#onlineUsers').html("");
			messageBox.value="";
			webSocket.close();
		}
	}
	
	/* 
	 * Transformation of Live Chat PM completed 
	 * Send the response from admin back to matching endUser
	 * search for liveMessageResponse or adminLiveMessage
	 * to understand better
	 */
	if (jsonData.liveMessageResponse!=null) {
		var sender
		if (jsonData.msgFrom!=null) {
			sender=jsonData.msgFrom
		}
		$('#chatMessages').append('<div id="msgReceived"><span class="msgPersonReceived">'+sender+':</span> <span class="msgReceivedContent">'+htmlEncode(jsonData.liveMessageResponse)+'</span></div>');
	}
	
	/*
	 * Just like above but part of dual trigger of admin
	 * joining a live room
	 */
	if (jsonData.liveMessageInitiate!=null) {
		$('#chatMessages').append('<div id="liveInitialisation">'+jsonData.liveMessageInitiate+"</div>");
	}

	
	/*
	 * this is triggered as soon as admin joins liveChat this then enables
	 * that chat input/send segment on any/all end users that are connected   
	 */
	if (jsonData.enabeLiveChat!=null) {
		$('#messageBox').prop("readonly", false);
		$('#waiting').show();
		$('#chatDialog').css('height', '435');
		$(".ui-widget-header,.ui-state-default,.ui-button").css(at);
		$(".ui-dialog-content,.ui-widget-content").css(ab);
	}
	
	
	scrollToBottom();
	
}
