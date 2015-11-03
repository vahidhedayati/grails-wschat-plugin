<div id="chatterBox">
	<div class='row' id="themeChoice">
		<nav id="Navbar" class="navbar navbar-fixed-top navbar-inverse" role="navigation">
			<div class="container">
				<ul class="nav-pills pull-left">
					<div class="navbar-header" id="topNavBar1">
						<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse1">
	        				<span class="sr-only">Toggle Room List</span>
	        				<span class="icon-bar"></span>
		           			<span class="icon-bar"></span>
		           			<span class="icon-bar"></span>
						</button>
					</div>
					<a class="navbar-brand" href="${createLink(uri: '/')}" id="size3">
						${g.message(code:'wschat.appName', default:'wsChat')}
					</a>
				</ul>
				<div class="collapse navbar-collapse navbar-collapse1" role="navigation1">
					<ul class="nav nav-pills navbar">
				<li>
					<a  href="${createLink(uri: '/')}"><i class="fa fa-home fa-2x text-primary fa-inverse" title="${g.message(code:'wschat.appName', default:'ChangeMe')} Home"></i></a>
				</li>
				<li>
					<a  onclick="javascript:closeChatPMs()" title="${g.message(code: 'wschat.close.PM.boxes.label', default: 'Attempt to close any stuck PM Boxes')}">
					<span class="fa fa-compress icon-color"></span></a>
				</li>
				<li style=" margin-right: 2px;"> 
					<span class="fa fa-folder-open fa-3x text-success" title="${g.message(code: 'wschat.chat.rooms.label', default: 'Chat rooms')}" alt="${message(code: 'wschat.choose.different.room.label', default: 'Choose different toom')}"></span>
				</li>
				<div id="chatRooms"></div>
				<div id="adminRooms"></div>
				<div id="colourthemes">
					<button id="themeChanger" class="btn btn-danger btn-xs"/>
					<button id="themeChanger2" class="btn btn-primary btn-xs"/>
					<button id="themeChanger3" class="btn btn-inverse btn-xs"/>
					<button id="themeChanger4" class="btn btn-default btn-xs"/>
				</div>
			</ul>	
		</div>
	</div>
</nav>

<g:render template="/il8n"/>
			