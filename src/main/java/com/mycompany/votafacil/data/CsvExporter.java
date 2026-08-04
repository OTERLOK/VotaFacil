package com.mycompany.votafacil.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.mycompany.votafacil.exception.ExportacionFallidaException;

/**
 * Exporta tablas de datos a archivos CSV. Define las cabeceras de cada tipo
 * de exportación y se encarga de escapar correctamente los campos.
 */
public class CsvExporter {

    public static final String[] CABECERAS_CANDIDATOS = {"Codigo", "Nombre", "Partido", "Descripcion"};
    public static final String[] CABECERAS_ELECTORES = {"Codigo", "Cedula", "Nombre", "Estado"};
    public static final String[] CABECERAS_ESCRUTINIO = {"Codigo", "Nombre", "Partido", "Votos", "Porcentaje"};

    private CsvExporter() {
    }

    /**
     * Exporta una tabla con cabeceras y filas a un archivo CSV. Los campos
     * que contienen comas se encierran entre comillas dobles.
     */
    public static void exportar(File archivo, String[] cabeceras, ArrayList<String[]> filas)
            throws ExportacionFallidaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, StandardCharsets.UTF_8))) {
            for (int i = 0; i < cabeceras.length; i++) {
                if (i > 0) {
                    bw.write(",");
                }
                bw.write(cabeceras[i]);
            }
            bw.newLine();
            for (String[] fila : filas) {
                for (int i = 0; i < fila.length; i++) {
                    if (i > 0) {
                        bw.write(",");
                    }
                    bw.write(campoCSV(fila[i]));
                }
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ExportacionFallidaException("No se pudo exportar el archivo CSV: " + e.getMessage());
        }
    }

    private static String campoCSV(String valor) {
        if (valor != null && (valor.contains(",") || valor.contains("\"") || valor.contains("\n"))) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor == null ? "" : valor;
    }
}
