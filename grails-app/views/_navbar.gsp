<g:render template="/il8n"/>

<nav id="Navbar" class="navbar navbar-fixed-top navbar-inverse" role="navigation">
	<div class="navbar-inner-sm">
		<div class="container">
	   		<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
        				<span class="sr-only">Toggle navigation</span>
        				<span class="icon-bar"></span>
	           			<span class="icon-bar"></span>
	           			<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="${createLink(uri: '/')}" id="size3">
						${g.message(code:'wschat.appName', default:'wsChat')}
					</a>
					<sec:ifLoggedIn>
					<form name="logout" method="POST" action="${createLink(controller:'logout') }">
                    		<g:submitButton name="logout" class="pull-right btn btn-danger btn-xs"/></form>
					</sec:ifLoggedIn>
			</div>
			<div class="collapse navbar-collapse navbar-ex1-collapse" role="navigation">
				<ul class="nav navbar-nav navbar-left">
					<div id="colourthemes">
						<button id="themeChanger" class="btn btn-danger btn-xs"/>
						<button id="themeChanger2" class="btn btn-primary btn-xs"/>
						<button id="themeChanger3" class="btn btn-inverse btn-xs"/>
						<button id="themeChanger4" class="btn btn-default btn-xs"/>
					</div>
					<li class="dropdown" id="adminMenu" style="display:none;">
						<a href="#" data-toggle="dropdown" class="dropdown-toggle"> 
							<i class="fa fa-gear icon-color" title="Admin menu"></i><b class="caret"></b
						></a>
						<ul class="dropdown-menu" >
							<li class="dopdown-menu">
								<a tabindex="-1" href="#"><b>Admin Options</b></a>
							</li>
							<li class="divider"></li>
							<g:if env="development">
								<li class="">
									<a href="${createLink(uri: '/dbconsole')}"><i class="icon-dashboard"></i><g:message code="default.dbconsole.label" default="db console"/></a>
								</li>
								<li class="divider"></li>
							</g:if>
							<div id="adminOptions"></div>
						</ul>
					</li>
				</ul>		
  				<ul class="nav navbar-nav ">
					<li><a href="${createLink(uri: '/')}"><i class="fa fa-home fa-2x text-primary fa-inverse" title="${g.message(code:'wschat.appName', default:'ChangeMe')} Home"></i></a></li>
					<li><a  onclick="javascript:closeChatPMs()" title="${g.message(code: 'wschat.close.PM.boxes.label', default: 'Attempt to close any stuck PM Boxes')}">
						<span class="fa fa-compress icon-color"></span></a>
					</li>
					<li style=" margin-right: 2px;"> 
						<span class="fa fa-folder-open fa-2x text-success" title="${g.message(code: 'wschat.chat.rooms.label', default: 'Chat rooms')}" alt="${message(code: 'wschat.choose.different.room.label', default: 'Choose different toom')}"></span>
						<div id="adminRoomOptions"></div>
					</li>
				</ul>
				<ul class="nav-pills pull-center">
					<div id="adminRooms"></div>
					<div id="chatRooms"></div>
				</ul>
			</div>
		</div>
	</div>
</nav>