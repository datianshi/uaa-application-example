package demo;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InMemeory {
    Map<String, String> cache;

    public InMemeory() {
        cache = new HashMap<String, String>();
        cache.put("Address", "Dallas");
    }

    public Map<String, String> getCache() {
        return cache;
    }

    public String getValue(String key){
        return cache.get(key);
    }

    public String putValue(String key, String value){
        return cache.put(key, value);
    }


}
