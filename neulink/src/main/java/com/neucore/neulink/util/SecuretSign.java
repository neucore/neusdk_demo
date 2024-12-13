package com.neucore.neulink.util;

public class SecuretSign {

    private String username = "";

    private String password = "";

    private String clientId = "";

    public String getUsername() { return this.username;}

    public String getPassword() { return this.password;}
    public String getSign(){
        return password;
    }
    public String getClientId() { return this.clientId;}
    public SecuretSign(String productKey, String deviceName, String deviceSecret, String macAddress){
        calculate(productKey,deviceName,deviceSecret,macAddress,"salt",macAddress);
    }
    public SecuretSign(String productKey, String deviceName, String deviceSecret, String macAddress, String saltKey){
        calculate(productKey,deviceName,deviceSecret,macAddress,saltKey,macAddress);
    }
    private void calculate(String productKey, String deviceName,String deviceSecret,String macAddress,String saltKey,String saltValue) {

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
            //
            /**
             * MQTT ClientId
             * ${productKey}.${YekerID}@${macAddress}|salt=${设备id｜用户id},end=${device|izhiju|app|minp|server},securemode=2,signmethod=hmacsha256,_v=paho-1.0.0|
             */
            this.clientId = String.format("%s.%s@%s|%s=%s,end=%s,securemode=2,signmethod=hmacsha256,_v=paho-1.0.0|",productKey,deviceName, macAddress,saltKey,saltValue,"device");
            //MQTT UserName
            this.username = deviceName + "|" + productKey;
            /**
             * "clientId:" + ${productKey} + "." + ${deviceName} + ",deviceName:" + ${deviceName} + ",productKey:" + ${productKey} +",macAddress:" + ${macAddress} + ",salt:" + ${设备id或者用户id}+",end:" + ${device|izhiju|app|wechat}
             */
            String plainTxt = String.format("clientId:%s.%s,deviceName:%s,productKey:%s,macAddress:%s,%s:%s,end:%s",productKey,deviceName,deviceName,productKey,macAddress,saltKey,saltValue,"device");
            //MQTT Password
            this.password = CryptoUtil.hmacSha256(plainTxt, deviceSecret);

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
        String productKey = "aiclock";
        String deviceName = "10aG-1Y201A04542240700000002";
        String deviceSecret = "LdcK29T9GvOA5yKrGdi8+RgKqcuxqkC0sS7iK+4clws=";
        String macAddress = "02AD3D0110D8";
        String salt = "salt";
        SecuretSign sign = new SecuretSign(productKey,deviceName,deviceSecret,macAddress,salt);
        System.out.println(sign.getClientId());
        System.out.println(sign.getUsername());
        System.out.println(sign.getPassword());
    }
}
