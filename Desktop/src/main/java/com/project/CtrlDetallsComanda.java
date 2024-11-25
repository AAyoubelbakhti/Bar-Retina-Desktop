package com.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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

    @FXML
    private Button botonPagarSeleccionados;

    @FXML
    private Button botonPagarTodo;

    private JSONArray productes;
    private List<JSONObject> productosSeleccionados = new ArrayList<>();

    private Integer idTaula;
    private Integer idCambrer;
    private String estatComanda;
    private double preuComanda;
    
    @FXML
    private Label labelTotalSeleccionado;

    private double totalSeleccionado = 0.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        botonVolver.setOnAction(event -> volverAVistaComandas());
        botonPagarSeleccionados.setOnAction(event -> pagarSeleccionados());
        botonPagarTodo.setOnAction(event -> pagarTodo());
        actualizarLabelTotalSeleccionado();
    }

    public void cargarProductos(JSONArray jsonArray) {
        if (productes == null) {
            productes = jsonArray;
        }
    
        ObservableList<VBox> productos = FXCollections.observableArrayList();
    
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject producto = jsonArray.getJSONObject(i);
    
                if (!producto.has("nom") || !producto.has("quantitat") || !producto.has("preu") || !producto.has("descripcio")) {
                    System.out.println("Producto con claves faltantes: " + producto.toString());
                    continue;
                }
    
                String nombre = producto.getString("nom");
                int cantidad = producto.getInt("quantitat");
                double precio = producto.getDouble("preu");
                String descripcion = producto.getString("descripcio");
    
                if (!producto.has("estat_producte")) {
                    producto.put("estat_producte", "pendent");
                }
    
                VBox vboxProducto = new VBox(5);
                vboxProducto.setStyle("-fx-padding: 10px; -fx-border-color: #a3a3a3; -fx-border-width: 1px; -fx-background-color: #f9f9f9;");
    
                Label labelNombre = new Label("Producto: " + nombre);
                Label labelCantidad = new Label("Cantidad: " + cantidad);
                Label labelPrecio = new Label("Precio unitario: " + String.format("%.2f €", precio));
                Label labelDescripcion = new Label("Descripción: " + descripcion);
                Label labelEstado = new Label("Estado: " + producto.getString("estat_producte"));
    
                // Spinner para seleccionar cantidad
                Spinner<Integer> spinnerCantidad = new Spinner<>();
                SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, cantidad, 0);
                spinnerCantidad.setValueFactory(valueFactory);
                spinnerCantidad.setEditable(false);
    
                HBox hboxCantidad = new HBox(10, new Label("Seleccionar cantidad:"), spinnerCantidad);
    
                // Botones de estado
                Button btnPendiente = new Button("Pendiente");
                Button btnEnCurso = new Button("En curso");
                Button btnListo = new Button("Listo");
                
                btnPendiente.setOnAction(e -> actualizarEstadoProducto(producto, "pendent", labelEstado));
                btnEnCurso.setOnAction(e -> actualizarEstadoProducto(producto, "en curs", labelEstado));
                btnListo.setOnAction(e -> actualizarEstadoProducto(producto, "llest", labelEstado));
    
                HBox hboxBotones = new HBox(10, btnPendiente, btnEnCurso, btnListo);
    
                spinnerCantidad.valueProperty().addListener((obs, oldValue, newValue) -> {
                    if (newValue > 0) {
                        boolean encontrado = false;
                        // Revisar si el producto ya está en productosSeleccionados
                        for (JSONObject productoSeleccionado : productosSeleccionados) {
                            try {
                                if (productoSeleccionado.getString("nom").equals(producto.getString("nom"))) {
                                    // Actualizamos la cantidad seleccionada
                                    totalSeleccionado += (newValue - (oldValue != null ? oldValue : 0)) * precio;
                                    productoSeleccionado.put("quantitat", newValue);
                                    encontrado = true;
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // Si no está, lo añadimos
                        if (!encontrado) {
                            try {
                                JSONObject productoSeleccionado = new JSONObject(producto.toString());
                                productoSeleccionado.put("quantitat", newValue);
                                productosSeleccionados.add(productoSeleccionado);
                                totalSeleccionado += newValue * precio;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        // Si la cantidad seleccionada es 0, lo eliminamos
                        productosSeleccionados.removeIf(p -> {
                            try {
                                return p.getString("nom").equals(producto.getString("nom"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return false;
                            }
                        });
                        totalSeleccionado -= oldValue * precio;
                    }
                    actualizarLabelTotalSeleccionado();
                });
    
                // Añadir elementos al VBox
                vboxProducto.getChildren().addAll(labelNombre, labelCantidad, labelPrecio, labelDescripcion, labelEstado, hboxCantidad, hboxBotones);
                productos.add(vboxProducto);
    
            } catch (Exception e) {
                System.out.println("Error procesando producto: " + e.getMessage());
                e.printStackTrace();
            }
        }
    
        listViewProductos.setItems(productos);
    }
    
    private void actualizarLabelTotalSeleccionado() {
        labelTotalSeleccionado.setText(String.format("Total Seleccionado: %.2f €", totalSeleccionado));
    }

    public void mostrarDatosComanda(int idComanda, int idTaula, int idCamarer, String estadoComanda,
            double precioComanda) {
        productes = null;
        productosSeleccionados = new ArrayList<>();
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

    private void actualizarEstadoProducto(JSONObject producto, String nuevoEstado, Label labelEstado) {
        try {
            String estadoActual = producto.getString("estat_producte");
            int cantidadActual = producto.getInt("quantitat");

            // Validar que hay suficientes productos
            if (cantidadActual <= 0) {
                System.out.println("No hay suficientes productos para mover al nuevo estado.");
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
            if (nuevoEstado.equals("llest")) {
                actualizarComanda(productes, false, producto.getString("nom"), true);
            } else {
                actualizarComanda(productes, false, "", false);
            }
        } catch (JSONException e) {
            System.out.println("Error actualizando estado: " + e.getMessage());
        }
    }

    private void pagarSeleccionados() {
        for (JSONObject productoSeleccionado : productosSeleccionados) {
            try {
                // Actualizar el estado de "pagat" en `productosSeleccionados`
                String nombre = productoSeleccionado.getString("nom");
                int cantidadSeleccionada = productoSeleccionado.getInt("quantitat");
                
                // Actualizamos la cantidad en la lista global de productos `productes`
                for (int i = 0; i < productes.length(); i++) {
                    JSONObject producto = productes.getJSONObject(i);
                    if (producto.getString("nom").equals(nombre)) {
                        int cantidadRestante = producto.getInt("quantitat") - cantidadSeleccionada;
                        producto.put("quantitat", cantidadRestante);
                        producto.put("estat_producte", "pagat");  // Cambiar el estado a "pagat"
                        break;
                    }
                }
            } catch (JSONException e) {
                System.out.println("Error actualizando estado: " + e.getMessage());
            }
        }
    
        // Limpiar la lista de productos seleccionados después de pagar
        productosSeleccionados.clear();
        totalSeleccionado = 0;
        actualizarLabelTotalSeleccionado();
        actualizarComanda(productes, false, "", false);
        cargarProductos(productes);  // Recargar los productos actualizados
    }
    
    

    private void pagarTodo() {
        for (int i = 0; i < productes.length(); i++) {
            try {
                JSONObject producto = productes.getJSONObject(i);
                producto.put("estat_producte", "pagat");
            } catch (JSONException e) {
                System.out.println("Error actualizando estado: " + e.getMessage());
            }
        }
        productosSeleccionados = new ArrayList<>();
        actualizarComanda(productes, true, "", false);
        cargarProductos(productes);
    }

    private void actualizarComanda(JSONArray jsonProductes, boolean todoPagado, String productoListo, boolean listo) {
        System.out.println(jsonProductes);
        JSONObject comandasJson = new JSONObject();
        try {
            comandasJson.put("idTaula", idTaula);
            comandasJson.put("idCambrer", idCambrer);
            comandasJson.put("estatComanda", todoPagado ? "pagat" : estatComanda);
            comandasJson.put("preuComanda", preuComanda);
            comandasJson.put("comandaTxt", jsonProductes);
            comandasJson.put("llest", listo);
            comandasJson.put("producte", productoListo);

        } catch (JSONException e) {
            System.out.println("Error al actualizar comanda: " + e.getMessage());
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
