package com.nefi.chainrat.server.network.requests;

import com.nefi.chainrat.server.CommandType;
import com.nefi.chainrat.server.network.IPacket;
import com.sun.glass.ui.Size;

public class CameraRequestPacket implements IPacket {
    public Size pictureSize;
    public String cameraID;
    public int udpPort;

    public final String type = "CameraRequestPacket";


    public CameraRequestPacket(Size pictureSize, String cameraID, int port){
        this.pictureSize = pictureSize;
        this.cameraID = cameraID;
        this.udpPort = port;
    }

    public CommandType type() {
        return CommandType.CAMERA;
    }
}
