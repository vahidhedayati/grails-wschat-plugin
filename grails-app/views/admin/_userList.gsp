<div class="fixTable">
<div id="myUsers">
<table class="col-sm-12">
<thead>
	<tr>
	<th>
		<a onclick="<g:remoteFunction controller="wsChat"  action="viewUsers" update="${bean.divupdate }"  
		id="" params="[ s: s, viewtype:viewtype, id:inputid, max:max, sort:sort, order:'username',
		 divupdate:divupdate,  pageSizes:pageSizes, sortby:sortby, offset:offset
		]"/>">
		<g:message code="wschat.username.label" default="username"/></a>
	</th>
	<th>
		<a onclick="<g:remoteFunction controller="wsChat"  action="viewUsers" update="${bean.divupdate }"  
		id="" params="[ s: s, viewtype:viewtype, id:inputid, max:max, sort:sort, order:'permissions.name',
		 divupdate:divupdate,  pageSizes:pageSizes, sortby:sortby, offset:offset
		]"/>">
		<g:message code="wschat.permission.label" default="permission"/></a>
	</th>
	<th>
		<a onclick="<g:remoteFunction controller="wsChat"  action="viewUsers" update="${bean.divupdate }"  
		id="" params="[ s: s, viewtype:viewtype, id:inputid, max:max, sort:sort, order:'profile.firstName',
		 divupdate:divupdate,  pageSizes:pageSizes, sortby:sortby, offset:offset
		]"/>">
		<g:message code="wschat.firstName.label" default="firstName"/></a>
	</th>
	<th>
		<a onclick="<g:remoteFunction controller="wsChat"  action="viewUsers" update="${bean.divupdate }"  
		id="" params="[ s: s, viewtype:viewtype, id:inputid, max:max, sort:sort, order:'profile.lastName',
		 divupdate:divupdate,  pageSizes:pageSizes, sortby:sortby, offset:offset
		]"/>">
		<g:message code="wschat.lastName.label" default="lastName"/></a>
	</th>
	<th>
		<a onclick="<g:remoteFunction controller="wsChat"  action="viewUsers" update="${bean.divupdate }"  
		id="" params="[ s: s, viewtype:viewtype, id:inputid, max:max, sort:sort, order:'profile.email',
		 divupdate:divupdate,  pageSizes:pageSizes, sortby:sortby, offset:offset
		]"/>">
		<g:message code="wschat.email.label" default="email"/></a>
	</th>
	
	<th><g:message code="wschat.status.label" default="Status"/></th>
	</tr>
</thead>
<tbody>
<g:each in="${bean?.userList}" var="users">
<tr <g:if test="${users?.username == session?.wschatuser }">class="bold"</g:if>>
	<td>
	<ul id="user_spot">
		<li>${users?.username }
			<ul id="user_spot_links">
				<g:if test="${bean?.uList?.contains(users?.username) }">
					<li><a onclick="javascript:pmuser('${users?.username}', ''+user+'');closeAdminModal();">PM  ${users?.username}</a></li>
					<li><a onclick="javascript:kickuser('${users?.username}');">Kick  ${users?.username}</a></li>
				</g:if>
				<li><a onclick="javascript:blockuser('${users?.username}', ''+user+'');closeAdminModal();">Block  ${users?.username}</a></li>
				<li><a  data-toggle="modal" href="#banuser1"  onclick="closeAdminModal();javascript:banuser('${users?.username}');">Ban  ${users?.username}</a></li>
			</ul>
		</li>
	</ul>
	</td>
	<td>${users?.permissions?.name }</td>
	<td>${users?.profile?.firstName }</td>
	<td>${users?.profile?.lastName }</td>
	<td>${users?.profile?.email }</td>
	<td>
		<div 
			<g:if test="${bean?.uList && bean?.uList?.contains(users?.username)}">class="online"</g:if>
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
