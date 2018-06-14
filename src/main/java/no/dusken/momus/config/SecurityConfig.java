package no.dusken.momus.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.SAMLWebSSOHoKProcessingFilter;
import org.springframework.security.saml.context.SAMLContextProviderLB;
import org.springframework.security.saml.key.EmptyKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataDisplayFilter;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.HTTPArtifactBinding;
import org.springframework.security.saml.processor.HTTPPAOS11Binding;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.HTTPSOAP11Binding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.ArtifactResolutionProfileImpl;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.security.saml.websso.WebSSOProfileConsumerHoKImpl;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.saml.websso.WebSSOProfileECPImpl;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import no.dusken.momus.authentication.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan("org.springframework.security.saml")
@PropertySource(value = {"classpath:momus.properties","classpath:local.properties"}, ignoreResourceNotFound = true)
@Profile("!noAuth")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Environment env;

    private final UserDetailsService userDetailsService;

    public SecurityConfig(Environment env, UserDetailsService userDetailsService) {
        this.env = env;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception{
        http
            .csrf().disable()
            .addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class)
            .addFilterAfter(samlFilter(), BasicAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers("/api/dev/**").access(String.valueOf(env.acceptsProfiles("dev")))
            .antMatchers("/**").fullyAuthenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(samlEntryPoint())
            .and().logout().invalidateHttpSession(true).logoutUrl("/api/logout");
    }

    @Bean
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
    }

    @Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(samlAuthenticationProvider());
    }

    @Bean
	public SAMLAuthenticationProvider samlAuthenticationProvider() {
		SAMLAuthenticationProvider provider = new SAMLAuthenticationProvider();
		provider.setUserDetails(userDetailsService);
		provider.setForcePrincipalAsString(false);
		return provider;
}

    @Bean
    public HttpClient httpClient() {
        return new HttpClient(new MultiThreadedHttpConnectionManager());
    }

    @Bean
    public MetadataGeneratorFilter metadataGeneratorFilter() {
        MetadataGenerator generator = new MetadataGenerator();
        generator.setEntityBaseURL(env.getProperty("saml.baseurl"));
        generator.setRequestSigned(false);
        generator.setEntityId(env.getProperty("saml.entityid"));
        generator.setBindingsSSO(Arrays.asList("artifact", "post", "paos"));
        generator.setBindingsSLO(Arrays.asList("artifact", "post", "paos", "redirect"));
        generator.setKeyManager(keyManager());
        
        return new MetadataGeneratorFilter(generator);
    }

    @Bean
    public CachingMetadataManager metadata() throws Exception {
        HTTPMetadataProvider provider = new HTTPMetadataProvider(new Timer(true), httpClient(), env.getProperty("saml.idpurl"));
        provider.setParserPool(parserPool());

        ExtendedMetadata metadata = new ExtendedMetadata();
        metadata.setRequireLogoutRequestSigned(false);
        metadata.setRequireLogoutResponseSigned(false);
        return new CachingMetadataManager(Collections.singletonList(new ExtendedMetadataDelegate(provider, metadata)));
    }

    @Bean
    public FilterChainProxy samlFilter() throws Exception{
        return new FilterChainProxy(Arrays.asList(
            new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"), samlEntryPoint()),
            new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"), samlLogoutFilter()),
            new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/metadata/**"), metadataDisplayFilter()),
            new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"), samlWebSSOProcessingFilter()),
            new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSOHoK/**"), samlWebSSOHoKProcessingFilter()),
            new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"), samlLogoutProcessingFilter())
            ));
    }

    @Bean
    public SAMLEntryPoint samlEntryPoint() {
        SAMLEntryPoint entryPoint = new SAMLEntryPoint();
        WebSSOProfileOptions options = new WebSSOProfileOptions();
        options.setIncludeScoping(false);
        options.setForceAuthN(false);
        entryPoint.setDefaultProfileOptions(options);
        return entryPoint;
    }

    @Bean
    public SAMLLogoutFilter samlLogoutFilter() {
        return new SAMLLogoutFilter(successLogoutHandler(), new LogoutHandler[]{logoutHandler()}, new LogoutHandler[]{logoutHandler()});
    }

    @Bean
    public MetadataDisplayFilter metadataDisplayFilter() {
        return new MetadataDisplayFilter();
    }

    @Bean
    public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception{
        SAMLProcessingFilter filter = new SAMLProcessingFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(successRedirectHandler());
        filter.setAuthenticationFailureHandler(failureRedirectHandler());
        return filter;
    }

    @Bean
    public SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter() throws Exception {
        SAMLWebSSOHoKProcessingFilter filter = new SAMLWebSSOHoKProcessingFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(successRedirectHandler());
        filter.setAuthenticationFailureHandler(failureRedirectHandler());
        return filter;
    }

    @Bean
    public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
        return new SAMLLogoutProcessingFilter(successLogoutHandler(), logoutHandler());
    }

    @Bean
    public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
        SimpleUrlLogoutSuccessHandler handler = new SimpleUrlLogoutSuccessHandler();
        handler.setDefaultTargetUrl("/");
        return handler;
    }

    @Bean
    public SecurityContextLogoutHandler logoutHandler() {
        SecurityContextLogoutHandler handler = new SecurityContextLogoutHandler();
        handler.setInvalidateHttpSession(false);
        return handler;
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler() {
        SavedRequestAwareAuthenticationSuccessHandler handler = 
            new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/");
        return handler;
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler failureRedirectHandler() {
        SimpleUrlAuthenticationFailureHandler handler = 
            new SimpleUrlAuthenticationFailureHandler("/error.jsp");
        handler.setUseForward(true);
        return handler;
    }

    @Bean
    public SAMLContextProviderLB contextProvider() {
        SAMLContextProviderLB contextProvider = new SAMLContextProviderLB();
        contextProvider.setScheme(env.getProperty("saml.scheme"));
        contextProvider.setServerName(env.getProperty("saml.serverurl"));
        contextProvider.setServerPort(Integer.parseInt(env.getProperty("saml.port")));
        contextProvider.setIncludeServerPortInRequestURL(false);
        contextProvider.setContextPath("/");
        return contextProvider;
    }

    @Bean
    public SAMLProcessorImpl processor() {
        return new SAMLProcessorImpl(Arrays.asList(
            redirectBinding(), 
            postBinding(), 
            artifactBinding(), 
            soapBinding(), 
            paosBinding()));
    }

    @Bean
    public WebSSOProfileConsumer webSSOprofileConsumer() {
        WebSSOProfileConsumerImpl webSSOProfileConsumer = new WebSSOProfileConsumerImpl();
        webSSOProfileConsumer.setResponseSkew(600);
        return webSSOProfileConsumer;
    }

    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
        return new WebSSOProfileConsumerHoKImpl();
    }

    @Bean
    public WebSSOProfileImpl webSSOprofile() {
        return new WebSSOProfileImpl();
    }

    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOprofile() {
        return new WebSSOProfileConsumerHoKImpl();
    }

    @Bean
    public WebSSOProfileECPImpl ecpprofile() {
        return new WebSSOProfileECPImpl();
    }

    @Bean
    public SingleLogoutProfileImpl logoutprofile() {
        SingleLogoutProfileImpl singleLogoutProfile = new SingleLogoutProfileImpl();
        singleLogoutProfile.setResponseSkew(600);
        return singleLogoutProfile;
    }

    @Bean
    public HTTPPostBinding postBinding() {
        return new HTTPPostBinding(parserPool(), velocityEngine());
    }

    @Bean
    public HTTPArtifactBinding artifactBinding() {
        ArtifactResolutionProfileImpl profile = new ArtifactResolutionProfileImpl(httpClient());
        profile.setProcessor(new SAMLProcessorImpl(soapBinding()));
        return new HTTPArtifactBinding(parserPool(), velocityEngine(), profile);
    }

    @Bean
    public HTTPRedirectDeflateBinding redirectBinding() {
        return new HTTPRedirectDeflateBinding(parserPool());
    }

    @Bean
    public HTTPSOAP11Binding soapBinding() {
        return new HTTPSOAP11Binding(parserPool());
    }

    @Bean
    public HTTPPAOS11Binding paosBinding() {
        return new HTTPPAOS11Binding(parserPool());
    }

    @Bean
    public VelocityEngine velocityEngine() {
        return VelocityFactory.getEngine();
    }

    @Bean
    public static SAMLBootstrap samlBootstrap() {
        return new SAMLBootstrap();
    }

    @Bean
    public SAMLDefaultLogger samlLogger() {
        return new SAMLDefaultLogger();
    }

    @Bean
    public KeyManager keyManager() {
        return new EmptyKeyManager();
    }

    @Bean(initMethod = "initialize")
    public StaticBasicParserPool parserPool() {
        return new StaticBasicParserPool();
    }

    @Bean
    public ParserPoolHolder parserPoolHolder() {
        return new ParserPoolHolder();
    }
}