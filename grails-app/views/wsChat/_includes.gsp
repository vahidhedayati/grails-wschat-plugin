<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
     <g:if test="${!request.xhr }">
		<meta name='layout' content="achat"/>
    </g:if>
    <g:else>
    	<g:render template="/assetsTop" />
    </g:else>
</g:if>
<g:else>
	<g:if test="${!request.xhr }">
		<meta name='layout' content="chat"/>
    </g:if>
    <g:else>
    	
   		<g:render template="/resourcesTop"/>
   	 </g:else>
</g:else>    
    

