/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fitxar;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author marc
 */
public class ExcelProcessor {

    public List<Horario> procesarArchivo(File file) throws IOException {
        List<Horario> horarios = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Leer la primera hoja

            // Obtener los días de la fila 3 (C3, D3, E3, F3, G3)
            Row thirdRow = sheet.getRow(2); // Fila 3 (índice 2)
            List<String> diasSemana = new ArrayList<>();
            for (int i = 2; i <= 6; i++) { // C3 a G3 (índices 2 a 6)
                String dia = obtenerValorCelda(thirdRow.getCell(i)); // Leer los días
                if (dia != null && !dia.isEmpty()) {
                    diasSemana.add(dia); // Guardar los días en una lista
                    System.out.println("Dia encontrado: " + dia); // Imprimir los días de la fila 3
                }
            }

            for (int i = 3; i <= 20; i++) { // Fila 4 a 21 (índices 3 a 20)
                Row row = sheet.getRow(i);
                String hora = obtenerValorCelda(row.getCell(1)); // Obtener la hora de la columna B (índice 1)

                if (hora == null || hora.isEmpty()) continue; // Si no hay hora, saltar

                // No procesar las filas 12 (índice 11) y 16 (índice 15)
                if (i == 11 || i == 15) continue;

                System.out.println("Hora: " + hora); // Imprimir la hora que está procesando

                // Iterar sobre las columnas de días (C4 a G21: de la columna C (índice 2) a la G (índice 6))
                for (int j = 2; j <= 6; j++) { // C (índice 2) a G (índice 6)
                    Cell cell = row.getCell(j);
                    String dia = diasSemana.get(j - 2); // Obtener el día de la lista

                    if (cell != null && cell.getCellType() == CellType.STRING && "X".equalsIgnoreCase(cell.getStringCellValue())) {
                        // Si hay una "X", creamos un objeto Horario
                        String horaInicio = hora;
                        String horaFin = calcularHoraFin(hora);

                        // Crear un objeto Horario con el día, la hora de inicio y la hora de fin
                        horarios.add(new Horario(0, dia, dia, horaInicio, horaFin)); // Suponiendo 0 para professorId y diaSemana
                        System.out.println("Horario creado: " + dia + " - " + horaInicio + " a " + horaFin); // Imprimir el horario creado
                    }
                }
            }
        }

        return horarios;
    }

    // Método auxiliar para obtener el valor de la celda de manera segura
    private String obtenerValorCelda(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Si el valor es numérico, podríamos devolverlo como un texto
                return String.valueOf(cell.getNumericCellValue());
            default:
                return null;
        }
    }

private String calcularHoraFin(String rangoHorario) {
    try {
        // Verificar si el formato es correcto: "HH:mm - HH:mm"
        if (rangoHorario == null || !rangoHorario.matches("\\d{2}:\\d{2} - \\d{2}:\\d{2}")) {
            System.err.println("Formato de rango horario no válido: " + rangoHorario);
            return null;
        }

        // Dividir la cadena en hora de inicio y hora de fin
        String[] partes = rangoHorario.split(" - ");
        String horaInicio = partes[0];
        String horaFin = partes[1];

        // Extraer horas y minutos de la hora de inicio
        String[] horaInicioParts = horaInicio.split(":");
        int horaInicioH = Integer.parseInt(horaInicioParts[0]);
        int minutoInicio = Integer.parseInt(horaInicioParts[1]);

        // Extraer horas y minutos de la hora de fin
        String[] horaFinParts = horaFin.split(":");
        int horaFinH = Integer.parseInt(horaFinParts[0]);
        int minutoFin = Integer.parseInt(horaFinParts[1]);

        // Calcular la diferencia en minutos entre la hora de inicio y la hora de fin
        int duracionMinutos = (horaFinH * 60 + minutoFin) - (horaInicioH * 60 + minutoInicio);

        // Calcular la nueva hora de fin, sumando la duración
        int nuevaHoraFinH = (horaInicioH * 60 + minutoInicio + duracionMinutos) / 60;
        int nuevosMinutos = (horaInicioH * 60 + minutoInicio + duracionMinutos) % 60;

        // Asegurar que las horas están dentro del rango de 0-23
        if (nuevaHoraFinH >= 24) {
            nuevaHoraFinH -= 24; // Si excede las 24 horas, resetear al día siguiente
        }

        // Formatear la hora de fin como una cadena "HH:mm"
        String horaDeFin = String.format("%02d:%02d", nuevaHoraFinH, nuevosMinutos);
        return horaDeFin;
    } catch (Exception e) {
        System.err.println("Error al calcular la hora de fin: " + e.getMessage());
        return null;
    }
}

}
