package com.mycompany.votafacil.ui;

import java.io.File;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import com.mycompany.votafacil.data.CsvExporter;
import com.mycompany.votafacil.exception.ElectorDuplicadoException;
import com.mycompany.votafacil.exception.ExportacionFallidaException;
import com.mycompany.votafacil.model.Elector;
import com.mycompany.votafacil.service.ServicioVotacion;

public class ElectoresScene {

    private static TableView<Elector> tabla;
    private static final TextField txtCodigo = new TextField();
    private static final TextField txtCedula = new TextField();
    private static final TextField txtNombre = new TextField();
    private static final PasswordField txtContrasena = new PasswordField();

    public static Parent crearContenido() {
        Label lblTitulo = new Label("Gestión de Electores");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Elector, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        TableColumn<Elector, String> colCedula = new TableColumn<>("Cédula");
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        TableColumn<Elector, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<Elector, String> colContrasena = new TableColumn<>("Contraseña");
        colContrasena.setCellValueFactory(new PropertyValueFactory<>("contrasenaOculta"));
        TableColumn<Elector, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        TableColumn<Elector, String> colHabilitado = new TableColumn<>("Habilitado");
        colHabilitado.setCellValueFactory(new PropertyValueFactory<>("habilitadoTexto"));
        tabla.getColumns().addAll(colCodigo, colCedula, colNombre, colContrasena, colEstado, colHabilitado);

        Button btnAgregar = new Button("Agregar");
        Button btnCargar = new Button("Cargar seleccionado");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");
        Button btnHabilitar = new Button("Habilitar");
        Button btnDeshabilitar = new Button("Deshabilitar");
        Button btnExportar = new Button("Exportar CSV");
        Button btnVolver = new Button("Volver al menú");

        btnAgregar.setOnAction(e -> agregar());
        btnCargar.setOnAction(e -> cargarSeleccionado());
        btnModificar.setOnAction(e -> modificar());
        btnEliminar.setOnAction(e -> eliminar());
        btnHabilitar.setOnAction(e -> cambiarHabilitacion(true));
        btnDeshabilitar.setOnAction(e -> cambiarHabilitacion(false));
        btnExportar.setOnAction(e -> exportar());
        btnVolver.setOnAction(e -> MenuAdminScene.mostrarBienvenida());

        HBox filaCampos = new HBox(8);
        filaCampos.getChildren().addAll(
                new Label("Código:"), txtCodigo,
                new Label("Cédula:"), txtCedula);
        HBox filaDatos = new HBox(8);
        filaDatos.getChildren().addAll(
                new Label("Nombre:"), txtNombre,
                new Label("Contraseña:"), txtContrasena);
        HBox filaBotones = new HBox(8);
        filaBotones.getChildren().addAll(btnAgregar, btnCargar, btnModificar, btnEliminar,
                btnHabilitar, btnDeshabilitar, btnExportar, btnVolver);

        VBox root = new VBox(10);
        root.getChildren().addAll(lblTitulo, tabla, filaCampos, filaDatos, filaBotones);

        refrescarTabla();
        return root;
    }

    public static void mostrar() {
        Parent contenido = crearContenido();
        Navegacion.cambiarEscena(new Scene(contenido, 950, 560));
    }

    private static void agregar() {
        try {
            ServicioVotacion.registrarElector(txtCodigo.getText(), txtCedula.getText(),
                    txtNombre.getText(), txtContrasena.getText());
            limpiarCampos();
            refrescarTabla();
            Dialogos.informacion("Elector agregado correctamente.");
        } catch (ElectorDuplicadoException ex) {
            Dialogos.error(ex.getMessage());
        }
    }

    private static void cargarSeleccionado() {
        Elector seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Dialogos.error("Debe seleccionar un elector de la tabla.");
            return;
        }
        txtCodigo.setText(seleccionado.getCodigo());
        txtCedula.setText(seleccionado.getCedula());
        txtNombre.setText(seleccionado.getNombre());
        txtContrasena.clear();
    }

    private static void modificar() {
        Elector seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Dialogos.error("Debe seleccionar un elector de la tabla.");
            return;
        }
        String nuevaContrasena = txtContrasena.getText();
        if (nuevaContrasena.isEmpty()) {
            nuevaContrasena = seleccionado.getContrasena();
        }
        try {
            if (ServicioVotacion.modificarElector(seleccionado.getCodigo(), txtCedula.getText(),
                    txtNombre.getText(), nuevaContrasena)) {
                refrescarTabla();
                Dialogos.informacion("Elector modificado correctamente.");
            } else {
                Dialogos.error("No se puede modificar un elector que ya emitió su voto.");
            }
        } catch (ElectorDuplicadoException ex) {
            Dialogos.error(ex.getMessage());
        }
    }

    private static void eliminar() {
        Elector seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Dialogos.error("Debe seleccionar un elector de la tabla.");
            return;
        }
        if (Dialogos.confirmar("¿Desea eliminar al elector " + seleccionado.getNombre() + "?")) {
            if (ServicioVotacion.eliminarElector(seleccionado.getCodigo())) {
                limpiarCampos();
                refrescarTabla();
                Dialogos.informacion("Elector eliminado correctamente.");
            } else {
                Dialogos.error("No se puede eliminar un elector que ya emitió su voto.");
            }
        }
    }

    private static void cambiarHabilitacion(boolean estado) {
        Elector seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Dialogos.error("Debe seleccionar un elector de la tabla.");
            return;
        }
        if (estado) {
            ServicioVotacion.habilitarElector(seleccionado.getCodigo());
        } else {
            ServicioVotacion.deshabilitarElector(seleccionado.getCodigo());
        }
        refrescarTabla();
    }

    private static void exportar() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Exportar electores a CSV");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File archivo = selector.showSaveDialog(Navegacion.stage);
        if (archivo == null) {
            return;
        }
        try {
            CsvExporter.exportar(archivo, CsvExporter.CABECERAS_ELECTORES,
                    ServicioVotacion.filasElectoresCSV());
            Dialogos.informacion("Archivo CSV exportado correctamente: " + archivo.getAbsolutePath());
        } catch (ExportacionFallidaException ex) {
            Dialogos.error(ex.getMessage());
        }
    }

    private static void refrescarTabla() {
        tabla.setItems(FXCollections.observableArrayList(new ArrayList<>(ServicioVotacion.getElectores())));
    }

    private static void limpiarCampos() {
        txtCodigo.clear();
        txtCedula.clear();
        txtNombre.clear();
        txtContrasena.clear();
    }
}
