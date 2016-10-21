package demo;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan
@Controller
public class ResourceController {
    private OAuth2RestTemplate oauth2RestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    private OAuth2ClientContext clientContext;

    @PostConstruct
    public void init() {
        oauth2RestTemplate = new OAuth2RestTemplate(address(), clientContext);
    }

    @Bean
    public OAuth2ProtectedResourceDetails address(){
        return new AuthorizationCodeResourceDetails();
    }

    @Value("${resource.addr}")
    String serverUrl;

    @Value("${ssoServiceUrl:placeholder}")
    private String ssoServiceUrl;

    @RequestMapping("/view")
    public String view(Model model) {
        ResponseEntity<String> entity = oauth2RestTemplate.getForEntity(serverUrl + "cache/{address}", String.class, "Address");
        Shaozhen shaozhen = new Shaozhen();
        shaozhen.setAddress(entity.getBody());
        model.addAttribute("shaozhen", shaozhen);
        return "view";
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("ssoServiceUrl",ssoServiceUrl);
        return "index";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Model model, @RequestParam String address) {
        Shaozhen shaozhen = new Shaozhen();
        shaozhen.setAddress(address);
        model.addAttribute("shaozhen", shaozhen);
        return "edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String post(@ModelAttribute Shaozhen shaozhen) {
        oauth2RestTemplate.put(serverUrl + "cache/{address}?value={value}", null, "Address", shaozhen.getAddress());
        return "redirect:view";
    }

    @ExceptionHandler(UserDeniedAuthorizationException.class)
    public String conflict() {
        return "noauth";
    }

}
