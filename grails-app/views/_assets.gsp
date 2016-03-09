<asset:stylesheet href="chat.css" id="chat_theme" />
<asset:stylesheet href="chat-ui.css" />
<g:if test="${bean?.wschatjs}">
<asset:javascript src="${bean.wschatjs}" />
</g:if>
<g:else>
<asset:javascript src="wschat.js" />
</g:else>
<g:if test="${bean?.usermenujs}">
<asset:javascript src="${bean.usermenujs}" />
</g:if>
<g:else>
<asset:javascript src="usermenu.js" />
</g:else>
<asset:javascript src="camclient.js" />
<asset:javascript src="client.js" />



