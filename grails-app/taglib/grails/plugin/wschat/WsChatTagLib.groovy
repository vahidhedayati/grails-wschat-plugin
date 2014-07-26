package grails.plugin.wschat

class WsChatTagLib {
	static namespace = "wschat"
	def grailsApplication
	
	def chooseLayout =  { attrs, body ->
		def file = attrs.remove('file')?.toString()
		
		def gver=grailsApplication.metadata['app.grails.version']
		double verify=getGrailsVersion(gver)
		if (verify >= 2.4 ) { 
			out << g.render(contextPath: pluginContextPath, template: "/wsChat/assets/${file}", model: [attrs:attrs])
		}else{
			out << g.render(contextPath: pluginContextPath, template: "/wsChat/resources/${file}", model: [attrs:attrs])
		}	
	}
	private getGrailsVersion(String appVersion) {
		if (appVersion && appVersion.indexOf('.')>-1) {
			int lastPos=appVersion.indexOf(".", appVersion.indexOf(".") + 1)
			double verify=appVersion.substring(0,lastPos) as double
		}
	}
}