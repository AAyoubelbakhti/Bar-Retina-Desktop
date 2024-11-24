package com.project;

import java.math.BigDecimal;
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
import org.json.JSONException;
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
    private Label labelPrecioComanda;

    @FXML
    private ListView<VBox> listViewProductos;

    @FXML
    private Button botonVolver;

    private JSONArray productes;

    private Integer idTaula;
    private Integer idCambrer;
    private String estatComanda;
    private double preuComanda;

    public void initialize(URL location, ResourceBundle resources) {
        // Configurar el botón para volver a la vista de comandas
        botonVolver.setOnAction(event -> volverAVistaComandas());
    }

    public void mostrarDatosComanda(int idComanda, int idTaula, int idCamarer, String estadoComanda, double precioComanda) {
        productes = null;
        labelIdComanda.setText("ID Comanda: " + idComanda);
        labelIdTaula.setText("ID Mesa: " + idTaula);
        this.idTaula = idTaula;
        labelIdCamarer.setText("ID Camarero: " + idCamarer);
        this.idCambrer = idCamarer;
        labelEstadoComanda.setText("Estado: " + estadoComanda);
        this.estatComanda = estadoComanda;
        labelPrecioComanda.setText("Precio: " + precioComanda);
        this.preuComanda = precioComanda;
    }

    public void cargarProductos(JSONArray jsonArray) {

        if (productes == null) {
            productes = jsonArray;
        }
        // Crear una lista observable para los VBoxes
        ObservableList<VBox> productos = FXCollections.observableArrayList();

        // Recorrer el JSONArray
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                // Obtener el producto actual
                JSONObject producto = jsonArray.getJSONObject(i);

                // Validar que el producto tiene las claves necesarias
                if (!producto.has("nom") || !producto.has("quantitat") || !producto.has("preu")
                        || !producto.has("descripcio")) {
                    System.out.println("Producto con claves faltantes: " + producto.toString());
                    continue; // Saltar a la siguiente entrada
                }

                // Extraer los datos del producto
                String nombre = producto.getString("nom");
                int cantidad = producto.getInt("quantitat");
                double precio = producto.getDouble("preu");
                String descripcion = producto.getString("descripcio");

                // Si el producto no tiene estado, agregarlo con un valor predeterminado
                if (!producto.has("estat_producte")) {
                    producto.put("estat_producte", "pendent");
                }

                // Crear un VBox para representar el producto
                VBox vboxProducto = new VBox(5); // Espaciado de 5px
                vboxProducto.setStyle(
                        "-fx-padding: 10px; -fx-border-color: #a3a3a3; -fx-border-width: 1px; -fx-background-color: #f9f9f9;");

                // Crear los Labels con la información del producto
                Label labelNombre = new Label("Producto: " + nombre);
                Label labelCantidad = new Label("Cantidad: " + cantidad);
                Label labelPrecio = new Label("Precio: " + String.format("%.2f €", precio));
                Label labelDescripcion = new Label("Descripción: " + descripcion);

                // Mostrar el estado actual del producto
                Label labelEstado = new Label("Estado: " + producto.getString("estat_producte"));

                // Crear botones
                Button btnEnCurso = new Button("En curso");
                Button btnListo = new Button("Listo");
                Button btnPagado = new Button("Pagado");

                // Contenedor para los botones
                HBox hboxBotones = new HBox(10, btnEnCurso, btnListo, btnPagado); // Espaciado de 10px

                // Añadir eventos a los botones
                btnEnCurso.setOnAction(e -> {
                    actualizarEstadoProducto(producto, "en curs", labelEstado);
                });
                btnListo.setOnAction(e -> {
                    actualizarEstadoProducto(producto, "llest", labelEstado);
                });
                btnPagado.setOnAction(e -> {
                    actualizarEstadoProducto(producto, "pagat", labelEstado);
                });

                // Añadir los elementos al VBox
                vboxProducto.getChildren().addAll(labelNombre, labelCantidad, labelPrecio, labelDescripcion,
                        labelEstado, hboxBotones);

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

    private void actualizarEstadoProducto(JSONObject producto, String nuevoEstado, Label labelEstado) {
        try {
            String estadoActual = producto.getString("estat_producte");
            int cantidadActual = producto.getInt("quantitat");

            // Validar que hay suficientes productos
            if (cantidadActual <= 0) {
                System.out.println("No hay suficientes productos para mover al nuevo estado.");
                return;
            }

            producto.put("quantitat", cantidadActual - 1);

            boolean eliminar = cantidadActual - 1 <= 0;
            boolean estadoEncontrado = false;

            // Iterar sobre los productos
            for (int i = productes.length() - 1; i >= 0; i--) {
                JSONObject prod = productes.getJSONObject(i);
                if (prod.getString("estat_producte").equals(nuevoEstado) &&
                        prod.getString("nom").equals(producto.getString("nom"))) {
                    prod.put("quantitat", prod.getInt("quantitat") + 1);
                    estadoEncontrado = true;
                    if (!eliminar) {
                        break;
                    }
                }
                if (eliminar && prod.getString("estat_producte").equals(estadoActual) &&
                        prod.getString("nom").equals(producto.getString("nom"))) {
                    productes.remove(i);
                    eliminar = false;
                    if (estadoEncontrado) {
                        break;
                    }
                }
            }

            // Crear un nuevo producto si no se encontró el estado
            if (!estadoEncontrado) {
                JSONObject nuevoProducto = new JSONObject(producto.toString());
                nuevoProducto.put("estat_producte", nuevoEstado);
                nuevoProducto.put("quantitat", 1);
                productes.put(nuevoProducto);
            }

            cargarProductos(productes);
            System.out.println("Producto actualizado: " + producto.toString());
            actualizarComanda(productes);

        } catch (JSONException e) {
            System.out.println("Error actualizando estado: " + e.getMessage());
        }
    }

    private void actualizarComanda(JSONArray jsonProductes) {
        System.out.println(jsonProductes);
        JSONObject comandasJson = new JSONObject();
        try {
            comandasJson.put("idTaula", idTaula);
            comandasJson.put("idCambrer", idCambrer);
            comandasJson.put("estatComanda", estatComanda);
            comandasJson.put("preuComanda", preuComanda);
            comandasJson.put("comandaTxt", jsonProductes);

        } catch (JSONException e) {
            System.out.println("Me duele es escroto");
            throw new RuntimeException(e);
        }
        JSONObject message = new JSONObject();
        message.put("type", "update-comanda");
        message.put("body", comandasJson);

        Main.sendMessageToServer(message.toString());
    }

    private void volverAVistaComandas() {
        UtilsViews.setViewAnimating("ViewComandes");
    }
}
