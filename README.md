wschat 0.7
=========

Grails websocket chat Plugin based on existing examples, provides  websocket chat that can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites.

Plugin will work with tomcat 7.0.54 + (8 as well) running java 1.7 +


Dependency :

	compile ":wschat:0.7" 

This plugin provides  basic chat page, once installed you can access
```
http://localhost:8080/yourapp/wsChat/
```

If you have disabled login as per configuration below, you must set
 
```
session.user
```
with your logged in username.

Then access chat by going to 
```
http://localhost:8080/yourapp/wsChat/chat
```

![Demo under 0.6](https://raw.githubusercontent.com/vahidhedayati/testwschat/master/demo/7.jpg)
Private messages using jquery-ui.chatbox. For this feature to work you need to ensure that when users log in they use a complete word with no spaces and not a numeric id either. The reason is a div id is created for the pm box and this needs to match javascript conventions. So spaces should be removed or replaced with dashes. refer to custom call which I need to update sometime soon to do the same..
 


# Video:
It is quite a straight forward plugin but if you must [youtube example grails app running wschat 0.3](https://www.youtube.com/watch?v=U211AZqpkxs)

	 	
# Config.groovy variables required:

Configure properties by adding following to grails-app/conf/Config.groovy under the "wschat" key:

```groovy
/*
* To disable default login page set following
* session.user must be set and now simply
*/
wschat.disable.login = "yes"


/*
* Your page title
*/
wschat.title='Grails Websocket Chat'


/* 
* Your page heading
*/
wschat.heading='Grails Websocket Chat'


/*
* This is the most important configuration 
* in my current version the hostname is being defined by tomcat start up setenv.sh
* In my tomcat setenv.sh I have
* HOSTNAME=$(hostname)
* JAVA_OPTS="$JAVA_OPTS -DSERVERURL=$HOSTNAME"
*
* Now as per below the hostname is getting set to this value
* if not defined wschat will default it localhost:8080
*
*/
wschat.hostname=System.getProperty('SERVERURL')+":8080"

```

# Commands:

Within chat you can execute

	/disco  		-- to disconnect or leave the page which will auto disconnect you
	/pm {username} {message}
	/pm {username},{message}
	
	either will send a private message to user defined.. comma for users that have a space in their name..
	
	
	
# Issues:

Whilst running this plugin on a tomcat server from an application that calls plugin, I have seen:
```
javax.websocket.DeploymentException: Multiple Endpoints may not be deployed to the same path [/WsChatEndpoint]
	at org.apache.tomcat.websocket.server.WsServerContainer.addEndpoint(WsServerContainer.java:209)
	at org.apache.tomcat.websocket.server.WsServerContainer.addEndpoint(WsServerContainer.java:268)
	at javax.websocket.server.ServerContainer$addEndpoint.call(Unknown Source)
```	
This does appear to be a warning and endpoint still works fine, and happens in tomcat... 7 + 8



	
# Custom calling plugin
https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Merging-plugin-with-your-own-custom-calls
	
	
# Version info
```
0.7 - 	Basic chat with private messaging using jquery-ui.chat popups now fully 
		functional. 
		Issue with long sentences in main room extending room width fixed.
		Issue with pop ups closed not re-opening fixed.
		Issue with multiple PM's overlapping - fixed.
		
		
0.6 -	Issues with pm boxes showing wrong name on response - fixed. 
		clicking send will no longer repeat sending blank response.
		Leave broadcasts not working fixed.
		logins with a space changed to underscore to allow javascript div calls
		html encoding added to user input so that it does not update page with raw html.
		Spaces between pm boxes dabbled not fixed yet..
		 
0.5 - 	Local tests for pm on 0.4 was working :( unsure why not via plugin

0.4 - 	Private message in pop up boxes using jquery-ui.chatbox. Still needs work on further releases.

0.3 - 	Private messaging added:  EITHER /pm username,message |  OR /pm username message
		Users list converted from textarea to html div element - easier to set up functions to 
		interact with users logged in. i.e. to create links to pm etc..

0.2	- 	Tidy up of javascript - additional checks enter auto submit - 	
