package no.dusken.momus.config;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultTlsDirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.authentication.UserDetailsService;
import no.dusken.momus.authentication.UserDetailsServiceDev;
import no.dusken.momus.authentication.UserDetailsServiceImpl;
import no.dusken.momus.exceptions.ExceptionHandler;
import no.dusken.momus.mapper.HibernateAwareObjectMapper;
import no.dusken.momus.service.repository.PersonRepository;

@Configuration
@EnableScheduling
@EnableAsync
@EnableTransactionManagement
@EnableSpringDataWebSupport
@ComponentScan(basePackages = "no.dusken.momus")
@EnableJpaRepositories(basePackages = "no.dusken.momus.service.repository")
@PropertySource("classpath:momus.properties")
@Slf4j
class ApplicationConfig extends WebMvcConfigurationSupport {

    private final Environment env;

    public ApplicationConfig(Environment env) {
        this.env = env;
        log.info("Active spring profiles: {}", Arrays.toString(env.getActiveProfiles()));
    }

    @Bean(name = "userDetailsService")
    @Profile("noAuth")
    public UserDetailsService userDetailsServiceDev() {
        return new UserDetailsServiceDev();
    }

    @Bean(name = "userDetailsService")
    @Profile("!noAuth")
    public UserDetailsService userDetailsServiceImpl(PersonRepository personRepository) {
        return new UserDetailsServiceImpl(personRepository);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(new HibernateAwareObjectMapper()));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        WebContentInterceptor interceptor = new WebContentInterceptor();
        interceptor.setCacheSeconds(0);
        registry.addInterceptor(interceptor);
    }

    @Bean
    @PostConstruct
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource());
        liquibase.setChangeLog("classpath:db-changelog.xml");
        liquibase.setContexts(env.getProperty("liquibase.contexts"));
        return liquibase;
    }

    @Bean
    public BasicDataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("dataSource.driver"));
        dataSource.setUrl(env.getProperty("dataSource.url"));
        dataSource.setUsername(env.getProperty("dataSource.username"));
        dataSource.setPassword(env.getProperty("dataSource.password"));

        dataSource.setValidationQuery(env.getProperty("dataSource.validationQuery"));
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(false);
        dataSource.setTimeBetweenEvictionRunsMillis(1800000);
        dataSource.setDefaultAutoCommit(false);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.valueOf(env.getProperty("dataSource.databaseType")));
        jpaVendorAdapter.setGenerateDdl(Boolean.valueOf(env.getProperty("dataSource.generateDdl")));
        jpaVendorAdapter.setShowSql(Boolean.valueOf(env.getProperty("dataSource.showSql")));

        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter);
        entityManagerFactory.setPersistenceUnitName("jpaPersistenceUnit");
        return entityManagerFactory;
    }

    @Bean 
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory);
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Bean
    public LdapTemplate ldapTemplate(){
        LdapContextSource ldapContextSource = new LdapContextSource();
        DefaultTlsDirContextAuthenticationStrategy tls = new DefaultTlsDirContextAuthenticationStrategy();
        tls.setHostnameVerifier((s, sslSession) -> true);
        ldapContextSource.setAuthenticationStrategy(tls);
        ldapContextSource.setUrl(env.getProperty("ldap.url"));
        ldapContextSource.setBase(env.getProperty("ldap.base"));
        ldapContextSource.setUserDn(env.getProperty("ldap.username"));
        ldapContextSource.setPassword(env.getProperty("ldap.password"));
        ldapContextSource.setBaseEnvironmentProperties(
                ImmutableMap.of("java.naming.ldap.attributes.binary", "objectGUID"));
        ldapContextSource.setPooled(false);
        ldapContextSource.afterPropertiesSet();        
        return new LdapTemplate(ldapContextSource);
    }

    @Bean
    public ExceptionHandler exceptionResolver() {
        return new ExceptionHandler();
    }

    /**
     * We need to set the order of assets to be before request mapping, so static files are prioritized to end points,
     * so we can route all urls not caught by the API over to index.html and the front end. See controllers/ViewController.java.
     * 
     * Order will be:
     * 1. is it a static file path?
     * 2. is it an api endpoint? 
     * 3. redirect to angularjs (index.html)
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**").addResourceLocations("/assets/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/favicon.ico");
        registry.setOrder(0);
    }

    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping mapping = super.requestMappingHandlerMapping();
        mapping.setOrder(1);
        return mapping;
    }
}