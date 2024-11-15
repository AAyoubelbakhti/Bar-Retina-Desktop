package com.project;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class CtrlComandes implements Initializable {
    
    @FXML
    private Button botonInicio;

    @FXML
    private Button botonProductos;

    @FXML
    private Button botonMesas;
    
    @FXML
    private Button botonSalir;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        botonInicio.setOnAction(event -> listenerBotonInicio());
        botonProductos.setOnAction(event -> listenerBotonProductos());
        botonMesas.setOnAction(event -> listenerBotonMesas());

        botonSalir.setOnAction(event -> System.exit(1));
    }

    private void listenerBotonInicio(){
        UtilsViews.setViewAnimating("ViewConfig");
    }

    private void listenerBotonProductos(){
        UtilsViews.setViewAnimating("ViewProductes");
    }

    private void listenerBotonMesas(){
        System.out.println("Boton mesas apretado");
        // UtilsViews.setViewAnimating("ViewTaules");
    }

}
