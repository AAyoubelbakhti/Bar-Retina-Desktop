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
        // Parseamos el JSON
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray productsArray = new JSONArray(jsonObject.getString("products"));
        JSONObject imagesJson = new  JSONObject(jsonObject.getString("imatges"));


        // Obtenemos el contenedor dentro del ScrollPane, suponiendo que sea un VBox
        VBox productsContainer = new VBox(10);
        scrollProductos.setContent(productsContainer);

        // Iteramos cada producto y creamos una vista para cada uno
        for (int i = 0; i < productsArray.length(); i++) {
            JSONObject product = productsArray.getJSONObject(i);

            // Extraemos los datos del producto
            String name = product.getString("nom");
            String description = product.getString("descripcio");
            String price = product.getString("preu");
            String imageName= product.getString("imatge");
            String imageBase64 = imagesJson.getString(imageName);
            

            // Decodificar la imagen de Base64 a bytes y crear el objeto Image
            ImageView imageView = new ImageView();
            try {
                byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
                Image image = new Image(new ByteArrayInputStream(imageBytes));
                imageView.setImage(image);
                imageView.setFitWidth(100); // Ajusta el tamaño de la imagen según necesites
                imageView.setPreserveRatio(true);
            } catch (IllegalArgumentException e) {
                System.out.println("Error al decodificar la imagen Base64: " + e.getMessage());
            }

            // Creamos una vista para el producto
            VBox productBox = new VBox(5);
            Label nameLabel = new Label("Nombre: " + name);
            Label descriptionLabel = new Label("Descripción: " + description);
            Label priceLabel = new Label("Precio: " + price + " €");

            productBox.getChildren().addAll(nameLabel, descriptionLabel, priceLabel, imageView);

            // Añadimos el producto al contenedor de productos
            productsContainer.getChildren().add(productBox);
        }
    }

}
