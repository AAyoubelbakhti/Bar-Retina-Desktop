package com.project;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;

public class CtrlComandes implements Initializable {

    @FXML
    private Button botonInicio;

    @FXML
    private Button botonProductos;

    @FXML
    private Button botonTopProductos;

    @FXML
    private Button botonMesas;

    @FXML
    private Button botonSalir;

    @FXML
    private Button botonActualizar;

    @FXML
    private ListView<VBox> listaPagadas;

    @FXML
    private ListView<VBox> listaEnCurso;

    @FXML
    private ListView<VBox> listaFinalizadas;

    private JSONArray comandas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        botonInicio.setOnAction(event -> listenerBotonInicio());
        botonProductos.setOnAction(event -> listenerBotonProductos());
        botonTopProductos.setOnAction(event -> listenerBotonTopProductos());
        botonMesas.setOnAction(event -> listenerBotonMesas());

        botonActualizar.setOnAction(event -> {
            JSONObject message = new JSONObject();
            message.put("type", "select-comanda");
            Main.wsClient.safeSend(message.toString());
        });
        botonSalir.setOnAction(event -> {
            System.exit(1);
        });


    }

    public void llenarListasDesdeJSONArray(JSONArray jsonArray) {
        // Crear listas observables para cada estado

        comandas = jsonArray;
        ObservableList<VBox> pagadas = FXCollections.observableArrayList();
        ObservableList<VBox> enCurso = FXCollections.observableArrayList();
        ObservableList<VBox> finalizados = FXCollections.observableArrayList();
    
        // Recorrer el JSONArray
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                // Obtener la comanda actual
                JSONObject comanda = jsonArray.getJSONObject(i);
    
                // Validar que la comanda tiene las claves necesarias
                if (!comanda.has("id_comanda") || !comanda.has("id_taula") || !comanda.has("id_cambrer") || !comanda.has("estat_comanda")) {
                    System.out.println("Comanda con claves faltantes: " + comanda.toString());
                    continue; // Saltar a la siguiente comanda
                }
    
                // Extraer los datos de la comanda
                int idComanda = comanda.getInt("id_comanda");
                int idTaula = comanda.getInt("id_taula");
                int idCamarer = comanda.getInt("id_cambrer");
                String estadoComanda = comanda.getString("estat_comanda");
                String stringProductes = comanda.getString("comanda");
                Double preuComanda = comanda.getBigDecimal("preu_comanda").doubleValue();
                // Crear un VBox para representar la comanda
                VBox vboxComanda = new VBox(5); // Espaciado de 5px
                vboxComanda.setStyle("-fx-padding: 10px; -fx-border-color: #a3a3a3; -fx-border-width: 1px; -fx-background-color: #f9f9f9;");
    
                // Crear los Labels con la información de la comanda
                Label labelComanda = new Label("Comanda: " + idComanda);
                Label labelTaula = new Label("Mesa: " + idTaula);
                Label labelCamarero = new Label("Camarero: " + idCamarer);
                // Label labelPreuComanda = new Label("Preu: "+ preuComanda+"€");
    
                // Agregar los Labels al VBox
                vboxComanda.getChildren().addAll(labelComanda, labelTaula, labelCamarero);
    
                // Asignar un evento de clic al VBox para mostrar los detalles de la comanda
                vboxComanda.setOnMouseClicked(event -> {
                    mostrarDetallesComanda(idComanda, idTaula, idCamarer, estadoComanda, stringProductes, preuComanda);
                });
    
                // Asignar la comanda al estado correspondiente inicialmente
                switch (estadoComanda) {
                    case "pendent":
                    case "en curs":
                        enCurso.add(vboxComanda);
                        break;
                    case "completa":
                    case "llesta":
                        finalizados.add(vboxComanda);
                        break;
                    default:
                        pagadas.add(vboxComanda);
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error procesando comanda: " + e.getMessage());
                e.printStackTrace(); // Para depurar si es necesario
            }
        }
    
        // Asignar las listas observables a las ListView
        listaPagadas.setItems(pagadas);
        listaEnCurso.setItems(enCurso);
        listaFinalizadas.setItems(finalizados);
    }
    
    private void mostrarDetallesComanda(int idComanda, int idTaula, int idCamarer, String estadoComanda, String stringProductes, double preuComanda) {
        // Cambiar a la vista de detalles
        UtilsViews.setViewAnimating("ViewDetallsComanda");
        if(!stringProductes.contains("[")){
            stringProductes = "[]";
        }
        // Pasar los datos de la comanda al controlador
        Main.ctrlDetallsComanda.mostrarDatosComanda(idComanda, idTaula, idCamarer, estadoComanda, preuComanda);
        try{
            JSONArray jsonProductes = new JSONArray(stringProductes);
            Main.ctrlDetallsComanda.cargarProductos(jsonProductes);
        } catch(Exception e){
            System.out.println(e);
        }
        

    }
    
    

    private void listenerBotonInicio() {
        UtilsViews.setViewAnimating("ViewConfig");
    }

    private void listenerBotonProductos() {
        UtilsViews.setViewAnimating("ViewProductes");
    }

    private void listenerBotonTopProductos() {
        UtilsViews.setViewAnimating("ViewMesVenuts");
    }

    private void listenerBotonMesas() {
        UtilsViews.setViewAnimating("ViewTaules");
        Main.ctrlMesas.actualizarEstadoMesas(comandas.toString());
    }

}
