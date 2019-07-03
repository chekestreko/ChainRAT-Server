package com.nefi.chainrat.server.forms;

import com.nefi.chainrat.server.Main;
import com.nefi.chainrat.server.log.Log;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class frmCameraManager implements Initializable {
    private Log log;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.log = Main.getLog();
        log.d(this, "Started CameraManager on thread: " + Thread.currentThread().getName());
    }
}
