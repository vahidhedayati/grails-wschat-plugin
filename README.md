wschat 0.20
=========

Grails websocket chat Plugin provides a multi-chat room facilty to an existing grails based site/application.


### wschat supports:

##### User roles (Admin/regular user)
##### Admin can:  kick/Ban (for specified time period)
##### Users can create profiles define details and upload photos.
##### Chat rooms can be created in Config.groovy +/ DB once logged in using UI.
##### 0.19+ supports webcam tested  on chrome/firefox.  
##### Audio not working as yet - future releases will contain audio too


 Websocket chat can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites.

Plugin will work with tomcat 7.0.54 + (8 as well) running java 1.7 +



Dependency :

	compile ":wschat:0.20" 

This plugin provides  basic chat page, once installed you can access
```
http://localhost:8080/yourapp/wsChat/
```
		

# Custom calling plugin disabled login

If you have disabled login as per configuration below, you must set
 
```
session.wschatuser
```
with your logged in username.


Then access chat by going to 
```
http://localhost:8080/yourapp/wsChat/chat
```

If your user details are provided by your own login mechanism. In this example the userId is passed to a service that tries to match a name from what is on DB if not strip email address of AD account and show before @ (this part of code is not provided but is what the service is doing)

[custom calls from authenticated user to chat plugin](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Merging-plugin-with-your-own-custom-calls)
 

 
# Video:
It is quite a straight forward plugin but if you must :

