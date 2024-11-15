package com.project;

import java.util.List;

import org.json.JSONObject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    public static UtilsWS wsClient;
    public static CtrlConfig ctrlConfig;
    public static CtrlProductes ctrlProductes;
    public static CtrlComandes ctrlComandes;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        final int windowWidth = 850;
        final int windowHeight = 600;

        UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");
        UtilsViews.addView(getClass(), "ViewConfig", "/assets/viewConfig.fxml");
        UtilsViews.addView(getClass(), "ViewProductes", "/assets/viewProductes.fxml");
        UtilsViews.addView(getClass(), "ViewComandes", "/assets/viewComandes.fxml");

        ctrlConfig = (CtrlConfig) UtilsViews.getController("ViewConfig");
        ctrlProductes = (CtrlProductes) UtilsViews.getController("ViewProductes");
        ctrlComandes = (CtrlComandes) UtilsViews.getController("ViewComandes");
        
        Scene scene = new Scene(UtilsViews.parentContainer);
        stage.setScene(scene);
        stage.onCloseRequestProperty();
        stage.setTitle("Bar Retina Desktop App");
        stage.setMinWidth(windowWidth);
        stage.setMinHeight(windowHeight);
        stage.show();
    }

    @Override
    public void stop() {
        System.exit(1);
    }

    public static void connectToServer() {
        // ctrlConfig.txtMessage.setTextFill(Color.BLACK);
        ctrlConfig.txtMessage.setText("Connecting ...");
        // String protocol = "ws";
        // String host = ctrlConfig.textUrl.getText();
        // String port = "4545";
        // wsClient = UtilsWS.getSharedInstance(protocol + "://" + "localhost" + ":" +
        // port);
        String wsLocation = "ws://localhost:4545";
        wsClient = UtilsWS.getSharedInstance(wsLocation);

        wsClient.onOpen(message -> {
            Platform.runLater(() -> {
                UtilsViews.setViewAnimating("ViewComandes");
            });

        });

        wsClient.onMessage((response) -> {
            Platform.runLater(() -> {
                wsMessage(response);
            });
        });
        wsClient.onError((response) -> {
            Platform.runLater(() -> {
                wsError(response);
            });
        });
        wsClient.onClose(message -> {
            System.out.println("Connexió tancada: " + message);
        });
    }

    private static void wsMessage(String response) {
        JSONObject msgObj = new JSONObject(response);
        switch (msgObj.getString("type")) {
            case "tags":
            case "productes":
                System.out.println("Se ha recibido respuesta!");
                ctrlProductes.cargarProductos(msgObj.toString());
                break;
            default:
                ctrlConfig.txtMessage.setText("Tipo de mensaje desconocido");
        }
    }

    private static void wsError(String response) {
        String connectionRefused = "Connection refused";
        if (response.contains(connectionRefused)) {
            ctrlConfig.txtMessage.setTextFill(Color.RED);
            ctrlConfig.txtMessage.setText(connectionRefused);
        }
    }

    public static void sendMessageToServer(String message){
        System.out.println("Se ha enviado el mensaje al servidor");
        wsClient.safeSend(message);
    }
}