package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import static javax.net.ssl.SSLContext.*;

@SpringBootApplication
public class UaaServiceApplication {

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
        SpringApplication.run(UaaServiceApplication.class, args);
    }

    @Configuration
    @EnableResourceServer
    protected static class ResourceServer extends ResourceServerConfigurerAdapter{
        public void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests().antMatchers(HttpMethod.GET,"/cache/**").access("#oauth2.hasScope('shaozhen.read')")
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.PUT, "/cache/**").access("#oauth2.hasScope('shaozhen.write')")
                    .anyRequest().permitAll(); //[4]
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId("shaozhen");
        }

        @Bean
        ResourceServerTokenServices tokenService(){
            RemoteTokenServices remoteUAA = new RemoteTokenServices();
            remoteUAA.setClientId("shaozhen");
            remoteUAA.setClientSecret("shaozhen");
            remoteUAA.setCheckTokenEndpointUrl("https://uaa.10.65.233.228.xip.io/check_token");
            return remoteUAA;
        }
    }

}
