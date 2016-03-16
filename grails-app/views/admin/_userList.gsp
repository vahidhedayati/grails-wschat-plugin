<g:set var="userOnline" value="${false}"/>
<table class="table">
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
	<th><g:message code="wschat.action.label" default="Action"/></th>
	</tr>
</thead>
<tbody>
<g:each in="${bean?.userList}" var="users">
<tr <g:if test="${users?.username == session?.wschatuser }">class="bold"</g:if>>
	<td>
		${users?.username } 
		<g:set var="userOnline" value="${bean?.uList?.contains(users?.username)}"/>
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
	<td class="dropdown">	
			<a class="btn btn-default actionButton"
			   data-toggle="dropdown" href="#" data-username="${users?.username}" data-user="${session?.wschatuser}">
			<span class="fa fa-gear icon-color"></span>
			</a>

		</td>
</tr>
</g:each>
 <ul id="contextMenu" class="dropdown-menu" role="menu" >
 	<g:if test="${userOnline}">
     	<li><a id="pm"><g:message code="default.pm.label" default="PM"/></a></li>
		<li><a id="kick"><g:message code="default.kick.label" default="Kick"/></a></li>
	</g:if>
	<li><a id="block"><g:message code="default.block.label" default="Block"/></a></li>
	<li><a id="ban" data-toggle="modal" ><g:message code="default.ban.label" default="Ban"/></a></li>
	<li><a id="logs" data-toggle="modal"><g:message code="default.logs.label" default="View logs"/></a></li>
	<li><a id="delete"><g:message code="default.button.delete.label"/></a></li>
</ul>        
</tbody>
</table>
<script>
		$(function() {
			$dropdown = $("#contextMenu");
			$(".actionButton").click(function() {
                var username = $(this).attr('data-username');
                var user = $(this).attr('data-user');
				//var id = $(this).closest("tr").children().first().html();
				$(this).after($dropdown);
				$dropdown.find("#pm").attr("onclick", "javascript:pmuser('"+username+"', '"+user+"');closeAdminModal()");
				$dropdown.find("#kick").attr("onclick", "javascript:kickuser('"+username+"');");
				$dropdown.find("#block").attr("onclick", "javascript:blockuser('"+username+"', '"+user+"');closeAdminModal()");
				$dropdown.find("#ban").attr("onclick", "javascript:banuser('"+username+"');").attr("href","#banuser1");
				$dropdown.find("#logs").attr("onclick", "javascript:viewLogs('"+username+"');").attr("href","#invitecontainer1");
				$dropdown.find("#delete").attr("onclick", "javascript:deleteUser('"+username+"','"+user+"');closeAdminModal();");
				$(this).dropdown();
				$dropdown.css({
					top: 38
				});
			});
		});
		</script>