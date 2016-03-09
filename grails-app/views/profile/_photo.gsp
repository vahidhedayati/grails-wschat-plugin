<!DOCTYPE html>
<html>
<head>
<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="achat"/>
    </g:if>
    <g:else>
    	<g:render template="/assets"/>
    </g:else>
</g:if>
<g:else>
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="chat"/>
    </g:if>
    <g:else>
   		<g:render template="/resources"/>
   	 </g:else>
</g:else> 

</head>
<body>
	<div class="modal-body">
		<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			
			
<g:if test="${username }">
	<g:form controller="wsChat" action="addPhoto"  name="photoUpload" id="1"  enctype="multipart/form-data">
		<input type="hidden" name="username" value="${username }">
		<input type="file"  class="btn btn-default" name="photo" />
		<g:submitButton class="btn btn-primary" name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Upload Photo')}"  />
	</g:form>
</g:if>


</body>
</html>