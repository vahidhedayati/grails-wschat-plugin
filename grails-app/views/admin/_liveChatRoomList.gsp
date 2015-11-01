<div class="fixTable">
<div id="myUsers">
<table class="col-sm-12">
<thead>
	<tr>
	<th>
		<g:message code="wschat.liveChatRoom.label" default="Live Chat Rooms"/></a>
	</th>
	<th><g:message code="wschat.hasAdmin.label" default="Admin logged in"/></th>
	</tr>
</thead>
<tbody>
<g:each in="${bean?.uList}" var="uList">
<tr>
	<td>
	<ul id="user_spot">
		<li>${uList.room}
			<ul id="user_spot_links">
				<li><a onclick="javascript:joinLiveChatRoom('${uList.room}', ''+user+'');closeAdminModal();">Join</a></li>
			</ul>
		</li>
	</ul>
	</td>
	<td>
	${uList?.hasAdmin}
		<div 
			<g:if test="${uList?.hasAdmin}">class="online"</g:if>
			<g:else>class="offline"</g:else>
		>
		</div>
	</td>
</tr>
</g:each>                      
</tbody>
</table>
</div>
</div>
