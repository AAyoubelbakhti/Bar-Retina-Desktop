package com.project;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class CtrlMesVenuts implements Initializable {

    @FXML
    private Button botonProductos;


    @FXML
    private Button botonPedidos;

    @FXML
    private ScrollPane scrollProductos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        botonProductos.setOnAction(event -> {
            JSONObject message = new JSONObject();
            message.put("type", "top-productes");

            Main.sendMessageToServer(message.toString());
        });

     

        botonPedidos.setOnAction(event -> UtilsViews.setViewAnimating("ViewComandes"));
    }

   

    public void cargarProductos(String jsonString) {
        // Parseamos el JSON
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray productsArray = new JSONArray(jsonObject.getString("products"));
    
        // Creamos el contenedor dentro del ScrollPane
        VBox productsContainer = new VBox(10); // Espaciado vertical entre los productos
        productsContainer.setStyle("-fx-padding: 16; -fx-background-color: #FFFFFF;"); // Fondo y padding
        scrollProductos.setContent(productsContainer);
    
        // Iteramos cada producto y creamos una vista estilizada para cada uno
        for (int i = 0; i < productsArray.length(); i++) {
            JSONObject product = productsArray.getJSONObject(i);
            try {
                // Extraemos los datos del producto
                String name = product.getString("nom");
                String quantitat = String.valueOf(product.getInt("quantitat"));
    
                // Creamos un contenedor para el producto
                VBox productBox = new VBox(5); // Espaciado entre elementos del producto
                productBox.setStyle(
                    "-fx-background-color: #F5F5F5;" +
                    "-fx-border-color: #CCCCCC;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-padding: 10;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 4, 0, 0, 2);"
                );
    
                // Crear etiquetas estilizadas
                Label nameLabel = new Label(String.valueOf(i+1) + ": " + name);
                nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #333333;");
    
                Label quantityLabel = new Label("Cantidad: " + quantitat);
                quantityLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #555555;");
    
                // Añadir las etiquetas al contenedor del producto
                productBox.getChildren().addAll(nameLabel, quantityLabel);
    
                // Añadimos el producto al contenedor principal
                productsContainer.getChildren().add(productBox);
    
            } catch (Exception e) {
                System.err.println("Error procesando producto: " + e.getMessage());
            }
        }
    }
    

}
