<!DOCTYPE html>
<html>
<head>
    <title>My WebRTC file sharing application</title>
<g:if test="${!request.xhr }">
	<meta name='layout' content="achat" />
</g:if>
<g:else>
	<g:render template="/assets" />
</g:else>
<asset:javascript src="media/rtclib.js" />
<asset:javascript src="media/adapter.js" />
    <style type="text/css">
        video {
            width: 384px;
            height: 288px;
            border: 1px solid black;
            text-align: center;
        }
        .container {
            width: 780px;
            margin: 0 auto;
        }
    </style>
    


</head>
<body>

<div class="container">
    <video id="videoscreen" autoplay controls></video>
    <video hidden id="remotevideo" autoplay></video>
</div>
<div class="container">
    <div id='status'></div>
    <div><br>
        ...Select .webm file to stream <input type="file" id="files" name="files[]"/>
    </div>
    <div>
        <button onclick="onSendBtnClick()">Start streaming !</button>
    </div>
</div>
<script>
    var filelist;
	var hostname="${hostname}";

	var sender="${sender}";

	function getHostName() {
		return hostname;
	}
 	var room = "${room }";
 	var baseapp="${meta(name:'app.name')}";
 	
 	function getUser() {
 		return "${chatuser}";
 	}
 	
 	function getUser1() {
 		return "${room }" 
 	}
	function getApp() {
		return baseapp;
	}
 	
    <g:if test="${addAppName=='no'}">
	var uri="ws://${hostname}/WsChatFileEndpoint/${room}/${chatuser}";
</g:if>
<g:else>
	var uri="ws://${hostname}/${meta(name:'app.name')}/WsChatFileEndpoint/${room}/${chatuser}";
</g:else>
    
    $(function() {
    	videoScreen = document.getElementById("videoscreen");
    	if (window.File && window.FileReader && window.FileList && window.Blob) {
    	} else {
        	alert('The File APIs are not fully supported in this browser.');
        	return;
    	}
    	document.getElementById('files').addEventListener('change', handleFileSelect, false);

    	var _url = window.location.href;
    	var _arr = _url.split("/");
    	var domain = _arr[2];

    	myrtclibinit(uri, document.getElementById("remotevideo"), sender);
  	});
    

function onSendBtnClick() {
    doStreamMedia(filelist[0]);
};

function OnRoomReceived(room) {
    var st = document.getElementById("status");
    st.innerHTML = "This has not worked for me, another user needs to join you";
};

function handleFileSelect(evt) {
    filelist = evt.target.files;
};

</script>

</body>
</html>
