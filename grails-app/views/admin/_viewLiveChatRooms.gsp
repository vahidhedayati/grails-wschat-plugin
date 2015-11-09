<div class='modal-header'>
<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
<div id='myModalLabel'><h3>${message(code: 'wschat.'+actionName+'.label', default: ''+actionName+'')}</h3>
</div>
</div>


<div id="resultSet">
<g:render template="/admin/liveChatRoomList" model="${[bean:bean]}"/>
</div>


