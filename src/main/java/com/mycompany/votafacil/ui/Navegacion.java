package com.mycompany.votafacil.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Encargada de la navegación entre escenas de la aplicación. Mantiene la
 * referencia a la ventana principal y permite cambiar la escena mostrada.
 */
public class Navegacion {

    /** Ventana principal de la aplicación. */
    public static Stage stage;

    private Navegacion() {
    }

    /** Cambia la escena que se muestra en la ventana principal. */
    public static void cambiarEscena(Scene escena) {
        stage.setScene(escena);
    }
}
