<asset:stylesheet href="chat.css" id="chat_theme" />
<asset:stylesheet href="chat-ui.css" />

<g:if test="${wschatjs}">
	<asset:javascript src="${wschatjs}" />
</g:if>
<g:else>
	<asset:javascript src="wschat.js" />
</g:else>
<g:if test="${usermenujs }">
	<asset:javascript src="${usermenujs}" />
</g:if>
<g:else>
	<asset:javascript src="usermenu.js" />
</g:else>

<asset:javascript src="camclient.js" />
<asset:javascript src="client.js" />

