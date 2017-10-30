package no.dusken.momus.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class WebInit implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext config = new AnnotationConfigWebApplicationContext();
        config.register(ApplicationConfig.class, SecurityConfig.class);
        
        servletContext.addListener(new ContextLoaderListener(config));
        ServletRegistration.Dynamic dispatcher = 
            servletContext.addServlet("momusapi", new DispatcherServlet());
        
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/api/*");

        // Add security to the servlet
        servletContext.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class)
            .addMappingForUrlPatterns(null, false, "/*");
	}

} 