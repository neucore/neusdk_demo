package com.neucore.neulink.impl.service.broadcast;

import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.registry.ServiceRegistry;
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

                    NeuLogUtils.iTag(TAG,"host_ip:  --------------------  " + host_ip);
                    NeuLogUtils.iTag(TAG,"quest_ip: --------------------  " + quest_ip);
                    NeuLogUtils.iTag(TAG,"target_ip: --------------------  " + target_ip);

                    if( (!host_ip.equals(""))  && host_ip.equals(quest_ip.substring(1)) ) {
                        continue;
                    }

                    final String codeString = new String(data, 0, dp.getLength());

                    NeuLogUtils.iTag(TAG,"收到来自: \n" + quest_ip.substring(1) + "\n" + "的udp请求\n");
                    NeuLogUtils.iTag(TAG,"请求内容: " + codeString + "\n\n");

                    try {
                        socket = new Socket(target_ip, 6788);
                        String devId = ServiceRegistry.getInstance().getDeviceService().getExtSN();
                        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));
                        writer.write(devId);
                        writer.flush();
                        socket.shutdownOutput();
                    } catch (IOException e) {
                        NeuLogUtils.eTag(TAG,e.getMessage());
                    } finally {
                        try {
                            if (socket != null)
                                socket.close();
                        } catch (IOException e) {
                            NeuLogUtils.eTag(TAG,e.getMessage());
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
