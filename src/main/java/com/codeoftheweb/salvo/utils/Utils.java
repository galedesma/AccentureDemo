package com.codeoftheweb.salvo.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class Utils {

    public static Map<String, Object> getDefaultDTO(String key, Object value){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put(key, value);
        return dto;
    }
}
