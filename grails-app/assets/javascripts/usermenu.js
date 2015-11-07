/**
 * 
 * @param message
 */	

function processMessage(message) {
	
	var jsonData=JSON.parse(message.data);

	if (debug == "on") {
		console.log('@onMessage: '+JSON.stringify(message.data));
	}
		
	if (jsonData.message!=null) {
		$('#chatMessages').append('<div id="msgBroadcast">'+jsonData.message.trim()+"<div>");
        scrollToBottom();
	}
	
	if (jsonData.isAdmin!=null) {
		isAdmin=jsonData.isAdmin
	}

	if (jsonData.isBanned!=null) {
		$('#chatMessages').append(htmlEncode(jsonData.isBanned)+"\n");
		webSocket.send("DISCO:-"+user);
		$('#chatMessages').append(user+' '+disconnectingMessage+"\n");
		$('#onlineUsers').html("");
		messageBox.value="";
		webSocket.close();
	}
	
	if (jsonData.game!=null) {
		if (jsonData.game=="restartGame") {
			$('body').removeClass('modal-open');
  			$('.modal-backdrop').remove();
			sendGame();
		}
		if (jsonData.game=="restartOpponent") {
			$('body').removeClass('modal-open');
  			$('.modal-backdrop').remove();
			
			setTimeout(function(){
				getGame(room);
			}, 500);
		}
	}
	
	if (jsonData.system!=null) {
		if (jsonData.system=="disconnect") { 
			webSocket.send("DISCO:-"+user);
			$('#chatMessages').append(user+' '+disconnectingMessage+"\n");
			$('#onlineUsers').html("");
			messageBox.value="";
			webSocket.close();
		}
		if (jsonData.system=="closecam") { 
			popup_window.close ();
			webSocket.send("/camdisabled "+user);
		}
	}
	
	if (jsonData.users!=null) {
		$('#onlineUsers').html("");
		var sb = [];
		var sb1 = [];
		var sb2 = [];
		var sb3 = [];
		var sb4 = [];
		var sb5 = [];
		
		jsonData.users.forEach(function(entry) {
			if (entry.owner_rtc!=null) {
				sb.push(addOwnerOptions(entry.owner_rtc, 'glyphicon-facetime-video glyphicon-volume-up'));
				rtcon(entry.owner_rtc);
			}
			if (entry.owner_file!=null) {
				sb.push(addOwnerOptions(entry.owner_file, 'glyphicon-file',''));
				fileshareon(entry.owner_file);
			}
			if (entry.owner_game!=null) {
				sb.push(addOwnerOptions(entry.owner_game, 'glyphicon-tower',''));
				addGame(entry.owner_game);
			}
			if (entry.owner_mediastream!=null) {
				sb.push(addOwnerOptions(entry.owner_mediastream, 'glyphicon-film',''));
				mediashareon(entry.owner_mediastream);
			}
			if (entry.owner_av!=null) {
				sb.push(addOwnerOptions(entry.owner_av, 'glyphicon-facetime-video',''));
				camon(entry.owner_av);
			}
			if (entry.owner!=null) {
				sb.push(addOwnerOptions(entry.owner, '','owner'));
			}
			/*------------------------------------------------------------------------------------------*/
			if (entry.friends_rtc!=null) {
				rtcon(entry.friends_rtc);
				sb1.push(addFriendOptions(user,entry.friends_rtc,'friend','rtc','glyphicon-facetime-video glyphicon-volume-up','chatFriend'));
			}
			if (entry.friends_av!=null) {
				sb1.push(addFriendOptions(user,entry.friends_av,'friend','av','glyphicon-facetime-video','chatFriend'));
				camon(entry.friends_av);
			}
			if (entry.friends_game!=null) {
				sb1.push(addFriendOptions(user,entry.friends_game,'friend','game',' glyphicon-tower','chatFriend'));
				addGame(entry.friends_game);
			}
			if (entry.friends_file!=null) {
				sb1.push(addFriendOptions(user,entry.friends_file,'friend','file',' glyphicon-file','chatFriend'));
				fileshareon(entry.friends_file);
			}
			if (entry.friends_mediastream!=null) {
				sb1.push(addFriendOptions(user,entry.friends_mediastream,'friend','mediastream',' glyphicon-film','chatFriend'));
				mediashareon(entry.friends_mediastream);
			}
			if (entry.friends!=null) {
				sb1.push(addFriendOptions(user,entry.friends,'friend','','','chatFriend'));				
			}
			/*------------------------------------------------------------------------------------------*/
			if (entry.user_rtc!=null) {
				rtcon(entry.friends_rtc);
				sb2.push(addFriendOptions(user,entry.user_rtc,'foe','rtc','glyphicon-facetime-video glyphicon-volume-up','chatUser'));
			}
			if (entry.user_av!=null) {
				sb2.push(addFriendOptions(user,entry.user_av,'foe','av','glyphicon-facetime-video','chatUser'));
				camon(entry.user_av);
			}
			if (entry.user_game!=null) {
				sb2.push(addFriendOptions(user,entry.user_game,'foe','game',' glyphicon-tower','chatUser'));
				addGame(entry.user_game);
			}
			if (entry.user_mediastream!=null) {
				sb2.push(addFriendOptions(user,entry.user_mediastream,'foe','mediastream',' glyphicon-film','chatUser'));
				mediashareon(entry.user_mediastream);
			}
			if (entry.user_file!=null) {
				sb2.push(addFriendOptions(user,entry.user_file,'foe','file',' glyphicon-file','chatUser'));
				fileshareon(entry.user_file);
			}
			if (entry.user_livechat!=null) {
				sb1.push(addFriendOptions(user,entry.user_livechat,'foe','livechat','','chatUser'));
			}
			if (entry.user!=null) {
				sb1.push(addFriendOptions(user,entry.user,'foe','','','chatUser'));
			}
			/*------------------------------------------------------------------------------------------*/
			if (entry.blocked!=null) {
				sb3.push(addFriendOptions(user,entry.blocked,'foe','','chatBlocked'));
			}
			/*------------------------------------------------------------------------------------------*/
			if (entry.offline_friends!=null) {
				sb4.push(addFriendOptions(user,entry.offline_friends,'friend','','chatOffline'));
			}
			/*------------------------------------------------------------------------------------------*/
			if (entry.online_friends!=null) {
				sb5.push(addFriendOptions(user,entry.online_friends,'friend','','chatOnline'));
			}
			

		});
		$('#onlineUsers').html(sb.join("")+sb1.join("")+sb2.join("")+sb3.join(""));
		
		$('#friendsList').html(sb5.join("")+sb4.join(""))
	}
	if (jsonData.currentRoom!=null) {
		currentRoom=jsonData.currentRoom
	}
	if (jsonData.liveChatMode!=null) {
		window.location.href = "/wsChat/joinLiveChat?roomName="+jsonData.liveChatMode+"&username="+user;
	}
	if (jsonData.rooms!=null) {
		var rms = [];
		jsonData.rooms.forEach(function(entry) {
			if (entry.room!=null) {
				if (currentRoom == entry.room) {
					rms.push('<li class="btn btn-default btn-xs"><b>'+entry.room+'</b></li>\n');
				}else{
    				rms.push('<li class="btn btn-default btn-xs"><a onclick="javascript:joinRoom('+wrapIt(user)+','+wrapIt(entry.room)+');">'+entry.room+'</a></li>\n');
    			}
    		}
    	});
    	$('#chatRooms').html(rms.join(""));
    	if (isAdmin=="true") {
    		$('#adminMenu').show();
    		var roomOptions = [];
    		roomOptions.push('<a data-toggle="modal" href="#roomcontainer1" class="glyphicon glyphicon-plus"');
    		roomOptions.push('onclick="javascript:addaRoom('+wrapIt(user)+');" title="Add a Room"></a>');
    		roomOptions.push('<a data-toggle="modal" href="#roomcontainer1" class="glyphicon glyphicon-minus "');
    		roomOptions.push('onclick="javascript:delaRoom('+wrapIt(user)+');" title="Remove a Room"></a>');
			$('#adminRoomOptions').html(roomOptions.join(""));
    	}
    }

    /*
     * this makes admins menu up from map entry returned.
     */
    if (jsonData.adminOptions!=null && isAdmin=="true") {
    	var roomOptions = [];
    	jsonData.adminOptions.forEach(function(entry) {
    		if (entry.actions!=null) {
    			roomOptions.push('<li><a data-toggle="modal" href="#admincontainer1" onclick="javascript:'+entry.actions+'('+wrapIt(user)+');" >');
    			//convert back result to il8n variable conversion
    			var converted= window[entry.actions+'Label'];
    			roomOptions.push(converted+'</a></li>\n');
    		}
    	});
    	$('#adminOptions').html(roomOptions.join(""));
    }

	/* 
	 * Convert usres Live Chat Message into a PM
	 * send it as new SendLivechatPm
	 * search for liveMessage or clientLiveMessage
	 */
	if (jsonData.liveMessage!=null) {
		var receiver
		var sender
		var room
		if (jsonData.msgFrom!=null) {
			sender=jsonData.msgFrom
		}
		if (jsonData.msgTo!=null) {
			receiver=jsonData.msgTo
		}
		if (jsonData.fromRoom!=null) {
			room=jsonData.fromRoom
		}
		$('#chatMessages').append('<div id="msgSent"><span class="msgPersonSent">'+sender+':</span> <span class="msgSentContent">'+jsonData.liveMessage+'</span></div>');
		sendLiveChatPM(receiver,sender,jsonData.liveMessage,room);
	}
	
	/* 
	 * Transformation of Live Chat PM completed 
	 * Send the response from admin back to matching endUser
	 * search for liveMessageResponse or adminLiveMessage
	 * to understand better
	 */
	if (jsonData.liveMessageResponse!=null) {
		var receiver
		var sender
		var room
		if (jsonData.msgFrom!=null) {
			sender=jsonData.msgFrom
		}
		if (jsonData.msgTo!=null) {
			receiver=jsonData.msgTo
		}
		if (jsonData.fromRoom!=null) {
			room=jsonData.fromRoom
		}
		//TODO - VH
		$('#chatMessages').append(sender+": "+jsonData.liveMessageResponse+"\n");

	}
	
	/*
	 * Just like above but part of dual trigger of admin
	 * joining a live room
	 */
	if (jsonData.liveMessageInitiate!=null) {
		$('#chatMessages').append(jsonData.liveMessageInitiate+"\n");
	}

	
	/*
	 * this is triggered as soon as admin joins liveChat this then enables
	 * that chat input/send segment on any/all end users that are connected   
	 */
	if (jsonData.enabeLiveChat!=null) {
		$('#messageBox').prop("readonly", false);
		$('#waiting').show();
		 $('#chatDialog').css('height', '440');
		 
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

	scrollToBottom();	
}
	

function adminOptions(isAdmin,user) {
	var sb = [];
	if (isAdmin=="true") {
		sb.push('<li class="btn-xs" id="sideBar">\n');
		sb.push('<a onclick="javascript:kickuser('+wrapIt(user)+');">'+kick+' '+user+'</a>\n');
		sb.push('</li>\n');
		sb.push('<li class="btn-xs" id="sideBar">\n');
		sb.push('<a  data-toggle="modal" href="#banuser1"  onclick="javascript:banuser('+wrapIt(user)+');">'+ban+' '+user+'</a>\n');
		sb.push('</li>\n');
	}
	return sb.join("")
}


function addFriendOptions(user,friend,userType,requestType,glyphicons,id) {
	var sb = [];
	sb.push('\n<li class="dropdown-submenu bttn-xs" id="'+id+'"><a tabindex="-1" id="user-title" class="user-title glyphicon '+glyphicons+'" >'+friend+'</a>\n');
	sb.push('<ul class="dropdown-menu">\n');
	sb.push('<li class="btn-xs" id="sideBar">\n');
	sb.push('<a  data-toggle="modal" href="#userprofile1"  onclick="javascript:userprofile('+wrapIt(friend)+');">'+friend+' '+profile+'</a>\n');
	sb.push('</li>\n');
	if (id=='chatBlocked') {
		sb.push('<li class="btn-xs" id="sideBar">\n');
		sb.push('<a onclick="javascript:unblockuser('+wrapIt(friend)+', '+wrapIt(user)+');">'+unblock+' '+friend+'</a>\n');
		sb.push('\n</li> ');
	}else if (requestType=='livechat') {
    	sb.push('<li class="btn-xs" id="sideBar">\n');
    	sb.push('<a onclick="javascript:livepmuser('+wrapIt(friend)+', '+wrapIt(user)+','+wrapIt(getRoom())+');">'+liveChatPM+'  '+friend+'</a>\n');
    	sb.push('\n</li> \n');
	} else {
		if (id=='chatOffline') {
			sb.push('<li class="btn-xs" id="sideBar">\n');
			sb.push('<a onclick="javascript:pmuser('+wrapIt(friend)+', '+wrapIt(user)+');"><i>'+offlinepm+' '+friend+'</i></a>\n');
			sb.push('\n</li> ');
		}
		
		sb.push('<li class="btn-xs" id="sideBar">\n');
		sb.push('<a onclick="javascript:pmuser('+wrapIt(friend)+', '+wrapIt(user)+');">'+pm+' '+friend+'</a>\n');
		sb.push('\n</li> \n');
		
		if (userType=='friend') {
			sb.push('<li class="btn-xs" id="sideBar"><a onclick="javascript:removefriend('+wrapIt(friend)+', '+wrapIt(user)+');">'+removeFriend+'</a>\n');
			sb.push('\n</li> ');
		} else if (userType=='foe') {
			sb.push('<li class="btn-xs" id="sideBar">\n');
			sb.push('<a onclick="javascript:adduser('+wrapIt(friend)+', '+wrapIt(user)+');">'+add+' '+friend+'</a>\n');
			sb.push('</li>\n');
			sb.push('<li class="btn-xs" id="sideBar">\n');
			sb.push('<a onclick="javascript:blockuser('+wrapIt(friend)+', '+wrapIt(user)+');">'+block+'  '+friend+'</a>\n');
			sb.push('</li>\n');
		}

		if (requestType=='rtc') {
			sb.push('<li class="btn-xs" id="sideBar">\n');
			sb.push('<a onclick="javascript:enableCam('+wrapIt(friend)+','+wrapIt('view')+','+wrapIt('webrtc')+');">'+webrtc+'</a>\n');
			sb.push('</li>\n');
			sb.push('<li class="btn-xs" id="sideBar">\n');
			sb.push('<a onclick="javascript:enableCam('+wrapIt(friend)+','+wrapIt('view')+','+wrapIt('webrtcscreen')+');">'+webrtcScreen+'</a>\n');
			sb.push('</li>\n');
		}
		if (requestType=='av') {
			sb.push('<li class="btn-xs" id="sideBar">\n');
			sb.push('<a onclick="javascript:enableCam('+wrapIt(friend)+','+wrapIt('view')+','+wrapIt('webcam')+');">'+viewCamera+'</a>\n');
			sb.push('</li>\n');
		}
		if (requestType=='game') {
			sb.push('<li class="btn-xs" id="sideBar">\n');
			sb.push('<a onclick="javascript:enableCam('+wrapIt(friend)+','+wrapIt('view')+','+wrapIt('game')+');">'+tictactoe+'</a>\n');
			sb.push('</li>\n');
		}
		if (requestType=='file') {
			sb.push('<li class="btn-xs" id="sideBar">\n');
			sb.push('<a onclick="javascript:enableCam('+wrapIt(friend)+','+wrapIt('view')+','+wrapIt('fileshare')+');">'+fileSharing+'</a>\n');
			sb.push('</li>\n');
		}
		if (requestType=='mediastream') {
			sb.push('<li class="btn-xs" id="sideBar">\n');
			sb.push('<a onclick="javascript:enableCam('+wrapIt(friend)+','+wrapIt('view')+','+wrapIt('mediastream')+');">'+mediaStreaming+'</a>\n');
			sb.push('</li>\n');
		}
	}	
	var admintool=adminOptions(isAdmin,friend)
	sb.push(admintool);
	sb.push('</ul>\n</li>\n\n\n');
	return sb.join("")
}

function addOwnerOptions(entry, glyphicons,userType) {
	var sb = [];
	sb.push('\n<li class="dropdown-submenu bttn-xs" id="ownerBar">\n<a tabindex="-1" id="user-title" class="user-title glyphicon  '+glyphicons+'">'+entry+'</a>\n');
	sb.push('<ul class="dropdown-menu">\n');
	sb.push('<li class="btn-xs" id="sideBar">\n');
	sb.push('<a  data-toggle="modal" href="#userprofile1"  onclick="javascript:userprofile('+wrapIt(entry)+');">'+entry+' '+profile+'</a>\n');
	sb.push('</li>\n');
	if (userType=='owner') {
		sb.push('<li class="btn-xs" id="sideBar">\n');
		sb.push('<a  onclick="javascript:enableCam('+wrapIt(entry)+','+wrapIt('send')+','+wrapIt('webcam')+');">'+enableCam+'</a>\n');
		sb.push('</li>\n');
		sb.push('<li class="btn-xs" id="sideBar">\n');
		sb.push('<a  onclick="javascript:enableCam('+wrapIt(entry)+','+wrapIt('send')+','+wrapIt('webrtc')+');">'+enableRTC+'</a>\n');
		sb.push('</li>\n');
		sb.push('<li class="btn-xs" id="sideBar">\n');
		sb.push('<a  onclick="javascript:enableCam('+wrapIt(entry)+','+wrapIt('send')+','+wrapIt('webrtcscreen')+');">'+enableRTCScreen+'</a>\n');
		sb.push('</li>\n');
		sb.push('<li class="btn-xs" id="sideBar">\n');
		sb.push('<a  onclick="javascript:enableCam('+wrapIt(entry)+','+wrapIt('send')+','+wrapIt('fileshare')+');">'+enableFileSharing+'</a>\n');
		sb.push('</li>\n');
		sb.push('<li class="btn-xs" id="sideBar">\n');
		sb.push('<a  onclick="javascript:enableCam('+wrapIt(entry)+','+wrapIt('send')+','+wrapIt('mediastream')+');">'+enableMediaStreaming+'</a>\n');
		sb.push('</li>\n');
		sb.push('<li class="btn-xs" id="sideBar">\n');
		sb.push('<a  onclick="javascript:enableCam('+wrapIt(entry)+','+wrapIt('send')+','+wrapIt('game')+');">'+enableTicTacToe+'</a>\n');
		sb.push('</li>\n');
	}
	sb.push('</ul>\n</li>\n\n\n');
	return sb.join("")
}

