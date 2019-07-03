package com.nefi.chainrat.server.network;

import com.nefi.chainrat.server.CommandType;
import com.nefi.chainrat.server.network.IPacket;

public class PingPongPacket implements IPacket {

    public final String type = "PingPongPacket";

    @Override
    public CommandType type() {
        return CommandType.PING;
    }
}
