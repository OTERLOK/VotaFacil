package com.mycompany.votafacil.exception;

/**
 * Se lanza cuando se intenta registrar un elector con un código
 * o una cédula que ya existe en el sistema.
 */
public class ElectorDuplicadoException extends Exception {

    public ElectorDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
