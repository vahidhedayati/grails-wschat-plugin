wschat 1.10-SNAPSHOT
=========

Grails websocket chat Plugin provides a multi-chat room facilty to an existing grails based site/application.


### wschat supports:

##### User roles (Admin/regular user)
##### Admin can:  kick/Ban (for specified time period)
##### Users can create profiles define details and upload photos.
##### Chat rooms can be created in Config.groovy +/ DB once logged in using UI.
##### 0.19+ supports webcam tested  on chrome/firefox.  
##### 1.0+ supports WebRTC (HD: Video/Audio streaming using cam/mic) currently only on Chrome Canary.

 Websocket chat can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites.

###### Plugin will work with tomcat 7.0.54 + (inc. 8) running java 1.7+


##### Dependency :

	compile ":wschat:1.10-SNAPSHOT" 

This plugin provides  basic chat page, once installed you can access
```
http://localhost:8080/yourapp/wsChat/
```
		

#### Custom calling plugin disabled login

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


#### Custom calls

1. [custom calls from authenticated user to chat plugin](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Merging-plugin-with-your-own-custom-calls)

2. Simpler method using taglib:
```gsp
<div id="homeBox">
<g:if test="${!session.user }">
	<g:render template="/auth/loginForm" model="[did: 'homeBox']"/>
</g:if>
<g:else>
	<chat:connect chatuser="${chatuser}" room="${room }"/>
</g:else>
</div>
```

If you wanted you could just call :
```gsp
 	<chat:connect chatuser="${chatuser}"/>
```
Above would go through your logics and assign default room to the user.


 
### Video:
It is quite a straight forward plugin but if you must :

