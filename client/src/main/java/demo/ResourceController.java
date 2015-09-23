package demo;


import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ResourceController {

//    @Autowired
//    RestTemplate rest;

    @Autowired
    private OAuth2RestOperations rest;

    @Value("${resource.addr}")
    String serverUrl;

    @RequestMapping("/view")
    public String view(Model model) {
        ResponseEntity<String> entity = rest.getForEntity(serverUrl + "cache/{address}", String.class, "Address");
        Shaozhen shaozhen = new Shaozhen();
        shaozhen.setAddress(entity.getBody());
        model.addAttribute("shaozhen", shaozhen);
        return "view";
    }

    @RequestMapping("/index")
    public String index() {
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
        rest.put(serverUrl + "cache/{address}?value={value}", null, "Address", shaozhen.getAddress());
        return "redirect:view";
    }

}
