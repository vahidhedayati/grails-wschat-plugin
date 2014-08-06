package grails.plugin.wschat

class WsChatTagLib {
	static namespace = "wschat"
	def grailsApplication
	
	def chooseLayout =  { attrs, body ->
		def file = attrs.remove('file')?.toString()
		def loadtemplate=attrs.remove('loadtemplate')?.toString()
		def hostname=attrs.remove('hostname')?.toString()
		def chatTitle=attrs.remove('chatTitle')?.toString()
		def chatHeader=attrs.remove('chatHeader')?.toString()
		def gver=grailsApplication.metadata['app.grails.version']
		double verify=getGrailsVersion(gver)
		def gfolder="resources"
		if (verify >= 2.4 ) {
			gfolder="assets"
		}
		out << g.render(contextPath: pluginContextPath, template: "/wsChat/${gfolder}/${file}", model: [hostname:hostname,loadtemplate:loadtemplate,chatTitle:chatTitle,chatHeader:chatHeader,attrs:attrs])
	}
	
	private getGrailsVersion(String appVersion) {
		if (appVersion && appVersion.indexOf('.')>-1) {
			int lastPos=appVersion.indexOf(".", appVersion.indexOf(".") + 1)
			double verify=appVersion.substring(0,lastPos) as double
		}
	}
}