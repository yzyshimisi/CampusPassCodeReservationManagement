package org.example.backend.utils;

import com.alibaba.fastjson2.JSONObject;
import java.util.List;

public class Tools {

    public static boolean areRequestFieldNull(JSONObject jsonData, List<String> fieldNames){
        if (jsonData == null || fieldNames == null || fieldNames.isEmpty()) {
            return false;
        }

        for (String fieldName : fieldNames) {
            if (!jsonData.containsKey(fieldName) || jsonData.get(fieldName) == null) {
                return false;
            }
        }
        return true;
    }
}
