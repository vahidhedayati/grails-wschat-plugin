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
		sb.push('<li class="btn-xs" id="sideBar">\n');
		sb.push('<a onclick="javascript:kickuser('+wrapIt(user)+');">Kick  '+user+'</a>\n');
		sb.push('</li>\n');
		sb.push('<li class="btn-xs" id="sideBar">\n');
		sb.push('<a  data-toggle="modal" href="#banuser1"  onclick="javascript:banuser('+wrapIt(user)+');">Ban  '+user+'</a>\n');
		sb.push('</li>\n');
	}
	return sb.join("")
}

function adminRooms(isAdmin) {
	if (isAdmin=="true") {
		var strUrl = "/wsChat/adminMenu", strReturn = "";
		  jQuery.ajax({
		    url: strUrl,
		    success: function(html) {
		      strReturn = html;
		    },
		    async:false
		  });
		return strReturn;
	}
	//var sb = [];
	//sb.push('<ul class="nav-pills pull-right">');
	//sb.push('<li class="btn-success btn"><a data-toggle="modal" href="#roomcontainer1" class="glyphicon glyphicon-plus" onclick="javascript:addaRoom('+wrapIt(user)+');" title="Add a Room"></a></li>');
	//sb.push('<li class="btn-danger btn"><a data-toggle="modal" href="#roomcontainer1" class="glyphicon glyphicon-minus" onclick="javascript:delaRoom('+wrapIt(user)+');" title="Remove a Room"></a></li>');
	//sb.push('</ul>');
	//console.log('-------'+sb.join(""));
}

function actOnEachLine(textarea, func) {
	var lines = textarea.replace(/\r\n/g, "\n").split("\n");
	var newLines, newValue, i;
	// Use the map() method of Array where available
	if (typeof lines.map != "undefined") {
	newLines = lines.map(func);
	} else {
	newLines = [];
	i = lines.length;
	while (i--) {
	newLines[i] = func(lines[i]);
	}
	}
	textarea.value = newLines.join("\r\n");
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

function viewUsers(user) {
	if (isAdmin=="true") {
		$.get("/wsChat/viewUsers",function(data){
			$('#adminsContainer').hide().html(data).fadeIn('slow');
		});
		$('#admincontainer').show();
	}
}

function createConference(user) {
	if (isAdmin=="true") {
		$.get("/wsChat/createConference",function(data){
			$('#adminsContainer').hide().html(data).fadeIn('slow');
		});
		$('#admincontainer').show();
	}
}

function createEmail(user,newuser) {
	if (isAdmin=="true") {
		$.get("/wsChat/addEmail?username="+newuser,function(data){
			$('#inviteUserContainer').hide().html(data).fadeIn('slow');
		});
		$('#invitecontainer').show();
	}
}

function addUser(user,newuser) {
	if (isAdmin=="true") {
		$.get("/wsChat/addUser?username="+newuser,function(data){
			$('#inviteUserContainer').hide().html(data).fadeIn('slow');
		});
		$('#invitecontainer').show();
	}
}
function addaRoom(user) {
	if (isAdmin=="true") {
		$.get("/wsChat/addaRoom",function(data){
			$('#roomsContainer').hide().html(data).fadeIn('slow');
		});
		$('#roomcontainer').show();
	}
}

function delaRoom(user) {
	if (isAdmin=="true") {
		$.get("/wsChat/delaRoom",function(data){
			$('#roomsContainer').hide().html(data).fadeIn('slow');
		});
		$('#roomcontainer').show();
	}
}

function userprofile(user) {
	$.get("/wsChat/verifyprofile?username="+user,function(data){
		$('#profileContainer').hide().html(data).fadeIn('slow');
	});
	$('#userprofile').show();
}

function uploadPhoto(user) {
	$.get("/wsChat/uploadPhoto?username="+user,function(data){
		$('#profileContainer').hide().html(data).fadeIn('slow');
	});

}

function editProfile(username) {
	$.get("/wsChat/editprofile?username="+user,function(data){
		$('#profileContainer').hide().html(data).fadeIn('slow');
	});
}

function fullPhoto(photoId) {
	$.get("/wsChat/realPhoto?photoId="+photoId,function(data){
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

function fileshareon(uid) {
	var idx = fileOn.indexOf(uid);
	if(idx == -1) {
		fileOn.push(uid);
	}	
}

function fileshareoff(uid) {
	var i = fileOn.indexOf(uid);
	if(i != -1) {
		fileOn.splice(i, 1);
	}
}

function delFileList(uid) {
	var i = fileOn.indexOf(uid);
	if(i != -1) {
		fileOn.splice(i, 1);
	}
}


function mediashareon(uid) {
	var idx = mediaOn.indexOf(uid);
	if(idx == -1) {
		mediaOn.push(uid);
	}	
}

function mediashareoff(uid) {
	var i = mediaOn.indexOf(uid);
	if(i != -1) {
		mediaOn.splice(i, 1);
	}
}

function delMediaList(uid) {
	var i = mediaOn.indexOf(uid);
	if(i != -1) {
		mediaOn.splice(i, 1);
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


/*
function discoCam(user) {
	console.log('camsend: '+user)
	$.get("/wsChat/camsend?user="+user+"&disco=true",function(data){
		$('#myCamContainer').html(data);
	});
}
*/

function getFile(user) {
	$.get("/wsChat/sendfile?room="+user,function(data){
		$('#camViewContainer').html(data);
	});
}

function sendFile() {
	$.get("/wsChat/sendfile?room="+user,function(data){
		$('#myCamContainer').html(data);
	});
	webSocket.send("/fileenabled "+user);
}

function disableFile() {
	delFileList(user);
	webSocket.send("/filedisabled "+user);
	
}


function getMedia(user) {
	$.get("/wsChat/sendmedia?room="+user,function(data){
		$('#camViewContainer').html(data);
	});
}

function sendMedia() {
	$.get("/wsChat/sendmedia?room="+user,function(data){
		$('#myCamContainer').html(data);
	});
	webSocket.send("/mediaenabled "+user);
}

function disableMedia() {
	delMediaList(user);
	webSocket.send("/mediadisabled "+user);
	
}


function getCam(user) {
	$.get("/wsChat/camrec?user="+user,function(data){
		$('#camViewContainer').html(data);
	});
}
function sendCam() {
	$.get("/wsChat/camsend?user="+user,function(data){
		$('#myCamContainer').html(data);
	});
	webSocket.send("/camenabled "+user);
}

function getWebrtc(user,rtcType) {
	$.get("/wsChat/webrtcrec?user="+user+"&rtc="+rtcType,function(data){
		$('#camViewContainer').html(data);
	});
}

function sendWebrtc(rtcType) {
	$.get("/wsChat/webrtcsend?user="+user+"&rtc="+rtcType,function(data){
		$('#myCamContainer').html(data);
	});
	webSocket.send("/webrtcenabled "+user);
}


/*
function getWebrtc(user) {
	$.get("/wsChat/webrtcrec?user="+user,function(data){
		$('#camViewContainer').html(data);
	});
}

function sendWebrtc() {
	$.get("/wsChat/webrtcsend?user="+user,function(data){
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
					title : viewtype+": "+camuser,
					sender: camuser,
					camaction: camaction,
					viewtype: viewtype,
					vidSent : function(id, camuser) {						
						$("#"+cmuser).videobox("option", "vidManager").vidMsg(camuser);
					}
				});
				if (camaction=="view") {
					webSocket.send("/pm "+camuser+", "+user+"is now viewing your "+viewtype);
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
