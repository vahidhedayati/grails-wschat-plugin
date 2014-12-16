import grails.util.Metadata

grails.project.work.dir = 'target'

grails.project.dependency.resolution = {
	def appVersion=Metadata.current.'app.grails.version'
	double appv=getGrailsVersion(appVersion)
	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	dependencies {
		build ('javax.websocket:javax.websocket-api:1.0') { export = false }
		//TicTacToe requirements
		//compile 'org.codehaus.jackson:jackson-core-asl:1.9.8',		'org.codehaus.jackson:jackson-mapper-asl:1.9.8'
	}
	
	if (appv>2.4) {
		plugins {
			build ":hibernate4:4.3.6.1", {
				export = false
			}
			runtime ":pluginbuddy:0.3"
			build ':release:3.0.1', ':rest-client-builder:2.0.3', {
				export = false
			}
		}

	}else if (appv<2.4) {
		plugins {
			build ":hibernate:3.6.10.6", {
				export = false
			}
			runtime ":pluginbuddy:0.3"
			build ':release:3.0.1', ':rest-client-builder:2.0.3', {
				export = false
			}
		}
	}
}

private  getGrailsVersion(String appVersion) {
	if (appVersion && appVersion.indexOf('.')>-1) {
		int lastPos=appVersion.indexOf(".", appVersion.indexOf(".") + 1)
		double verify=appVersion.substring(0,lastPos) as double
	}
}
