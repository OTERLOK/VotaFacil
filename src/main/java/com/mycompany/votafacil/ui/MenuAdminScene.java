package com.mycompany.votafacil.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import com.mycompany.votafacil.data.DatosVotacion;
import com.mycompany.votafacil.service.ServicioVotacion;

public class MenuAdminScene {

    private static StackPane panelDerecho;
    private static Button btnVotacion;
    private static Button btnEscrutinio;

    public static void mostrar() {
        Button btnCandidatos = crearBotonNav("Gestionar Candidatos");
        Button btnElectores = crearBotonNav("Gestionar Electores");
        btnVotacion = crearBotonNav("");
        btnEscrutinio = crearBotonNav("Ver Escrutinio");
        Button btnBusqueda = crearBotonNav("Búsqueda Global");
        Button btnExportar = crearBotonNav("Exportar Datos");
        Button btnCerrar = crearBotonNav("Cerrar Sesión");

        actualizarBotonVotacion();
        btnEscrutinio.setDisable(ServicioVotacion.isVotacionAbierta());

        btnCandidatos.setOnAction(e -> cargarContenido(CandidatosScene.crearContenido()));
        btnElectores.setOnAction(e -> cargarContenido(ElectoresScene.crearContenido()));
        btnVotacion.setOnAction(e -> {
            if (ServicioVotacion.isVotacionAbierta()) {
                ServicioVotacion.cerrarVotacion();
            } else {
                String error = ServicioVotacion.abrirVotacion();
                if (error != null) {
                    Dialogos.error(error);
                }
            }
            actualizarBotonVotacion();
            btnEscrutinio.setDisable(ServicioVotacion.isVotacionAbierta());
        });
        btnEscrutinio.setOnAction(e -> cargarContenido(EscrutinioScene.crearContenido()));
        btnBusqueda.setOnAction(e -> cargarContenido(BusquedaScene.crearContenido()));
        btnExportar.setOnAction(e -> cargarContenido(ExportacionScene.crearContenido()));
        btnCerrar.setOnAction(e -> {
            DatosVotacion.guardar();
            ServicioVotacion.cerrarSesion();
            LoginScene.mostrar();
        });

        VBox sidebar = new VBox(5);
        sidebar.setStyle("-fx-background-color: #2c3e50; -fx-padding: 15;");
        sidebar.setPrefWidth(220);
        sidebar.getChildren().addAll(
                btnCandidatos, btnElectores, btnVotacion,
                btnEscrutinio, btnBusqueda, btnExportar,
                new Separator(),
                btnCerrar);

        panelDerecho = new StackPane();
        panelDerecho.setStyle("-fx-background-color: #f5f6fa;");
        panelDerecho.getChildren().add(crearBienvenida());
        StackPane.setMargin(panelDerecho.getChildren().get(0), new Insets(20));

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(panelDerecho);

        Navegacion.cambiarEscena(new Scene(root, 1000, 600));
    }

    public static void cargarContenido(javafx.scene.Parent contenido) {
        panelDerecho.getChildren().clear();
        contenido.setStyle("-fx-background-color: #f5f6fa;");
        StackPane.setMargin(contenido, new Insets(20));
        panelDerecho.getChildren().add(contenido);
    }

    public static void mostrarBienvenida() {
        panelDerecho.getChildren().clear();
        VBox bienvenida = crearBienvenida();
        StackPane.setMargin(bienvenida, new Insets(20));
        panelDerecho.getChildren().add(bienvenida);
    }

    private static VBox crearBienvenida() {
        Text logo = new Text("\uD83D\uDF73");
        logo.setFont(Font.font(72));

        Text nombre = new Text("VotaFacil");
        nombre.setFont(Font.font("System", FontWeight.BOLD, 42));
        nombre.setFill(Color.web("#2c3e50"));

        Text slogan = new Text("La forma más sencilla de votar");
        slogan.setFont(Font.font("System", 18));
        slogan.setFill(Color.web("#7f8c8d"));

        Line linea = new Line(0, 0, 200, 0);
        linea.setStroke(Color.web("#bdc3c7"));
        linea.setStrokeWidth(2);

        VBox vbox = new VBox(12, logo, nombre, linea, slogan);
        vbox.setAlignment(Pos.CENTER);
        vbox.setMaxWidth(400);
        return vbox;
    }

    private static Button crearBotonNav(String texto) {
        Button boton = new Button(texto);
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10 15; " +
                "-fx-alignment: CENTER_LEFT; " +
                "-fx-cursor: hand;");
        boton.setOnMouseEntered(e -> boton.setStyle(
                "-fx-background-color: #34495e; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10 15; " +
                "-fx-alignment: CENTER_LEFT; " +
                "-fx-cursor: hand;"));
        boton.setOnMouseExited(e -> boton.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10 15; " +
                "-fx-alignment: CENTER_LEFT; " +
                "-fx-cursor: hand;"));
        return boton;
    }

    private static void actualizarBotonVotacion() {
        if (ServicioVotacion.isVotacionAbierta()) {
            btnVotacion.setText("Cerrar Votación");
        } else {
            btnVotacion.setText("Abrir Votación");
        }
    }
}
