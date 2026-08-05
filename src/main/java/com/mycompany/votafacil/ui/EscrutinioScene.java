package com.mycompany.votafacil.ui;

import java.io.File;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import com.mycompany.votafacil.data.CsvExporter;
import com.mycompany.votafacil.data.DatosVotacion;
import com.mycompany.votafacil.exception.ExportacionFallidaException;
import com.mycompany.votafacil.model.Candidato;
import com.mycompany.votafacil.model.ResultadoEscrutinio;
import com.mycompany.votafacil.service.ServicioVotacion;

public class EscrutinioScene {

    public static Parent crearContenido() {
        Label lblTitulo = new Label("Escrutinio de la votación");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TableView<ResultadoEscrutinio> tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ResultadoEscrutinio, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        TableColumn<ResultadoEscrutinio, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<ResultadoEscrutinio, String> colPartido = new TableColumn<>("Partido");
        colPartido.setCellValueFactory(new PropertyValueFactory<>("partido"));
        TableColumn<ResultadoEscrutinio, String> colVotos = new TableColumn<>("Votos");
        colVotos.setCellValueFactory(new PropertyValueFactory<>("votos"));
        TableColumn<ResultadoEscrutinio, String> colPorcentaje = new TableColumn<>("Porcentaje");
        colPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
        tabla.getColumns().addAll(colCodigo, colNombre, colPartido, colVotos, colPorcentaje);

        Label lblResumen = new Label();

        Button btnActualizar = new Button("Actualizar");
        Button btnExportar = new Button("Exportar a CSV");
        Button btnVolver = new Button("Volver al menú");

        btnActualizar.setOnAction(e -> {
            DatosVotacion.cargar();
            actualizarTabla(tabla, lblResumen);
            Dialogos.informacion("Datos Actualizados.");
        });
        btnExportar.setOnAction(e -> exportar());
        btnVolver.setOnAction(e -> MenuAdminScene.mostrarBienvenida());

        HBox filaBotones = new HBox(10);
        filaBotones.getChildren().addAll(btnActualizar, btnExportar, btnVolver);
        filaBotones.setAlignment(Pos.CENTER);

        actualizarTabla(tabla, lblResumen);

        VBox root = new VBox(10);
        root.getChildren().addAll(lblTitulo, tabla, lblResumen, filaBotones);

        return root;
    }

    private static void actualizarTabla(TableView<ResultadoEscrutinio> tabla, Label lblResumen) {
        ArrayList<ResultadoEscrutinio> filas = new ArrayList<>();
        for (Candidato c : ServicioVotacion.candidatosPorVotosDesc()) {
            int votos = ServicioVotacion.contarVotos(c.getCodigo());
            filas.add(new ResultadoEscrutinio(c.getCodigo(), c.getNombre(), c.getPartido(),
                    String.valueOf(votos), String.format("%.1f%%", ServicioVotacion.porcentajeVotos(votos))));
        }
        tabla.setItems(FXCollections.observableArrayList(filas));

        lblResumen.setText("Total de votos emitidos: " + ServicioVotacion.totalVotosEmitidos()
                + "   |   Total de electores habilitados: " + ServicioVotacion.totalElectoresHabilitados()
                + "   |   Porcentaje de participación: "
                + String.format("%.1f%%", ServicioVotacion.porcentajeParticipacion()));
    }

    public static void mostrar() {
        Parent contenido = crearContenido();
        Navegacion.cambiarEscena(new Scene(contenido, 800, 540));
    }

    private static void exportar() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Exportar escrutinio a CSV");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File archivo = selector.showSaveDialog(Navegacion.stage);
        if (archivo == null) {
            return;
        }
        try {
            CsvExporter.exportar(archivo, CsvExporter.CABECERAS_ESCRUTINIO,
                    ServicioVotacion.filasEscrutinioCSV());
            Dialogos.informacion("Archivo CSV exportado correctamente: " + archivo.getAbsolutePath());
        } catch (ExportacionFallidaException ex) {
            Dialogos.error(ex.getMessage());
        }
    }
}
