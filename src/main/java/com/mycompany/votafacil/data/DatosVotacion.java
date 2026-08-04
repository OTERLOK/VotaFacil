package com.mycompany.votafacil.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.votafacil.model.Candidato;
import com.mycompany.votafacil.model.Elector;
import com.mycompany.votafacil.model.Voto;

/**
 * Capa de datos del sistema. Mantiene los tres arreglos en memoria
 * (candidatos, electores y votos) y se encarga de leer y escribir los
 * archivos de datos de la carpeta "datos".
 *
 * Se usa UTF-8 en la lectura y escritura para que los acentos se guarden y se
 * lean correctamente.
 */
public class DatosVotacion {

    private static final String CARPETA_DATOS = "datos";
    private static final String ARCHIVO_CANDIDATOS = CARPETA_DATOS + File.separator + "candidatos.txt";
    private static final String ARCHIVO_ELECTORES = CARPETA_DATOS + File.separator + "electores.txt";
    private static final String ARCHIVO_VOTOS = CARPETA_DATOS + File.separator + "votos.txt";

    /** Datos en memoria del sistema. */
    public static final List<Candidato> candidatos = new ArrayList<>();
    public static final List<Elector> electores = new ArrayList<>();
    public static final List<Voto> votos = new ArrayList<>();

    private DatosVotacion() {
    }

    /**
     * Carga los tres archivos de datos en los arreglos en memoria.
     */
    public static void cargar() {
        File carpeta = new File(CARPETA_DATOS);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        candidatos.clear();
        electores.clear();
        votos.clear();
        cargarCandidatos();
        cargarElectores();
        cargarVotos();
    }

    /**
     * Reescribe los tres archivos con el contenido de los arreglos.
     */
    public static void guardar() {
        guardarCandidatos();
        guardarElectores();
        guardarVotos();
    }

    private static void cargarCandidatos() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_CANDIDATOS, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                Candidato c = candidatoDesdeLinea(linea);
                if (c != null) {
                    candidatos.add(c);
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo de candidatos: " + e.getMessage());
        }
    }

    private static void cargarElectores() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_ELECTORES, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                Elector e = electorDesdeLinea(linea);
                if (e != null) {
                    electores.add(e);
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo de electores: " + e.getMessage());
        }
    }

    private static void cargarVotos() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_VOTOS, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                Voto v = votoDesdeLinea(linea);
                if (v != null) {
                    votos.add(v);
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo de votos: " + e.getMessage());
        }
    }

    private static void guardarCandidatos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_CANDIDATOS, StandardCharsets.UTF_8))) {
            for (Candidato c : candidatos) {
                bw.write(lineaCandidato(c));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo de candidatos: " + e.getMessage());
        }
    }

    private static void guardarElectores() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_ELECTORES, StandardCharsets.UTF_8))) {
            for (Elector e : electores) {
                bw.write(lineaElector(e));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo de electores: " + e.getMessage());
        }
    }

    private static void guardarVotos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_VOTOS, StandardCharsets.UTF_8))) {
            for (Voto v : votos) {
                bw.write(lineaVoto(v));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo de votos: " + e.getMessage());
        }
    }

    private static String lineaCandidato(Candidato c) {
        return c.getCodigo() + ";" + c.getNombre() + ";" + c.getPartido() + ";" + c.getDescripcion();
    }

    private static Candidato candidatoDesdeLinea(String linea) {
        String[] partes = linea.split(";");
        if (partes.length >= 4) {
            return new Candidato(partes[0], partes[1], partes[2], partes[3]);
        }
        return null;
    }

    private static String lineaElector(Elector e) {
        return e.getCodigo() + ";" + e.getCedula() + ";" + e.getNombre() + ";" + e.getContrasena() + ";" + e.isYaVoto();
    }

    private static Elector electorDesdeLinea(String linea) {
        String[] partes = linea.split(";");
        if (partes.length >= 5) {
            return new Elector(partes[0], partes[1], partes[2], partes[3], Boolean.parseBoolean(partes[4]));
        }
        return null;
    }

    private static String lineaVoto(Voto v) {
        return v.getCodigoVoto() + ";" + v.getCodigoElector() + ";" + v.getCodigoCandidato();
    }

    private static Voto votoDesdeLinea(String linea) {
        String[] partes = linea.split(";");
        if (partes.length >= 3) {
            return new Voto(partes[0], partes[1], partes[2]);
        }
        return null;
    }
}
