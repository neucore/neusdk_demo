package com.neucore.neusdk_demo.neulink.extend.auth.response;

import com.google.gson.annotations.SerializedName;

public class AuthActionResult {

    @SerializedName("device")
    private DeviceResult[] devices;
    @SerializedName("domain")
    private DeviceResult[] domains;
    @SerializedName("link")
    private LinkResult[] links;
    @SerializedName("auth")
    private AuthResult[] auths;

    public DeviceResult[] getDevices() {
        return devices;
    }

    public void setDevices(DeviceResult[] devices) {
        this.devices = devices;
    }

    public DeviceResult[] getDomains() {
        return domains;
    }

    public void setDomains(DeviceResult[] domains) {
        this.domains = domains;
    }

    public LinkResult[] getLinks() {
        return links;
    }

    public void setLinks(LinkResult[] links) {
        this.links = links;
    }

    public AuthResult[] getAuths() {
        return auths;
    }

    public void setAuths(AuthResult[] auths) {
        this.auths = auths;
    }
}
