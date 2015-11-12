// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'grails.plugin.wschat.ChatAuth'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'grails.plugin.wschat.ChatAuthChatRole'
grails.plugin.springsecurity.authority.className = 'grails.plugin.wschat.ChatRole'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	'/':                ['permitAll'],
	'/error':           ['permitAll'],
	'/index':           ['permitAll'],
	'/index.gsp':       ['permitAll'],
	'/shutdown':        ['permitAll'],
	'/assets/**':       ['permitAll'],
	'/**/js/**':        ['permitAll'],
	'/**/css/**':       ['permitAll'],
	'/**/images/**':    ['permitAll'],
	'/**/favicon.ico':  ['permitAll'],
	'/wsChat/**':       ['permitAll'],
	'/wsChatAdmin/**':       ['permitAll'],
	'/wsCamEndpoint/**':		['permitAll'],
	'/wsChatEndpoint/**':		['permitAll'],
	'/wsChatFileEndpoint/**':	['permitAll'],
	'/TicTacToeServer/**':		['permitAll'],
	'/wsCamEndpoint':		['permitAll'],
	'/wsChatEndpoint':		['permitAll'],
	'/wsChatFileEndpoint':	['permitAll'],
	'/TicTacToeServer':		['permitAll'],
	'/dbconsole/**':       ['permitAll'],
	'/ChatClientEndpoint/**':	['permitAll'],
	'/WsChatClientEndpoint/**':	['permitAll'],
	'/ChatClientEndpoint':	['permitAll'],
	'/WsChatClientEndpoint':	['permitAll']
]

grails.plugin.springsecurity.securityConfigType = "Annotation"

