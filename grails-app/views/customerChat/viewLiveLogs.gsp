<div class="fixTable" style="height: 600px;">
	<div id="myUsers">
		<div class='modal-header'>
			<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
			<div id='myModalLabel'><h3>${message(code: 'wschat.'+actionName+'.label', default: ''+actionName+'')}</h3>
				<div class=heading1></div>
			</div>
		</div>

		<table  class="col-sm-12">
		<tr>From</tr><tr>Message:</tr><tr>Date</tr>

			<g:each in="${livelogs}" var="log">
			<tr>
				<td>
					<g:if test="${log.user}">
						Agent:${log.user}
					</g:if>
					<g:else>
						User
					</g:else>
				</td>
				<td>${log.message}</td>
				<td>${log.date}</td>
			</tr>
		</g:each>
	</table>
</div>
</div>
