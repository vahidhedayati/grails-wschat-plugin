<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
   	<g:render template="/assets"/>
</g:if>
<g:else>
	<g:render template="/resources"/>
</g:else>    
    

