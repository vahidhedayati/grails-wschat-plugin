<!DOCTYPE html>
<html>
<head>
    <title>${bean.chatTitle}</title>
	<g:render template="includes" model="${[bean:bean]}"/>
	<asset:javascript src="rtcfile/rtclib.js" />
	<asset:javascript src="rtcfile/adapter.js" />
</head>
<body>
<div id='status'></div>
    <input  class="btn btn-danger btn-xs" type="file" id="files" name="files[]" multiple />
    <output id="list"></output>
    <button  class="btn btn-primary btn-xl" onclick="onSendBtnClick()">Send</button>

<g:javascript>
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
    st.innerHTML = "Someone can join this by going over your<br> chat name and choosing fileSharing<br><br>";
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
