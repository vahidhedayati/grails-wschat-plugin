wschat
=========

Grails websocket chat Plugin provides a multi-chat room add-on to an existing grails based site/application.

###### [How to install](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Installation)

###### [Configuration variables required](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Config.groovy)
 		
###### [Version info](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Version-info)

###### [Creating admin accounts](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Creating-admin-accounts)

###### [Customised chat menus for your chat users](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Customised-chat-menus-for-your-chat-users)

###### [Customise fonts chat output through css](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Customise-fonts-chat-output-through-css)

###### [Internationalisation](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Configure-internationlisation---customise-chat-output---menus)

###### [How to use configure Live chat & Chat Bot](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Chat-Room-Bot---Live-Chat---Live-Chat-Bot)

###### [Add profile with taglib connection call](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/profile-creation)

###### [Chat room booking/reservations](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Booking-chat-event)

###### [WebtRTC WebCam walk through](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/WebtRTC-WebCam-walk-through)

###### [WebtRTC File sharing](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/WebRTC-File-sharing-peer2peer)

###### [Integrating with existing grails application](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Integrating-with-existing-grails-application)
 
###### Videos on youtube:
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

10. [Video 1.25 LiveChat (1 room multiple users with 1 admin interacting with all) ](https://www.youtube.com/watch?v=udbOq6fiD9o)

###### [Chat Commands](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Commands)

###### [STUN Server, setting up your own server & WebRTC-terminology](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/WebRTC-terminology)

###### [0.10+ & resources based apps (pre 2.4)](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/resources-based-apps)

###### [ChatClientEndPoint Client/Server Messaging  new feature since 1.11](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/wsChatClient-Client-Server-Messaging-new-feature-since-1.11)

###### [How to screen capture](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Screen-capture-commands)

###### [Chat to DB](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Persist-Chat-to-DB)

###### [1.13 Offline Messaging - enable override](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/offline-pm)

###### [demo sites](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/complete-sites-demo-sites)

###### [Known issues](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Known-issues)

###### [Thanks to](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Thanks-to)

##### Grails wschat release/feature summary:

```
3.0.6/1.27 	- Major tidyup removed most ajax processing besides popup pages. 
			  Added css/internationlisation customisation
			  
3.0.5/1.26	- Tidy up with new look and new feature to monitor Live Chat requests
3.0.4/1.25  - Live Chat Many users to 1 admin (Video 10).

3.0.3/1.24  - ChatBot and badWords added to the main chat room

3.0.3/1.23  - Live Chat feature added as part of the plugin(watch video 7)

3.0.3/1.21  - Websocket TicTacToe game added (Watch video 6)

3.0.3/1.20  - Multiple login with same user but must be different rooms - removal of dbSupport check

1.18 -  Mediastreaming enabled but has not worked for me 
   
1.17  - Websocket -> WebRTC File sharing peer2peer available -  
  
1.12  - Chat room bookings for multiple participants
   
1.0+  - Supports WebRTC currently only on Chrome Canary.

0.19+ - Supports webcam tested  on chrome/firefox.

Admin can:  kick/Ban (for specified time period)
Users can create profiles define details and upload photos.
Chat rooms can be created in Config.groovy +/ DB once logged in using UI.

You can websocket :
	send files / use webRTC technology to have video chat or share screen
	play Tictactoe / noughts + crosses
	Live chat - set up a client easily and monitor live chats
	Configure chat bookings / invite external attendees (emails)
```
