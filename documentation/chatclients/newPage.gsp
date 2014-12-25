
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'aa.label', default: 'aa')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
<chat:clientWsConnect
user="randomUser" 
receivers = "['cc']"
message="haha I am just a random client sending a message and disconnecting"
/>
-

</body>
</html>