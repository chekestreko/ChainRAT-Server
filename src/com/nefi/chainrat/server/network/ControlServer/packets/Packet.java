package com.nefi.chainrat.server.network.ControlServer.packets;

import com.nefi.chainrat.server.network.ControlServer.CommandType;

public class Packet {
    public CommandType type;
    public String content;
    public Packet(CommandType type, String content){
        this.type = type;
        this.content = content;
    }
}
