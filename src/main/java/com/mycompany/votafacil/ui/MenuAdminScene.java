package com.mycompany.votafacil.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import com.mycompany.votafacil.data.DatosVotacion;
import com.mycompany.votafacil.service.ServicioVotacion;

/**
 * Menú principal del administrador con acceso a la gestión de candidatos,
 * electores, apertura o cierre de la votación, escrutinio, búsqueda global
 * y exportación de datos.
 */
public class MenuAdminScene {

    public static void mostrar() {
        Label lblTitulo = new Label("Menú del Administrador");

        Button btnCandidatos = new Button("Gestionar Candidatos");
        Button btnElectores = new Button("Gestionar Electores");
        Button btnVotacion = new Button();
        Button btnEscrutinio = new Button("Ver Escrutinio");
        Button btnBusqueda = new Button("Búsqueda Global");
        Button btnExportar = new Button("Exportar Datos");
        Button btnCerrar = new Button("Cerrar Sesión");

        actualizarBotonVotacion(btnVotacion);
        btnEscrutinio.setDisable(ServicioVotacion.isVotacionAbierta());

        btnCandidatos.setOnAction(e -> CandidatosScene.mostrar());
        btnElectores.setOnAction(e -> ElectoresScene.mostrar());
        btnVotacion.setOnAction(e -> {
            if (ServicioVotacion.isVotacionAbierta()) {
                ServicioVotacion.cerrarVotacion();
            } else {
                String error = ServicioVotacion.abrirVotacion();
                if (error != null) {
                    Dialogos.error(error);
                }
            }
            actualizarBotonVotacion(btnVotacion);
            btnEscrutinio.setDisable(ServicioVotacion.isVotacionAbierta());
        });
        btnEscrutinio.setOnAction(e -> EscrutinioScene.mostrar());
        btnBusqueda.setOnAction(e -> BusquedaScene.mostrar());
        btnExportar.setOnAction(e -> ExportacionScene.mostrar());
        btnCerrar.setOnAction(e -> {
            DatosVotacion.guardar();
            ServicioVotacion.cerrarSesion();
            LoginScene.mostrar();
        });

        VBox root = new VBox(15);
        root.getChildren().addAll(lblTitulo, btnCandidatos, btnElectores, btnVotacion,
                btnEscrutinio, btnBusqueda, btnExportar, btnCerrar);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Navegacion.cambiarEscena(new Scene(root, 440, 440));
    }

    private static void actualizarBotonVotacion(Button boton) {
        if (ServicioVotacion.isVotacionAbierta()) {
            boton.setText("Cerrar Votación");
        } else {
            boton.setText("Abrir Votación");
        }
    }
}
