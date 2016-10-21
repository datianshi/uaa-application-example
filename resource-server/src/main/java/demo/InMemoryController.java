package demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Map;

@RestController
public class InMemoryController {

    @Autowired
    InMemeory cache;

    @RequestMapping("/cache")
    @ResponseBody
    public Map<String, String> getValue(){
        return cache.getCache();
    }

    @RequestMapping("/cache/{key}")
    @ResponseBody
    @PreAuthorize("#oauth2.hasScope('shaozhen.read')")
    public String getValue(@PathVariable String key){
        return cache.getValue(key);
    }

    @RequestMapping(value="/cache/{key}", method= RequestMethod.PUT)
    @ResponseBody
    @PreAuthorize("#oauth2.hasScope('shaozhen.write')")
    public String putValue(@PathVariable String key, @RequestParam("value") String value){
        return cache.putValue(key, value);
    }
}
