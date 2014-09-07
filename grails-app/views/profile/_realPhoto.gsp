

	
	<div class="modal-body">
	<g:if test="${photoId }">
	<img class="Photo" onclick="closePhotos()" src="${createLink(controller:'wsChat', action:'viewPic', id:''+photoId+'')}" /><br/>
	</g:if>
	</div>

