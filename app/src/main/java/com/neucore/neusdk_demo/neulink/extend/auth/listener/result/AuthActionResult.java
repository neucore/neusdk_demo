package com.neucore.neusdk_demo.neulink.extend.auth.listener.result;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.AuthItemResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.DeviceResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.DomainResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.LinkResult;

import java.util.ArrayList;
import java.util.List;

public class AuthActionResult extends ActionResult {

    @SerializedName("device")
    private List<DeviceResult> devices;
    @SerializedName("domain")
    private List<DomainResult> domains;
    @SerializedName("link")
    private List<LinkResult> links;
    @SerializedName("auth")
    private List<AuthItemResult> authItems;

    public List<DeviceResult> getDevices() {
        return devices;
    }

    public void add(DeviceResult result){
        if(result!=null && getDevices()==null){
            devices = new ArrayList<>();
        }
        if(result!=null ){
            devices.add(result);
        }
    }

    public void setDevices(List<DeviceResult> devices) {
        this.devices = devices;
    }

    public List<DomainResult> getDomains() {
        return domains;
    }

    public void add(DomainResult result){
        if(result!=null && getDomains()==null){
            domains = new ArrayList<>();
        }
        if(result!=null ){
            domains.add(result);
        }
    }

    public void setDomains(List<DomainResult> domains) {
        this.domains = domains;
    }

    public List<LinkResult> getLinks() {
        return links;
    }

    public void add(LinkResult result){
        if(result!=null && getLinks()==null){
            links = new ArrayList<>();
        }
        if(result!=null ){
            links.add(result);
        }
    }

    public void setLinks(List<LinkResult> links) {
        this.links = links;
    }

    public List<AuthItemResult> getAuthItems() {
        return authItems;
    }

    public void add(AuthItemResult result){
        if(result!=null && getAuthItems()==null){
            authItems = new ArrayList<>();
        }
        if(result!=null ){
            authItems.add(result);
        }
    }
    public void setAuthItems(List<AuthItemResult> authItems) {
        this.authItems = authItems;
    }
}
