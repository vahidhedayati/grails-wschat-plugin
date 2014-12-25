
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'aa.label', default: 'aa')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
<chat:clientWsConnect
user="eventUser2" 
frontenduser="true"
sendType = "event"
event = "some_Event" 
message="abc"
context = "some_Context" 
jsonData = '{"content": {"name": "jim", "address": "123" }}'
receivers = "['eventUser', 'cc']"

/>
-

</body>
</html>