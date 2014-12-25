var config = {
		width : 155, //px
		gap : 2,
		maxBoxes : 5,
		messageSent : function(dest, msg) {
			// override this
			$("#" + dest).chatbox("option", "boxManager").addMsg(dest, msg);
		}
};

function verifyAdded(uid) {
	var added="false";
	var idx = idList.indexOf(uid);
	if (idx != -1) {
		added="true";
	}
	return added;
}		


function adduList(uid) { 
	var idx = idList.indexOf(uid);
	if(idx == -1) {
		idList.push(uid);
	}	
}

function deluList(uid) {
	var i = idList.indexOf(uid);
	if(i != -1) {
		idList.splice(i, 1);
	}
}

function convertToBinary (dataURI) {
	// convert base64 to raw binary data held in a string
	// doesn't handle URLEncoded DataURIs
	var byteString = atob(dataURI.split(',')[1]);
	// separate out the mime component
	var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0]
	// write the bytes of the string to an ArrayBuffer
	var ab = new ArrayBuffer(byteString.length);
	var ia = new Uint8Array(ab);
	for (var i = 0; i < byteString.length; i++) {
		ia[i] = byteString.charCodeAt(i);
	}
	// write the ArrayBuffer to a blob, and you're done
	var bb = new Blob([ab]);
	return bb;
}   

function hasGetUserMedia() {
	// Note: Opera is unprefixed.
	return !!(navigator.getUserMedia || navigator.webkitGetUserMedia ||
			navigator.mozGetUserMedia || navigator.msGetUserMedia);
}



function verifyPosition(uid) {
	var idx = idList.indexOf(uid);
	if(idx == -1) {
		idList.push(uid);
	}	
	if (idList.length>1) { 
		var getNextOffset = function() {
			return (config.width + config.gap) * idList.length;
		};	
		$("#"+uid).chatbox("option", "offset", getNextOffset());
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
		sb.push('<a  data-toggle="modal" href="#banuser1"  onclick="javascript:banuser('+wrapIt(user)+');">Ban  '+user+'</a>\n');
		sb.push('</li>\n');
	}
	return sb.join("")
}

function adminRooms(isAdmin) {
	var sb = [];
	if (isAdmin=="true") {
		sb.push('<ul class="nav-pills pull-right">');
		sb.push('<li class="btn-success btn"><a data-toggle="modal" href="#roomcontainer1" class="glyphicon glyphicon-plus" onclick="javascript:addaRoom('+wrapIt(user)+');" title="Add a Room"></a></li>');
		sb.push('<li class="btn-danger btn"><a data-toggle="modal" href="#roomcontainer1" class="glyphicon glyphicon-minus" onclick="javascript:delaRoom('+wrapIt(user)+');" title="Remove a Room"></a></li>');
		sb.push('</ul>');
	}
	return sb.join("")
}	

