package com.project;

import java.net.URL;
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

public class CtrlProductes implements Initializable {

    @FXML
    private Button botonProductos;

    @FXML
    private TextField textFieldTag;

    @FXML
    private Button botonTag;

    @FXML
    private ScrollPane scrollProductos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        botonProductos.setOnAction(event -> Main.sendMessageToServer("productes"));
        botonTag.setOnAction(event -> buttonTags());
    }

    public void buttonTags() {
        System.out.println("Se ha pulsado el botón de tags");
        if (!textFieldTag.getText().equals("")){
            System.out.println("El tag es válido");
            Main.sendMessageToServer(textFieldTag.getText());
        } else {
            System.out.println("El tag no es válido");
        }
    }

    public void cargarProductos(String jsonString) {
        // Parseamos el JSON
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray productsArray = new JSONArray(jsonObject.getString("products"));

        // Obtenemos el contenedor dentro del ScrollPane, suponiendo que sea un VBox
        VBox productsContainer = new VBox(10); // Espaciado entre productos
        scrollProductos.setContent(productsContainer);

        // Iteramos cada producto y creamos una vista para cada uno
        for (int i = 0; i < productsArray.length(); i++) {
            JSONObject product = productsArray.getJSONObject(i);

            // Extraemos los datos del producto
            String name = product.getString("nom");
            String description = product.getString("descripcio");
            String price = product.getString("preu");

            // Creamos una vista para el producto
            VBox productBox = new VBox(5);
            Label nameLabel = new Label("Nombre: " + name);
            Label descriptionLabel = new Label("Descripción: " + description);
            Label priceLabel = new Label("Precio: " + price + " €");

            productBox.getChildren().addAll(nameLabel, descriptionLabel, priceLabel);

            // Añadimos el producto al contenedor de productos
            productsContainer.getChildren().add(productBox);
        }
    }

}
