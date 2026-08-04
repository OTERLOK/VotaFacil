package com.mycompany.votafacil.service;

import java.util.ArrayList;
import java.util.Comparator;

import com.mycompany.votafacil.data.DatosVotacion;
import com.mycompany.votafacil.exception.CandidatoDuplicadoException;
import com.mycompany.votafacil.exception.ElectorDuplicadoException;
import com.mycompany.votafacil.exception.ElectorYaVotoException;
import com.mycompany.votafacil.exception.VotacionCerradaException;
import com.mycompany.votafacil.model.Candidato;
import com.mycompany.votafacil.model.Elector;
import com.mycompany.votafacil.model.ResultadoBusqueda;
import com.mycompany.votafacil.model.Voto;

/**
 * Lógica de negocio del sistema de votación: autenticación, gestión de
 * candidatos y electores, apertura y cierre de la votación, emisión de votos,
 * escrutinio, búsqueda global y preparación de datos para exportar.
 *
 * Los datos en memoria viven en {@link DatosVotacion}.
 */
public class ServicioVotacion {

    public static final String ADMIN_USUARIO = "admin";
    public static final String ADMIN_CONTRASENA = "admin123";

    private static boolean votacionAbierta = false;
    private static String codigoElectorActual = null;

    private ServicioVotacion() {
    }

    /**
     * Verifica las credenciales. Devuelve "admin", "elector" o un mensaje de
     * error. Para los electores valida contraseña, habilitación, estado de
     * voto y que la votación esté abierta.
     */
    public static String iniciarSesion(String usuario, String contrasena)
            throws VotacionCerradaException, ElectorYaVotoException {
        if (ADMIN_USUARIO.equals(usuario)) {
            if (ADMIN_CONTRASENA.equals(contrasena)) {
                return "admin";
            }
            return "La contraseña del administrador es incorrecta.";
        }
        Elector elector = buscarElectorPorCodigo(usuario);
        if (elector == null) {
            return "El código ingresado no está registrado como elector.";
        }
        if (!elector.getContrasena().equals(contrasena)) {
            return "La contraseña no coincide con el elector.";
        }
        if (!elector.isHabilitado()) {
            return "El elector está deshabilitado y no puede iniciar sesión.";
        }
        if (elector.isYaVoto()) {
            throw new ElectorYaVotoException("Este elector ya emitió su voto y no puede volver a iniciar sesión.");
        }
        if (!votacionAbierta) {
            throw new VotacionCerradaException("Votación cerrada. Solo el administrador puede iniciar sesión.");
        }
        codigoElectorActual = elector.getCodigo();
        return "elector";
    }

    /**
     * Registra un candidato validando que el código y el nombre sean únicos.
     */
    public static void registrarCandidato(String codigo, String nombre, String partido, String descripcion)
            throws CandidatoDuplicadoException {
        if (codigo == null || codigo.trim().isEmpty() || nombre == null || nombre.trim().isEmpty()) {
            throw new CandidatoDuplicadoException("El código y el nombre son obligatorios.");
        }
        for (Candidato c : DatosVotacion.candidatos) {
            if (c.getCodigo().equalsIgnoreCase(codigo.trim())) {
                throw new CandidatoDuplicadoException("Ya existe un candidato con el código " + codigo.trim() + ".");
            }
            if (c.getNombre().equalsIgnoreCase(nombre.trim())) {
                throw new CandidatoDuplicadoException("Ya existe un candidato con el nombre " + nombre.trim() + ".");
            }
        }
        DatosVotacion.candidatos.add(new Candidato(codigo.trim(), nombre.trim(), partido.trim(), descripcion.trim()));
    }

