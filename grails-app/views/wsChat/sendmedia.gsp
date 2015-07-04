<!DOCTYPE html>
<html>
<head>
    <title>	${bean.chatTitle}</title>
	<g:render template="includes" model="${[bean:bean]}"/>
	<asset:javascript src="rtcmedia/rtclib.js" />
	<asset:javascript src="rtcmedia/adapter.js" />
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
	var hostname="${bean.hostname}";
	var sender="${bean.sender}";
	function getHostName() {
		return hostname;
	}
 	var room = "${bean.room}";
 	function getUser() {
 		return "${bean.chatuser}";
 	}
 	function getUser1() {
 		return "${bean.room}" 
 	}
	function getApp() {
		return baseapp;
	}
    var uri="${bean.fileEndpoint}/${bean.room}/${bean.chatuser}";
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