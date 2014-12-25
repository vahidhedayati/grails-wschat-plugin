
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'aa.label', default: 'aa')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
	
<chat:clientConnect
user="masteruser" 
receivers = "['clientuser']"
strictMode="false"
masterNode="true"
frontenduser="true"
divId="serverNode"
message="hi this is an automated websocket SERVER message"
actionMap="['do_task_1': 'performed_task_1', 'do_task_2': 'performed_task_2', 'do_task_3': 'performed_task_3', 'close_my_connection': 'close_connection', 'list_domain': 'testwschat.userbase']"
/>
-
<div id="serverNode">
</div>

<div id="showMasters">
</div>
<div id="masterList">
</div>

<g:javascript>
function getOnline2() {
	var sb = [];
	
	$.getJSON('${createLink(controller:"wsChatClient", action: "masterList")}',function(data){
		sb.push('<table>');
		var divId='';
		data.forEach(function(entry) {
			var actionThis=entry.actionThis;
			var sendThis=entry.sendThis;
			var divId=entry.divId;
			var msgFrom=entry.msgFrom
			sb.push('<tr><td>\n');
			sb.push(actionThis+'</td><td>'+sendThis+'</td><td>'+msgFrom+'</td></tr>\n');
			
		});
		sb.push('</table>');
		$('#masterList').html(sb.join(""));			
	});
}

function getOnline() {
	<g:remoteFunction controller="wsChatClient" action="showMasters" update="showMasters"/>
}
function pollPage() {
	getOnline();
	getOnline2();
	setTimeout('pollPage()', 5000);
}
pollPage();
</g:javascript>

</body>
</html>