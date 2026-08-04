package com.mycompany.votafacil.exception;

/**
 * Se lanza cuando no se puede generar un archivo CSV de exportación.
 */
public class ExportacionFallidaException extends Exception {

    public ExportacionFallidaException(String mensaje) {
        super(mensaje);
    }
}
