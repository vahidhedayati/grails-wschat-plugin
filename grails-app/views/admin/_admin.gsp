<ul class="nav-pills pull-right">
<li class="btn-success btn">
<a data-toggle="modal" href="#roomcontainer1" class="glyphicon glyphicon-plus" 
onclick="javascript:addaRoom('+wrapIt(user)+');" title="Add a Room"></a>
</li>
<li class="btn-danger btn">
<a data-toggle="modal" href="#roomcontainer1" class="glyphicon glyphicon-minus" 
onclick="javascript:delaRoom('+wrapIt(user)+');" title="Remove a Room"></a>
</li>
</ul>

<ul class="nav-pills pull-right navbar-nav nav">
	<li class="dropdown">
		<a href="#" data-toggle="dropdown" class="dropdown-toggle"> 
			<i class="glyphicon glyphicon-cog" title="Admin menu"></i><b class="caret"></b>
		</a>

		<ul class="dropdown-menu" >
			<li class="dopdown-menu">
				<a tabindex="-1" href="#"><b>Admin Options</b></a>
			</li>
			<li class="divider"></li>
			<g:if env="development">
			<li class="">
				<a href="${createLink(uri: '/dbconsole')}">
					<i class="icon-dashboard"></i>
					<g:message code="default.dbconsole.label" default="DB Console"/>
				</a>
			</li>
			<li class="divider"></li>
			</g:if>

			<li>
				<a data-toggle="modal" href="#admincontainer1" 
				onclick="javascript:viewUsers('+wrapIt(user)+');" >
				<g:message code="wschat.viewusers.default" default="View Users"/>
				</a>
			</li>

			<li>
				<a data-toggle="modal" href="#admincontainer1" 
				onclick="javascript:createConference('+wrapIt(user)+');" >
				<g:message code="wschat.create.conference.default" default="Create Conference"/>
				</a>
			</li>
			<li>
            	<a data-toggle="modal" href="#admincontainer1"
            	onclick="javascript:viewLiveChats('+wrapIt(user)+');" >
            	<g:message code="wschat.view.liveChats.default" default="View Live Chat Requests"/>
            	</a>
            </li>
		</ul>
	</li>
</ul>
