package com.mycompany.votafacil.model;

/**
 * Representa a un candidato del proceso electoral.
 * El código y el nombre deben ser únicos en el sistema.
 */
public class Candidato {

    private String codigo;
    private String nombre;
    private String partido;
    private String descripcion;

    public Candidato(String codigo, String nombre, String partido, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.partido = partido;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPartido() {
        return partido;
    }

    public void setPartido(String partido) {
        this.partido = partido;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
