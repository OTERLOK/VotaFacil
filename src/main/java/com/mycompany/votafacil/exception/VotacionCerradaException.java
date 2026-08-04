package com.mycompany.votafacil.exception;

/**
 * Se lanza cuando un elector intenta iniciar sesión o votar estando la
 * votación cerrada.
 */
public class VotacionCerradaException extends Exception {

    public VotacionCerradaException(String mensaje) {
        super(mensaje);
    }
}
