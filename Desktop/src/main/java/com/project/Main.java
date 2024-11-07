package com.project;

import java.util.List;

import org.json.JSONObject;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    public static UtilsWS wsClient;
    public static CtrlConfig ctrlConfig;
    public static void main(String[] args) {
        launch(args);        
    }

    @Override
    public void start(Stage stage) throws Exception {
        final int windowWidth = 600;
        final int windowHeight = 450;

        UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");
        UtilsViews.addView(getClass(), "ViewConfig", "/assets/viewConfig.fxml");

        ctrlConfig = (CtrlConfig) UtilsViews.getController("ViewConfig");

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
        System.exit(1); // Kill all executor services
    }

    public static void connectToServer() {
        //ctrlConfig.txtMessage.setTextFill(Color.BLACK);
        ctrlConfig.txtMessage.setText("Connecting ...");

        pauseDuring(1500, () -> { // Give time to show connecting message ...
            String protocol = "wss";
            String host = ctrlConfig.textUrl.getText();
            String port = "443";
            wsClient = UtilsWS.getSharedInstance(protocol + "://" + host + ":" + port);

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
        });
    }

    private static void wsMessage(String response) {
        JSONObject msgObj = new JSONObject(response);
        switch (msgObj.getString("type")) {
            case "client":
                // Procesar mensaje de tipo "client"
                ctrlConfig.txtMessage.setText("Mensaje recibido del servidor: " + response);
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
            pauseDuring(1500, () -> ctrlConfig.txtMessage.setText(""));
        }
    }

    public static void pauseDuring(long milliseconds, Runnable action) {
        PauseTransition pause = new PauseTransition(Duration.millis(milliseconds));
        pause.setOnFinished(event -> Platform.runLater(action));
    }
}