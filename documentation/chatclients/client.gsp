
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'aa.label', default: 'aa')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
	
<chat:clientConnect
user="clientuser" 
receivers = "['masteruser']"
message="do_task_1"
strictMode="false"
frontenduser="true"
masterNode="false"
divId="myNode"
actionMap="['performed_task_1': 'do_task_2', 'performed_task_2': 'do_task_3', 'performed_task_3': 'close_my_connection' ]"
/>
-
<div id="myNode">
</div>

<div id="slaveList">
</div>
<g:javascript>
function getOnline() {
	var sb = [];
	
	$.getJSON('${createLink(controller:"wsChatClient", action: "slaveList")}',function(data){
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
		$('#slaveList').html(sb.join(""));			
	});
}
function pollPage() {
	getOnline();
	setTimeout('pollPage()', 5000);
}
pollPage();
</g:javascript>


</body>
</html>