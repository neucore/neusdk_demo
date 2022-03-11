package com.neucore.neulink.impl.service.broadcast;

import android.util.Log;

import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class UdpReceiveAndtcpSend extends  Thread implements NeulinkConst{
    private String TAG = TAG_PREFIX+"UdpReceiveAndtcpSend";
    private MulticastSocket ms = null;
    private DatagramPacket dp = null;
    private Socket socket = null;
    public UdpReceiveAndtcpSend(){

    }
    @Override
    public void run() {
        byte[] data = new byte[1024];
        try {
            /**
             * 224.0.0.0/4（CIDR表示法）为多播地址。
             *
             * 224.0.0.1：该子网上的所有主机
             *
             * 224.0.0.2：该子网上的所有路由器
             *
             * 224.0.0.5： 开放最短路径优先算法(OSPF)第2版，设计用于到达某个网络上的所有OSPF路由器。
             *
             * 224.0.0.6：开放OSPF，设计用于到达某个网络上的所有OSPF指定的路由器。
             *
             * 224.0.0.9：路由信息协议第2版
             *
             * 224.0.1.1：网络时间协议
             *
             */
            InetAddress groupAddress = InetAddress.getByName("224.0.0.1");
            ms = new MulticastSocket(6789);
            ms.setLoopbackMode(true);
            ms.joinGroup(groupAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                dp = new DatagramPacket(data, data.length);
                if (ms != null) {
                    ms.receive(dp);
                }

                if (dp.getAddress() != null) {
                    String host_ip = DeviceUtils.getIpAddress(ContextHolder.getInstance().getContext());
                    final String quest_ip = dp.getAddress().toString();
                    final String target_ip = quest_ip.substring(1);

                    Log.i(TAG,"host_ip:  --------------------  " + host_ip);
                    Log.i(TAG,"quest_ip: --------------------  " + quest_ip);
                    Log.i(TAG,"target_ip: --------------------  " + target_ip);

                    if( (!host_ip.equals(""))  && host_ip.equals(quest_ip.substring(1)) ) {
                        continue;
                    }

                    final String codeString = new String(data, 0, dp.getLength());

                    Log.i(TAG,"收到来自: \n" + quest_ip.substring(1) + "\n" + "的udp请求\n");
                    Log.i(TAG,"请求内容: " + codeString + "\n\n");

                    try {
                        socket = new Socket(target_ip, 6788);
                        String devId = ServiceFactory.getInstance().getDeviceService().getExtSN();
                        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));
                        writer.write(devId);
                        writer.flush();
                        socket.shutdownOutput();
                    } catch (IOException e) {
                        Log.e(TAG,e.getMessage());
                    } finally {
                        try {
                            if (socket != null)
                                socket.close();
                        } catch (IOException e) {
                            Log.e(TAG,e.getMessage());
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
