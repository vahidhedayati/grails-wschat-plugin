<!DOCTYPE html>
<html>
<head>
    <title>My WebRTC file sharing application</title>
<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<g:if test="${!request.xhr }">
		<meta name='layout' content="achat" />
	</g:if>
	<g:else>
		<g:render template="/assets" />
	</g:else>
</g:if>
<g:else>
	<g:if test="${!request.xhr }">
		<meta name='layout' content="chat" />
	</g:if>
	<g:else>
		<g:render template="/resources" />
	</g:else>
</g:else>
<asset:javascript src="rtc/rtclib.js" />
<asset:javascript src="rtc/adapter.js" />


</head>
<body>
<div id='status'></div>
<div>
    <input type="file" id="files" name="files[]" multiple />
    <output id="list"></output>
</div>
<div>
    <button onclick="onSendBtnClick()">Send</button>
</div>
<g:javascript>
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
    if (window.File && window.FileReader && window.FileList && window.Blob) {
    } else {
        alert('The File APIs are not fully supported in this browser.');
        return;
    }
    document.getElementById('files').addEventListener('change', handleFileSelect, false);

    var _url = window.location.href;
    var _arr = _url.split("/");
    var domain = _arr[2];

    myrtclibinit(uri, sender);
});
function onSendBtnClick() {
    for (var i = 0, f; f = filelist[i]; i++) {
        var reader = new FileReader();
        reader.onload = (function(theFile) {
            return function(evt) {
                var msg = JSON.stringify({"type" : "file", "name" : theFile.name, "size" : theFile.size, "data" : evt.target.result});
                sendDataMessage(msg);
            };
        })(f);
        reader.readAsDataURL(f);
    }
};

function OnRoomReceived(room) {
    var st = document.getElementById("status");
    st.innerHTML = "Now, if somebody wants to join you, should use this link: <a href=\""+window.location.href+"?room="+room+"\">"+window.location.href+"?room="+room+"</a>";
};

function onFileReceived(name,size,data) {
    var output = [];
    output.push('<li>just reived a new file: <a href=' + data + '>', name + '</a> ', size, ' bytes', '</li>');
    document.getElementById('list').innerHTML = '<ul>' + output.join('') + '</ul>';
}

function handleFileSelect(evt) {
    var files = evt.target.files;
    filelist = files;

    var output = [];
    for (var i = 0, f; f = files[i]; i++) {
        output.push('<li><strong>', escape(f.name), '</strong> (', f.type || 'n/a', ') - ',
                f.size, ' bytes, last modified: ',
                f.lastModifiedDate ? f.lastModifiedDate.toLocaleDateString() : 'n/a',
                '</li>');
    }
    document.getElementById('list').innerHTML = '<ul>' + output.join('') + '</ul>';
};
</g:javascript>

</body>
</html>
