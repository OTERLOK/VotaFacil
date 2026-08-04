package com.mycompany.votafacil.ui;

import java.io.File;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import com.mycompany.votafacil.data.CsvExporter;
import com.mycompany.votafacil.exception.CandidatoDuplicadoException;
import com.mycompany.votafacil.exception.ExportacionFallidaException;
import com.mycompany.votafacil.model.Candidato;
import com.mycompany.votafacil.service.ServicioVotacion;

/**
 * Pantalla de gestión de candidatos. Muestra todos los candidatos en una
 * tabla y permite agregar, modificar, eliminar y exportar a CSV. Los botones
 * de agregar, modificar y eliminar se deshabilitan cuando la votación está
 * abierta.
 */
public class CandidatosScene {

    private static TableView<Candidato> tabla;
    private static final TextField txtCodigo = new TextField();
    private static final TextField txtNombre = new TextField();
    private static final TextField txtPartido = new TextField();
    private static final TextField txtDescripcion = new TextField();
    private static Button btnAgregar;
    private static Button btnModificar;
    private static Button btnEliminar;

    public static void mostrar() {
        Label lblTitulo = new Label("Gestión de Candidatos");

        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Candidato, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        TableColumn<Candidato, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<Candidato, String> colPartido = new TableColumn<>("Partido");
        colPartido.setCellValueFactory(new PropertyValueFactory<>("partido"));
        TableColumn<Candidato, String> colDescripcion = new TableColumn<>("Descripción");
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        tabla.getColumns().addAll(colCodigo, colNombre, colPartido, colDescripcion);

        btnAgregar = new Button("Agregar");
        Button btnCargar = new Button("Cargar seleccionado");
        btnModificar = new Button("Modificar");
        btnEliminar = new Button("Eliminar");
        Button btnExportar = new Button("Exportar CSV");
        Button btnVolver = new Button("Volver");

        btnAgregar.setOnAction(e -> agregar());
        btnCargar.setOnAction(e -> cargarSeleccionado());
        btnModificar.setOnAction(e -> modificar());
        btnEliminar.setOnAction(e -> eliminar());
        btnExportar.setOnAction(e -> exportar());
        btnVolver.setOnAction(e -> MenuAdminScene.mostrar());

        HBox filaCampos = new HBox(8);
        filaCampos.getChildren().addAll(
                new Label("Código:"), txtCodigo,
                new Label("Nombre:"), txtNombre);
        HBox filaOtros = new HBox(8);
        filaOtros.getChildren().addAll(
                new Label("Partido:"), txtPartido,
                new Label("Descripción:"), txtDescripcion);
        HBox filaBotones = new HBox(8);
        filaBotones.getChildren().addAll(btnAgregar, btnCargar, btnModificar, btnEliminar, btnExportar, btnVolver);

        VBox root = new VBox(10);
        root.getChildren().addAll(lblTitulo, tabla, filaCampos, filaOtros, filaBotones);
        root.setPadding(new Insets(15));

        refrescarTabla();
        Navegacion.cambiarEscena(new Scene(root, 950, 560));
    }

    private static void agregar() {
        try {
            ServicioVotacion.registrarCandidato(txtCodigo.getText(), txtNombre.getText(),
                    txtPartido.getText(), txtDescripcion.getText());
            limpiarCampos();
            refrescarTabla();
            Dialogos.informacion("Candidato agregado correctamente.");
        } catch (CandidatoDuplicadoException ex) {
            Dialogos.error(ex.getMessage());
        }
    }

    private static void cargarSeleccionado() {
        Candidato seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Dialogos.error("Debe seleccionar un candidato de la tabla.");
            return;
        }
        txtCodigo.setText(seleccionado.getCodigo());
        txtNombre.setText(seleccionado.getNombre());
        txtPartido.setText(seleccionado.getPartido());
        txtDescripcion.setText(seleccionado.getDescripcion());
    }

    private static void modificar() {
        Candidato seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Dialogos.error("Debe seleccionar un candidato de la tabla.");
            return;
        }
        try {
            if (ServicioVotacion.modificarCandidato(seleccionado.getCodigo(), txtNombre.getText(),
                    txtPartido.getText(), txtDescripcion.getText())) {
                refrescarTabla();
                Dialogos.informacion("Candidato modificado correctamente.");
            } else {
                Dialogos.error("No se puede modificar el candidato porque la votación está abierta.");
            }
        } catch (CandidatoDuplicadoException ex) {
            Dialogos.error(ex.getMessage());
        }
    }

    private static void eliminar() {
        Candidato seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Dialogos.error("Debe seleccionar un candidato de la tabla.");
            return;
        }
        if (Dialogos.confirmar("¿Desea eliminar al candidato " + seleccionado.getNombre() + "?")) {
            if (ServicioVotacion.eliminarCandidato(seleccionado.getCodigo())) {
                limpiarCampos();
                refrescarTabla();
                Dialogos.informacion("Candidato eliminado correctamente.");
            } else {
                Dialogos.error("No se puede eliminar el candidato porque la votación está abierta o tiene votos.");
            }
        }
    }

    private static void exportar() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Exportar candidatos a CSV");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File archivo = selector.showSaveDialog(Navegacion.stage);
        if (archivo == null) {
            return;
        }
        try {
            CsvExporter.exportar(archivo, CsvExporter.CABECERAS_CANDIDATOS,
                    ServicioVotacion.filasCandidatosCSV());
            Dialogos.informacion("Archivo CSV exportado correctamente: " + archivo.getAbsolutePath());
        } catch (ExportacionFallidaException ex) {
            Dialogos.error(ex.getMessage());
        }
    }

    private static void refrescarTabla() {
        tabla.setItems(FXCollections.observableArrayList(new ArrayList<>(ServicioVotacion.getCandidatos())));
        boolean bloqueado = ServicioVotacion.isVotacionAbierta();
        btnAgregar.setDisable(bloqueado);
        btnModificar.setDisable(bloqueado);
        btnEliminar.setDisable(bloqueado);
    }

    private static void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPartido.clear();
        txtDescripcion.clear();
    }
}
