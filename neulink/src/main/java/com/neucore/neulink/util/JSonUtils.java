package com.neucore.neulink.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

public class JSonUtils {

    private static Gson gson = new Gson();
    //private static ObjectMapper mapper = new ObjectMapper();

    public static String toString(Object obj){
        return gson.toJson(obj);
    }

    public static String toString(Object obj,Class field,JsonSerializer serializer){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(field,serializer).setPrettyPrinting().create();
        return gson.toJson(obj);
    }

    public static <T> T toObject(String paylog, Class<T> t){
        return gson.fromJson(paylog,t);
    }

    public static List toList(String paylog, Type t ){
        return gson.fromJson(paylog,t);
    }

    public static String toJson(byte[] feature){
        return gson.toJson(feature);
    }

    public static String toJson(short[] feature){
        return gson.toJson(feature);
    }

    /*public static String toJsonWithJackson(byte[] feature){
        return mapper.writeValueAsString(feature);
    }

    public static Byte[] fromJsonWithJackson(String feature){
        return gson.fromJson(feature,Byte[].class);
    }*/

    public static String toJson(Object[] feature){
        return gson.toJson(feature);
    }
}
