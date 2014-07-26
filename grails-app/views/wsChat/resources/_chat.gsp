<!DOCTYPE html>
<html>
<head>
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="chat"/>
    </g:if>
    <g:else>
    	<link rel="stylesheet" href="${resource(dir: 'css', file: 'chat.css')}" type="text/css">
    </g:else>
   <title>${chatTitle }</title>
   <g:javascript library="jquery"/>
</head>
<body>

<g:render template="/wsChat/chatpage"/>
</body>
</html>
