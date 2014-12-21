<g:if test="${clientSlave }">
	<ul>
		<g:each in="${clientSlave}" var="cuser">
			<li><a >${cuser }</a></li>
		</g:each>
	</ul>
</g:if>

<g:if test="${clientMaster }">
	<ul>
		<g:each in="${clientMaster}" var="cuser">
			<li><a >${cuser }</a></li>
		</g:each>
	</ul>
</g:if>
