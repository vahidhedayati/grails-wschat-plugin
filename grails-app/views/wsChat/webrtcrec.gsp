<!DOCTYPE html>
<html>
<head>

<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<g:render template="/assets"/>
	<asset:javascript src="client.js"/>
</g:if>
<g:else>
	<g:render template="/resources"/>
	 <script type="text/javascript" src="${resource(dir: 'js', file: 'client.js')}"></script>
	
</g:else>
   <title>${chatTitle }</title>
</head>
<body>
		<input type="hidden" id="roomidInput" value="${user}">
		<section id="room" class="section room">
			<div class="video-wrapper">
				<video id="otherVideo" class="other-video" src="" autoplay="true"></video>
				<video id="ownVideo" class="own-video" src="" autoplay="true" muted="true"></video>
			</div>
		</section>
		
<script type="text/javascript">
	function getIceServers() {
		return "${iceservers}";
	}

	/*
	 *	Get DOM-Elements
	 */
	var roomidInput = document.getElementById('roomidInput');
	var createRoom = document.getElementById('createRoom');
	var joinRoom = document.getElementById('joinRoom');
	var login = document.getElementById('login');
	//var roomId = document.getElementById('roomId');
	var room = document.getElementById('room');
	var ownVideo = document.getElementById('ownVideo');
	var otherVideo = document.getElementById('otherVideo');
	
	/*
	 *	Create some Helper-Functions
	 */
	var hasClass  = function(el,className) {
		if(!el || !className){return;}
		return (new RegExp("(^|\\s)" + className + "(\\s|$)").test(el.className));
	};
	var removeClass = function(el,className) {
		if(!el || !className){return;}
		el.className = el.className.replace(new RegExp('(?:^|\\s)'+className+'(?!\\S)'),'' );
		return el;
	};
	var addClass = function(el,className) {
		if(!el || !className){return;}
		if(!hasClass(el,className)) { el.className += ' '+className; }
		return el;
	};
	
	/*
	 *	Open Websocket-Connection
	 */
	
	//create new WebRTC-Object
	var WebRTC = new WebRTC();
	
	//connect to websocket server
	var uri="ws://${hostname}/${meta(name:'app.name')}/WsCamEndpoint/${user}/${chatuser}"
		WebRTC.connectToSocket(uri);
	
	/*
	 *	Add Click-Handler and Event-Listener
	 */
	//add a eventlister when the server has answered
	document.addEventListener('socketEvent', function(socketEvent){
		switch(socketEvent.eventType) {
		case 'roomCreated':
			// hide login and show room
			removeClass(login,'active');
			addClass(room,'active');
			// display the room-ID
			//roomId.innerHTML = WebRTC.getRoomId();
			break;
		case 'p2pConnectionReady':
			// hide login and show room
			removeClass(login,'active');
			addClass(room,'active');
			// display the room-ID
			//roomId.innerHTML = WebRTC.getRoomId();
			break;
		case 'streamAdded':
			// we receive the video-stream of our partner
			// and play it on the video-element
			otherVideo.src = URL.createObjectURL(WebRTC.getOtherStream());
			break;
		}
	});
	
	
	//get media-stream
	var success = function(myStream){
		// set videostream on ownVideo-object
		ownVideo.src = URL.createObjectURL(myStream);
		// join a room
		WebRTC.joinRoom(roomidInput.value);
	};
	WebRTC.getMedia({audio: true, video: true},success);		
</script>
</body>
</html>