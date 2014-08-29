	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">x</button>
		<h3 id="prompt">Viewing ${username }'s image</h3>
	</div>
	
	<div class="modal-body">
	<g:if test="${photoId }">
	<img class="Photo" onclick="closePhotos()" src="${createLink(controller:'wsChat', action:'viewPic', id:''+photoId+'')}" /><br/>
	</g:if>
	</div>