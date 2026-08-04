package com.mycompany.votafacil.model;

/**
 * Representa la emisión de un voto. Relaciona el código del elector con el
 * código del candidato elegido, pero el elector nunca aparece en pantallas ni
 * en reportes, lo que garantiza el secreto del voto.
 */
public class Voto {

    private String codigoVoto;
    private String codigoElector;
    private String codigoCandidato;

    public Voto(String codigoVoto, String codigoElector, String codigoCandidato) {
        this.codigoVoto = codigoVoto;
        this.codigoElector = codigoElector;
        this.codigoCandidato = codigoCandidato;
    }

    public String getCodigoVoto() {
        return codigoVoto;
    }

    public void setCodigoVoto(String codigoVoto) {
        this.codigoVoto = codigoVoto;
    }

    public String getCodigoElector() {
        return codigoElector;
    }

    public void setCodigoElector(String codigoElector) {
        this.codigoElector = codigoElector;
    }

    public String getCodigoCandidato() {
        return codigoCandidato;
    }

    public void setCodigoCandidato(String codigoCandidato) {
        this.codigoCandidato = codigoCandidato;
    }
}
