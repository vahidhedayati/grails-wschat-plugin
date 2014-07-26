<!DOCTYPE html>
<html>
<head>
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="chat"/>
    </g:if>
    <g:else>
    	<link rel="stylesheet" href="${resource(dir: 'css', file: 'chat.css')}" type="text/css">
    </g:else>
   <title>Grails Websocket Chat</title>

</head>
<body>
<div id="chatterBox">
	<div class="message-container" id="contact-area">
	<form id="form1" method="post" action="login">
		Nickname: <input type="text" name="username">
		<input type="submit" value="login" class="sendbtn">
	</form>
	</div>						
</div>
</body>
</html>
