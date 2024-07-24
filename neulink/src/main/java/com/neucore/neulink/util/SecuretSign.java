package com.neucore.neulink.util;

public class SecuretSign {

    private String username = "";

    private String sign = "";

    private String clientId = "";

    public String getUsername() { return this.username;}

    public String getSign() { return this.sign;}

    public String getClientId() { return this.clientId;}
    public SecuretSign(String productKey, String deviceName, String deviceSecret, String macAddress, String timestamp){
        calculate(productKey,deviceName,deviceSecret,macAddress,timestamp);
    }
    private void calculate(String productKey, String deviceName,String deviceSecret,String macAddress,String timestamp) {

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
            //MQTT UserName
            this.username = deviceName + "|" + productKey;
            String plainTxt = String.format("clientId:%s.%s,deviceName:%s,productKey:%s,macAddress:%s,timestamp:%s",productKey,deviceName,deviceName,productKey,macAddress,timestamp);
            //MQTT Password
            this.sign = CryptoUtil.hmacSha256(plainTxt, deviceSecret);

            //MQTT ClientId
            this.clientId = productKey + "." + deviceName + "@" + macAddress + "|" + "timestamp=" + timestamp +
                    ",securemode=2,signmethod=hmacsha256,_v=paho-1.0.0|";
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
        String productKey = "aiclock";
        String deviceName = "10aG-1Y201A04542240700000002";
        String deviceSecret = "LdcK29T9GvOA5yKrGdi8+RgKqcuxqkC0sS7iK+4clws=";
        String macAddress = "02AD3D0110D8";
        String timestamp = String.valueOf(System.currentTimeMillis());
        SecuretSign sign = new SecuretSign(productKey,deviceName,deviceSecret,macAddress,timestamp);
        System.out.println(sign.getClientId());
        System.out.println(sign.getUsername());
        System.out.println(sign.getSign());
    }
}
