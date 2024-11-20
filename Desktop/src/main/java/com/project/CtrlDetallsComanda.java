package com.project;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;



public class CtrlDetallsComanda implements Initializable {

    @FXML
    private Label labelIdComanda;

    @FXML
    private Label labelIdTaula;

    @FXML
    private Label labelIdCamarer;

    @FXML
    private Label labelEstadoComanda;

    @FXML
    private ListView<VBox> listViewProductos;

    @FXML
    private Button botonVolver;

    public void initialize(URL location, ResourceBundle resources) {
        // Configurar el botón para volver a la vista de comandas
        botonVolver.setOnAction(event -> volverAVistaComandas());
    }

    public void mostrarDatosComanda(int idComanda, int idTaula, int idCamarer, String estadoComanda) {
        labelIdComanda.setText("ID Comanda: " + idComanda);
        labelIdTaula.setText("ID Mesa: " + idTaula);
        labelIdCamarer.setText("ID Camarero: " + idCamarer);
        labelEstadoComanda.setText("Estado: " + estadoComanda);
    }
    
    public void cargarProductos(JSONArray jsonArray) {
        // Crear una lista observable para los VBoxes
        ObservableList<VBox> productos = FXCollections.observableArrayList();

        // Recorrer el JSONArray
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                // Obtener el producto actual
                JSONObject producto = jsonArray.getJSONObject(i);

                // Validar que el producto tiene las claves necesarias
                if (!producto.has("nom") || !producto.has("quantitat") || !producto.has("preu") || !producto.has("descripcio")) {
                    System.out.println("Producto con claves faltantes: " + producto.toString());
                    continue; // Saltar a la siguiente entrada
                }

                // Extraer los datos del producto
                String nombre = producto.getString("nom");
                int cantidad = producto.getInt("quantitat");
                double precio = producto.getDouble("preu");
                String descripcion = producto.getString("descripcio");

                // Crear un VBox para representar el producto
                VBox vboxProducto = new VBox(5); // Espaciado de 5px
                vboxProducto.setStyle("-fx-padding: 10px; -fx-border-color: #a3a3a3; -fx-border-width: 1px; -fx-background-color: #f9f9f9;");

                // Crear los Labels con la información del producto
                Label labelNombre = new Label("Producto: " + nombre);
                Label labelCantidad = new Label("Cantidad: " + cantidad);
                Label labelPrecio = new Label("Precio: " + String.format("%.2f €", precio));
                Label labelDescripcion = new Label("Descripción: " + descripcion);

                // Crear botones
                Button btnEnCurso = new Button("En curso");
                Button btnListo = new Button("Listo");
                Button btnPagado = new Button("Pagado");

                // Contenedor para los botones
                HBox hboxBotones = new HBox(10, btnEnCurso, btnListo, btnPagado); // Espaciado de 10px

                // Añadir eventos a los botones
                btnEnCurso.setOnAction(e -> System.out.println(nombre + " marcado como 'En curso'"));
                btnListo.setOnAction(e -> System.out.println(nombre + " marcado como 'Listo'"));
                btnPagado.setOnAction(e -> System.out.println(nombre + " marcado como 'Pagado'"));

                // Añadir los elementos al VBox
                vboxProducto.getChildren().addAll(labelNombre, labelCantidad, labelPrecio, labelDescripcion, hboxBotones);

                // Añadir el VBox a la lista observable
                productos.add(vboxProducto);

            } catch (Exception e) {
                System.out.println("Error procesando producto: " + e.getMessage());
                e.printStackTrace(); // Para depuración
            }
        }

        // Asignar la lista observable al ListView
        listViewProductos.setItems(productos);
    }


    private void volverAVistaComandas() {
        UtilsViews.setViewAnimating("ViewComandes");
    }
}
