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
	}

	plugins {
		build ':release:2.2.1', ':rest-client-builder:1.0.3',  ':pluginbuddy:0.1', {
			export = false
		}
		
	}
}
//grails.plugin.location.'pluginbuddy' = "../grails-pluginbuddy-plugin"