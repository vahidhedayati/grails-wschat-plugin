<div class="fixTable">
<div id="myUsers">
<table class="col-sm-12">
<thead>
	<tr>
	<th>
		<a onclick="<g:remoteFunction controller="wsChat"  action="viewLiveChats" update="${bean.divupdate }"  
		id="" params="[ s: s, viewtype:viewtype, id:inputid, max:max, sort:sort, order:'username',
		 divupdate:divupdate,  pageSizes:pageSizes, sortby:sortby, offset:offset
		]"/>">
		<g:message code="wschat.username.label" default="username"/></a>
	</th>
	<th>
		<a onclick="<g:remoteFunction controller="wsChat"  action="viewLiveChats" update="${bean.divupdate }"  
		id="" params="[ s: s, viewtype:viewtype, id:inputid, max:max, sort:sort, order:'.name',
		 divupdate:divupdate,  pageSizes:pageSizes, sortby:sortby, offset:offset
		]"/>">
		<g:message code="wschat.name.label" default="name"/></a>
	</th>
	<th>
		<a onclick="<g:remoteFunction controller="wsChat"  action="viewLiveChats" update="${bean.divupdate }"  
		id="" params="[ s: s, viewtype:viewtype, id:inputid, max:max, sort:sort, order:'emailAddress',
		 divupdate:divupdate,  pageSizes:pageSizes, sortby:sortby, offset:offset
		]"/>">
		<g:message code="wschat.emailAddress.label" default="emailAddress"/></a>
	</th>
	<th>
		<a onclick="<g:remoteFunction controller="wsChat"  action="viewLiveChats" update="${bean.divupdate }"  
		id="" params="[ s: s, viewtype:viewtype, id:inputid, max:max, sort:sort, order:'roomName',
		 divupdate:divupdate,  pageSizes:pageSizes, sortby:sortby, offset:offset
		]"/>">
		<g:message code="wschat.roomName.label" default="roomName"/></a>
	</th>
	<th>
		<a onclick="<g:remoteFunction controller="wsChat"  action="viewLiveChats" update="${bean.divupdate }"  
		id="" params="[ s: s, viewtype:viewtype, id:inputid, max:max, sort:sort, order:'startTime',
		 divupdate:divupdate,  pageSizes:pageSizes, sortby:sortby, offset:offset
		]"/>">
		<g:message code="wschat.startTime.label" default="startTime"/></a>
	</th>
	
	<th><g:message code="wschat.active.label" default="active"/></th>
	</tr>
</thead>
<tbody>
<g:each in="${bean?.uList}" var="users">
<tr <g:if test="${users?.username == session?.wschatuser }">class="bold"</g:if>>
	<td>
	<ul id="user_spot">
		<li>${users?.username }
			<ul id="user_spot_links">
				<li><a  data-toggle="modal" href="#invitecontainer1"  onclick="javascript:viewLiveLogs('${users?.username}');">View logs</a></li>
			</ul>
		</li>
	</ul>
	</td>
	<td>${users?.name }</td>
	<td>${users?.emailAddress}</td>
	<td>${users?.roomName }</td>
	<td>${users?.startTime }</td>
	<td>
		<div 
			<g:if test="${users?.active}">class="online"</g:if><g:else>class="offline"</g:else>>
		</div>
	</td>
</tr>
</g:each>                      
</tbody>
</table>
</div>
</div>
