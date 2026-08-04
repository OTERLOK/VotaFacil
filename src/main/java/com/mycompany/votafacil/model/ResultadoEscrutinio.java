package com.mycompany.votafacil.model;

/**
 * Fila de la tabla de escrutinio. Guarda los datos de un candidato con su
 * cantidad de votos y su porcentaje, listos para mostrar en la tabla.
 */
public class ResultadoEscrutinio {

    private String codigo;
    private String nombre;
    private String partido;
    private String votos;
    private String porcentaje;

    public ResultadoEscrutinio(String codigo, String nombre, String partido, String votos, String porcentaje) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.partido = partido;
        this.votos = votos;
        this.porcentaje = porcentaje;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPartido() {
        return partido;
    }

    public String getVotos() {
        return votos;
    }

    public String getPorcentaje() {
        return porcentaje;
    }
}
