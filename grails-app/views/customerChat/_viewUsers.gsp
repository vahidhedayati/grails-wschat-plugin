<div class='modal-header'>
<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
<div id='myModalLabel'><h3>${message(code: 'wschat.'+actionName+'.label', default: ''+actionName+'')}</h3>
<div class=heading1>
			<g:message code="wschat.search.label" default="Search"/>:
			<g:remoteField name="mq"  id="searchform"  update="resultSet" paramName="mq" url="[controller:'wsChat', action:'searchLiveChat']"></g:remoteField>
</div>

</div>
</div>


<div id="resultSet">
<g:render template="/customerChat/userList" model="${[bean:bean]}"/>
</div>

<div class="pagination">
<util:remotePaginate controller="wsChat" action="viewUsers" 
params="[max: max, divupdate:divupdate, id:inputid, s:s, order:order, pageSizes:pageSizes, sortby:sortby, 
offset:offset,  viewtype: 'na']" total="${bean.userListCount}" update="${bean.divupdate }"
max="${bean.max}" pageSizes="[2: '2', 10:'10 Per Page', 20: '20 Per Page', 50:'50 Per Page',
100:'100 Per Page',250:'250 Per Page',500:'500 Per Page',1000:'1000 Per Page']" />
</div> 
