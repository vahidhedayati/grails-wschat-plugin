grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	dependencies {
		build ('javax.websocket:javax.websocket-api:1.0') { export = false }
		compile 'org.codehaus.jackson:jackson-core-asl:1.9.8',		'org.codehaus.jackson:jackson-mapper-asl:1.9.8'
	}

	plugins {
		compile ":hibernate4:4.3.5.4", {
			export = false
		}
		runtime ":pluginbuddy:0.3"
		build ':release:2.2.1', ':rest-client-builder:1.0.3', {
			export = false
		}
	}
}
