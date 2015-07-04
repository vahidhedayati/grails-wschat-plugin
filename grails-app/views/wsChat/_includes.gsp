<!-- START wschat top js-->
     <g:if test="${!request.xhr }">
		<meta name='layout' content="achat"/>
		<g:if test="${bean.addLayouts}">
			<g:render template="/assetsTop" model="${[bean:bean]}"/>
		</g:if>
    </g:if>
    <g:else>
    	<g:if test="${bean.addLayouts}">
    		<g:render template="/assetsTop" model="${[bean:bean]}" />
    	</g:if>	
    </g:else>
<!-- END wschat top js-->