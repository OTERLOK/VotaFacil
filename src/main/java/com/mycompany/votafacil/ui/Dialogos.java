package com.mycompany.votafacil.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Utilidades para mostrar diálogos de información, error y confirmación.
 */
public class Dialogos {

    private Dialogos() {
    }

    public static void informacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public static void error(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra un diálogo de confirmación y devuelve true si el usuario
     * confirmó.
     */
    public static boolean confirmar(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmación");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        return alerta.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
