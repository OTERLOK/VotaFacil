package com.mycompany.votafacil.exception;

/**
 * Se lanza cuando se intenta registrar un candidato con un código
 * o un nombre que ya existe en el sistema.
 */
public class CandidatoDuplicadoException extends Exception {

    public CandidatoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
