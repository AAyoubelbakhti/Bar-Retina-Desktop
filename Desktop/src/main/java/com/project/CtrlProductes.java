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
import javafx.scene.layout.HBox;

public class CtrlProductes implements Initializable {

    @FXML
    private Button botonProductos;

    @FXML
    private TextField textFieldTag;

    @FXML
    private Button botonTag;

    @FXML
    private Button botonPedidos;

    @FXML
    private ScrollPane scrollProductos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Aplicar estilos consistentes
        botonProductos.setStyle("-fx-background-color: #B22222; -fx-text-fill: #FFFFFF; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 8;");
        botonTag.setStyle("-fx-background-color: #B22222; -fx-text-fill: #FFFFFF; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 8;");
        botonPedidos.setStyle("-fx-background-color: #B22222; -fx-text-fill: #FFFFFF; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 8;");
        textFieldTag.setStyle("-fx-padding: 8; -fx-border-color: #CCCCCC; -fx-border-radius: 5;");
        scrollProductos.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-padding: 8;");

        botonProductos.setOnAction(event -> {
            JSONObject message = new JSONObject();
            message.put("type", "productes");

            Main.sendMessageToServer(message.toString());
        });

        botonTag.setOnAction(event -> buttonTags());

        botonPedidos.setOnAction(event -> UtilsViews.setViewAnimating("ViewComandes"));
    }

    public void buttonTags() {
        System.out.println("Se ha pulsado el botón de tags");
        if (!textFieldTag.getText().equals("")) {
            JSONObject message = new JSONObject();
            message.put("type", "tags");
            System.out.println("El tag es válido");

            message.put("body", textFieldTag.getText());
            Main.sendMessageToServer(message.toString());
        } else {
            System.out.println("El tag no es válido");
        }
    }

    public void cargarProductos(String jsonString) {
        try {
            // Parseamos el JSON
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray productsArray = new JSONArray(jsonObject.getString("products"));
            JSONObject imagesJson = new JSONObject(jsonObject.getString("imatges"));

            // Creamos un VBox para el contenedor de productos
            VBox productsContainer = new VBox(10);
            productsContainer.setStyle("-fx-padding: 10; -fx-background-color: #F5F5F5;");

            scrollProductos.setContent(productsContainer);

            // Iteramos cada producto y creamos una vista para cada uno
            for (int i = 0; i < productsArray.length(); i++) {
                JSONObject product = productsArray.getJSONObject(i);

                // Extraemos los datos del producto
                String name = product.getString("nom");
                String description = product.getString("descripcio");
                String price = product.getString("preu");
                String imageName = product.getString("imatge");
                String imageBase64 = imagesJson.getString(imageName);

                // Decodificar la imagen
                ImageView imageView = new ImageView();
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    imageView.setImage(image);
                    imageView.setFitWidth(100);
                    imageView.setPreserveRatio(true);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error al decodificar la imagen Base64: " + e.getMessage());
                }

                // Creamos una vista para el producto
                VBox productBox = new VBox(5);
                productBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 0);");

                Label nameLabel = new Label("Nombre: " + name);
                nameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

                Label descriptionLabel = new Label("Descripción: " + description);
                descriptionLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");

                Label priceLabel = new Label("Precio: " + price + " €");
                priceLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #B22222; -fx-font-weight: bold;");

                productBox.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel);

                // Añadimos el producto al contenedor de productos
                productsContainer.getChildren().add(productBox);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
