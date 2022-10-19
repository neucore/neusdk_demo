package com.neucore.neulink.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.util.ObjectUtil;

public class LocalCache {

    private static LocalCache instance = new LocalCache();

    private Map<String,CacheObj> cache = new ConcurrentHashMap<>();

    public static LocalCache getInstance(){
        return instance;
    }

    public void set(String key,Object data){
        cache.put(key,new CacheObj(null,data));
    }
    /**
     * 多秒钟过期
     * @param key
     * @param expireTime 过期时间【单位秒】
     * @param data
     */
    public void set(String key,Long expireTime,Object data){
        Long expiration = null;
        if(ObjectUtil.isNotEmpty(expireTime)){
            expiration = System.currentTimeMillis()/1000+expireTime;
        }
        cache.put(key,new CacheObj(expiration,data));
    }

    /**
     *
     * @param key
     * @param expiration 过期时间点；即：unixTimestamp
     * @param data
     */
    public void setExpiration(String key,Long expiration,Object data){
        cache.put(key,new CacheObj(expiration,data));
    }
    /**
     *
     * @param key
     */
    public <T> T remove(String key,Class<T> clazz){
        T obj = (T)cache.remove(key);
        return obj;
    }

    /**
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(String key,Class<T> clazz){
        CacheObj<T> cacheObj = cache.get(key);
        if(ObjectUtil.isEmpty(cacheObj)){
            return null;
        }
        Long expire = cacheObj.getExpiration();
        T obj = cacheObj.getObject();
        /**
         * 没有过期时间
         */
        if(ObjectUtil.isEmpty(expire)){
            return obj;
        }
        else {
            /**
             * 过期时间单位精确到秒
             */
            Long now = System.currentTimeMillis()/1000;
            /**
             * 已经过期
             */
            if(expire < now){
                /**
                 * 清除缓存
                 */
                cache.remove(key);
                return null;

            }
            else{
                return obj;
            }
        }
    }
    class CacheObj<T> {
        private Long expiration;
        private T object;
        public CacheObj(Long expiration,T object){
            this.expiration = expiration;
            this.object = object;
        }

        public Long getExpiration() {
            return expiration;
        }

        public void setExpiration(Long expiration) {
            this.expiration = expiration;
        }

        public T getObject() {
            return object;
        }

        public void setObject(T object) {
            this.object = object;
        }
    }
}
