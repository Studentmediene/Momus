package no.dusken.momus.config;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class WebInit implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        servletContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        AnnotationConfigWebApplicationContext config = new AnnotationConfigWebApplicationContext();
        config.register(ApplicationConfig.class);

        servletContext.addListener(new ContextLoaderListener(config));
        servletContext.addListener(new RequestContextListener());

        ServletRegistration.Dynamic dispatcher =  servletContext
            .addServlet("momusapi", new DispatcherServlet());
        dispatcher.setAsyncSupported(true);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        // Add security to the servlet
        FilterRegistration.Dynamic securityFilter = servletContext
            .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);

        securityFilter.setAsyncSupported(true);
        securityFilter.addMappingForUrlPatterns(null, false, "/*");
    }

} 