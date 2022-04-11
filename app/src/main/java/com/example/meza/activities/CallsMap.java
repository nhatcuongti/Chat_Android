package com.example.meza.activities;

import java.util.HashMap;

public class CallsMap {
    public static HashMap<Object, Object> callsMap = new HashMap<>();

    public static void putData(Object key, Object value){
        callsMap.put(key, value);
    }

    public static Object getData(Object key){
        return callsMap.get(key);
    }
}
