dataSource {
	pooled = true
	driverClassName = 'org.h2.Driver'
	username = 'sa'
	password = ''
	dbCreate = 'update'
	url = 'jdbc:h2:mem:testDb'
}
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = false
	//cache.provider_class = 'org.hibernate.cache.EhCacheProvider'
	cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
	singleSession = true // configure OSIV singleSession mode
}