[youtube example grails app running wschat 0.14 part1](https://www.youtube.com/watch?v=E-NmbDZg9G4)

[youtube example grails app running wschat 0.14 part2](https://www.youtube.com/watch?v=xPxV_iEYYm0)


	 	
# Config.groovy variables required:

Configure properties by adding following to grails-app/conf/Config.groovy under the "wschat" key:

```groovy
/*
* To disable default login page set following
* session.wschatuser must be set and now simply
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


/* timeout 
* 0.10+ feature
* This is the default timeout value for your chat users.
* by default it is set to 0 which means indefinite login.
* If you wish to get user to be timed out if inactive set this to a millisecond value
*/
wschat.timeout=0

/*dbsupport
* 0.10 +
* can be set to NO - 
* this will stop ban/kick add/remove friend features
* not really played with this myself.
*/
wschat.dbsupport='YES'

/*defaultperm
* this be your chat users being added to db
* wschat.defaultperm='admin'
* by default it is set to user if nothing defined
*/
wschat.defaultperm='user'

/*rooms
* this is the list of rooms that user can choose to join
* if defined it will list them in the default index page
* if not it will check db room table if not in db either
* then default wschat room set for all 
*/

wschat.rooms = ['room1','room2']


/*
* show room heading or not by default it will
*/
wschat.showtitle="NO"

```


##### Creating admin accounts, in your bootstrap.groovy add something like this:

```groovy
import grails.plugin.wschat.ChatPermissions
import grails.plugin.wschat.ChatUser


class BootStrap {
    def init = { servletContext ->		
		def perm=ChatPermissions.findOrSaveWhere(name: 'admin').save(flush:true)
		ChatUser.findOrSaveWhere(username:'firefox', permissions:perm).save(flush:true)
		ChatUser.findOrSaveWhere(username:'chrome', permissions:perm).save(flush:true)
    }
    def destroy = {
    }
}

```
	

# 0.10+ & resources based apps (pre  2.4) :
Under Resources based application you can still use the latest code base, but you need to exclude hibernate. Something like this:

```groovy
compile (":wschat:XXX") { excludes 'hibernate' }
```

Or you could do something like this:

```groovy
compile ":wschat:X.XXX", {
			transitive = false
		}
		
 ```

Now you will need to also upgrade hibernate, on a test project I was able to include hibernate:
```groovy
runtime ":hibernate4:4.3.5.4"
```


But on my current 2.3.7 app doing such a thing caused a lot of issues and I backed down back to original :
```groovy
runtime ":hibernate:3.6.10.10"
```

With this enabled there is one more thing that needs to be done, in your config.groovy you need to disable DB support for wschat, it sucks ye I know but at least basic chat works. The proper solution is to ensure you are running grails 2.4+:

```groovy
wschat.dbsupport="no"
```



# Commands:

Within chat you can execute:

Pm methods: either will send a private message to user defined.. comma for users that have a space in their name..

```
/disco  		-- to disconnect or leave the page which will auto disconnect you
/joinRoom {roomname}
/camenabled {yourusername}
/camdisabled {yourusername}
/pm {username} {message}
/pm {username},{message}

```


Admin commands

```	
// Admin commands now supported in modalbox popups 
// no need to manually define any admin commands
/addRoom {roomName}
/delRoom {roomName}  --- This will also kick off all users in that room
/kickuser {username}
/banuser {username},1:minutes
/banuser {username},10:hours
/banuser {username},10:days
/banuser {username},10:years	
```	

	
# Version info
```
0.20 -	Issues with PM introduced in 0.19 tidy up - now fixed.

0.19 - 	Webcam fixed working/tested in firefox/chrome. Both show camera from either 
		broadcasting. stopped user logging on twice into chat.
		Webcam support functional
		
0.18 - 	Rooms added, configurable via Config.groovy +/or Database - which admin 
		can do via front end.
		Webcam support added - using websockets and html5 getUserMedia. 
		Although very flakey - 
		seems to send/receive on chrome. Can send on firefox and be recieved on 
		Chrome clients. 
		
0.17 - 	Issue with profiles/birthDate fixed (tested under mysql). pluginbuddy ver updated.
		dbsupport check added around all DB calls in endpoint.

0.16 - 	Appears to be issues with pluginbuddy returning resources - returns assets fine..

0.15 -	Changed session.user to session.wschatuser - to stop any conflicts with 	
		existing application session information.
		Documentation on custom calls updated in wiki to show how to use 
		session.wschatuser since this is now a requirement in order to use profile 
		features.
		Dot/Space seperated usernames changed to _  before setting wschatuser 
		session value to ensure chatuser matches what further checks expect.
		
0.14 - 	Bug fixes. new videos. 

0.13 -	User profile modalbox feature added.
		User can upload many images which appears on their profile. 
		(Last 5 uploaded shown)
		
0.12 - 	modalbox added with admin menu addition to each user hover options.
		Banning user pops open modalbox where you define the period as admin to 
		ban end user for. 
		Ban/Kick users added as additional drop downs to admin users.
		 
0.11 -	DBsupport by default turned on 
		Tidy up of javascript call created new wschat.js
		Tidy up of friends/block list wrapped around dbcheck on endpoint.
		
0.10 - 	MaxIdleTimeout value defined in overrides as timeout,
		by default endless chat users connections
		DB tables added, user can now:
		Block/unblock another user from pms.
		Add/remove a friend
		Admin privilages added. - /kickuser username as admin will kick out user
			 

0.9 - 	Tidy up of gsp pages - added pluginbuddy - cleaned up gsp display calls 
		for backwards compatibility.
		revisit of css for chat area which was not working well
		Last version of basic chat with no DB relations.
		
0.8 - 	Tidy up - removed html generation from EndPoint.
		Html generated within javascript parsing json values.
		Removed unnecessary js file.
		 
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
		Users list converted from textarea to html div element - easier to set up 
		functions to interact with users logged in. i.e. to create links to pm etc..

0.2	- 	Tidy up of javascript - additional checks enter auto submit - 	
```


# Issues:

Whilst running this plugin on a tomcat server from an application that calls plugin, I have seen:
```
javax.websocket.DeploymentException: Multiple Endpoints may not be deployed to the same path [/WsChatEndpoint]
	at org.apache.tomcat.websocket.server.WsServerContainer.addEndpoint(WsServerContainer.java:209)
	at org.apache.tomcat.websocket.server.WsServerContainer.addEndpoint(WsServerContainer.java:268)
	at javax.websocket.server.ServerContainer$addEndpoint.call(Unknown Source)
```	
This does appear to be a warning and endpoint still works fine, and happens in tomcat... 7 + 8