    /**
     * Modifica los datos de un candidato existente. No permite modificar un
     * candidato cuando la votación está abierta.
     */
    public static boolean modificarCandidato(String codigo, String nombre, String partido, String descripcion)
            throws CandidatoDuplicadoException {
        if (votacionAbierta) {
            return false;
        }
        Candidato candidato = buscarCandidatoPorCodigo(codigo);
        if (candidato == null) {
            return false;
        }
        for (Candidato c : DatosVotacion.candidatos) {
            if (!c.getCodigo().equalsIgnoreCase(codigo) && c.getNombre().equalsIgnoreCase(nombre.trim())) {
                throw new CandidatoDuplicadoException("Ya existe un candidato con el nombre " + nombre.trim() + ".");
            }
        }
        candidato.setNombre(nombre.trim());
        candidato.setPartido(partido.trim());
        candidato.setDescripcion(descripcion.trim());
        return true;
    }

    /**
     * Elimina un candidato siempre que la votación no esté abierta y el
     * candidato no tenga votos registrados.
     */
    public static boolean eliminarCandidato(String codigo) {
        Candidato candidato = buscarCandidatoPorCodigo(codigo);
        if (candidato == null || votacionAbierta) {
            return false;
        }
        if (contarVotos(codigo) > 0) {
            return false;
        }
        return DatosVotacion.candidatos.remove(candidato);
    }

    /**
     * Registra un elector validando que el código y la cédula sean únicos.
     */
    public static void registrarElector(String codigo, String cedula, String nombre, String contrasena)
            throws ElectorDuplicadoException {
        if (codigo == null || codigo.trim().isEmpty()
                || cedula == null || cedula.trim().isEmpty()
                || nombre == null || nombre.trim().isEmpty()) {
            throw new ElectorDuplicadoException("El código, la cédula y el nombre son obligatorios.");
        }
        for (Elector e : DatosVotacion.electores) {
            if (e.getCodigo().equalsIgnoreCase(codigo.trim())) {
                throw new ElectorDuplicadoException("Ya existe un elector con el código " + codigo.trim() + ".");
            }
            if (e.getCedula().equals(cedula.trim())) {
                throw new ElectorDuplicadoException("Ya existe un elector con la cédula " + cedula.trim() + ".");
            }
        }
        DatosVotacion.electores.add(new Elector(codigo.trim(), cedula.trim(), nombre.trim(), contrasena, false));
    }

    /**
     * Modifica los datos de un elector existente. No permite modificar un
     * elector que ya emitió su voto.
     */
    public static boolean modificarElector(String codigo, String cedula, String nombre, String contrasena)
            throws ElectorDuplicadoException {
        Elector elector = buscarElectorPorCodigo(codigo);
        if (elector == null || elector.isYaVoto()) {
            return false;
        }
        for (Elector e : DatosVotacion.electores) {
            if (!e.getCodigo().equalsIgnoreCase(codigo) && e.getCedula().equals(cedula.trim())) {
                throw new ElectorDuplicadoException("Ya existe un elector con la cédula " + cedula.trim() + ".");
            }
        }
        elector.setCedula(cedula.trim());
        elector.setNombre(nombre.trim());
        elector.setContrasena(contrasena);
        return true;
    }

    /**
     * Elimina un elector siempre que no haya emitido su voto.
     */
    public static boolean eliminarElector(String codigo) {
        Elector elector = buscarElectorPorCodigo(codigo);
        if (elector == null || elector.isYaVoto()) {
            return false;
        }
        return DatosVotacion.electores.remove(elector);
    }

    public static void habilitarElector(String codigo) {
        Elector elector = buscarElectorPorCodigo(codigo);
        if (elector != null) {
            elector.setHabilitado(true);
        }
    }

    public static void deshabilitarElector(String codigo) {
        Elector elector = buscarElectorPorCodigo(codigo);
        if (elector != null) {
            elector.setHabilitado(false);
        }
    }

