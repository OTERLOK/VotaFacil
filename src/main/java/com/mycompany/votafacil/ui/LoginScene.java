package com.mycompany.votafacil.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import com.mycompany.votafacil.exception.ElectorYaVotoException;
import com.mycompany.votafacil.exception.VotacionCerradaException;
import com.mycompany.votafacil.service.ServicioVotacion;

public class LoginScene {

    public static void mostrar() {
        Text logo = new Text("\uD83D\uDF73");
        logo.setFont(Font.font(60));

        Text nombre = new Text("VotaFacil");
        nombre.setFont(Font.font("System", FontWeight.BOLD, 32));
        nombre.setFill(Color.web("#2c3e50"));

        Text slogan = new Text("La forma más sencilla de votar");
        slogan.setFont(Font.font("System", 14));
        slogan.setFill(Color.web("#7f8c8d"));

        Line linea = new Line(0, 0, 180, 0);
        linea.setStroke(Color.web("#bdc3c7"));
        linea.setStrokeWidth(2);

        Label lblCedula = new Label("Cédula");
        lblCedula.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 13px; -fx-font-weight: bold;");
        TextField txtCedula = new TextField();
        txtCedula.setPromptText("Número de cédula");
        txtCedula.setPrefWidth(280);
        txtCedula.setStyle("-fx-font-size: 13px; -fx-padding: 8 10;");

        Label lblContrasena = new Label("Contraseña");
        lblContrasena.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 13px; -fx-font-weight: bold;");
        PasswordField txtContrasena = new PasswordField();
        txtContrasena.setPromptText("Ingrese su contraseña");
        txtContrasena.setPrefWidth(280);
        txtContrasena.setStyle("-fx-font-size: 13px; -fx-padding: 8 10;");

        Button btnIniciar = new Button("Iniciar Sesión");
        btnIniciar.setPrefWidth(280);
        btnIniciar.setStyle(
                "-fx-background-color: #2c3e50; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 0; " +
                "-fx-cursor: hand; " +
                "-fx-background-radius: 4;");
        btnIniciar.setOnMouseEntered(e -> btnIniciar.setStyle(
                "-fx-background-color: #34495e; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 0; " +
                "-fx-cursor: hand; " +
                "-fx-background-radius: 4;"));
        btnIniciar.setOnMouseExited(e -> btnIniciar.setStyle(
                "-fx-background-color: #2c3e50; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 0; " +
                "-fx-cursor: hand; " +
                "-fx-background-radius: 4;"));

        btnIniciar.setOnAction(e -> iniciarSesion(txtCedula.getText(), txtContrasena.getText()));

        VBox logoBox = new VBox(8, logo, nombre, linea, slogan);
        logoBox.setAlignment(Pos.CENTER);

        VBox camposBox = new VBox(12, lblCedula, txtCedula, lblContrasena, txtContrasena);
        camposBox.setAlignment(Pos.CENTER);
        VBox.setMargin(camposBox, new Insets(30, 0, 0, 0));

        VBox.setMargin(btnIniciar, new Insets(25, 0, 0, 0));

        VBox root = new VBox(0);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(35, 40, 40, 40));
        root.getChildren().addAll(logoBox, new Separator(), camposBox, btnIniciar);
        root.setStyle("-fx-background-color: #f5f6fa;");

        Navegacion.cambiarEscena(new Scene(root, 460, 420));
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
