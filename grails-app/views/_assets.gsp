
<asset:stylesheet href="chat.css" id="chat_theme" />
<asset:stylesheet href="chat-ui.css" />
<asset:javascript src="jquery.min.js" />
<asset:javascript src="jquery-ui.min.js" />
<asset:stylesheet href="jquery.ui.chatbox.css" />
<asset:stylesheet src="jquery-ui.min.css" />
<asset:javascript src="bootstrap.min.js" />
<asset:stylesheet href="bootstrap.min.css" />
<g:if test="${wschatjs }">
	<asset:javascript src="${wschat}" />
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
<asset:javascript src="jquery.ui.chatbox.js" />
<asset:javascript src="jquery.ui.videobox.js" />
<asset:javascript src="jquery-ui-timepicker-addon.js" />
<asset:javascript src="jquery-ui-timepicker-addon-i18n.min.js" />
