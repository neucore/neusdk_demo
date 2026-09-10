package com.neucore.neulink;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;

public class LoginUser implements NeulinkConst{
    private Long loginTime;
    private Integer role;
    private Integer isNew;
    private String userId;
    private String scope;
    private String scopeId;
    private String currentScopeId;
    private Integer type;
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Long expires;

    public LoginUser(JSONObject jsonObject){
        loginTime = jsonObject.getLong("time");
        JSONObject data = (JSONObject) jsonObject.get("data");
        role = data.getInt("role");
        isNew = data.getInt("isNew");
        userId = data.getStr("userId");
        scope = data.getStr("scope");
        scopeId = data.getStr("scopeId");
        currentScopeId = data.getStr("currentScopeId");
        type = data.getInt("type");
        tokenType = data.getStr("tokenType");
        accessToken = data.getStr("access_token");
        refreshToken = data.getStr("refresh_token");
        expires = data.getLong("expires_in");
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getIsNew() {
        return isNew;
    }

    public void setIsNew(Integer isNew) {
        this.isNew = isNew;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public String getCurrentScopeId() {
        return currentScopeId;
    }

    public void setCurrentScopeId(String currentScopeId) {
        this.currentScopeId = currentScopeId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

    public static boolean isSuccessedLogin(LoginUser loginUser){

        return ObjectUtil.isNotEmpty(loginUser)
                && ObjectUtil.isNotEmpty(loginUser.accessToken)
                && ObjectUtil.isNotEmpty(loginUser.refreshToken)
                && ObjectUtil.isNotEmpty(loginUser.loginTime)
                && ObjectUtil.isNotEmpty(loginUser.expires);
    }

    public static boolean isLoginExpire(LoginUser loginUser){
        long expireTime = loginUser.loginTime+loginUser.expires*1000;
        /**
         * 提前5秒
         */
        if(System.currentTimeMillis()<expireTime-5000){
            return true;
        }
        else{
            return false;
        }
    }
}
