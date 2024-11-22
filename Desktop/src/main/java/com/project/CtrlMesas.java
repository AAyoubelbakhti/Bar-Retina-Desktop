package com.project;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CtrlMesas implements Initializable {

    @FXML
    private Button botonVolver;

    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        botonVolver.setOnAction(event -> listenerBotonVolver());
    }

    public void listenerBotonVolver(){
        UtilsViews.setViewAnimating("ViewComandes");
    }    
}
