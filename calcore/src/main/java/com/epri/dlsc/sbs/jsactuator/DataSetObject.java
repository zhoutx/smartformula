package com.epri.dlsc.sbs.jsactuator;

import java.util.HashMap;
import java.util.Map;

public class DataSetObject {
    private Map<String, String> data = new HashMap<>();
    private String value;

    public void setAttrivate(String key, String value){
        data.put(key, value);
    }
    public String getAttribute(String key){
        return data.get(key);
    }
}
