package com.mycompany.votafacil.ui;

import java.io.File;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import com.mycompany.votafacil.data.CsvExporter;
import com.mycompany.votafacil.exception.ExportacionFallidaException;
import com.mycompany.votafacil.service.ServicioVotacion;

/**
 * Pantalla de exportación de datos. Permite seleccionar qué datos exportar
 * (candidatos, electores o resultados del escrutinio) y generar el archivo
 * CSV correspondiente.
 */
public class ExportacionScene {

    public static void mostrar() {
        Label lblTitulo = new Label("Exportar Datos a CSV");

        ComboBox<String> combo = new ComboBox<>(FXCollections.observableArrayList(
                "Candidatos", "Electores", "Resultados del escrutinio"));
        combo.getSelectionModel().selectFirst();

        Button btnExportar = new Button("Exportar CSV");
        Button btnVolver = new Button("Volver");

        btnExportar.setOnAction(e -> exportar(combo.getValue()));
        btnVolver.setOnAction(e -> MenuAdminScene.mostrar());

        HBox filaBotones = new HBox(10);
        filaBotones.getChildren().addAll(btnExportar, btnVolver);
        filaBotones.setAlignment(Pos.CENTER);

        VBox root = new VBox(15);
        root.getChildren().addAll(lblTitulo, new Label("Seleccione los datos a exportar:"), combo, filaBotones);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Navegacion.cambiarEscena(new Scene(root, 420, 260));
    }

    private static void exportar(String tipo) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Exportar datos a CSV");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File archivo = selector.showSaveDialog(Navegacion.stage);
        if (archivo == null) {
            return;
        }
        try {
            String[] cabeceras;
            ArrayList<String[]> filas;
            if (tipo.equals("Candidatos")) {
                cabeceras = CsvExporter.CABECERAS_CANDIDATOS;
                filas = ServicioVotacion.filasCandidatosCSV();
            } else if (tipo.equals("Electores")) {
                cabeceras = CsvExporter.CABECERAS_ELECTORES;
                filas = ServicioVotacion.filasElectoresCSV();
            } else {
                cabeceras = CsvExporter.CABECERAS_ESCRUTINIO;
                filas = ServicioVotacion.filasEscrutinioCSV();
            }
            CsvExporter.exportar(archivo, cabeceras, filas);
            Dialogos.informacion("Archivo CSV exportado correctamente: " + archivo.getAbsolutePath());
        } catch (ExportacionFallidaException ex) {
            Dialogos.error(ex.getMessage());
        }
    }
}
