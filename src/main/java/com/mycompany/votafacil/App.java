package com.mycompany.votafacil;

import javafx.application.Application;
import javafx.stage.Stage;

import com.mycompany.votafacil.data.DatosVotacion;
import com.mycompany.votafacil.ui.LoginScene;
import com.mycompany.votafacil.ui.Navegacion;

/**
 * Aplicación JavaFX del sistema de votación electrónica. Carga los datos al
 * arrancar, muestra la pantalla de inicio de sesión y guarda los datos al
 * cerrar el programa.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        Navegacion.stage = primaryStage;
        DatosVotacion.cargar();
        primaryStage.setTitle("Sistema de Votación Electrónica");
        primaryStage.setOnCloseRequest(e -> DatosVotacion.guardar());
        LoginScene.mostrar();
        primaryStage.show();
    }
}
