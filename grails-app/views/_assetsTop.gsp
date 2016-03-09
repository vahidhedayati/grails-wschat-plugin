

<g:if test="${bean?.jquery}">
<asset:javascript src="jquery.min.js" />
</g:if>	
<asset:stylesheet href="jquery.ui.chatbox.css" />
<g:if test="${bean?.jqueryui}">
<asset:javascript src="jquery-ui.min.js" />
<asset:stylesheet src="jquery-ui.min.css" />
</g:if>
<asset:stylesheet src="font-awesome.css"/>
<g:if test="${bean?.bootstrap}">
<asset:javascript src="bootstrap.min.js" />
<asset:stylesheet href="bootstrap.min.css" />
</g:if>
<asset:javascript src="jquery.ui.chatbox.js" />
<asset:javascript src="jquery.ui.videobox.js" />
<asset:javascript src="jquery-ui-timepicker-addon.js" />
<asset:javascript src="jquery-ui-timepicker-addon-i18n.min.js" />


