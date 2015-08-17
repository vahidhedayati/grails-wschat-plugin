wschat
=========

Grails websocket chat Plugin provides a multi-chat room add-on to an existing grails based site/application.


##### grails wschat plugin supports:
```
User roles (Admin/regular user)
Admin can:  kick/Ban (for specified time period)
Users can create profiles define details and upload photos.
Chat rooms can be created in Config.groovy +/ DB once logged in using UI.

0.19+ -  			supports webcam tested  on chrome/firefox.
  
1.0+  -  			supports WebRTC currently only on Chrome Canary.

1.11-SNAPSHOT4 + 	supports WebRTC screen sharing + chat client/server 
					messaging/event services.
					screen sharing only Chrome Canary+ (no plugins req)
					
1.12 				Chat room bookings for multiple participants
					Persist messages to DB override config required.
					Lots of bug fixing + ui changes.
					On the fly colour theme changes extended.
					
1.13				Friends List / Room list - showing any online friends 
					anywhere in chat. Issue with friends/block list unique ids 
					causing issue (Fixed).
					Offline Messaging added - offline PM friends. Or if a user that
					leaves room. Upon next login offline messages appears.
					More UI updates. Button to close PMs added.
					
					
1.17 				Websocket -> WebRTC File sharing peer2peer available -  
					Once websocket negotiation are completed, WebRTC and 
					HTML5 File API are used in conjunction to allow file transfer
					between the users physical machines.
					Limitations 
					1. (Chrome) - seems to only work on small files 18k worked..
					50k files failed. On firefox tested as far as 2MB files all good.
					2. Only chrome -> chrome OR Firefox -> Firefox. 
					Can not send from FF to Chrome and vice versa.. read here:
					https://bloggeek.me/send-file-webrtc-data-api/
										 
										 
1.17-SNAPSHOT1      Mediastreaming enabled but has not worked for me personally, might do for someone else. 
					Only supports .webm files.
					firefox
    				about:config
    				media.mediasource.enabled = true

    				screen casting for Chrome
    				chrome://flags
    				enable screen capture support in getusermedia()
    				HTTPS is MANDATORY for screen casting
    				Chrome will ask 'Do you want <web site name> to share your screen? - say YES
    				firefox show what shared by chrome but FF doesn't share crseen itself 
    				(transmits just video from cam)
    				Or run
    				chromium-browser --allow-http-screen-capture --enable-usermedia-screen-capturing


1.20 				Long overdue and popular request - Same user logging in twice in two different rooms		

1.21 				Websocket TicTacToe game added (Watch video 6)

1.23				Live Chat feature added as part of the plugin(watch video 7)

1.24				ChatBot and badWords added to the main chat room

```


 Websocket chat can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites.

###### Plugin will work with tomcat 7.0.54 + (inc. 8) running java 1.7+


###### Dependency (Grails 2.X) :
```groovy
        compile ":wschat:1.24-SNAPSHOT1"          
```

