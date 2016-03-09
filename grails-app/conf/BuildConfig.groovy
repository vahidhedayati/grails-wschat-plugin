

grails.project.work.dir = 'target'

grails.project.dependency.resolution = {
	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
		mavenRepo "https://repo.grails.org/grails/plugins"
	}

	dependencies {
		build ('javax.websocket:javax.websocket-api:1.0') { export = false }
		//TicTacToe requirements
		//compile 'org.codehaus.jackson:jackson-core-asl:1.9.13' ,'org.codehaus.jackson:jackson-mapper-asl:1.9.13', { export = false }
	}

	plugins {
		runtime ":hibernate4:4.3.6.1", {
			export = false
		}
		
		build ':release:3.0.1', ':rest-client-builder:2.0.3', {
			export = false
		}
		
		runtime ":pluginbuddy:0.3"
		compile ":remote-pagination:0.4.8"
		compile ":mail:1.0.7"
		compile "org.grails.plugins:spring-security-core:2.0.0"
	}


}
