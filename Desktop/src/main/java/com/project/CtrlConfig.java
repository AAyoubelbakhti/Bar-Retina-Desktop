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
        if (existeArchivoConfig()) {
            if (cargarDatosDesdeXml()) {
                if (connect()) {
                    txtMessage.setText("Conexión exitosa con la configuración existente.");
                    buttonConectar.setDisable(true);
                } else {
                    txtMessage.setText("Error al conectar con la configuración existente.");
                }
            } else {
                txtMessage.setText("Error al cargar el archivo de configuración.");
            }
        } else {
            txtMessage.setText("No se encontró el archivo de configuración.");
            buttonConectar.setOnAction(event -> connect());
        }
    }

    public boolean cargarDatosDesdeXml() {
        try {
            File archivo = new File("configuracion.xml");
            if (!archivo.exists()) {
                return false;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);

            doc.getDocumentElement().normalize();

            String ubicacion = doc.getElementsByTagName("ubicacion").item(0).getTextContent();
            String url = doc.getElementsByTagName("url").item(0).getTextContent();

            textUbicacion.setText(ubicacion);
            textUrl.setText(url);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeArchivoConfig() {
        try {
            File archivo = new File("configuracion.xml");
            return archivo.exists();
        } catch (Exception e) {
            e.printStackTrace();
            txtMessage.setText("Error al verificar el archivo de configuración.");
            return false;
        }
    }

    public void guardarDatosEnXml() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("configuracion");
            doc.appendChild(rootElement);

            Element ubicacion = doc.createElement("ubicacion");
            ubicacion.appendChild(doc.createTextNode(textUbicacion.getText()));
            rootElement.appendChild(ubicacion);

            Element url = doc.createElement("url");
            url.appendChild(doc.createTextNode(textUrl.getText()));
            rootElement.appendChild(url);

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
            try {
                Main.connectToServer();
                if (!existeArchivoConfig()) {
                    guardarDatosEnXml();
                }
                return true;
            } catch (Exception e) {
                System.out.println("Error: "+ e);
            }
        }

        return false;
    }

}
