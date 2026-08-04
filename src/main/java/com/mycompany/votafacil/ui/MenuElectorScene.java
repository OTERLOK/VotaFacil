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
 * Menú del elector. Muestra la bienvenida y permite acceder a la pantalla
 * de votación o cerrar la sesión.
 */
public class MenuElectorScene {

    public static void mostrar() {
        Label lblBienvenida = new Label("Bienvenido(a), " + ServicioVotacion.getElectorActualNombre());
        Button btnVotar = new Button("Ir a Votación");
        Button btnCerrar = new Button("Cerrar Sesión");

        btnVotar.setOnAction(e -> VotacionScene.mostrar());
        btnCerrar.setOnAction(e -> {
            ServicioVotacion.cerrarSesion();
            DatosVotacion.guardar();
            LoginScene.mostrar();
        });

        VBox root = new VBox(15);
        root.getChildren().addAll(lblBienvenida, btnVotar, btnCerrar);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Navegacion.cambiarEscena(new Scene(root, 420, 240));
    }
}
