package com.neucore.neulink.util;

public class SecuretSign {
    private String username = "";

    private String password = "";
    private String plainTxt = "";
    private String clientid = "";

    public String getUsername() { return this.username;}

    public String getPassword() { return this.password;}

    public String getClientId() { return this.clientid;}

    public String getPlainTxt() {
        return plainTxt;
    }

    public SecuretSign(String productKey, String deviceName, String deviceSecret, String macAddress, String saltKey,String saltValue,String end,String securemode,String signmethod,String version){
        calculate(productKey,deviceName,deviceSecret,macAddress,saltKey,saltValue,end,securemode,signmethod,version);
    }

    private void calculate(String productKey, String deviceName, String deviceSecret, String macAddress,String saltKey, String saltValue, String end,String securemode,String signmethod,String version) {

        if (productKey == null||productKey.trim().length() == 0) {
            throw new IllegalArgumentException("productKey can not be null");
        }
        if(deviceName == null ||deviceName.trim().length() == 0){
            throw new IllegalArgumentException("deviceName  can not be null");
        }
        if(deviceSecret == null||deviceSecret.trim().length() == 0) {
            throw new IllegalArgumentException("deviceSecret  can not be null");
        }
        if(macAddress == null||macAddress.trim().length() == 0) {
            throw new IllegalArgumentException("macAddress  can not be null");
        }
        macAddress = macAddress.replace(":","").toUpperCase();
        try {
            //MQTT ClientId
            this.clientid = String.format("%s.%s@%s|%s=%s,end=%s,securemode=%s,signmethod=%s,_v=%s|",productKey,deviceName, macAddress,saltKey,saltValue,end,securemode,signmethod,version);
            //MQTT UserName
            this.username = deviceName + "|" + productKey;
            //MQTT PlainTxt: paho-1.0.0不含securemode/signmethod/_v，其他版本追加
            this.plainTxt = String.format("clientId:%s.%s,deviceName:%s,productKey:%s,macAddress:%s,%s:%s,end:%s",productKey,deviceName,deviceName,productKey,macAddress,saltKey,saltValue,end);
            if (!"paho-1.0.0".equals(version)) {
                this.plainTxt = this.plainTxt + String.format(",securemode:%s,signmethod:%s,_v:%s", securemode, signmethod, version);
            }
            //MQTT Password
            this.password = CryptoUtil.hmacSha256(plainTxt, deviceSecret);

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
