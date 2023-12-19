package com.neucore.neuskit;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ResponseHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        try{
            ByteBuf buf = (ByteBuf) msg;
            /**
             * TODO 解包
             */
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
