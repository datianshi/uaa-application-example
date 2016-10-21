package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.net.ssl.*;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static javax.net.ssl.SSLContext.*;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class ResourceServerApplication {

    static {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});
            SSLContext context = getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        SpringApplication.run(ResourceServerApplication.class, args);
    }

    private TokenExtractor tokenExtractor = new BearerTokenExtractor();

    @Bean
    public GlobalMethodSecurityConfiguration globalMethodSecurityConfiguration() {
        return new GlobalMethodSecurityConfiguration() {
            @Override
            protected MethodSecurityExpressionHandler createExpressionHandler() {
                return new OAuth2MethodSecurityExpressionHandler();
            }
        };
    }

    @Bean
    public ResourceServerConfigurer resourceServerConfigurerAdapter() {
        return new ResourceServerConfigurerAdapter() {
            @Override
            public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
                resources.resourceId("shaozhen");
            }

            @Override
            public void configure(HttpSecurity http) throws Exception {
                http.addFilterAfter(new OncePerRequestFilter() {
                    @Override
                    protected void doFilterInternal(HttpServletRequest request,
                                                    HttpServletResponse response, FilterChain filterChain)
                            throws ServletException, IOException {
                        // We don't want to allow access to a resource with no token so clear
                        // the security context in case it is actually an OAuth2Authentication
                        if (tokenExtractor.extract(request) == null) {
                            SecurityContextHolder.clearContext();
                        }
                        filterChain.doFilter(request, response);
                    }
                }, AbstractPreAuthenticatedProcessingFilter.class);
                http.csrf().disable();
                http.authorizeRequests().anyRequest().authenticated();
            }
        };
    }

}
