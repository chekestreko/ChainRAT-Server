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

import java.lang.reflect.Type;

public class Main extends Application {
    private static frmMainController mainController;
    private static final int PORT = 4488;
    private static Log log;
    private static GsonBuilder gsonBuilder = new GsonBuilder();
    private static Gson gson = gsonBuilder.create();
    public static String serialize(Object obj, Type type){
        return gson.toJson(obj, type);
    }
    public static Object deserialize(String msg, Type type){
        return gson.fromJson(msg, type);
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
