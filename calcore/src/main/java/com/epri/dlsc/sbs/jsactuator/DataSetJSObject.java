package com.epri.dlsc.sbs.jsactuator;

import java.util.HashMap;
import java.util.Map;

public class DataSetJSObject {
    private Map<String, String> data = new HashMap<String, String>();
    private String value;

    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    public void setAttrivate(String key, String value){
        data.put(key, value);
    }

    public String getAttribute(String key){
//        if(key == null || "".equals(key.trim())){
//            return null;
//        }
//        String attribute = CommonUtil.getMD5(key.trim());
//        String value = data.get(attribute);

        return null;
    }
}
