package com.neucore.neuskit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TnClient {
    private RequestHandler requestHandler;
    private ResponseHandler responseHandler;
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public TnClient(String ip,Integer port){
        Bootstrap bootstrap = new Bootstrap();
        //绑定线程组
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        responseHandler = new ResponseHandler();
        /*
         * 客户端必须绑定处理器，也就是必须调用handler方法
         */
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast();
            }
        });

        ChannelFuture future = bootstrap.connect(ip, port);
        try {
            requestHandler = new RequestHandler(future.sync());
        } catch (InterruptedException e) {

        }
    }

    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public void close(){
        requestHandler.close();
    }
}
