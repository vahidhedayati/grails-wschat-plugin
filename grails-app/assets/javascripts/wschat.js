var config = {
		width : 300, //px
		gap : 2,
		maxBoxes : 10,
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
		var getNextOffset = function() {
            return (config.width + config.gap) * (idList.length-1);
        };
        if (idList.length>1) {
              $("#"+uid).chatbox("option", "offset", getNextOffset());
        }
	  //  }else if (idList.length>1) {
      //      $("#"+uid).chatbox("option", "offset",305);
	  //  }
	}
}

function wrapIt(value) {
	return "'"+value+"'"
}

function htmlEncode(value){
	return $('<div/>').text(value).html();
}
/*
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
}
*/

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

function blockuser(blockid,user) {
	webSocket.send("/block "+user+","+blockid);
	$('#chatMessages').append(blockid+' '+addedTo+' '+user+' '+block+' '+listLabel+'\n');
}

function confirmBan(username,duration,period) { 
	webSocket.send("/banuser "+username+","+duration+":"+period);
	$('#chatMessages').append(username+"  "+bannedTill+' '+duration+" "+period+"\n");
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

function viewLiveChats(user) {
	if (isAdmin=="true") {
		$.get("/wsChat/viewLiveChats",function(data){
			$('#adminsContainer').hide().html(data).fadeIn('slow');
		});
		$('#admincontainer').show();
	}
}
function viewLiveLogs(user) {
	if (isAdmin=="true") {
		$.get("/wsChat/viewLiveLogs?username="+user,function(data){
			$('#inviteUserContainer').hide().html(data).fadeIn('slow');
		});
		$('#invitecontainer').show();
	}
}

function viewLogs(user) {
	if (isAdmin=="true") {
		$.get("/wsChat/viewLogs?username="+user,function(data){
			$('#inviteUserContainer').hide().html(data).fadeIn('slow');
		});
		$('#invitecontainer').show();
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
	$('#chatMessages').append(blockid+' '+removedFrom+' '+user+" blocklist\n");
}

function adduser(addid,user) {
	webSocket.send("/add "+user+","+addid);
	$('#chatMessages').append(addid+' '+addedTo+' '+user+' '+addedTo+' '+listLabel+'\n');
}

function removefriend(addid,user) {
	webSocket.send("/removefriend "+user+","+addid);
	$('#chatMessages').append(addid+' '+removedFrom+' '+user+' '+friendLabel+' '+listLabel+'\n');
}

function isGameOn(uid) {
 	var camadded="false";
 	var idx = gameOn.indexOf(uid);
 	if (idx != -1) {
		camadded="true";
 	}
	return camadded;
}
function delGame(uid) {
 	var i = gameOn.indexOf(uid);
 	if(i != -1) {
 		mediaOn.splice(i, 1);
	}
}
function addGame(uid) {
 	var idx = gameOn.indexOf(uid);
	if(idx == -1) {
 		mediaOn.push(uid);
	}
}

function getGame(user) {
 	$.get("/wsChat/xojoin?user="+user,function(data){
 		$('#camViewContainer').html(data);
 	});
 }

function sendGame() {
	$.get("/wsChat/xo",function(data){
 		$('#myCamContainer').html(data);
	});
 	webSocket.send("/gameenabled "+user);
}

function disableGame() {
 	delGame(user);
 	webSocket.send("/gamedisabled "+user);
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
	//var cmuser=camuser+'_webcam'
	var cmuser=camuser+'_'+viewtype
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
					webSocket.send("/pm "+camuser+", "+user+" "+nowViewing+' '+viewtype);
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
		} else if (viewtype=="fileshare") {
       		disableFile();
       	} else if (viewtype=="mediastream") {
       		disableMedia();
       	} else if (viewtype=="game") {
       		disableGame();
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
function closeVideos()  {
	for	(index = 0; index < camList.length; index++) {
		$("#"+camList[index]).videobox("option", "vidManager").toggleBox();
	}
}

function liveChatsRooms(user) {
	if (isAdmin=="true") {
		$.get("/wsChat/liveChatsRooms",function(data){
			$('#adminsContainer').hide().html(data).fadeIn('slow');
		});
		$('#admincontainer').show();
	}
}
function joinLiveChatRoom(room,user) {
	 webSocket.send("/joinLiveChatRoom "+user+","+room);
}


/*
 * This appears under admin liveChat window
 * and when sending messages it converts pm back to message
 * to display in liveChat window which imitates pm window
 */
function sendLiveChatPM(receiver,sender,pm,room) {
    var added=verifyAdded(sender);
	$(function(event, ui) {
		var box = null;
		if(box) {
			box.chatbox("option", "boxManager").toggleBox();
		}else {
			var el="#"+sender
			if (added=="false") {
				el = document.createElement('div')
				el.setAttribute('id', sender);
				//el.setAttribute('draggable', true);
			}
			box =  $(el).chatbox({id:sender,
				user:{key : "value"},
				title : "LIVECHAT from: "+sender,
				messageSent : function(id, user, msg) {
				    if (added=="false") {
					    verifyPosition(sender);
					}
					$("#"+sender).chatbox("option", "boxManager").addMsg(getUser(), msg);
					webSocket.send("/cl "+sender+","+room+":"+msg);
				}
			});
		}
	});
	 if (added=="false") {
	    verifyPosition(sender);
	}
	$("#"+sender).chatbox("option", "boxManager").addMsg(sender, pm);
}

function livepmuser(suser,sender,room) {
var added=verifyAdded(suser);
	var sus=suser ? suser : user
	$(function(event, ui) {
		var box = null;
		if(box) {
			box.chatbox("option", "boxManager").toggleBox();
		}else {
			var el="#"+suser
			if (added=="false") {
				el = document.createElement('div')
				el.setAttribute('id', suser);
			} else {
			    $(el).chatbox("option", "boxManager").toggleBox();
			}
			box = $(el).chatbox({id:suser,
				user:{key : "value"},
				title : "LIVECHAT: "+suser,
				messageSent : function(id, user, msg) {
				    if (added=="false") {
				        verifyPosition(suser);
				    }
					$("#"+suser).chatbox("option", "boxManager").addMsg(suser, msg);
					webSocket.send("/cl "+suser+","+room+":"+msg);
				}
			});
			//box.chatbox("option", "show",1);
		}
	});
}

function sendLiveMessage() {
	if (messageBox.value!="/disco") {
		if (messageBox.value!="") {
			$('#chatMessages').append('<div id="msgSent"><span class="msgPersonSent">'+getUser()+':</span> <span class="msgSentContent">'+htmlEncode(messageBox.value)+'</span></div>');
			webSocket.send("/lc "+getUser()+","+roomName+":"+messageBox.value);
			messageBox.value="";
			messageBox.value.replace(/^\s*[\r\n]/gm, "");
			scrollToBottom();
		}
	}else {
		webSocket.send("DISCO:-"+user);
		$('#chatMessages').append(user+' '+disconnectingMessage+"\n");
		messageBox.value="";
		webSocket.close();
	}
}



function sendPM(receiver,sender,pm) {
    var added=verifyAdded(sender);
	$(function(event, ui) {
		var box = null;
		if(box) {
			box.chatbox("option", "boxManager").toggleBox();
		}else {

			var el="#"+sender
			if (added=="false") {
				el = document.createElement('div');
				el.setAttribute('id', sender);
			}
			box =  $(el).chatbox({id:sender,
				user:{key : "value"},
				title : "PM from: "+sender,
				messageSent : function(id, user, msg) {
					if (added=="false") {
                        verifyPosition(sender);
                    }
					$("#"+sender).chatbox("option", "boxManager").addMsg(receiver, msg);
					webSocket.send("/pm "+sender+","+msg);
				}
				//,
				//boxClosed: function(id) {
                //    removePosition(sender);
                //}
			});
		}
	});
	if (added=="false") {
      	verifyPosition(suser);
    }
	//verifyPosition(sender);
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
			box = $(el).chatbox({id:suser,
				user:{key : "value"},
				title : "PM: "+suser,
				messageSent : function(id, user, msg) {
				    if (added=="false") {
                	    verifyPosition(suser);
                	}
					$("#"+suser).chatbox("option", "boxManager").addMsg(id, msg);
					webSocket.send("/pm "+suser+","+msg);
				}
				//,
				//boxClosed: function(id) {
				 //   removePosition(suser);
				//}
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
		$('#chatMessages').append(user+' '+disconnectingMessage+"\n");
		messageBox.value="";
		webSocket.close();
	}   
}

function processChatClose(message) {
	webSocket.send("deactive_chat_bot");
	webSocket.send("DISCO:-"+user);
	$('#chatMessages').append(user+' '+disconnectingMessage+"\n");
	webSocket.close();
}

function processLiveClose(message) {
	webSocket.send("deactive_me");
	webSocket.send("DISCO:-"+user);
	$('#chatMessages').append(user+' '+disconnectingMessage+"\n");
	webSocket.close();
}

function processClose(message) {
	webSocket.send("DISCO:-"+user);
	$('#chatMessages').append(user+' '+disconnectingMessage+"\n");
	webSocket.close();
}

function processCamClose(message) {
	WebCam.send("DISCO:-");
	WebCam.close();
}
function processError(message) {
	$('#chatMessages').append(errorMessage+"<br/>");
}

function scrollToBottom() {
	$('#cmessage').scrollTop($('#cmessage')[0].scrollHeight);
}

function toggleBlock(caller,called,calltext) {
	$(caller).click(function() {
		if($(called).is(":hidden")) {
 			$(caller).html(hideLabel+' '+calltext).fadeIn('slow');
    	}else{
        	$(caller).html(showLabel+' '+calltext).fadeIn('slow');

    	}
 		$(called).slideToggle("fast");

  	});
}
