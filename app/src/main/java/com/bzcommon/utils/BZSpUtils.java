package com.bzcommon.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.Map;

/**
 * SharedPreferences 的简单封装
 * Created by jack_liu on 2015/6/16.
 */
public class BZSpUtils {
    private static SharedPreferences sharedPreferences = null;
    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "bzmedia";

    public static synchronized void init(@NonNull Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getApplicationContext().getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE);
        }
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    public static void put(String key, Object object) {
        if (null == sharedPreferences) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            if (object == null) return;
            editor.putString(key, object.toString());
        }
        editor.apply();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    public static Object get(String key, Object defaultObject) {
        if (null == sharedPreferences) return defaultObject;
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        }
        return defaultObject;
    }

    public static String getString(String key) {
        if (null == sharedPreferences) return "";
        return sharedPreferences.getString(key, "");
    }

    public static int getInt(String key, int defaultValue) {
        if (null == sharedPreferences) return defaultValue;
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue) {
        if (null == sharedPreferences) return defaultValue;
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        if (null == sharedPreferences) return defaultValue;
        return sharedPreferences.getBoolean(key, defaultValue);
    }


    /**
     * 移除某个key值已经对应的值
     */
    public static void remove(String key) {
        if (null == sharedPreferences) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 清除所有数据
     */
    public static void clear() {
        if (null == sharedPreferences) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 查询某个key是否已经存在
     */
    public static boolean contains(String key) {
        if (null == sharedPreferences) return false;
        return sharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public static Map<String, ?> getAll() {
        if (null == sharedPreferences) return null;
        return sharedPreferences.getAll();
    }


}
