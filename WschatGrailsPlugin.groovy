import grails.plugin.wschat.TicTacToeServer
import grails.plugin.wschat.WsCamEndpoint
import grails.plugin.wschat.WsChatEndpoint
import grails.plugin.wschat.WsChatFileEndpoint

class WschatGrailsPlugin {
    def version = "1.23"
    def grailsVersion = "2.0 > *"
    def title = "Websocket Chat Plugin"
    def description = 'Default WebSocket Multi-chat room plugin, supports Admin privilages, kicking banning users. Webcam support for chrome/firefox. WebRTC (audio/video & screen) support 0.24+. Websocket TicTacToe. Grails application Live Chat'
    def documentation = "https://github.com/vahidhedayati/grails-wschat-plugin"
    def license = "APACHE"
    def developers = [name: 'Vahid Hedayati', email: 'badvad@gmail.com']
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/vahidhedayati/grails-wschat-plugin/issues']
    def scm = [url: 'https://github.com/vahidhedayati/grails-wschat-plugin']
	
	def doWithWebDescriptor = { xml ->
		def listenerNode = xml.'listener'
		listenerNode[listenerNode.size() - 1] + {
			listener {
				'listener-class'(WsChatEndpoint.name)
			}
			listener {
				'listener-class'(WsCamEndpoint.name)
			}
			listener {
				'listener-class'(WsChatFileEndpoint.name)
			}
			listener {
				'listener-class'(TicTacToeServer.name)
			}
		}
	}
}
