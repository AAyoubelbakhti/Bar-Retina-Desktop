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
                System.out.println("Producto: " + producto);
                if (!producto.has("nom") || !producto.has("quantitat") || !producto.has("preu-unitat") || !producto.has("descripcio")) {
                    System.out.println("Producto con claves faltantes: " + producto.toString());
                    continue;
                }
    
                String nombre = producto.getString("nom");
                int cantidad = producto.getInt("quantitat");
                double precio = producto.getDouble("preu-unitat");
                String descripcion = producto.getString("descripcio");
    
                if (!producto.has("estat_producte")) {
                    producto.put("estat_producte", "pendent");
                }
    
                // VBox que contiene la información del producto
                VBox vboxProducto = new VBox(10); // Espaciado de 10 entre elementos
                vboxProducto.setStyle("-fx-padding: 15px; -fx-border-color: #B0B0B0; -fx-border-width: 1px; -fx-background-color: #F9F9F9; -fx-border-radius: 5px;");
    
                // Labels de información
                Label labelNombre = new Label("Producto: " + nombre);
                labelNombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
                
                Label labelCantidad = new Label("Cantidad: " + cantidad);
                labelCantidad.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
                
                Label labelPrecio = new Label("Precio unitario: " + String.format("%.2f €", precio));
                labelPrecio.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
                
                Label labelDescripcion = new Label("Descripción: " + descripcion);
                labelDescripcion.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
                
                Label labelEstado = new Label("Estado: " + producto.getString("estat_producte"));
                labelEstado.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
    
                // Spinner para seleccionar cantidad
                Spinner<Integer> spinnerCantidad = new Spinner<>();
                SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, cantidad, 0);
                spinnerCantidad.setValueFactory(valueFactory);
                spinnerCantidad.setEditable(false);
                spinnerCantidad.setStyle("-fx-background-color: #F0F0F0; -fx-font-size: 12px;");
                
                HBox hboxCantidad = new HBox(10, new Label("Seleccionar cantidad:"), spinnerCantidad);
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
                hboxCantidad.setStyle("-fx-alignment: center-left;");
    
                // Botones de estado
                Button btnPendiente = new Button("Pendiente");
                btnPendiente.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-weight: bold;");
                Button btnEnCurso = new Button("En curso");
                btnEnCurso.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white; -fx-font-weight: bold;");
                Button btnListo = new Button("Listo");
                btnListo.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white; -fx-font-weight: bold;");
                
                btnPendiente.setOnAction(e -> actualizarEstadoProducto(producto, "pendent", labelEstado));
                btnEnCurso.setOnAction(e -> actualizarEstadoProducto(producto, "en curs", labelEstado));
                btnListo.setOnAction(e -> actualizarEstadoProducto(producto, "llest", labelEstado));
    
                HBox hboxBotones = new HBox(10, btnPendiente, btnEnCurso, btnListo);
                hboxBotones.setStyle("-fx-alignment: center-left;");
    
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

        productosSeleccionados.clear();
        actualizarLabelTotalSeleccionado();
        totalSeleccionado = 0.0;
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
                String estadoActual = productoSeleccionado.getString("estat_producte");
                
                // Actualizamos la cantidad en la lista global de productos `productes`
                for (int i = 0; i < productes.length(); i++) {
                    JSONObject producto = productes.getJSONObject(i);
                    if (producto.getString("nom").equals(nombre) && producto.getString("estat_producte").equals(estadoActual)) {
                        int cantidadRestante = producto.getInt("quantitat") - cantidadSeleccionada;
                        if(cantidadRestante <= 0){
                            productes.remove(i);
                        } else{
                            producto.put("quantitat", cantidadRestante);
                        }
                        
                        JSONObject productePagat = new JSONObject(productoSeleccionado.toString());
                        productePagat.put("estat_producte", "pagat");
                        productes.put(productePagat);
                        break;
                    }
                }
            } catch (JSONException e) {
                System.out.println("Error actualizando estado: " + e.getMessage());
            }
        }
        
        productosSeleccionados.clear();
        totalSeleccionado = 0;
        actualizarLabelTotalSeleccionado();
        actualizarComanda(productes, comprobarComandaPagada(), "", false);
        cargarProductos(productes);
    }
    
    public boolean comprobarComandaPagada(){
        for(int i = 0; i <= productes.length(); i++){
            JSONObject producto = productes.getJSONObject(i);

            if(!producto.getString("estat_producte").equals("pagat")){
                return false;
            }
        }
        return true;
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