[youtube example grails app running wschat 0.14 part1](https://www.youtube.com/watch?v=E-NmbDZg9G4)

[youtube example grails app running wschat 0.14 part2](https://www.youtube.com/watch?v=xPxV_iEYYm0)



### WebRTC/WebCam explained:

If you are using 1.5+ you will notice a new feature that appears when you hover over your name: 

```
Enable WebCam 
```
(Works in firefox/chrome)

If you click this option a popup will appear on the top of the browser asking permission to use your webcam.
Click allow and a videobox will appear showing you on your webcam. your name status will change to show a webcam to you and others in that chat room.

When another user hovers over your name they will see :

```
View WebCam 

```
(Multiple users can view your webcam)

If they click this button a pm is sent to you saying their viewing you and they will be able to view you on webcam.

Alternatively if they open a PM box to you a play button should appear if they click play the same as above will happen.
If they had been in PM previous to you opening webcam the play button will not show. If this user clicks the far right hand (white) X. This will destroy the PM box. Upon them clicking pm again the play button will now appear. Sounds like a lot of work to me since they could just hover your name and click view cam :)


```
Enable WebRTCE
```
(Works only in Chrome Canary + ) 

WebRTC is really amazing new technology. I will be trying focus some more time into this and attempting to expand the amount of users that can interact with one another as well as extending to firefox.

At the moment 1.5 - Only 2 people can interact.

So if you enable this option the same as before you are asked for permission to the ip running the websocket chat server for permission.

If you enable it, you will see yourself in a small video box.  Your name should have a different icon a video+phone. 

If another user in the same room (on another pc) hovers over your name they will see:

```
WebRTCE
```
(Only 1 person can )

The same rules as above for the play button in the PM box. The play button has built in intelligence to figure out which mode you are running (Webcam/webrtc) and to attempt to call relevant method.

If they click this, it will also ask them permissions for their audio/video.  Once accepted after a few seconds you will appear in a large HD video box for them, whilst they appear small in their own video box, vice versa for you. You should now be able to interact real time.



Technical details of how webrtc is working:

As it stands by default whether you confgure the STUN server in the Config example as the same or if not at all (Which will default to the same google server). the information will be sent to google's public server.

If you wanted you could enable console.log by function sendToServer is sending messages within client.js - you will the log out the messages sent to websocket.


When second user attempts to view person running webrtc an offer is sent via sockets to primary candidate. 
Which triggers a handshake followed by heavy payloads delivering icecandidate packets to the websocket server from both candidates. To understand more about it have a look here: [SDP](http://tools.ietf.org/id/draft-nandakumar-rtcweb-sdp-01.html).  

Once this is all done, it all goes very quiet on the socket connections.


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
wschat.camtimeout=0

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




/*
* 1.1+ WEBRTC :
* STUN SERVER CONFIGURATION
* Please add a block like this below to your Config.groovy
*/

stunServers {
	iceServers=[
		[url: 'stun:stun.l.google.com:19302']
	]
}


/* 
*If you do not set left/join room to a value by default
* it will log joiners/leavers. You can set the timeout value 
* by default to 0 to a ms value of your preference if user out/in is quick
* This may slow down msg being sent..
* Logleavers sets a log.error to a value of current username room to verify
* abnormalities in the username (spaces and so forth)
* by default this is no
* so set a timeout if you wish which is only worthy if send.leftroom='yes'
* 
*/ 
wschat.left.timeout='1000'
wschat.send.leftroom='no'
wschat.logleavers='yes'
wschat.send.joinroom='no'



```


#### STUN Server, setting up your own server:
[WebRTC-terminology](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/WebRTC-terminology), lots of useful information to break down and further info on  STUN/TURN

First thing to note is that you will need a machine that has two network interfaces, once this is in place:

http://www.stunprotocol.org/

http://www.rtcquickstart.org/ICE-STUN-TURN-server-installation


https://github.com/mozilla/stun-vm


https://github.com/jselbie/stunserver

https://code.google.com/p/rfc5766-turn-server/

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
	

#### 0.10+ & resources based apps (pre  2.4) :
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
1.10-SNAPSHOT - Back to synchronizedSet again, removed iterator calls and called .each instead,
				Tidy up of nested loop call of sendusers.
				 
1.10 - As a result of 1.9 webcam was broken, function moved to chatUtils.
	 	chatroomUsers and camsessions change from Set to Collection.
	 	synchronized added around all iterators.
	 	 
1.9	- Moved code out of extended class to Grails services. 

1.8-SNAPSHOT2 : Fancier method user can now enable/disable joiners/leavers in their config.groovy

1.8-SNAPSHOT1 : commented out leavers send block
1.8-SNAPSHOT : https://github.com/vahidhedayati/grails-wschat-plugin/issues/3
1.8 -	Added withTransaction to all DB calls, to fix issue with 2.4.4 apps. 
		Added some wrappers to enable backward compatibility for hibernate to BuildConfig/DataSource.
		
1.7 -	Issue with 1.6 in prod - a fix put in for 1.7

1.6 -	Tidy up - removal of Holders.config - removed _Events.groovy listener added to plugin descriptor.

1.5 - 	Minor stuff - disabled extra dependencies, fixed 
		chat message double echos for user sending msg.
		
1.4 - 	TicTacToe gsp pages causing issues - removed until I have
		some time to port it over.
1.3	-	uivideobox issues in 1.2 with reload page - fixed. Media disconnects upon user pressing X.
		So potential other streams should continue to work.
		Tidy up of various JS methods/calls.
		
1.2 -	Issues in 1.1 with private messaging - fixed
		2 close buttons added to PM boxes.
		Dark black or first X will hide PM.
		Last on the right or white X removes PM box and its content.
		This is useful if user enables cam, using this close method upon next
		- PM Play button to watch cam/webrtc is shown.
		For now given up attempts to unload getUserMedia upon closing ui.videobox
		hack added to jquery.ui.videobox Line 162 to window reload in order to cut off
		any user media. This now means if user cuts off own cam any cams viewed 
		will be lost since page is reloaded.
		
		
1.1 - 	Tidy up of rushed out 1.0 release - stun server introduced as config override.
		Fixed up enable rtc/webcam to broadcast correct type.
		Friend/user can now choose correct mode as per broadcast method webcam/webrtc.
		Issues with camuser/camusername logic fixed.
		
1.0 -	Managed to get webrtc now working. User can enable older webcam method or
		enable webrtc where audio/video streaming happens using html5 webrtc via 
		websockets to exchange SDP information.
		On this specific release the stun server will be defaulted 
		to :stun.l.google.com:19302 (found in client.js work of Felix Hagspiel)
		On the next release I want to give this configuration over to enduser
		
		
0.23 -  Tidy up - hope nothing breaks.
0.22 -	Issue with send pm on user sending pm. It send pm but displayed username as msg -fixed.
		Lots of issues around viewing webcam - clashing with ui.chatbox - a hack has been put in,
		read known issues above.
		Video play button now only appears in the pm box if remote user has cam enabled.
		This loads up two types of chat boxes one with the new style buttons if the user has a cam,
		standard chatbox if no cam - this needs more work really a big mess.
		Multiple requests to view the same cam should no longer happen. 
		I think pm/video is now functioning without issues, can start to focus on audio for 0.23.
		
0.21 -	Got a bit messy - need a clean up soon. Changes are:
		Webcam view/send now moved within page so ui.chatbox extended and ui.videobox created.
		ui.chatbox changed now includes play button - this needs a tidy up should not show when no cam.
		new taglib call added documented above.
		calls to controller from index page was not working in production - tidy up of gsp/calls.
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


### Known issues/work arounds:

Since 0.20+ ui.videobox has been added, earlier versions and even current version suffers from conflicts with jquery.ui.chatbox and does not send message. Currently the temporary fix is when you open a cam/rtc session to another user or as the initiator. If you did have pm boxes open you will find they will all close down as you open the video box. If you then go to the user and click on PM your currently pm history will reappear along with their pm box.