function processChatMessage(message) {
	var jsonData=JSON.parse(message.data);
	if (jsonData.system!=null) {
		if (jsonData.system=="disconnect") { 
			view_cam_window.close ();
			//ws.send("DISCO:-"+user);
			WebCam.close();
		}	
	}
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
		
		// for games use glyphicon-tower
		
		jsonData.users.forEach(function(entry) {
			if (entry.owner_rtc!=null) {
				sb.push('\n<li class="dropdown-submenu active btn-xs">\n<a tabindex="-1" class="user-title glyphicon glyphicon-facetime-video glyphicon-volume-up">'+entry.owner_rtc+'</a>\n');
				sb.push('<ul class="dropdown-menu">\n');
				sb.push('<li class="btn-xs">\n');
				sb.push('<a  data-toggle="modal" href="#userprofile1"  onclick="javascript:userprofile('+wrapIt(entry.owner_rtc)+');">'+entry.owner_rtc+'\'s profile</a>\n');
				sb.push('</li>\n');
				//sb.push('<li class="btn-xs">\n');
				//sb.push('<a  onclick="enableCam('+wrapIt(entry.owner_rtc)+','+wrapIt('disable')+','+wrapIt('webrtc')+');">Disable WebRTC</a>\n');
				//sb.push('</li>\n');
				camoff(entry.owner_rtc);
				rtcon(entry.owner_rtc);
				sb.push('</ul>\n</li>\n\n\n');
			}	
			if (entry.owner_av!=null) {
				sb.push('\n<li class="dropdown-submenu active btn-xs">\n<a tabindex="-1" class="user-title glyphicon glyphicon-facetime-video " >'+entry.owner_av+'</a>\n');
				sb.push('<ul class="dropdown-menu">\n');
				sb.push('<li class="btn-xs">\n');
				sb.push('<a  data-toggle="modal" href="#userprofile1"  onclick="javascript:userprofile('+wrapIt(entry.owner_av)+');">'+entry.owner_av+'\'s profile</a>\n');
				sb.push('</li>\n');
			//	sb.push('<li class="btn-xs">\n');
			//	sb.push('<a  onclick="enableCam('+wrapIt(entry.owner_av)+','+wrapIt('disable')+','+wrapIt('webcam')+');">Disable Webcam</a>\n');
			//	sb.push('</li>\n');
				camon(entry.owner_av);
				rtcoff(entry.owner_av);
				sb.push('</ul>\n</li>\n\n\n');
			}
			if (entry.owner!=null) {
				sb.push('\n<li class="dropdown-submenu active btn-xs">\n<a tabindex="-1" class="user-title" href="#">'+entry.owner+'</a>\n');
				sb.push('<ul class="dropdown-menu">\n');
				sb.push('<li class="btn-xs">\n');
				sb.push('<a  data-toggle="modal" href="#userprofile1"  onclick="javascript:userprofile('+wrapIt(entry.owner)+');">'+entry.owner+'\'s profile</a>\n');
				sb.push('</li>\n');
				sb.push('<li class="btn-xs">\n');
				sb.push('<a  onclick="javascript:enableCam('+wrapIt(entry.owner)+','+wrapIt('send')+','+wrapIt('webcam')+');">Enable Webcam</a>\n');
				sb.push('</li>\n');
				sb.push('<li class="btn-xs">\n');
				sb.push('<a  onclick="javascript:enableCam('+wrapIt(entry.owner)+','+wrapIt('send')+','+wrapIt('webrtc')+');">Enable WebRTC</a>\n');
				sb.push('</li>\n');
	
				sb.push('<li class="btn-xs">\n');
				sb.push('<a  onclick="javascript:enableCam('+wrapIt(entry.owner)+','+wrapIt('send')+','+wrapIt('webrtcscreen')+');">WebRTC ScreenShare</a>\n');
				sb.push('</li>\n');
	
				camoff(entry.owner);
				rtcoff(entry.owner);
				sb.push('</ul>\n</li>\n\n\n');
			}
			if (entry.friends_rtc!=null) {
				sb1.push('\n<li class="dropdown-submenu btn-warning btn-xs"><a tabindex="-1" class="user-title glyphicon glyphicon-facetime-video glyphicon-volume-up" >'+entry.friends_rtc+'</a>\n');
				sb1.push('<ul class="dropdown-menu">\n');
				sb1.push('<li class="btn-xs">\n');
				sb1.push('<a  data-toggle="modal" href="#userprofile1"  onclick="javascript:userprofile('+wrapIt(entry.friends_rtc)+');">'+entry.friends_rtc+'\'s profile</a>\n');
				sb1.push('</li>\n');
				sb1.push('<li class="btn-xs">\n');
				sb1.push('<a onclick="javascript:pmuser('+wrapIt(entry.friends_rtc)+', '+wrapIt(user)+');">PM  '+entry.friends_rtc+'</a>\n');
				sb1.push('\n</li> \n');

				sb1.push('<li class="btn-xs"><a onclick="javascript:removefriend('+wrapIt(entry.friends_rtc)+', '+wrapIt(user)+');">Remove  '+entry.friends_rtc+' from friends list</a>\n');
				sb1.push('\n</li> ');
				camoff(entry.friends_rtc);
				rtcon(entry.friends_rtc);
				sb1.push('<li class="btn-xs">\n');
				sb1.push('<a onclick="javascript:enableCam('+wrapIt(entry.friends_rtc)+','+wrapIt('view')+','+wrapIt('webrtc')+');">WebRTC</a>\n');
				sb1.push('</li>\n');
				sb1.push('<li class="btn-xs">\n');
				sb1.push('<a onclick="javascript:enableCam('+wrapIt(entry.friends_rtc)+','+wrapIt('view')+','+wrapIt('webrtcscreen')+');">WebRTC Screen</a>\n');
				sb1.push('</li>\n');
				var admintool=adminOptions(isAdmin,entry.friend)
				sb1.push(admintool);
				sb1.push('</ul>\n</li>\n\n\n');
			}
			
			if (entry.friends_av!=null) {
				sb1.push('\n<li class="dropdown-submenu btn-warning btn-xs"><a tabindex="-1" class="user-title glyphicon glyphicon-facetime-video">'+entry.friends_av+'</a>\n');
				sb1.push('<ul class="dropdown-menu">\n');
				sb1.push('<li class="btn-xs">\n');
				sb1.push('<a  data-toggle="modal" href="#userprofile1"  onclick="javascript:userprofile('+wrapIt(entry.friends_av)+');">'+entry.friends_av+'\'s profile</a>\n');
				sb1.push('</li>\n');
				sb1.push('<li class="btn-xs">\n');
				sb1.push('<a onclick="javascript:pmuser('+wrapIt(entry.friends_av)+', '+wrapIt(user)+');">PM  '+entry.friends_av+'</a>\n');
				sb1.push('\n</li> \n');

				sb1.push('<li class="btn-xs"><a onclick="javascript:removefriend('+wrapIt(entry.friends_av)+', '+wrapIt(user)+');">Remove  '+entry.friends_av+' from friends list</a>\n');
				sb1.push('\n</li> ');
				camon(entry.friends_av);
				rtcoff(entry.friends_av);
				sb1.push('<li class="btn-xs">\n');
				sb1.push('<a onclick="javascript:enableCam('+wrapIt(entry.friends_av)+','+wrapIt('view')+','+wrapIt('webcam')+');">View Camera</a>\n');
				sb1.push('</li>\n');
				var admintool=adminOptions(isAdmin,entry.friend)
				sb1.push(admintool);
				sb1.push('</ul>\n</li>\n\n\n');
			}

			if (entry.friends!=null) {
				sb1.push('\n<li class="dropdown-submenu btn-warning btn-xs"><a tabindex="-1" class="user-title" href="#">'+entry.friends+'</a>\n');
				sb1.push('<ul class="dropdown-menu">\n');
				sb1.push('<li class="btn-xs">\n');
				sb1.push('<a  data-toggle="modal" href="#userprofile1"  onclick="javascript:userprofile('+wrapIt(entry.friends)+');">'+entry.friends+'\'s profile</a>\n');
				sb1.push('</li>\n');
				sb1.push('<li class="btn-xs">\n');
				sb1.push('<a onclick="javascript:pmuser('+wrapIt(entry.friends)+', '+wrapIt(user)+');">PM  '+entry.friends+'</a>\n');
				sb1.push('\n</li> \n');

				sb1.push('<li class="btn-xs"><a onclick="javascript:removefriend('+wrapIt(entry.friends)+', '+wrapIt(user)+');">Remove  '+entry.friends+' from friends list</a>\n');
				sb1.push('\n</li> ');
				camoff(entry.friends);
				rtcoff(entry.friends);
				var admintool=adminOptions(isAdmin,entry.friend)
				sb1.push(admintool);
				sb1.push('</ul>\n</li>\n\n\n');
			}
			if (entry.user_rtc!=null) {
				sb2.push('\n<li class="dropdown-submenu btn-xs"><a tabindex="-1" class="user-title glyphicon glyphicon-facetime-video glyphicon-volume-up">'+entry.user_rtc+'</a>\n');
				sb2.push('<ul class="dropdown-menu">\n');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a  data-toggle="modal" href="#userprofile1"  onclick="javascript:userprofile('+wrapIt(entry.user_rtc)+');">'+entry.user_rtc+'\'s profile</a>\n');
				sb2.push('</li>\n');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:pmuser('+wrapIt(entry.user_rtc)+', '+wrapIt(user)+');">PM  '+entry.user_rtc+'</a>\n');
				sb2.push('\n</li> ');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:adduser('+wrapIt(entry.user_rtc)+', '+wrapIt(user)+');">Add  '+entry.user_rtc+'</a>\n');
				sb2.push('</li class="btn-xs">\n');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:blockuser('+wrapIt(entry.user_rtc)+', '+wrapIt(user)+');">Block  '+entry.user_rtc+'</a>\n');
				sb2.push('</li>\n');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:enableCam('+wrapIt(entry.user_rtc)+','+wrapIt('view')+','+wrapIt('webrtc')+');">WebRTC</a>\n');
				sb2.push('</li>\n');	
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:enableCam('+wrapIt(entry.user_rtc)+','+wrapIt('view')+','+wrapIt('webrtcscreen')+');">WebRTC Screen</a>\n');
				sb2.push('</li>\n');	
				
				camoff(entry.user_rtc);
				rtcon(entry.user_rtc);
				var admintool=adminOptions(isAdmin,entry.user_rtc)
				sb2.push(admintool);
				sb2.push('</ul>\n</li>\n\n\n');
			}
			
			if (entry.user_av!=null) {
				sb2.push('\n<li class="dropdown-submenu btn-xs"><a tabindex="-1" class="user-title glyphicon glyphicon-facetime-video">'+entry.user_av+'</a>\n');
				sb2.push('<ul class="dropdown-menu">\n');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a  data-toggle="modal" href="#userprofile1"  onclick="javascript:userprofile('+wrapIt(entry.user_av)+');">'+entry.user_av+'\'s profile</a>\n');
				sb2.push('</li>\n');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:pmuser('+wrapIt(entry.user_av)+', '+wrapIt(user)+');">PM  '+entry.user_av+'</a>\n');
				sb2.push('\n</li> ');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:adduser('+wrapIt(entry.user_av)+', '+wrapIt(user)+');">Add  '+entry.user_av+'</a>\n');
				sb2.push('</li class="btn-xs">\n');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:blockuser('+wrapIt(entry.user_av)+', '+wrapIt(user)+');">Block  '+entry.user_av+'</a>\n');
				sb2.push('</li>\n');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:enableCam('+wrapIt(entry.user_av)+','+wrapIt('view')+','+wrapIt('webcam')+');">View Camera</a>\n');
				sb2.push('</li>\n');
				camon(entry.user_av);
				rtcoff(entry.user_av);
				var admintool=adminOptions(isAdmin,entry.user_av)
				sb2.push(admintool);
				sb2.push('</ul>\n</li>\n\n\n');
			}

			if (entry.user!=null) {
				sb2.push('\n<li class="dropdown-submenu btn-xs"><a tabindex="-1" class="user-title" href="#">'+entry.user+'</a>\n');
				sb2.push('<ul class="dropdown-menu">\n');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a  data-toggle="modal" href="#userprofile1"  onclick="javascript:userprofile('+wrapIt(entry.user)+');">'+entry.user+'\'s profile</a>\n');
				sb2.push('</li>\n');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:pmuser('+wrapIt(entry.user)+', '+wrapIt(user)+');">PM  '+entry.user+'</a>\n');
				sb2.push('\n</li> ');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:adduser('+wrapIt(entry.user)+', '+wrapIt(user)+');">Add  '+entry.user+'</a>\n');
				sb2.push('</li class="btn-xs">\n');
				sb2.push('<li class="btn-xs">\n');
				sb2.push('<a onclick="javascript:blockuser('+wrapIt(entry.user)+', '+wrapIt(user)+');">Block  '+entry.user+'</a>\n');
				sb2.push('</li>\n');
				camoff(entry.user);
				rtcoff(entry.user);
				var admintool=adminOptions(isAdmin,entry.user)
				sb2.push(admintool);
				sb2.push('</ul>\n</li>\n\n\n');
			}

			if (entry.blocked!=null) {
				sb3.push('\n<li class="dropdown-submenu btn-danger btn-xs"><a tabindex="-1" class="user-title">'+entry.blocked+'</a>\n');
				sb3.push('<ul class="dropdown-menu">\n');
				sb3.push('<li class="btn-xs">\n');
				sb3.push('<a  data-toggle="modal" href="#userprofile1"  onclick="javascript:userprofile('+wrapIt(entry.blocked)+');">'+entry.blocked+'\'s profile</a>\n');
				sb3.push('</li>\n');
				sb3.push('<li class="btn-xs">\n');
				sb3.push('<a onclick="javascript:unblockuser('+wrapIt(entry.blocked)+', '+wrapIt(user)+');">UNBLOCK  '+entry.blocked+'</a>\n');
				sb3.push('\n</li> ');
				var admintool=adminOptions(isAdmin,entry.blocked)
				sb3.push(admintool);
				sb3.push('</ul>\n</li>\n\n\n');
			}

		});
		$('#onlineUsers').html(sb.join("")+sb1.join("")+sb2.join("")+sb3.join(""));
	}
	if (jsonData.currentRoom!=null) {
		currentRoom=jsonData.currentRoom
	}
	if (jsonData.rooms!=null) {
		var rms = [];
		rms.push('<ul class="nav-pills pull-center">\n');
		jsonData.rooms.forEach(function(entry) {
			if (entry.room!=null) {
				if (currentRoom == entry.room) {
					rms.push('<li class="btn btn-primary dropdown">'+entry.room+'</li>\n');
				}else{
					rms.push('<li class="btn btn-default dropdown"><a onclick="javascript:joinRoom('+wrapIt(user)+','+wrapIt(entry.room)+');">'+entry.room+'</a></li>\n');
				}
			}
			rms.push('</ul>\n');
		});
		var admintool=adminRooms(isAdmin)
		rms.push(admintool);
		$('#chatRooms').html(rms.join(""));
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

function joinRoom(user,room) {
	webSocket.send("/joinRoom "+user+","+room);
}

function sendPM(receiver,sender,pm) {
	//alert('--'+receiever+"--"+sender+"---"+pm);
	$(function(event, ui) {
		var box = null;
		if(box) {
			box.chatbox("option", "boxManager").toggleBox();
		}else {
			var added=verifyAdded(sender);
			var el="#"+sender
			if (added=="false") {
				//alert('none found');
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

function addaRoom(user) {
	if (isAdmin=="true") {
		$.get("/"+getApp()+"/wsChat/addaRoom",function(data){
			$('#roomsContainer').hide().html(data).fadeIn('slow');
		});
		$('#roomcontainer').show();
	}
}

function delaRoom(user) {
	if (isAdmin=="true") {
		$.get("/"+getApp()+"/wsChat/delaRoom",function(data){
			$('#roomsContainer').hide().html(data).fadeIn('slow');
		});
		$('#roomcontainer').show();
	}
}

function userprofile(user) {
	$.get("/"+getApp()+"/wsChat/verifyprofile?username="+user,function(data){
		$('#profileContainer').hide().html(data).fadeIn('slow');
	});
	$('#userprofile').show();
}

function uploadPhoto(user) {
	$.get("/"+getApp()+"/wsChat/uploadPhoto?username="+user,function(data){
		$('#profileContainer').hide().html(data).fadeIn('slow');
	});

}

function editProfile(username) {
	$.get("/"+getApp()+"/wsChat/editprofile?username="+user,function(data){
		$('#profileContainer').hide().html(data).fadeIn('slow');
	});
}

function fullPhoto(photoId) {
	$.get("/"+getApp()+"/wsChat/realPhoto?photoId="+photoId,function(data){
		$('#photoContainer').hide().html(data).fadeIn('slow');
	});
	$('#userphoto').show();
}

function closePhoto() {
	$('#userphoto').hide();
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

function verifyCam(uid) {
	var camadded="false";
	var idx = camList.indexOf(uid);
	if (idx != -1) {
		camadded="true";
	}
	return camadded;
}		

function addcamList(uid) { 
	var idx = camList.indexOf(uid);
	if(idx == -1) {
		camList.push(uid);
	}	
}

function delCamList(uid) {
	var i = camList.indexOf(uid);
	if(i != -1) {
		camList.splice(i, 1);
	}
}

function rtcon(uid) {
	var idx = rtcOn.indexOf(uid);
	if(idx == -1) {
		rtcOn.push(uid);
	}	
}

function rtcoff(uid) {
	var i = rtcOn.indexOf(uid);
	if(i != -1) {
		camOn.splice(i, 1);
	}
}


function isrtcOn(uid) {
	var camadded="false";
	var idx = rtcOn.indexOf(uid);
	if (idx != -1) {
		camadded="true";
	}
	return camadded;
}	


function camon(uid) {
	var idx = camOn.indexOf(uid);
	if(idx == -1) {
		camOn.push(uid);
	}	
}

function camoff(uid) {
	var i = camOn.indexOf(uid);
	if(i != -1) {
		camOn.splice(i, 1);
	}
}


function isCamOn(uid) {
	var camadded="false";
	var idx = camOn.indexOf(uid);
	if (idx != -1) {
		camadded="true";
	}
	return camadded;
}	

function disableAV() {
	delCamList(user);
	webSocket.send("/camdisabled "+user);
	//WebCam.send("DISCO:-");
	//WebCam.onclose = function() { }
	//WebCam.close();
}

function getCam(user) {
	$.get("/"+getApp()+"/wsChat/camrec?user="+user,function(data){
		$('#camViewContainer').html(data);
	});
}

/*
function discoCam(user) {
	console.log('camsend: '+user)
	$.get("/"+getApp()+"/wsChat/camsend?user="+user+"&disco=true",function(data){
		$('#myCamContainer').html(data);
	});
}
*/

function sendCam() {
	$.get("/"+getApp()+"/wsChat/camsend?user="+user,function(data){
		$('#myCamContainer').html(data);
	});
	webSocket.send("/camenabled "+user);
}

function getWebrtc(user,rtcType) {
	$.get("/"+getApp()+"/wsChat/webrtcrec?user="+user+"&rtc="+rtcType,function(data){
		$('#camViewContainer').html(data);
	});
}

function sendWebrtc(rtcType) {
	$.get("/"+getApp()+"/wsChat/webrtcsend?user="+user+"&rtc="+rtcType,function(data){
		$('#myCamContainer').html(data);
	});
	webSocket.send("/webrtcenabled "+user);
}


/*
function getWebrtc(user) {
	$.get("/"+getApp()+"/wsChat/webrtcrec?user="+user,function(data){
		$('#camViewContainer').html(data);
	});
}

function sendWebrtc() {
	$.get("/"+getApp()+"/wsChat/webrtcsend?user="+user,function(data){
		$('#myCamContainer').html(data);
	});
	webSocket.send("/webrtcenabled "+user);
}
*/
function disablertc() {
	delCamList(user);
	webSocket.send("/webrtcdisabled "+user);
	//WebCam.send("DISCO:-");
	//WebCam.onclose = function() { }
	//WebCam.close();
}

function verifyCamPosition(uid) {
	/*
	var idx = camList.indexOf(uid);
	if(idx == -1) {
		camList.push(uid);
	}	
	 */
	if (camList.length>1) { 
		var getNextOffset = function() {
			return (config.width + config.gap) * camList.length;
		};	
		$("#"+uid).videobox("option", "offset", getNextOffset());
	} 		
}

function enableCam(camuser, camaction,viewtype){
	var goahead=false;
	var cmuser=camuser+'_webcam'
	if (camaction!="disable") {
		var camonn=verifyCam(cmuser);
		if (camonn=="false") {
			goahead=true;
		}
	}else{
		//$('#'+cmuser).videobox("option", "vidManager").closeBox();
		goahead=true;
	}	
	if (goahead==true) { 
		$(function(event, ui) {
			var vbox = null;
			//console.log('no vbox'+camaction+'--'+viewtype+'--'+cmuser);
			if(vbox) {
				if (camaction=="disable") {
					vbox.videobox("option", "vidManager").closeBox();
				}else{
					vbox.videobox("option", "vidManager").toggleBox();
				}				
			}else {
				var added=verifyCam(cmuser);
				verifyCamPosition(cmuser);
				var ell="#"+cmuser
				if ((added=="false")&&(camaction!="disable")) {
					var ell = document.createElement('div');
					ell.setAttribute('id', cmuser);
					addcamList(cmuser);
				}	
				vbox = $(ell).videobox({id:cmuser, 
					//user:{key : "value"},
					user:user,
					title : "Webcam: "+camuser,
					sender: camuser,
					camaction: camaction,
					viewtype: viewtype,
					vidSent : function(id, camuser) {						
						$("#"+cmuser).videobox("option", "vidManager").vidMsg(camuser);
					}
				});
				if (camaction=="view") {
					webSocket.send("/pm "+camuser+", "+user+"is now viewing your cam");
				}
				//else{
				//	webSocket.send("/pm "+user+", "+user+" you cam is now active");
				//}
				vbox.videobox("option", "show",1); 
				closeChatPMs();
			}
			
			
			
		});
	}
	if (camaction=="disable") {
		if (viewtype=="webrtc") { 
			disablertc();
		} else if (viewtype=="webcam") {
			disableAV();
		}
	}
}

function closeChatPMs()  { 
	/* jquery.ui.chatbox VS jquery.ui.videobox nasty hack
	* This should attempt to toggle status of any open chat boxes upon user calling 
	* video box. Content not lost when user opens pm content returns and all is well. 
	 
	* This hides all chat windows, ready to reopen - fixes issue with 
	* cannot call methods on dialog prior to initialization
	* 
	*/
	for	(index = 0; index < idList.length; index++) {
		$("#"+idList[index]).chatbox("option", "boxManager").toggleBox();

	} 
	
}

function pmuser(suser,sender) {
	//alert(''+suser+"===="+sender);
	$(function(event, ui) {
		var box = null;
		if(box) {
			box.chatbox("option", "boxManager").toggleBox();
		}else {
			var added=verifyAdded(suser);
			var el="#"+suser
			if (added=="false") {
				//	alert ('suser '+suser+' not found div ');
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

function processCamClose(message) {
	WebCam.send("DISCO:-");
	WebCam.close();
}
function processError(message) {
	$('#chatMessages').append("Error.... <br/>");
}

function scrollToBottom() {
	$('#cmessage').scrollTop($('#cmessage')[0].scrollHeight);

}
