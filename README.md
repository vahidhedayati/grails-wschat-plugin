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

1.20 / 3.0.3 		Multiple login with same user but must be different rooms - removal of dbSupport check

1.21 / 3.0.3 		Websocket TicTacToe game added (Watch video 6)

1.23 / 3.0.3 		Live Chat feature added as part of the plugin(watch video 7)
```


 Websocket chat can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites.

###### Plugin will work with tomcat 7.0.54 + (inc. 8) running java 1.7+


###### Dependency (Grails 2.X) :
```groovy
	compile ":wschat:1.23-SNAPSHOT2"
```

[codebase for grails 2.X](https://github.com/vahidhedayati/grails-wschat-plugin/tree/grails2)


###### Dependency (Grails 3.X) :
```groovy
	compile "org.grails.plugins:wschat:3.0.3"
```

[codebase for grails 3.X](https://github.com/vahidhedayati/grails-wschat-plugin/)


This plugin provides  basic chat page, once installed you can access Grails 2:

```
http://localhost:8080/yourapp/wsChat/
```

Grails 3:
```
http://localhost:8080/wsChat/
````


Memory Configuration (SHOULD NOT be required under Grails 3 under development)
This should not be required but if you run into Heapspace issues you could try these:
```bash
export GRAILS_OPTS="-Xmx1G -Xms1024m -XX:MaxPermSize=1024m"
export MAVEN_OPTS="-XX:MaxPermSize=1024m -Xms1024m -Xmx1024m"
export JAVA_OPTS='-server -Xms1024m -Xmx1024m -XX:PermSize=1024m -XX:MaxPermSize=1024m'
```

before running grails run-app




## Config.groovy variables required:
 [Config.groovy variables required:](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Config-V3.groovy)
 		


##Version info
[Version info](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Version-info-V3)



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


#### 1.24 Live chat:
```gsp
<chat:customerChatButton user="${session.user}"/>
<!-- OR -->
<chat:customerChatButton />

```
If  user is not defined it will default their username to be Guest{sessionID} so their username will default to something like: GuestH5d4F9SDF943JGFHSD9DS

Config
```groovy
wschat.liveChatAssistant='assistant' // the chat client assistant name.. so if userx requests chat .. userx_assistant = this what this is .
wschat.liveChatPerm='admin'  // this is the group of users that livechat belongs to and if those uses have an email address in profile they will also be emailed
wschat.liveContactEmail='youremail@gmail.com' // this is the hard coded live chat email
wschat.liveChatUsername='masterv'  // this is the nickname upon them joining a live request
wschat.liveContactName='Mr V'  // this is the person that email title is set to
wschat.emailFrom="me@domain.com"  //this is for sending emails
wschat.store_live_messages=true  // store records of offline messaging
wschat.enable_AI=true  // enable Aritificial Intelligence ? refer to ChatAI.groovy for example and understanding
wschat.liveChatTitle="My Live chat"
```

With this all set - upon someone clicking live chat on the page with that taglib call an email is sent, follow the email link to join that user in the chat room to help them. Watch part 7 of the videos above to understand better.

As described in video 8, the customerChatButton is far from secure, for a more secure method build your own button, like this:

```gsp
<g:if test="${params.chat}">
	we have params
	<chat:customerChat user="myuser" />
</g:if>
<g:else>
	<g:form name="something" action="livechat">
		<g:hiddenField name="chat" value="yes"/>
		<g:submitButton name="Chat" value="chat"/>
	</g:form>
</g:else>
```


### Known issues/work arounds:
Since 0.20+ ui.videobox has been added, earlier versions and even current version suffers from conflicts with jquery.ui.chatbox and does not send message. In 1.13 an option above left of rooms will attempt to close PM windows. Otherwise refresh your page.

### Complete site wrapper example 
[example chat web application with Bootstrap/Shiro:LDAP/AD](https://github.com/vahidhedayati/kchat)

[Demo of customised chat views and client/server Grails 2.4.4](https://github.com/vahidhedayati/testwschat)

[Demo of customised chat views and client/server Grails 3.0.1](https://github.com/vahidhedayati/testwschat3)