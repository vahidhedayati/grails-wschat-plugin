
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'aa.label', default: 'aa')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
<chat:clientWsConnect
user="eventUser" 
frontenduser="true"
sendType = "event"
event = "some_Event" 
message="abc"
context = "some_Context" 
jsonData = "${myJson}"
receivers = "['eventUser2', 'cc']"
/>
-

</body>
</html>