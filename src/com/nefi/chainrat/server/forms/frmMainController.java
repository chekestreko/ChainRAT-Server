package com.nefi.chainrat.server.forms;

import com.nefi.chainrat.server.Main;
import com.nefi.chainrat.server.log.Log;
import com.nefi.chainrat.server.network.Server;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class frmMainController implements Initializable {
    public TextArea tbLogOut;
    public Label lbLog;
    public MenuItem btnClose;
    public MenuItem btnBuild;
    public MenuItem btnCamManager;
    public MenuItem btnMicManager;
    public MenuItem btnCallManager;
    public MenuItem btnFileManager;
    public MenuItem btnSMSManager;
    public MenuItem btnLocationManager;
    public MenuItem btnContactsManager;
    public MenuItem btnAccountManager;
    public MenuItem btnKeyloggerManager;
    public MenuItem btnApplicationsManager;
    public MenuItem btnReverseShellManager;
    public ListView listView;
    public Button btnTest;
    public HashMap<Integer, Server.ClientHandler> clMap = new HashMap<>();
    private Log log;

    private static frmMainController mainController;
    private static frmCameraManager cameraManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log = Main.getLog();
        log.d(this, "Started MainForm :) on thread: " + Thread.currentThread().getName());

        //Start networking in seperate thread
        Runnable runnable = new Server();
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void btnTest_Click(ActionEvent actionEvent) {
        Server.ClientHandler cap = clMap.get(0);
    }

    public void updateListview() {
        ObservableList<String> items = FXCollections.observableArrayList();
        for(Integer i : clMap.keySet()){
            log.d(this, "Looping through hashmap.." + Log.newLine + clMap.get(i).toString());
            items.add(i.toString());
        }
        listView.setItems(items);
    }

    public void btnClose_Click(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void btnBuild_Click(ActionEvent actionEvent) {
    }

    public void btnCamManager_Click(ActionEvent actionEvent) throws Exception {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("forms/frmCameraManager.fxml"));
        Parent root = loader.load();
        stage.setOnCloseRequest( event -> {
            System.exit(0);
        } );
        stage.setTitle("Camera Manager");
        stage.setScene(new Scene(root, 800, 400));
        stage.show();
    }

    public void btnMicManager_Click(ActionEvent actionEvent) {
    }

    public void btnSMSManager_Click(ActionEvent actionEvent) {
    }

    public void btnFileManager_Click(ActionEvent actionEvent) {
    }

    public void btnCallManager_Click(ActionEvent actionEvent) {
    }

    public void btnLocationManager_Click(ActionEvent actionEvent) {
    }

    public void btnContactsManager_Click(ActionEvent actionEvent) {
    }

    public void btnKeyloggerManager_Click(ActionEvent actionEvent) {
    }

    public void btnAccountManager_Click(ActionEvent actionEvent) {
    }

    public void btnReverseShellManager_Click(ActionEvent actionEvent) {
    }

    public void btnApplicationsManager_Click(ActionEvent actionEvent) {
    }

}
