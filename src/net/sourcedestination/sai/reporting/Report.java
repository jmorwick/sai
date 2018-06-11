package net.sourcedestination.sai.reporting;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report extends HashMap<String, Object> {

    public static Report fromJson(String json) {
        return null;
    }

    private JSONObject toJsonObject(Map<String, Object> m) {
        JSONObject jmap = new JSONObject();
        for (String key : m.keySet()) {
            Object o = m.get(key);
            if(o instanceof Map) {
                jmap.put(key, toJsonObject((Map<String,Object>)o));
            } else if(o instanceof List) {
                jmap.put(key, toJsonArray((List)o));
            } else jmap.put(key, o.toString());
        }
        return jmap;
    }


    private JSONArray toJsonArray(List ls) {
        JSONArray jarr = new JSONArray();
        for (Object o : ls) {
            if(o instanceof Map) {
                jarr.put(toJsonObject((Map<String,Object>)o));
            } else if(o instanceof List) {
                jarr.put(toJsonArray((List)o));
            } else jarr.put(o.toString());
        }
        return jarr;
    }


    public String toJson() {
        return toJsonObject(this).toString();
    }

}
