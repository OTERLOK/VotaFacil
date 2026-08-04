package com.mycompany.votafacil.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import com.mycompany.votafacil.exception.ElectorYaVotoException;
import com.mycompany.votafacil.exception.VotacionCerradaException;
import com.mycompany.votafacil.service.ServicioVotacion;

/**
 * Pantalla de inicio de sesión. Sirve tanto para el administrador como
 * para los electores y redirige al menú correspondiente según las
 * credenciales.
 */
public class LoginScene {

    public static void mostrar() {
        Label lblTitulo = new Label("Sistema de Votación Electrónica");
        Label lblUsuario = new Label("Usuario:");
        TextField txtUsuario = new TextField();
        txtUsuario.setPromptText("Código del elector o admin");
        Label lblContrasena = new Label("Contraseña:");
        PasswordField txtContrasena = new PasswordField();
        Button btnIniciar = new Button("Iniciar Sesión");

        btnIniciar.setOnAction(e -> iniciarSesion(txtUsuario.getText(), txtContrasena.getText()));

        VBox root = new VBox(10);
        root.getChildren().addAll(lblTitulo, lblUsuario, txtUsuario, lblContrasena, txtContrasena, btnIniciar);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Navegacion.cambiarEscena(new Scene(root, 480, 320));
    }

    private static void iniciarSesion(String usuario, String contrasena) {
        try {
            String resultado = ServicioVotacion.iniciarSesion(usuario, contrasena);
            if (resultado.equals("admin")) {
                MenuAdminScene.mostrar();
            } else if (resultado.equals("elector")) {
                MenuElectorScene.mostrar();
            } else {
                Dialogos.error(resultado);
            }
        } catch (VotacionCerradaException | ElectorYaVotoException ex) {
            Dialogos.error(ex.getMessage());
        }
    }
}
