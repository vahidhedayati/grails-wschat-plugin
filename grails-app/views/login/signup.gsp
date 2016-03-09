<html>
<head>
	<title><g:message code="wschat.signup.title" default="signup page"/></title>
<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<meta name='layout' content="achat"/>
	<asset:javascript src="jquery.min.js" />
	<asset:javascript src="jquery-ui.min.js" />
    <asset:stylesheet src="jquery-ui.min.css" />
    <asset:stylesheet src="font-awesome.css"/>
	<asset:javascript src="bootstrap.min.js" />
    <asset:stylesheet href="bootstrap.min.css" />
    <asset:stylesheet href="chat.css" />
    <asset:stylesheet href="chat-ui.css" />
</g:if>
<g:else>
<meta name='layout' content="chat"/>
<script type="text/javascript"	src="${resource(dir: 'js', file: 'jquery.min.js')}"></script>
<script type="text/javascript"	src="${resource(dir: 'js', file: 'jquery-ui.min.js')}"></script>
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'jquery-ui.min.css')}" type="text/css" media="screen" />
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'font-awesome.css')}" type="text/css" />
<script type="text/javascript"	src="${resource(dir: 'js', file: 'bootstrap.min.js')}"></script>
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css"/>
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'chat.css')}" type="text/css" media="screen" id="chat_theme" />
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'chat-ui.css')}" type="text/css"/>
</g:else>
</head>
<body>
 <g:hasErrors bean="${bean}">
    <ul class="errors" role="alert">
       <g:eachError bean="${bean}" var="error">
           <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
        </g:eachError>
    </ul>
  </g:hasErrors>

 <div class="container">
   <div class="col-md-6 col-md-offset-2" >
        <div class="panel panel-default">
           <div class="panel-body">
              <g:set var="lang" value="${session.'org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE' ?: org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).toString().substring(0,2)}"/>
          		<nav id="Navbar" class="navbar" role="navigation" >
      				<ul class="nav navbar-nav">
           				<li class="controller">
        					 <h2><g:message code="wschat.signup.header" default="Signup"/></h2>
        				</li>
        				<li class="controller">
        				     <g:if test='${flash.message}'>
                         		<div class='login_message'>${flash.message}</div>
                            </g:if>
        				</li>

           			</ul>
        		</nav>

        <g:form  class="form-horizontal"name="form1"  method="post" controller="login" action="auth">
            <g:submitButton name="${g.message(code:'wschat.login.title', default:'login')}" class="btn btn-danger btn-block"/>
        </g:form>
        <br/>

       <g:form  class="form-horizontal"name="form1"  method="post" controller="wsChat" action="register">
            <div style="clear:both;"></div>
    	    <div class="form-group fieldcontain ${hasErrors(bean: bean, field: 'username', 'error')} ">
			    <label for='username'class="col-sm-2 control-label"><g:message code="springSecurity.login.username.label"/>:</label>
			    <div class="col-sm-4">

			       <g:field type="text" name="username" value="${bean.username}" class="${hasErrors(bean: bean, field: 'username', 'error')}" required=""/>

                </div>
            </div>
              <div class="form-group fieldcontain ${hasErrors(bean: bean, field: 'email', 'error')} ">
               <label for='username'class="col-sm-2 control-label"><g:message code="wschat.email.label" default="Email"/>:</label>
               <div class="col-sm-4">
                    <g:field type="text" name="email" value="${bean.email}" class="${hasErrors(bean:bean, field:'email', 'error')}" required=""/>
                </div>
             </div>
	          <div class="form-group fieldcontain ${hasErrors(bean: bean, field: 'password', 'error')} ">
			    <label for='password' class="col-sm-2 control-label"><g:message code="springSecurity.login.password.label"/>:</label>
				 <div class="col-sm-4">
				    <g:field type='password' name='password' class="${hasErrors(bean:bean, field:'password', 'error')}" required=""/>
				</div>
			</div>
            <div class="form-group fieldcontain ${hasErrors(bean: bean, field: 'password2', 'error')} ">
                <label for='password2' class="col-sm-2 control-label">
                <g:message code="wschat.repeat.password.label" default="Repeat Password"/>:</label>
            	<div class="col-sm-4" >
            	   <g:field type='password' name='password2' class="${hasErrors(bean:bean, field:'password2', 'error')}" required=""/>
            	</div>
            </div>

			<div class="form-group">
                  <label for="submit" class="col-sm-2 control-label"></label>
                 <div class="col-sm-4">
				    <g:submitButton name="submit" class="btn btn-primary" value="${message(code: 'wschat.signup.button', default:'signup')}"/>
			    </div>
			</div>
		</g:form>
    </div></div></div>
</div>
</body>
</html>
