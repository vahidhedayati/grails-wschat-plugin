<g:if test="${wschatjs}">
	<script type="text/javascript"
		src="${resource(dir: 'js', file: ''+wschatjs+'')}"></script>
</g:if>
<g:else>
	<script type="text/javascript"
		src="${resource(dir: 'js', file: 'wschat.js')}"></script>
</g:else>

<g:if test="${usermenujs}">
	<script type="text/javascript" src="${resource(dir: 'js', file: ''+usermenujs+'')}"></script>
</g:if>
<g:else>
	<script type="text/javascript" src="${resource(dir: 'js', file: 'usermenu.js')}"></script>
</g:else>

<script type="text/javascript" src="${resource(dir: 'js', file: 'client.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'camclient.js')}"></script>

<link rel="stylesheet" href="${resource(dir: 'css', file: 'chat.css')}"	type="text/css" id="chat_theme">
<link rel="stylesheet" href="${resource(dir: 'css', file: 'chat-ui.css')}" type="text/css">