[codebase for grails 2.X](https://github.com/vahidhedayati/grails-wschat-plugin/tree/grails2)


###### Dependency (Grails 3.X) :
```groovy
        compile "org.grails.plugins:wschat:3.0.3"
```

[codebase for grails 3.X](https://github.com/vahidhedayati/grails-wschat-plugin/)


This plugin provides  basic chat page, once installed you can access
```
http://localhost:8080/yourapp/wsChat/
```



## Config.groovy variables required:
 [Config.groovy variables required:](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Config.groovy)
 		


##Version info
[Version info](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Version-info)



###Commands
[Commands](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Commands)




### Intergrating chat with your apps authentication
[Definitely disable index](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Custom-calling-plugin-disabled-login)

[Method 1](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Merging-plugin-with-your-own-custom-calls)

[Method 2](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Custom-calls)

 
### Videos:
1. [Video: grails app running wschat 0.14 part1](https://www.youtube.com/watch?v=E-NmbDZg9G4)

2. [Video: grails app running wschat 0.14 part2](https://www.youtube.com/watch?v=xPxV_iEYYm0)

3. [Video: Client/Server Messaging part 1.](https://www.youtube.com/watch?v=zAySkzNid3E)
 Earlier version focus on backend control of socket messages. So the backend of your application. Override a service for this. Explained in links further below.
 
4. [Video: Client/Server Messaging part 2](https://www.youtube.com/watch?v=xagMYM9n3l0)
(Messages via websockets to frontend). In short update frontend via websocket callbacks.

5. [Video: 1.12 Chat room booking/reservations](https://www.youtube.com/watch?v=ZQ86b6zN4aE)

6. [Video: 1.22 Multiple login with same Chat user and TicTacToe](https://www.youtube.com/watch?v=aib29xIMkwU)

7. [Video 1.23 Add Live chat to your grails application](https://www.youtube.com/watch?v=VrvJNPQ-K7M)

8. [Video 1.23-SNAPSHOT1 Live chat Logs BOT Artificial Intelligence and more](https://www.youtube.com/watch?v=fUIckOntais)

9. [Video 1.24 Chat BOT now working in chat rooms, returning responses and banning/kicking users](https://www.youtube.com/watch?v=jUm7QrQhpTk)

##### WebtRTC WebCam walk through
[WebtRTC WebCam walk through](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/WebtRTC-WebCam-walk-through)



### Customised chat menus for your chat users:
[index.gsp](https://github.com/vahidhedayati/testwschat/tree/master/grails-app/views/test/index.gsp)
```gsp
<g:form action="index2">
<label>Chat username</label><g:textField name="username"/>
<label>Choose Chat room style</label><g:select name="chatType" from="${selectMap}" optionKey="key" optionValue="value" />
<g:submitButton name="submit" value="go" />
</g:form>
```

[Controller for index:](https://github.com/vahidhedayati/testwschat/blob/master/grails-app/controllers/testwschat/TestController.groovy)
```groovy
def index() { 
		Map selectMap = ['usermenu':'default (as per plugin)','webrtcav':'webrtc AV only options', 'webrtcscreen':'screen share options only', 
			'fileonly':'display file interaction only', 'webcam':'web cam only', 'none':'no AV options' ]
		render view: 'index', model: [selectMap:selectMap]
	}
```

[View for index2:](https://github.com/vahidhedayati/testwschat/blob/master/grails-app/views/test/index2.gsp)
```gsp
<chat:includeAllStyle
addLayouts="true"
jquery="true"
jqueryui="true"
bootstrap="true"
 />

 <chat:connect 
 chatuser="${params.username}"
 usermenujs="${params.chatType}.js"
 profile="[email: '${params.username}2@example.com']" 
 />
```

[chatTypes which are Javascripts can be found here](https://github.com/vahidhedayati/testwschat/tree/master/grails-app/assets/javascripts)

This now loads in a specific style of menus for the end chat user, you can customise further
 
```
wschatjs="${params.wschatjs}.js"
```

Pass in wschatjs="something" to override plugin main core wschat.js functionality in your local code. The jquery, addLayouts jqueryui bootstrap have now been added as a new functionality that you can use in <chat:includeStyle or <chat:includeAllStyle - by default their all true you can either set them as ${false} or 'false' to disable a given set of core scripts. This is since you may have your own bootstrap or jquery-ui etc already built in. In which case you would use <chat:includeStyle ... If you wanted to overrider your grails application look feel then use <chat:includeAllStyle. 


	

#### STUN Server, setting up your own server:
[WebRTC-terminology](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/WebRTC-terminology)


##### Creating admin accounts
[Creating admin accounts](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Creating-admin-accounts)
	

##### 0.10+ & resources based apps (pre 2.4)
[pre 2.4 apps](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/resources-based-apps)

##### ChatClientEndPoint Client/Server Messaging  new feature since 1.11
[Client/Server Messaging explained](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/wsChatClient-Client-Server-Messaging-new-feature-since-1.11)

#### 1.12 additions:
[add profile with taglib connection call](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/profile-creation)

[how to screen capture](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Screen-capture-commands)

[Chat to DB](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Persist-Chat-to-DB)

[Chat room booking/reservations](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Booking-chat-event)

#### 1.13 Offline Messaging - enable override:

It will work when this is enabled in your Config.groovy

```
wschat.offline_pm=true
```

#### [1.24 Live chat & Chat Bot](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Chat-Room-Bot---Live-Chat---Live-Chat-Bot)

### Known issues/work arounds:
Since 0.20+ ui.videobox has been added, earlier versions and even current version suffers from conflicts with jquery.ui.chatbox and does not send message. In 1.13 an option above left of rooms will attempt to close PM windows. Otherwise refresh your page.

### Complete site wrapper example 
[example chat web application with Bootstrap/Shiro:LDAP/AD](https://github.com/vahidhedayati/kchat)
