package com.neucore.neuskit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;

public class RequestHandler {
    private ChannelFuture channelFuture;
    public RequestHandler(ChannelFuture future){
        this.channelFuture = future;
    }
    public void request(byte[] bytes){
        //请求报文
        ByteBuf byteBufMsg = Unpooled.buffer();
        byteBufMsg.writeBytes(bytes);
        channelFuture.channel().writeAndFlush(byteBufMsg);
    }
    public void close(){
        channelFuture.channel().closeFuture();
    }
}
