/*
* Sample application.groovy in plugin to give an example of 
* how to use within your applicatoin
*/

//enable this and set to true to enable spring security within plugin 
//wschat.enableSecurity='false'

grails.plugin.springsecurity.userLookup.userDomainClassName = 'grails.plugin.wschat.ChatAuth'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'grails.plugin.wschat.ChatAuthChatRole'
grails.plugin.springsecurity.authority.className = 'grails.plugin.wschat.ChatRole'
//grails.plugin.springsecurity.securityConfigType = "Annotation"
grails.plugins.springsecurity.securityConfigType = 'InterceptUrlMap'
//grails.plugin.springsecurity.successHandler.defaultTargetUrl = '/wschat'
//grails.plugin.springsecurity.successHandler.alwaysUseDefault = true
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/error', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/index', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/index.gsp', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/shutdown', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/assets/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/**/js/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/**/css/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/**/images/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/**/favicon.ico', access:['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/wsChat/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/wsChatAdmin/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/wsCamEndpoint/**', access:  ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/wsChatEndpoint/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/wsChatFileEndpoint/**', access:  ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/TicTacToeServer/**', access:  ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/wsCamEndpoint', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/wsChatEndpoint', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/wsChatFileEndpoint', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/TicTacToeServer', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/dbconsole/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/ChatClientEndpoint/**', access:  ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/WsChatClientEndpoint/**', access:['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/ChatClientEndpoint', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
      [pattern: '/WsChatClientEndpoint', access:   ['IS_AUTHENTICATED_ANONYMOUSLY']]

]


//wschat.defaultperm='admin'
//wschat.rooms = ['room1','room2','room3']
//wschat.showtitle="no"
//wschat.hostname='localhost:8080'
//stunServers { iceServers=[ [url: 'stun:stun.l.google.com:19302'] ] }
//wschat.send.leftroom='yes'
//wschat.send.joinroom='yes'
//wschat.frontenduser='_frontend'
//wschat.storeForFrontEnd=true
//wschat.dbstore=true
//wschat.dbsupport='yes'
//wschat.dbstore_pm_messages=true
//wschat.dbstore_room_messages=true
//wschat.debug='true'
//wschat.dbstore_user_messages=true
//wschat.liveChatAssistant='assistant' // the chat client assistant name.. so if userx requests chat .. userx_assistant = this what this is .
//wschat.liveChatPerm='admin'  // this is the group of users that livechat belongs to and if those uses have an email address in profile they will also be emailed
//wschat.liveContactEmail='youremail@gmail.com' // this is the hard coded live chat email
//wschat.liveChatUsername='masterv'  // this is the nickname upon them joining a live request
//wschat.liveContactName='Mr V'  // this is the person that email title is set to
//wschat.emailFrom="me@domain.com"  //this is for sending emails
//wschat.store_live_messages=true  // store records of offline messaging
//wschat.enable_AI=true  // enable Aritificial Intelligence ? refer to ChatAI.groovy for example and understanding
//wschat.liveChatTitle="My Live chat"
//wschat.addFile='false'
//wschat.addGame='false'
//wschat.liveChatAskName='true'
//wschat.liveChatAskEmail='true'
//wschat.enable_Chat_Bot=true
//wschat.enable_Chat_AI=true
//wschat.enable_Chat_BadWords=true


