package com.project;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CtrlConfig implements Initializable {

    @FXML
    private TextField textUbicacion;

    @FXML
    public TextField textUrl;

    @FXML
    private Button buttonConectar;

    @FXML
    public Label txtMessage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(existeArchivoConfig()){
            txtMessage.setText("Ya existe el archivo!");
            buttonConectar.setDisable(true);
        }else{
            buttonConectar.setOnAction(event -> connect());
        }

        
    }

    public boolean existeArchivoConfig() {
        File archivo = new File("configuracion.xml");
        return archivo.exists();
    }

    public void guardarDatosEnXml() {
        try {
            // Crear el documento XML
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Elemento raíz
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("configuracion");
            doc.appendChild(rootElement);

            // Crear el elemento ubicación
            Element ubicacion = doc.createElement("ubicacion");
            ubicacion.appendChild(doc.createTextNode(textUbicacion.getText()));
            rootElement.appendChild(ubicacion);

            // Crear el elemento URL
            Element url = doc.createElement("url");
            url.appendChild(doc.createTextNode(textUrl.getText()));
            rootElement.appendChild(url);

            // Escribir el contenido en un archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("configuracion.xml"));

            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
            txtMessage.setText("Error al guardar los datos");
        }
    }

    private boolean connect() {

        if (!textUbicacion.getText().equals("") && !textUrl.getText().equals("")) {
            try{
                Main.connectToServer();
                guardarDatosEnXml();
                return true;
            }catch(Exception e){
                System.out.println("Error !!!!!!!!");
            }
            
        }

        return false;
    }

}
