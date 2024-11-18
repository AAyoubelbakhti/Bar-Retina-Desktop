package com.project;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CtrlDetallsComanda implements Initializable {

    @FXML
    private Label labelIdComanda;

    @FXML
    private Label labelIdTaula;

    @FXML
    private Label labelIdCamarer;

    @FXML
    private Label labelEstadoComanda;

    @FXML
    private Button botonVolver;

    public void initialize(URL location, ResourceBundle resources) {
        // Configurar el botón para volver a la vista de comandas
        botonVolver.setOnAction(event -> volverAVistaComandas());
    }

    /**
     * Muestra los datos de la comanda en la vista.
     */
    public void mostrarDatosComanda(int idComanda, int idTaula, int idCamarer, String estadoComanda) {
        labelIdComanda.setText("ID Comanda: " + idComanda);
        labelIdTaula.setText("ID Mesa: " + idTaula);
        labelIdCamarer.setText("ID Camarero: " + idCamarer);
        labelEstadoComanda.setText("Estado: " + estadoComanda);
    }

    /**
     * Cambia la vista de regreso a la lista de comandas.
     */
    private void volverAVistaComandas() {
        UtilsViews.setViewAnimating("ViewComandes");
    }
}
