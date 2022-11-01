package com.neucore.neulink.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.neucore.neulink.extend.IgnorePropExclusionStrategy;

import java.lang.reflect.Type;
import java.util.List;


public class JSonUtils {

    private static Gson gson = null;
    private static GsonBuilder builder = null;
    //private static ObjectMapper mapper = new ObjectMapper();
    static {
        builder = new GsonBuilder()
//                .registerTypeAdapter(LocalDateTime.class,new LocalDateTimeSerializer())
//                .registerTypeAdapter(LocalDateTime.class,new LocalDateTimeDeserializer())
//                .registerTypeAdapter(LocalDate.class,new LocalDateSerializer())
//                .registerTypeAdapter(LocalDate.class,new LocalDateDeserializer())
//                .registerTypeAdapter(LocalTime.class,new LocalTimeSerializer())
//                .registerTypeAdapter(LocalTime.class,new LocalTimeDeserializer())
                .setExclusionStrategies(new IgnorePropExclusionStrategy())
                .setPrettyPrinting();
        gson = builder.create();
    }

    public static String toString(Object obj){
        return gson.toJson(obj);
    }

    public static String toString(Object obj,Class field,JsonSerializer serializer){
        Gson gson = builder
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

    public static String toJson(Object[] feature){
        return gson.toJson(feature);
    }
}
