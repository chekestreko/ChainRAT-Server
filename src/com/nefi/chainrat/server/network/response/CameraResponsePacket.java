package com.nefi.chainrat.server.network.response;

import com.nefi.chainrat.server.CommandType;
import com.nefi.chainrat.server.network.IPacket;
import com.sun.glass.ui.Size;

public class CameraResponsePacket implements IPacket {
    public String[] cameraIDs;
    public String frontID;
    public String backID;
    public Size[] frontSizes;
    public Size[] backSizes;

    public String type = "CameraResponsePacket";

    public CameraResponsePacket(String[] cameraIDs, String frontID, String backID, Size[] frontSizes, Size[] backSizes){
        this.cameraIDs = cameraIDs;
        this.frontID = frontID;
        this.backID  = backID;
        this.frontSizes = frontSizes;
        this.backSizes = backSizes;
    }

    @Override
    public CommandType type() {
        return CommandType.CAMERA;
    }
}

