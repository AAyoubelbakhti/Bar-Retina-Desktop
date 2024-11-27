package com.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    public static UtilsWS wsClient;
    public static CtrlConfig ctrlConfig;
    public static CtrlProductes ctrlProductes;
    public static CtrlMesVenuts ctrlMesVenuts;
    public static CtrlComandes ctrlComandes;
    public static CtrlDetallsComanda ctrlDetallsComanda;
    public static CtrlMesas ctrlMesas;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            final int windowWidth = 850;
            final int windowHeight = 600;

            UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");
            UtilsViews.addView(getClass(), "ViewConfig", "/assets/viewConfig.fxml");
            UtilsViews.addView(getClass(), "ViewProductes", "/assets/viewProductes.fxml");
            UtilsViews.addView(getClass(), "ViewMesVenuts", "/assets/viewMesVenuts.fxml");
            UtilsViews.addView(getClass(), "ViewComandes", "/assets/viewComandes.fxml");
            UtilsViews.addView(getClass(), "ViewDetallsComanda", "/assets/viewDetallsComanda.fxml");
            UtilsViews.addView(getClass(), "ViewTaules", "/assets/viewTaules.fxml");

            ctrlConfig = (CtrlConfig) UtilsViews.getController("ViewConfig");
            ctrlProductes = (CtrlProductes) UtilsViews.getController("ViewProductes");
            ctrlMesVenuts = (CtrlMesVenuts) UtilsViews.getController("ViewMesVenuts");

            ctrlComandes = (CtrlComandes) UtilsViews.getController("ViewComandes");
            ctrlDetallsComanda = (CtrlDetallsComanda) UtilsViews.getController("ViewDetallsComanda");
            ctrlMesas = (CtrlMesas) UtilsViews.getController("ViewTaules");

            Scene scene = new Scene(UtilsViews.parentContainer);
            stage.setScene(scene);
            stage.onCloseRequestProperty();
            stage.setTitle("Bar Retina Desktop App");
            stage.setMinWidth(windowWidth);
            stage.setMinHeight(windowHeight);
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    @Override
    public void stop() {
        System.exit(1);
    }

    public static boolean existeArchivoConfig() {
        try {
            File archivo = new File("configuracion.xml");
            return archivo.exists();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String cargarDatosDesdeXml() {
        try {
            File archivo = new File("configuracion.xml");
            if (!archivo.exists()) {
                return "barretina1.ieti.site";
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);

            doc.getDocumentElement().normalize();
            String url = doc.getElementsByTagName("url").item(0).getTextContent();

            return url;

        } catch (Exception e) {
            e.printStackTrace();
            return "barretina1.ieti.site";
        }
    }

    public static void connectToServer() {
        String host = "";
        if(!existeArchivoConfig()){
            ctrlConfig.txtMessage.setText("Connecting ...");
            host = ctrlConfig.textUrl.getText();
        }else{
            host = cargarDatosDesdeXml();
        }
        String protocol = "wss";
        String port = "443";
        wsClient = UtilsWS.getSharedInstance(protocol + "://" + host + ":" + port);
        // String wsLocation = "ws://localhost:4545";
        // String wsLocation = "wss://barretina1.ieti.site:443";
        // wsClient = UtilsWS.getSharedInstance(wsLocation);

        wsClient.onOpen(message -> {
            Platform.runLater(() -> {
                UtilsViews.setViewAnimating("ViewComandes");
                JSONObject context = new JSONObject();
                context.put("type", "select-comanda");
                Main.wsClient.safeSend(context.toString());
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
                //System.out.println(msgObj.toString());

                ctrlProductes.cargarProductos(msgObj.toString());
                break;
            case "top-productes":
                //System.out.println(msgObj.toString());
                ctrlMesVenuts.cargarProductos(msgObj.toString());
                break;
            case "comandes":
                System.out.println("Se han recibido las comandas!");
                JSONArray comandes = new JSONArray(msgObj.getString("body"));
                System.out.println(comandes.toString());
                ctrlComandes.llenarListasDesdeJSONArray(comandes);
                break;

            case "comanda-taula":
                // System.out.println("hola2");
                System.out.println(msgObj);
                JSONObject comanda  = new JSONObject(msgObj.getString("body"));
                // System.out.println("hola3");
                UtilsViews.setViewAnimating("ViewDetallsComanda");
                // System.out.println("hola4");
                ctrlDetallsComanda.mostrarDatosComanda(comanda.getInt("id_comanda"), comanda.getInt("id_taula"), comanda.getInt("id_cambrer"), comanda.getString("estat_comanda"), comanda.getDouble("preu_comanda"));
                // System.out.println("hola5");
                ctrlDetallsComanda.cargarProductos(new JSONArray(comanda.getString("comanda")));
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

    public static void sendMessageToServer(String message) {
        System.out.println("Se ha enviado el mensaje al servidor");
        System.out.println(message);
        wsClient.safeSend(message);
        // System.out.println("Hola");
    }
}