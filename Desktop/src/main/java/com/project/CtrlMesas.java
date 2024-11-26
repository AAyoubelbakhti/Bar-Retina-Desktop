package com.project;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CtrlMesas implements Initializable {

    @FXML
    private Button botonVolver;

    @FXML
    private Button mesa1;
    @FXML
    private Button mesa2;
    @FXML
    private Button mesa3;
    @FXML
    private Button mesa4;
    @FXML
    private Button mesa5;
    @FXML
    private Button mesa6;
    @FXML
    private Button mesa7;
    @FXML
    private Button mesa8;
    @FXML
    private Button mesa9;
    @FXML
    private Button mesa10;
    @FXML
    private Button mesa11;
    @FXML
    private Button mesa12;

    private Map<Integer, Button> botonesMesas = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Mapa de botones para simplificar acceso
        botonesMesas.put(1, mesa1);
        botonesMesas.put(2, mesa2);
        botonesMesas.put(3, mesa3);
        botonesMesas.put(4, mesa4);
        botonesMesas.put(5, mesa5);
        botonesMesas.put(6, mesa6);
        botonesMesas.put(7, mesa7);
        botonesMesas.put(8, mesa8);
        botonesMesas.put(9, mesa9);
        botonesMesas.put(10, mesa10);
        botonesMesas.put(11, mesa11);
        botonesMesas.put(12, mesa12);

        // Listener para volver
        botonVolver.setOnAction(event -> listenerBotonVolver());

        // Asignar listeners a cada botón de mesa
        for (int idMesa : botonesMesas.keySet()) {
            int mesaId = idMesa; // Necesario para lambdas
            botonesMesas.get(idMesa).setOnAction(event -> listenerMesa(mesaId));
        }
    }

    public void actualizarEstadoMesas(String jsonData) {
        JSONArray comandas = new JSONArray(jsonData);
    
        // Mapa para guardar la comanda más reciente por mesa
        Map<Integer, JSONObject> ultimaComandaPorMesa = new HashMap<>();
    
        // Iterar por las comandas para encontrar la más reciente por mesa
        for (int i = 0; i < comandas.length(); i++) {
            JSONObject comanda = comandas.getJSONObject(i);
            int idMesa = comanda.getInt("id_taula");
            int idComanda = comanda.getInt("id_comanda");
    
            // Si no existe una comanda para esta mesa o esta es más reciente, actualizar
            if (!ultimaComandaPorMesa.containsKey(idMesa) || idComanda > ultimaComandaPorMesa.get(idMesa).getInt("id_comanda")) {
                ultimaComandaPorMesa.put(idMesa, comanda);
            }
        }
    
        // Actualizar la información visual de cada mesa
        for (int idMesa : botonesMesas.keySet()) {
            Button botonMesa = botonesMesas.get(idMesa);
            if (ultimaComandaPorMesa.containsKey(idMesa)) {
                JSONObject comanda = ultimaComandaPorMesa.get(idMesa);
                String estatComanda = comanda.getString("estat_comanda");
                int idCambrer = comanda.getInt("id_cambrer");
    
                if ("pagat".equals(estatComanda)) {
                    // Si la comanda más reciente está en estado "pagat", mostrar la mesa como libre
                    botonMesa.setText("Mesa " + idMesa + "\nLibre");
                    botonMesa.setStyle("-fx-background-color: #8c8c8c; -fx-text-fill: white;"); // Gris para libre
                } else {
                    // Mostrar información de la comanda en curso
                    botonMesa.setText(String.format("Mesa %d\nEn curso\nCamarero: %d", idMesa, idCambrer));
                    botonMesa.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;"); // Naranja para en curso
                }
            } else {
                // Si no hay comandas asociadas, mostrar la mesa como libre
                botonMesa.setText("Mesa " + idMesa + "\nLibre");
                botonMesa.setStyle("-fx-background-color: #8c8c8c; -fx-text-fill: white;"); // Gris para libre
            }
        }
    }
    
    

    private void listenerMesa(int idMesa){
        JSONObject message = new JSONObject();

        message.put("type", "select-comanda-taula");
        message.put("idMesa", idMesa);
        Main.sendMessageToServer(message.toString());
    }

    public void listenerBotonVolver(){
        UtilsViews.setViewAnimating("ViewComandes");
    }    
}
