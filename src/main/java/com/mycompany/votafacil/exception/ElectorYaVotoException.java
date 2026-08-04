package com.mycompany.votafacil.exception;

/**
 * Se lanza cuando un elector que ya emitió su voto intenta iniciar sesión
 * de nuevo.
 */
public class ElectorYaVotoException extends Exception {

    public ElectorYaVotoException(String mensaje) {
        super(mensaje);
    }
}
