wschat
=========

Grails websocket chat Plugin provides a multi-chat room facilty to an existing grails based site/application.


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
					
```

 Websocket chat can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites.

###### Plugin will work with tomcat 7.0.54 + (inc. 8) running java 1.7+


###### Dependency :
```groovy
	compile ":wschat:1.12" 
```

This plugin provides  basic chat page, once installed you can access
```
http://localhost:8080/yourapp/wsChat/
```
		

##### Custom calling plugin disabled login
[Custom calling plugin](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Custom-calling-plugin-disabled-login)

##### Custom calls 
[Custom calls](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Custom-calls)

 
##### Videos:
[Video: grails app running wschat 0.14 part1](https://www.youtube.com/watch?v=E-NmbDZg9G4)

[Video: grails app running wschat 0.14 part2](https://www.youtube.com/watch?v=xPxV_iEYYm0)

[Video: Client/Server Messaging part 1](https://www.youtube.com/watch?v=zAySkzNid3E)

[Video: Client/Server Messaging part 2](https://www.youtube.com/watch?v=xagMYM9n3l0)


##### WebtRTC WebCam walk through
[WebtRTC WebCam walk through](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/WebtRTC-WebCam-walk-through)



##### Config.groovy variables required:
 [Config.groovy variables required:](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Config.groovy)

#### STUN Server, setting up your own server:
[WebRTC-terminology](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/WebRTC-terminology)


##### Creating admin accounts
[Creating admin accounts](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Creating-admin-accounts)
	

##### 0.10+ & resources based apps (pre 2.4)
[pre 2.4 apps](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/resources-based-apps)

##### ChatClientEndPoint Client/Server Messaging  new feature since 1.11
[Client/Server Messaging explained](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/wsChatClient-Client-Server-Messaging-new-feature-since-1.11)

#### 1.12 additions:
Profile creation/updating feature added to taglib call. This works in conjunction with invites which with email address will contact end user.
```
<chat:connect can include a map collection of user profile:
profile="[email: 'something@domain.com']"
```

Full tag:
```
profile="[email: 'something@domain.com', firstName:firstName, middleName:middleName, lastName:lastName, age:age, birthDate:birthDate, wage:wage, email:email, homePage:homePage, martialStatus:martialStatus, children:children , gender:"Male/Female"]"
```

If you wish to update profile upon each usage:
```
updateProfile="true/false" -- default is false

```

Two additional tag libs to work with <chat:connect:
```
<chat:includeAllStyle/>
<chat:includeStyle/>
```
Use one or the other, I found on a default grails site I needed includeAllStyles.



1.12 Screen capture

Your clients need to open up chrome with these options, in order for this to work:
 
```
chromium-browser --allow-http-screen-capture --enable-usermedia-screen-capturing
chrome --allow-http-screen-capture --enable-usermedia-screen-capturing
```

1.12 Persist Room/User/PM Messages to DB:

To enable this feature, add the following configuration to your Config.groovy:
```groovy
wschat.dbstore=true
wschat.dbstore_pm_messages=true
wschat.dbstore_room_messages=true
//wschat.dbstore_user_messages=true 
```

1.12 - Viewing users + Scheduled - Conferencing.
As an administrator you will now find a new cog icon by +- room icons, this when triggered will open and show a drop down of two additional features.

1. View users:
 
This in short allows you to search / find users store on DB, more features such as barring email/user is required.

2. Booking a conference :

This option took a while due to UI requirements, a modal box that provides a jquery ajax driven page that acts quite similar to a responsive angularJs built page.
In short set a name then start typing username, if found a drop down auto complete is provided, once it is selected or typed, it looks up user. If found and has email it will add email as a checkbox and empty field for you to add other participants, If user found no email a secondary modal can be triggered to add email which upon submission + success of email found added to list. If not found at all a secondary modal can be triggered to add user/email. Upon submission final check to ensure user exists+email and added as check box. Repeat this as much as required.
Finally set the start dateTime using datepicker and set the end conference end date+Time.

Save this. Now those selected users/emails are emailed with a unique URL to join on specified period. They can join 5 minutes pre and all the way until 5 minutes after set end Date. 

In theory it can go on for much longer than endDate - the endDate is I think best described the time the end user can join + 5.

So if both was set to now - then it would be now + 10 min window 5 before 5 after.

Lots of DB changes so these conference rooms will not appear in normal chat room listing due to roomTypes.

I hope that explains it all.   


#####Commands
[Commands](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Commands)

#####Version info
[Version info](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Version-info)


### Known issues/work arounds:
Since 0.20+ ui.videobox has been added, earlier versions and even current version suffers from conflicts with jquery.ui.chatbox and does not send message. Currently the temporary fix is when you open a cam/rtc session to another user or as the initiator. If you did have pm boxes open you will find they will all close down as you open the video box. If you then go to the user and click on PM your currently pm history will reappear along with their pm box.

