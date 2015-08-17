<div class="fixTable">
<div id="myUsers">
<div class='modal-header'>
<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
<div id='myModalLabel'><h3>${message(code: 'wschat.'+actionName+'.label', default: ''+actionName+'')}</h3>
<div class=heading1>
</div>
</div>
</div>
<div id="resultSet">
	<table  class="col-sm-12">
	<thead><th>From</th><th>Message:</th><th>Date</th></thead>
	<tbody>
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
	</tbody>
	</table>
</div>
</div>
</div>