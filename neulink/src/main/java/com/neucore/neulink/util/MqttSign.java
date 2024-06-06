package com.neucore.neulink.util;

public class MqttSign {
    private String username = "";

    private String password = "";

    private String clientid = "";

    public String getUsername() { return this.username;}

    public String getPassword() { return this.password;}

    public String getClientid() { return this.clientid;}

    public void calculate(String productKey, String deviceName,String deviceSecret,String macAddress,String timestamp) {

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
        macAddress = macAddress.replace(":","");
        try {
            //MQTT UserName
            this.username = deviceName;
            String plainPasswd = "clientId" + productKey + "." + deviceName + "deviceName" +
                    deviceName + "productKey" + productKey +"macAddress" + macAddress + "timestamp" + timestamp;
            //MQTT Password
            this.password = CryptoUtil.hmacSha256(plainPasswd, deviceSecret);

            //MQTT ClientId
            this.clientid = productKey + "." + deviceName + "@" + macAddress + "|" + "timestamp=" + timestamp +
                    ",securemode=2,signmethod=hmacsha256,_v=paho-java-1.0.0|";
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
        MqttSign sign = new MqttSign();
        String productKey = "aiclock";
        String deviceName = "10aG-1Y201A04542240700000002";
        String deviceSecret = "LdcK29T9GvOA5yKrGdi8+RgKqcuxqkC0sS7iK+4clws=";
        String macAddress = "02AD3D0110D8";
        String timestamp = String.valueOf(System.currentTimeMillis());
        sign.calculate(productKey,deviceName,deviceSecret,macAddress,timestamp);
        System.out.println(sign.getClientid());
        System.out.println(sign.getUsername());
        System.out.println(sign.getPassword());
    }
}
