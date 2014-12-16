import grails.util.Metadata

def appVersion=Metadata.current.'app.grails.version'
double appv=getGrailsVersion(appVersion)
dataSource {
	pooled = true
	driverClassName = 'org.h2.Driver'
	username = 'sa'
	password = ''
	dbCreate = 'update'
	url = 'jdbc:h2:mem:testDb'
}
if (appv>2.4) {
	
	hibernate {
		cache.use_second_level_cache = true
		cache.use_query_cache = false
		//cache.provider_class = 'org.hibernate.cache.EhCacheProvider'
		cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
		singleSession = true // configure OSIV singleSession mode
	}
	
}else{

	hibernate {
		cache.use_second_level_cache = false
		cache.use_query_cache = false
		cache.provider_class = 'org.hibernate.cache.EhCacheProvider'
	}
	
}

private  getGrailsVersion(String appVersion) {
	if (appVersion && appVersion.indexOf('.')>-1) {
		int lastPos=appVersion.indexOf(".", appVersion.indexOf(".") + 1)
		double verify=appVersion.substring(0,lastPos) as double
	}
}