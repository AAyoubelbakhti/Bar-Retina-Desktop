package com.project;

import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class CtrlProductes implements Initializable {

    @FXML
    private Label labelProd;

    @FXML
    private Button botonProductos;
    
    @FXML
    private ScrollPane scrollProductos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        botonProductos.setOnAction(event -> Main.wsClient.safeSend("productes"));
    }

    public void cargarProductos(String productos){
        labelProd.setText(productos);
    }
    
}
