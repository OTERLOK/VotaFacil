package com.mycompany.votafacil.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import com.mycompany.votafacil.data.DatosVotacion;
import com.mycompany.votafacil.exception.VotacionCerradaException;
import com.mycompany.votafacil.model.Candidato;
import com.mycompany.votafacil.service.ServicioVotacion;

/**
 * Pantalla de votación del elector. Muestra la lista de candidatos con su
 * nombre, partido y descripción. Al seleccionar un candidato y confirmar, se
 * registra el voto y la sesión se cierra automáticamente.
 */
public class VotacionScene {

    private static TableView<Candidato> tabla;
    private static Button btnVotar;

    public static void mostrar() {
        Label lblTitulo = new Label("Seleccione el candidato de su preferencia:");

        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Candidato, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<Candidato, String> colPartido = new TableColumn<>("Partido");
        colPartido.setCellValueFactory(new PropertyValueFactory<>("partido"));
        TableColumn<Candidato, String> colDescripcion = new TableColumn<>("Descripción");
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        tabla.getColumns().addAll(colNombre, colPartido, colDescripcion);

        btnVotar = new Button("Votar");
        btnVotar.setDisable(true);
        Button btnCerrar = new Button("Cerrar Sesión");

        btnVotar.setOnAction(e -> votar());
        btnCerrar.setOnAction(e -> {
            ServicioVotacion.cerrarSesion();
            DatosVotacion.guardar();
            LoginScene.mostrar();
        });

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) ->
                btnVotar.setDisable(nuevo == null));

        HBox filaBotones = new HBox(10);
        filaBotones.getChildren().addAll(btnVotar, btnCerrar);
        filaBotones.setAlignment(Pos.CENTER);

        VBox root = new VBox(10);
        root.getChildren().addAll(lblTitulo, tabla, filaBotones);
        root.setPadding(new Insets(15));

        refrescarTabla();
        Navegacion.cambiarEscena(new Scene(root, 750, 520));
    }

    private static void votar() {
        Candidato seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Dialogos.error("Debe seleccionar un candidato de la lista.");
            return;
        }
        if (Dialogos.confirmar("¿Confirma su voto por " + seleccionado.getNombre()
                + " (" + seleccionado.getPartido() + ")?")) {
            try {
                ServicioVotacion.votar(seleccionado.getCodigo());
                DatosVotacion.guardar();
                Dialogos.informacion("Su voto fue registrado exitosamente. Gracias por participar. "
                        + "Su sesión fue cerrada automáticamente.");
                LoginScene.mostrar();
            } catch (VotacionCerradaException ex) {
                Dialogos.error(ex.getMessage());
            }
        }
    }

    private static void refrescarTabla() {
        tabla.setItems(FXCollections.observableArrayList(ServicioVotacion.getCandidatos()));
    }
}
