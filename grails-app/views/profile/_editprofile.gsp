

	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">x</button>
		Edit ${username }'s profile
	</div>
	
	

	<g:formRemote 
	name="urlParams"  
	class="form-horizontal" 
	url="[controller:'wsChat', action:'updateProfile']" 
	update="profileconfirmation"  
	onComplete="closeModal()"
	>

	<div class="modal-body">
		<input type=hidden name=username value="${username }"/>
		<div class='row'>
		
		<div class='col-sm-4'>
		<div class='form-group'>
		<label for="firstname">
		<g:message code="wschat.firstname.label" default="firstname" />
		</label>
		<g:textField name="firstName" value="${profile?.firstName}"/>
		</div>
		</div>
				
				
		<div class='col-sm-4'>
		<div class='form-group'>
		<label for="firstname">
		<g:message code="wschat.middlename.label" default="middlename" />
		</label>
		<g:textField name="middleName" value="${profile?.middleName}"/>
		</div>
		</div>
			
		<div class='col-sm-4'>
		<div class='form-group'>
		<label for="firstname">
		<g:message code="wschat.surname.label" default="Surname" />
		</label>
		<g:textField name="lastName" value="${profile?.lastName}"/>
		</div>
		</div>
		
		</div>	
				
				
			
		<div class='row'>
			
		<div class='col-sm-2'>
		<div class='form-group'>
		<label for="age">
		<g:message code="wschat.firstname.label" default="Your Age" />
		</label>
		<g:select id="duration" name="age" from="${5..99}" required="" value="${profile?.age}" class="one-to-one"/>
		</div>
		</div>
				
				
		<div class='col-sm-4'>
		<div class='form-group'>
		<label for="birthDate">
		<g:message code="wschat.birthDate.label" default="Date Of Birth" />
		</label>
		<g:textField name="birthDate" value="${profile?.birthDate ?: current}" />
		</div>
		</div>
				
		<div class='col-sm-2'>
		<div class='form-group'>
		<label for="gender">
		<g:message code="wschat.gender.label" default="gender" />
		</label>
		<g:select  name="gender" from="${['Male':'Male','Female':'Female']}"  optionKey="key" optionValue="value"   value="${profile?.gender}" class="one-to-one"/>
		</div>
		</div>
		
		
		<div class='col-sm-2'>
		<div class='form-group'>
		<label for="children">
		<g:message code="wschat.gender.label" default="children" />
		</label>
		<g:select id="children" name="age" from="${0..10}" required="" value="${profile?.children}" class="one-to-one"/>
		</div>
		</div>
		
		</div>
				
				
		<div class='row'>
			
		<div class='col-sm-2'>
		<div class='form-group'>
		<label for="status">
		<g:message code="wschat.gender.label" default="status" />
		</label>
		<g:select  name="martialStatus" from="${['Single':'Single','Married':'Married']}"  optionKey="key" optionValue="value"   value="${profile?.gender}" class="one-to-one"/>
		</div>
		</div>
			
		<div class='col-sm-2'>
		<div class='form-group'>
		<label for="age">
		<g:message code="wschat.firstname.label" default="\$\$" />
		</label>
		<g:select  name="wage" from="${['5000.00':'5k','10000.00':'10k','20000.00':'20k','30000.00':'30k','40000.00':'40k','50000.00':'50k','60000.00':'60k','70000.00':'70k','80000.00':'80k','90000.00':'90k+']}" required="" optionKey="key" optionValue="value"  value="${profile?.wage }" class="one-to-one"/>
		</div>
		</div>
				
				
		<div class='col-sm-8'>
		<div class='form-group'>
		
		<g:textField name="email" placeholder="${profile?.email ?: 'user@domain.com'}"  />
		<g:textField name="homePage" placeholder="${profile?.homePage ?: 'http://grails.org/plugin/wschat'}"  />
		</div>
		</div>
				
	
		
		</div>
				
		
		</div>	
		<div class="modal-footer">
			<g:submitToRemote class="btn btn-primary" url="[controller:'wsChat',action:'updateProfile']"	update="profileconfirmation"  onComplete="closeModal()" value="Update profile" />
		</div>	
		</g:formRemote>
					

	
	
	
	
	
