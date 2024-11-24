package com.project;

import java.net.URL;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        botonVolver.setOnAction(event -> listenerBotonVolver());
        mesa1.setOnAction(event -> listenerMesa(1));
        mesa2.setOnAction(event -> listenerMesa(2));
        mesa3.setOnAction(event -> listenerMesa(3));
        mesa4.setOnAction(event -> listenerMesa(4));
        mesa5.setOnAction(event -> listenerMesa(5));
        mesa6.setOnAction(event -> listenerMesa(6));
        mesa7.setOnAction(event -> listenerMesa(7));
        mesa8.setOnAction(event -> listenerMesa(8));
        mesa9.setOnAction(event -> listenerMesa(9));
        mesa10.setOnAction(event -> listenerMesa(10));
        mesa11.setOnAction(event -> listenerMesa(11));
        mesa12.setOnAction(event -> listenerMesa(12));

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
