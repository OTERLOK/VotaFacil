package com.mycompany.votafacil.model;

/**
 * Representa a un elector del proceso electoral.
 * El código y la cédula deben ser únicos en el sistema.
 * El atributo habilitado solo se mantiene en memoria, no se guarda
 * en el archivo.
 */
public class Elector {

    private String codigo;
    private String cedula;
    private String nombre;
    private String contrasena;
    private boolean yaVoto;
    private boolean habilitado;

    public Elector(String codigo, String cedula, String nombre, String contrasena, boolean yaVoto) {
        this.codigo = codigo;
        this.cedula = cedula;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.yaVoto = yaVoto;
        this.habilitado = true;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public boolean isYaVoto() {
        return yaVoto;
    }

    public void setYaVoto(boolean yaVoto) {
        this.yaVoto = yaVoto;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    /**
     * Devuelve "Ya votó" o "Pendiente" para mostrar en la tabla.
     */
    public String getEstado() {
        if (yaVoto) {
            return "Ya votó";
        }
        return "Pendiente";
    }

    /**
     * Devuelve "Sí" o "No" para mostrar en la tabla.
     */
    public String getHabilitadoTexto() {
        if (habilitado) {
            return "Sí";
        }
        return "No";
    }

    /**
     * Devuelve la contraseña oculta con asteriscos para no mostrarla
     * directamente en la tabla.
     */
    public String getContrasenaOculta() {
        return "*".repeat(contrasena.length());
    }
}
