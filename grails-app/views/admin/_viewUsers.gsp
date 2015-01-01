

<div id="resultSet">
<g:render template="/admin/userList"/>
</div>

<div class="pagination">
<util:remotePaginate controller="wsChat" action="viewUsers" 
params="[max: max, divupdate:divupdate, id:inputid, s:s, order:order, pageSizes:pageSizes, sortby:sortby, 
offset:offset,  viewtype: 'na']" total="${userListCount}" update="${divupdate }"
max="${params.max}" pageSizes="[2: '2', 10:'10 Per Page', 20: '20 Per Page', 50:'50 Per Page',
100:'100 Per Page',250:'250 Per Page',500:'500 Per Page',1000:'1000 Per Page']" />
</div> 
