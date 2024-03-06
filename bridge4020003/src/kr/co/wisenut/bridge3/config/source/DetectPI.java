package kr.co.wisenut.bridge3.config.source;

import java.util.HashMap;
import java.util.Map;

public class DetectPI {
    private Map<String, String> rgxMap = new HashMap<>();

    public Map<String, String> getRgxMap() {
        return rgxMap;
    }
    public void putRgx(String key, String val) {
        rgxMap.put(key, val);
    }
}
