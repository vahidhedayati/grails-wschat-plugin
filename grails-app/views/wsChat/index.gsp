<!DOCTYPE html>
<html>
    <head>
        <g:render template="includes" model="${[bean:bean]}"/>
        <title>${bean.chatTitle }</title>
    </head>
<body>
    <g:if test="${flash.message}">
		<div class="message" role="status">
			${flash.message}
		</div>
	</g:if>
    <div class="container">
      <div class="row clearfix">
         <div class="col-md-12 column">
            <g:form  class="form-horizontal"name="form1"  method="post" controller="wsChat" action="login">
			<div class="page-header" >
				<h2>${bean.chatHeader }</h2>
				<small> ${bean.now}</small>
			</div>

		    <div class="form-group">
             <label for="nickname" class="col-sm-2 control-label">
        		<g:message code="wschat.nickname.label" default="ChatName"  class="form-control" />
        	 </label>
        	   <div class="col-sm-4" id="userDetails">
					<g:textField name="username" value="${session.wschatuser?:''}"/>
			    </div>
			</div> 
			<div class="form-group">
             <label for="room" class="col-sm-2 control-label">
             	<g:message code="wschat.choose.room.label" default="Choose Room" />						
        		
        	 </label>
        	   <div class="col-sm-4">	
					<g:select name="room" from="${bean.rooms}"  class="form-control"/>
			    </div>
			</div>
			<div class="form-group">
             <label for="submit" class="col-sm-2 control-label">
        		<g:message code="wschat.go.chat.label" default="Go Chat"  class="form-control" />
        	 </label>
        	   <div class="col-sm-4">	
					<input type="submit" value="${g.message(code:'wschat.login.label', default:'Login') }" class="btn btn-primary btn-lg">
			    </div>
			</div>
		    </g:form>
	      </div>
        </div>
    </div>
<g:javascript>
<g:if test="${session.wschatuser}">
    $('#username').prop('disabled',true);
    //$('#userDetails').html('${session.wschatuser}');
</g:if>
</g:javascript>
</body>
</html>