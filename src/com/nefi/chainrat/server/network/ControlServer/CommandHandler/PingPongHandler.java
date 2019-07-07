package com.nefi.chainrat.server.network.ControlServer.CommandHandler;

import com.google.common.base.Charsets;
import com.nefi.chainrat.server.network.ControlServer.CommandType;
import com.nefi.chainrat.server.network.ControlServer.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class PingPongHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //String went through JSON Serializer first
        System.out.println("[PingPongHandler]");
        Packet packet = (Packet) msg;

        if(packet.type == CommandType.PING){
            String pong = "PONG";
            ByteBuf out = ctx.alloc().buffer(pong.length());
            out.writeCharSequence(pong, Charsets.UTF_8);
            ctx.writeAndFlush(out);
            return;
        }

        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        cause.printStackTrace();
        ctx.close();
    }
}

