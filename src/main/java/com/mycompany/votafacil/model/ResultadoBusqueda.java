package com.mycompany.votafacil.model;

/**
 * Fila de la tabla de búsqueda global. Indica el tipo de entidad encontrada
 * (candidato, elector o voto) y muestra sus datos.
 */
public class ResultadoBusqueda {

    private String tipo;
    private String datos;

    public ResultadoBusqueda(String tipo, String datos) {
        this.tipo = tipo;
        this.datos = datos;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDatos() {
        return datos;
    }
}
