	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">x</button>
		<h3 id="prompt">
<g:if test="${actualuser}">
	 ${username } 
	<button class="btn btn-success btn-xs" onclick="editProfile('${username}')">Edit profile</button>
	<button class="btn btn-warning btn-xs" onclick="uploadPhoto('${username}')">Upload Photo</button>
</g:if>
<g:else>
	Viewing ${username }'s profile
</g:else>
</h3>
		
	</div>
				<div class="modal-body">
	<div class='row'>
	<div class='col-sm-10 pull-left'>		
				<div class='row'>
				<g:if test="${(profile?.firstName||profile?.lastName)}">
				<div class='col-sm-5'>
				
				<label for="firstname">
				<g:message code="wschat.firstname.label" default="name" />
				</label>
				${profile?.firstName } ${profile?.middleName } ${profile?.lastName }
				</div>
				</g:if>
				<g:else>
					<label>Incomplete profile</label>
				</g:else>
				<g:if test="${profile?.children}">
				<div class='col-sm-3'>
				<label for="children">
				<g:message code="wschat.gender.label" default="Kids" />
				</label>
				${profile?.children }
				</div>
				</g:if>
		
		
				<g:if test="${profile?.wage}">
				<div class='col-sm-3	'>
				<label for="wages">$£
				${profile?.wage }
				</label>
				</div>
				</g:if>
			</div>
			<div class='row'>
				<g:if test="${profile?.gender}">
				<div class='col-sm-1'>
				<label for="gender">
				${profile?.gender }
				</label>
				</div>
				</g:if>
				<g:if test="${profile?.martialStatus}">
				<div class='col-sm-2'>
				<label for="status">				
				${profile?.martialStatus }
				</label>
				</div>
				</g:if>
				
				<g:if test="${profile?.age}">
				<div class='col-sm-2'>
				<label for="age">
				<g:message code="wschat.firstname.label" default="age" />
				</label>
				${profile?.age }
				</div>
				</g:if>
				<g:if test="${profile?.birthDate}">
				<div class='col-sm-5'>
				<label for="birthDate">
				<g:message code="wschat.birthDate.label" default="birthDate" />
				</label>
				<g:formatDate format="dd-MM-yyyy" date="${profile?.birthDate}"/>
				</div>
				</g:if>
			</div>

						
			<div class='row'>
			
				<g:if test="${profile?.email}">
				<div class='col-sm-5'>
				<label for="email">
				<g:message code="wschat.email.label" default="email" />
				</label>
				${profile?.email } 
				</div>
				</g:if>
				<g:if test="${profile?.homePage}">
				<div class='col-sm-5'>
				<label for="email">
				<g:message code="wschat.homePage.label" default="HomePage" />
				</label>
				${profile?.homePage }
				</div>
				</g:if>
				
		</div>
	</div>
	<div class="pull-right col-sm-2">
	<g:if test="${photos}">
		<g:each in="${photos }" var="ph">
			<a  data-toggle="modal" href="#photobox1" onclick="javascript:fullPhoto(${ph.id})"><img class="Photo" width="75px" height="100px" src="${createLink(controller:'wsChat', action:'viewPic', id:''+ph.id+'')}" /></a><br/>
		</g:each>
	</g:if>
	</div>
	</div>
	</div>