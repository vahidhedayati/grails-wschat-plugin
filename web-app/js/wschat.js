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

var isAdmin="false";

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

function wrapIt(value) {
	return "'"+value+"'"
}

function htmlEncode(value){
	return $('<div/>').text(value).html();
}

function adminOptions(isAdmin,user) {
	
	var sb = [];
	if (isAdmin=="true") {
		sb.push('<li class="btn-xs">\n');
		sb.push('<a onclick="javascript:kickuser('+wrapIt(user)+');">Kick  '+user+'</a>\n');
		sb.push('</li>\n');
		sb.push('<li class="btn-xs">\n');
		//sb.push('<a  data-toggle="modal" href="#banuser1"  onclick="javascript:banuser(getApp(),'+wrapIt(user)+');">Ban  '+user+'</a>\n');
		sb.push('<a  data-toggle="modal" href="#banuser1"  onclick="javascript:banuser('+wrapIt(user)+');">Ban  '+user+'</a>\n');
		
		sb.push('</li>\n');
	}
	return sb.join("")
}


function processMessage(message) {
	var jsonData=JSON.parse(message.data);

	if (jsonData.message!=null) {
		$('#chatMessages').append(htmlEncode(jsonData.message)+"\n");
	}
	if (jsonData.isAdmin!=null) {
		isAdmin=jsonData.isAdmin
	}
	
	if (jsonData.isBanned!=null) {
		$('#chatMessages').append(htmlEncode(jsonData.isBanned)+"\n");
		webSocket.send("DISCO:-"+user);
		$('#chatMessages').append(user+" disconnecting from server... \n");
		$('#onlineUsers').html("");
		messageBox.value="";
		webSocket.close();
	}
	
	if (jsonData.system!=null) {
		if (jsonData.system=="disconnect") { 
			webSocket.send("DISCO:-"+user);
			$('#chatMessages').append(user+" disconnecting from server... \n");
			$('#onlineUsers').html("");
			messageBox.value="";
			webSocket.close();
		}	
	}
	
	if (jsonData.users!=null) {

		$('#onlineUsers').html("");
		var sb = [];
		var sb1 = [];
		var sb2 = [];
		var sb3 = [];
		jsonData.users.forEach(function(entry) {
			if (entry.owner!=null) {
				sb.push('\n<li class="dropdown-submenu active btn-xs">\n<a tabindex="-1" class="user-title" href="#">'+entry.owner+'</a>\n');
				sb.push('<ul class="dropdown-menu">\n<li class="btn-xs"><a>'+entry.owner+'s profile</a>\n</li>\n</ul>\n</li>\n\n\n');
			}

			if (entry.friends!=null) {
				sb1.push('\n<li class="dropdown-submenu btn-warning btn-xs"><a tabindex="-1" class="user-title" href="#">** '+entry.friends+'</a>\n');
				sb1.push('<ul class="dropdown-menu">\n<li class="btn-xs">\n');
				sb1.push('<a onclick="javascript:pmuser('+wrapIt(entry.friends)+', '+wrapIt(user)+');">PM  '+entry.friends+'</a>\n');
				sb1.push('\n</li> \n');

				sb1.push('<li class="btn-xs"><a onclick="javascript:removefriend('+wrapIt(entry.friends)+', '+wrapIt(user)+');">Remove  '+entry.friends+' from friends list</a>\n');
				sb1.push('\n</li> ');

				sb1.push('<li class="btn-xs">\n');
				sb1.push('<a onclick="javascript:adduser('+wrapIt(entry.friends)+', '+wrapIt(user)+');">Add  '+entry.friends+'</a>\n');
				sb1.push('</li>\n');
				sb1.push('<li class="btn-xs">\n');
				sb1.push('<a onclick="javascript:blockuser('+wrapIt(entry.friends)+', '+wrapIt(user)+');">Block  '+entry.friends+'</a>\n');
				sb1.push('</li>\n');
				var admintool=adminOptions(isAdmin,entry.friend)
				sb1.push(admintool);
				sb1.push('</ul>\n</li>\n\n\n');
			}

			if (entry.user!=null) {
				sb2.push('\n<li class="dropdown-submenu btn-xs"><a tabindex="-1" class="user-title" href="#">'+entry.user+'</a>\n');
				sb2.push('<ul class="dropdown-menu">\n<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:pmuser('+wrapIt(entry.user)+', '+wrapIt(user)+');">PM  '+entry.user+'</a>\n');
				sb2.push('\n</li> ');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:adduser('+wrapIt(entry.user)+', '+wrapIt(user)+');">Add  '+entry.user+'</a>\n');
				sb2.push('</li class="btn-xs">\n');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:blockuser('+wrapIt(entry.user)+', '+wrapIt(user)+');">Block  '+entry.user+'</a>\n');
				sb2.push('</li>\n');
				
				var admintool=adminOptions(isAdmin,entry.user)
				sb2.push(admintool);
				sb2.push('</ul>\n</li>\n\n\n');

			}
			if (entry.blocked!=null) {
				sb3.push('\n<li class="dropdown-submenu btn-danger btn-xs"><a tabindex="-1" class="user-title" href="#">'+entry.blocked+'</a>\n');
				sb3.push('<ul class="dropdown-menu">\n<li class="btn-xs">\n');
				sb3.push('<a onclick="javascript:unblockuser('+wrapIt(entry.blocked)+', '+wrapIt(user)+');">UNBLOCK  '+entry.blocked+'</a>\n');
				sb3.push('\n</li> ');
				var admintool=adminOptions(isAdmin,entry.blocked)
				sb3.push(admintool);
				sb3.push('</ul>\n</li>\n\n\n');
				

			}

		});
		$('#onlineUsers').html(sb.join("")+sb1.join("")+sb2.join("")+sb3.join(""));
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

		$('#chatMessages').append("PM("+sender+"): "+jsonData.privateMessage+"\n");
		sendPM(receiver,sender,jsonData.privateMessage);

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

function blockuser(blockid,user) {
	webSocket.send("/block "+user+","+blockid);
	$('#chatMessages').append(blockid+" has been added to "+user+"'s blocklist\n");
}

function confirmBan(username,duration,period) { 
	webSocket.send("/banuser "+username+","+duration+":"+period);
	$('#chatMessages').append(username+" is about to be banned until: "+duration+" "+period+"\n");
}

function kickuser(user) {
	webSocket.send("/kickuser "+user);
	//$('#chatMessages').append(user+" has been kicked\n");
}
function banuser(user) {
	var s=document.getElementById('banUsername')
	s.value=user;
	$('#banuserField').html(user);
	$('#banuser').show();
}


function unblockuser(blockid,user) {
	webSocket.send("/unblock "+user+","+blockid);
	$('#chatMessages').append(blockid+" has been removed from "+user+"'s blocklist\n");
}

function adduser(addid,user) {
	webSocket.send("/add "+user+","+addid);
	$('#chatMessages').append(addid+" has been added to "+user+"'s friends list\n");
}

function removefriend(addid,user) {
	webSocket.send("/removefriend "+user+","+addid);
	$('#chatMessages').append(addid+" has been removed to "+user+"'s friends list\n");
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
	$('#chatMessages').append("Error.... <br/>");
}

function scrollToBottom() {
	$('#cmessage').scrollTop($('#cmessage')[0].scrollHeight);

}

