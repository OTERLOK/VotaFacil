package com.mycompany.votafacil.ui;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import com.mycompany.votafacil.model.ResultadoBusqueda;
import com.mycompany.votafacil.service.ServicioVotacion;

public class BusquedaScene {

    public static Parent crearContenido() {
        Label lblTitulo = new Label("Búsqueda Global");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextField txtBusqueda = new TextField();
        txtBusqueda.setPromptText("Escriba el término a buscar...");
        Button btnBuscar = new Button("Buscar");
        Button btnVolver = new Button("Volver al menú");

        TableView<ResultadoBusqueda> tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ResultadoBusqueda, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        TableColumn<ResultadoBusqueda, String> colDatos = new TableColumn<>("Datos");
        colDatos.setCellValueFactory(new PropertyValueFactory<>("datos"));
        colDatos.setPrefWidth(620);
        tabla.getColumns().addAll(colTipo, colDatos);

        btnBuscar.setOnAction(e -> {
            ArrayList<ResultadoBusqueda> resultados = ServicioVotacion.buscarGlobal(txtBusqueda.getText());
            tabla.setItems(FXCollections.observableArrayList(resultados));
        });
        btnVolver.setOnAction(e -> MenuAdminScene.mostrarBienvenida());

        HBox filaBusqueda = new HBox(10);
        filaBusqueda.getChildren().addAll(txtBusqueda, btnBuscar, btnVolver);

        VBox root = new VBox(10);
        root.getChildren().addAll(lblTitulo, filaBusqueda, tabla);

        return root;
    }

    public static void mostrar() {
        Parent contenido = crearContenido();
        Navegacion.cambiarEscena(new Scene(contenido, 850, 520));
    }
}
