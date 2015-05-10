package wschat

import grails.plugin.wschat.WsCamEndpoint
import grails.plugin.wschat.WsChatEndpoint
import grails.plugin.wschat.WsChatFileEndpoint
import org.springframework.boot.context.embedded.ServletContextInitializer
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean
import org.springframework.context.annotation.Bean

import javax.servlet.ServletContext
import javax.servlet.ServletException

class DefaultWsChatConfig {

	@Bean
	public ServletContextInitializer myInitializer() {
		return new ServletContextInitializer() {
			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {
				servletContext.addListener(WsCamEndpoint)
				servletContext.addListener(WsChatFileEndpoint)

			}
		}
	}

	// Alternative way
	@Bean
	public ServletListenerRegistrationBean<WsChatEndpoint>  httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean<WsChatEndpoint>(new WsChatEndpoint())

	}
}
