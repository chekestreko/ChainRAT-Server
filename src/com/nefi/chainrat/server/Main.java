package com.nefi.chainrat.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static frmMainController mainController;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("frmMain.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.show();
    }

    public static frmMainController getMainController(){
        return mainController;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
