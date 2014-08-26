grails.project.work.dir = 'target'

//grails.project.dependency.resolver = 'maven'
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
	}

	plugins {

		compile ":hibernate4:4.3.5.4", {
			export = false
		}
		
		build ':release:2.2.1', ':rest-client-builder:1.0.3',  ':pluginbuddy:0.1', {
			export = false
		}
		
	}
}
//grails.plugin.location.'pluginbuddy' = "../grails-pluginbuddy-plugin"