    /**
     * Abre la votación si existen al menos dos candidatos y un elector.
     * Devuelve un mensaje de error o null si se abrió correctamente.
     */
    public static String abrirVotacion() {
        if (DatosVotacion.candidatos.size() < 2) {
            return "No se puede abrir la votación: se requieren al menos dos candidatos registrados.";
        }
        if (DatosVotacion.electores.size() < 1) {
            return "No se puede abrir la votación: se requiere al menos un elector registrado.";
        }
        votacionAbierta = true;
        return null;
    }

    public static void cerrarVotacion() {
        votacionAbierta = false;
    }

    /**
     * Registra el voto del elector en sesión: genera el código de voto,
     * agrega el voto al arreglo, marca al elector como "ya votó" y cierra
     * la sesión automáticamente.
     */
    public static void votar(String codigoCandidato) throws VotacionCerradaException {
        if (!votacionAbierta) {
            throw new VotacionCerradaException("Votación cerrada. No se pueden emitir más votos.");
        }
        Elector elector = buscarElectorPorCodigo(codigoElectorActual);
        if (elector == null) {
            return;
        }
        elector.setYaVoto(true);
        DatosVotacion.votos.add(new Voto(generarCodigoVoto(), elector.getCodigo(), codigoCandidato));
        codigoElectorActual = null;
    }

    /**
     * Genera el siguiente código de voto, por ejemplo V001, V002, etc.
     */
    private static String generarCodigoVoto() {
        int mayor = 0;
        for (Voto v : DatosVotacion.votos) {
            try {
                int numero = Integer.parseInt(v.getCodigoVoto().replace("V", "").trim());
                if (numero > mayor) {
                    mayor = numero;
                }
            } catch (NumberFormatException ex) {
                // Se ignora un voto con formato inválido y se sigue con los demás.
            }
        }
        return String.format("V%03d", mayor + 1);
    }

    /**
     * Cuenta cuántos votos recibió un candidato recorriendo el arreglo de votos.
     */
    public static int contarVotos(String codigoCandidato) {
        int total = 0;
        for (Voto v : DatosVotacion.votos) {
            if (v.getCodigoCandidato().equals(codigoCandidato)) {
                total++;
            }
        }
        return total;
    }

    /**
     * Ordena los candidatos de mayor a menor según sus votos.
     */
    public static ArrayList<Candidato> candidatosPorVotosDesc() {
        ArrayList<Candidato> ordenados = new ArrayList<>(DatosVotacion.candidatos);
        ordenados.sort(Comparator.comparingInt((Candidato c) -> contarVotos(c.getCodigo())).reversed());
        return ordenados;
    }

    public static int totalVotosEmitidos() {
        return DatosVotacion.votos.size();
    }

    public static int totalElectoresHabilitados() {
        int total = 0;
        for (Elector e : DatosVotacion.electores) {
            if (e.isHabilitado()) {
                total++;
            }
        }
        return total;
    }

    /**
     * Calcula el porcentaje de votos de un candidato sobre el total emitido.
     */
    public static double porcentajeVotos(int votosCandidato) {
        int total = totalVotosEmitidos();
        if (total == 0) {
            return 0.0;
        }
        return (votosCandidato * 100.0) / total;
    }

    /**
     * Calcula el porcentaje de participación: votos emitidos entre electores
     * habilitados.
     */
    public static double porcentajeParticipacion() {
        int habilitados = totalElectoresHabilitados();
        if (habilitados == 0) {
            return 0.0;
        }
        return (totalVotosEmitidos() * 100.0) / habilitados;
    }

