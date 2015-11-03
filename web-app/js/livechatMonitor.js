/*
 * process message for livechat monitoring
 */
function processMessage(message) {
	
	var jsonData=JSON.parse(message.data);

	if (debug == "on") {
		console.log('@onMessage: '+JSON.stringify(message.data));
	}
		
	if (jsonData.system!=null) {
		if (jsonData.system=="disconnect") { 
			webSocket.send("DISCO:-"+user);
			webSocket.close();
		}
	}

	/**
	 * JSON string returned with 
	 * dynamic keys being the room names
	 * each element parsed and users times etc shown 
	 * back to livechat monitoring system
	 */
	if (jsonData.liveChatrooms!=null) {
		var rms = [];
		rms.push('<table>\n');
		rms.push('<tr><th>'+roomLabel+'</th>\n<th>'+userLabel+'</th>\n');
		rms.push('<th>'+startLabel+'</th><th>'+joinedTime+'</th>\n<th>'+adminLabel+'</th>\n');
		rms.push('<th>'+actionLabel+'</th></tr>\n');
		for(var i in jsonData.liveChatrooms) {
			rms.push('<tr>\n');	
			rms.push('<td>\n');
			rms.push(i);
			rms.push('</td>\n');
			var j=0;
			jsonData.liveChatrooms[i].forEach(function(entry) {
				if (j>=1) {
					rms.push('\n<tr><td></td>');
				}
				rms.push('\n<td>');
				rms.push(entry.user);
				rms.push('</td>\n');
				rms.push('<td>');
				rms.push(entry.startTime);
				rms.push('</td>\n');
				rms.push('<td>');
				rms.push(entry.joinedRoom);
				rms.push('</td>\n');
				rms.push('<td>');
				rms.push(entry.isAdmin);
				rms.push('</td>\n');
				rms.push('<td>');
				rms.push('<a href = "/'+getApp()+'/wsChat/joinLiveChat?roomName='+entry.room+'&username='+getUser()+'">'+joinLabel+' '+entry.room+'</a>');
				rms.push('</td>\n');
				if (j>=1) {
					rms.push('\n</tr>\n');
				}
				j++
			});
			rms.push('</tr>\n');
		}
		rms.push('</table>\n');
		$('#liveChatUsersList').html(rms.join(""));
	}
}
