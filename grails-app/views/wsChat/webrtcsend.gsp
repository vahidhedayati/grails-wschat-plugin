<g:render template="includes" model="${[bean:bean]}"/>
<input type="hidden" id="roomidInput" value="${bean.user}">
<section id="room" class="section room">
	<div class="video-wrapper">
		<video id="otherVideo" class="other-video" src="" autoplay="true"></video>
		<video id="ownVideo" class="own-video" src="" autoplay="true" muted="true"></video>
	</div>
</section>

<g:javascript>
function getIceServers() {
	return "${bean.iceservers}";
}

/*
 *	Get DOM-Elements
 */
 var videosContainer = document.getElementById('videos-container') || document.body;
var roomidInput = document.getElementById('roomidInput');
var createRoom = document.getElementById('createRoom');
var joinRoom = document.getElementById('joinRoom');
var login = document.getElementById('login');
var roomId = document.getElementById('roomId');
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

// create new WebRTC-Object
var WebRTC = new WebRTC();
var uri="${bean.camEndpoint}/${bean.user}/${bean.user}";
//if (debug == "on") {
	console.log('Connecting to '+uri);
//}
WebRTC.connectToSocket(uri);

/*
 *	Add Click-Handler and Event-Listener
 */


// add a eventlister when the server has answered
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

// get media-stream
var success = function(myStream){
	ownVideo.src = URL.createObjectURL(myStream);
	// create a room
	WebRTC.createRoom();
};
//WebRTC.getMedia({audio: true, video: true},success);
//WebRTC.getMedia(JSON.stringify(webvid),success);

<g:if test="${bean.rtc=='webrtcscreen'}">
	console.log('Should be screen sharing');
	WebRTC.getMedia({video:{mandatory: {chromeMediaSource: 'screen'}}},success);
</g:if>
<g:else>
	console.log('Should be normal AV');
	WebRTC.getMedia({audio: true, video: true},success);
</g:else>

</g:javascript>
</body>
</html>