    /**
     * Busca coincidencias parciales del término en los tres arreglos y
     * devuelve una lista consolidada con el tipo de entidad y sus datos.
     */
    public static ArrayList<ResultadoBusqueda> buscarGlobal(String termino) {
        ArrayList<ResultadoBusqueda> resultados = new ArrayList<>();
        if (termino == null || termino.trim().isEmpty()) {
            return resultados;
        }
        String buscado = termino.trim().toLowerCase();
        for (Candidato c : DatosVotacion.candidatos) {
            if (c.getCodigo().toLowerCase().contains(buscado)
                    || c.getNombre().toLowerCase().contains(buscado)
                    || c.getPartido().toLowerCase().contains(buscado)
                    || c.getDescripcion().toLowerCase().contains(buscado)) {
                resultados.add(new ResultadoBusqueda("Candidato",
                        "Código: " + c.getCodigo() + " | Nombre: " + c.getNombre() + " | Partido: " + c.getPartido()));
            }
        }
        for (Elector e : DatosVotacion.electores) {
            if (e.getCodigo().toLowerCase().contains(buscado)
                    || e.getCedula().contains(buscado)
                    || e.getNombre().toLowerCase().contains(buscado)) {
                resultados.add(new ResultadoBusqueda("Elector",
                        "Código: " + e.getCodigo() + " | Cédula: " + e.getCedula() + " | Nombre: " + e.getNombre()));
            }
        }
        for (Voto v : DatosVotacion.votos) {
            if (v.getCodigoVoto().toLowerCase().contains(buscado)) {
                Candidato c = buscarCandidatoPorCodigo(v.getCodigoCandidato());
                String candidato = c != null ? c.getNombre() : v.getCodigoCandidato();
                resultados.add(new ResultadoBusqueda("Voto",
                        "Código de voto: " + v.getCodigoVoto() + " | Candidato votado: " + candidato));
            }
        }
        return resultados;
    }

    /**
     * Prepara las filas de la tabla de candidatos para exportar a CSV.
     */
    public static ArrayList<String[]> filasCandidatosCSV() {
        ArrayList<String[]> filas = new ArrayList<>();
        for (Candidato c : DatosVotacion.candidatos) {
            filas.add(new String[]{c.getCodigo(), c.getNombre(), c.getPartido(), c.getDescripcion()});
        }
        return filas;
    }

    /**
     * Prepara las filas de la tabla de electores para exportar a CSV.
     */
    public static ArrayList<String[]> filasElectoresCSV() {
        ArrayList<String[]> filas = new ArrayList<>();
        for (Elector e : DatosVotacion.electores) {
            filas.add(new String[]{e.getCodigo(), e.getCedula(), e.getNombre(), e.getEstado()});
        }
        return filas;
    }

    /**
     * Prepara las filas del escrutinio para exportar a CSV.
     */
    public static ArrayList<String[]> filasEscrutinioCSV() {
        ArrayList<String[]> filas = new ArrayList<>();
        for (Candidato c : candidatosPorVotosDesc()) {
            int votos = contarVotos(c.getCodigo());
            filas.add(new String[]{c.getCodigo(), c.getNombre(), c.getPartido(),
                String.valueOf(votos), String.format("%.1f", porcentajeVotos(votos))});
        }
        return filas;
    }

    public static Candidato buscarCandidatoPorCodigo(String codigo) {
        for (Candidato c : DatosVotacion.candidatos) {
            if (c.getCodigo().equalsIgnoreCase(codigo)) {
                return c;
            }
        }
        return null;
    }

    public static Elector buscarElectorPorCodigo(String codigo) {
        for (Elector e : DatosVotacion.electores) {
            if (e.getCodigo().equalsIgnoreCase(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static Elector getElectorActual() {
        return buscarElectorPorCodigo(codigoElectorActual);
    }

    public static String getElectorActualNombre() {
        Elector elector = getElectorActual();
        return elector != null ? elector.getNombre() : "";
    }

    public static void cerrarSesion() {
        codigoElectorActual = null;
    }

    public static ArrayList<Candidato> getCandidatos() {
        return new ArrayList<>(DatosVotacion.candidatos);
    }

    public static ArrayList<Elector> getElectores() {
        return new ArrayList<>(DatosVotacion.electores);
    }

    public static ArrayList<Voto> getVotos() {
        return new ArrayList<>(DatosVotacion.votos);
    }

    public static boolean isVotacionAbierta() {
        return votacionAbierta;
    }
}
