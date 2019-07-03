package com.nefi.chainrat.server.network;

import com.nefi.chainrat.server.CommandType;

public interface IPacket {
    CommandType type();
}

