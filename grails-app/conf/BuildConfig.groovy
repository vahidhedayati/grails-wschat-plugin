

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
		//TicTacToe requirements
		//compile 'org.codehaus.jackson:jackson-core-asl:1.9.8',		'org.codehaus.jackson:jackson-mapper-asl:1.9.8'
	}

	plugins {
		build ":hibernate4:4.3.6.1", {
			export = false
		}
		runtime ":pluginbuddy:0.3"
		build ':release:3.0.1', ':rest-client-builder:2.0.3', {
			export = false
		}
	}


}
