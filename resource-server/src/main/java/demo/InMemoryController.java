package demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Map;

@Controller
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
    public String getValue(@PathVariable String key){
        return cache.getValue(key);
    }

    @RequestMapping(value="/cache/{key}", method= RequestMethod.PUT)
    @ResponseBody
    public String putValue(@PathVariable String key, @RequestParam("value") String value){
        return cache.putValue(key, value);
    }
}
