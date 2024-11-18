package com.project;

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
    private Button botonMesas;

    @FXML
    private Button botonSalir;

    @FXML
    private ListView<VBox> listaPendientes;

    @FXML
    private ListView<VBox> listaEnCurso;

    @FXML
    private ListView<VBox> listaFinalizados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        botonInicio.setOnAction(event -> listenerBotonInicio());
        botonProductos.setOnAction(event -> listenerBotonProductos());
        botonMesas.setOnAction(event -> listenerBotonMesas());

        botonSalir.setOnAction(event -> {
            JSONObject message = new JSONObject();
            message.put("type", "select-comanda");
            Main.wsClient.safeSend(message.toString());
        });
    }

    public void llenarListasDesdeJSONArray(JSONArray jsonArray) {
        // Crear listas observables para cada estado
        ObservableList<VBox> pendientes = FXCollections.observableArrayList();
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
    
                // Crear un VBox para representar la comanda
                VBox vboxComanda = new VBox(5); // Espaciado de 5px
                vboxComanda.setStyle("-fx-padding: 10px; -fx-border-color: #a3a3a3; -fx-border-width: 1px; -fx-background-color: #f9f9f9;");
    
                // Crear los Labels con la información de la comanda
                Label labelComanda = new Label("Comanda: " + idComanda);
                Label labelTaula = new Label("Mesa: " + idTaula);
                Label labelCamarero = new Label("Camarero: " + idCamarer);
    
                // Agregar los Labels al VBox
                vboxComanda.getChildren().addAll(labelComanda, labelTaula, labelCamarero);
    
                // Crear el botón solo si la comanda no está finalizada
                if (!estadoComanda.equals("llesta") && !estadoComanda.equals("pagada") && !estadoComanda.equals("completa")) {
                    Button btnCambiarEstado = new Button();
                    btnCambiarEstado.setText("Mover a...");
                    btnCambiarEstado.setOnAction(event -> {
                        // Cambiar el estado de la comanda dependiendo de su estado actual
                        String nuevoEstado = estadoComanda;
                        switch (estadoComanda) {
                            case "pendent":
                                nuevoEstado = "en curs";
                                break;
                            case "en curs":
                                nuevoEstado = "llesta";
                                break;
                            case "completa":
                            case "llesta":
                            case "pagada":
                                break;
                        }
                        // Aquí actualizamos el estado dentro de la comanda si es necesario
                        actualizarEstadoComanda(idComanda, nuevoEstado);
    
                        // Actualizar la vista de las listas
                        listaPendientes.setItems(pendientes);
                        listaEnCurso.setItems(enCurso);
                        listaFinalizados.setItems(finalizados);
                    });
    
                    // Agregar el botón al VBox
                    vboxComanda.getChildren().add(btnCambiarEstado);
                }
    
                // Asignar un evento de clic al VBox para mostrar los detalles de la comanda
                vboxComanda.setOnMouseClicked(event -> {
                    mostrarDetallesComanda(idComanda, idTaula, idCamarer, estadoComanda);
                });
    
                // Asignar la comanda al estado correspondiente inicialmente
                switch (estadoComanda) {
                    case "en curs":
                        enCurso.add(vboxComanda);
                        break;
                    case "completa":
                    case "pagada":
                    case "llesta":
                        finalizados.add(vboxComanda);
                        break;
                    default:
                        pendientes.add(vboxComanda);
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error procesando comanda: " + e.getMessage());
                e.printStackTrace(); // Para depurar si es necesario
            }
        }
    
        // Asignar las listas observables a las ListView
        listaPendientes.setItems(pendientes);
        listaEnCurso.setItems(enCurso);
        listaFinalizados.setItems(finalizados);
    }
    
    // Método para actualizar el estado de la comanda (puedes modificarlo si lo necesitas)
    private void actualizarEstadoComanda(int idComanda, String nuevoEstado) {
        
        

        System.out.println("Comanda " + idComanda + " ha cambiado a estado: " + nuevoEstado);
    }
    
    
    private void mostrarDetallesComanda(int idComanda, int idTaula, int idCamarer, String estadoComanda) {
        // Cambiar a la vista de detalles
        UtilsViews.setViewAnimating("ViewDetallsComanda");
        // Pasar los datos de la comanda al controlador
        Main.ctrlDetallsComanda.mostrarDatosComanda(idComanda, idTaula, idCamarer, estadoComanda);
    }
    
    

    private void listenerBotonInicio() {
        UtilsViews.setViewAnimating("ViewConfig");
    }

    private void listenerBotonProductos() {
        UtilsViews.setViewAnimating("ViewProductes");
    }

    private void listenerBotonMesas() {
        System.out.println("Boton mesas apretado");
        // UtilsViews.setViewAnimating("ViewTaules");
    }

}
