grails.project.work.dir = 'target'

//grails.project.dependency.resolver = 'maven'
grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
		//mavenRepo "http://bytedeco.org/javacv/"
	}

	dependencies {
		build ('javax.websocket:javax.websocket-api:1.0') { export = false }
		// Facial recognition
		//compile 'com.github.hoary.ffmpeg:JavaAV-FFmpeg:1.0'
		//build ('org.bytedeco:javacv:0.8') { export = false }
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
