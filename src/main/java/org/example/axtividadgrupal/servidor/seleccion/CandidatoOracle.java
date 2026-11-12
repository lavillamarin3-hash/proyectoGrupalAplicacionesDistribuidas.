
package org.example.axtividadgrupal.servidor.seleccion;

import org.example.axtividadgrupal.servidor.conexion.ConexionOracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;

public class CandidatoOracle {

    public boolean registrar(String cedula, String nombre, LocalDate fechaNacimiento)
            throws SQLException {

        //validaciones de los datos
        if (cedula == null || !cedula.matches("\\d{10}")) {
            throw new IllegalArgumentException(
                    "La cedula no es valida, se requieren completar los 10 digitos."
            );
        }

        if (nombre == null || nombre.isBlank() || nombre.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "El campo del nombre no puede estar vacio."
            );
        }

        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Ingrese una fecha de nacimiento válida."
            );
        }

        // Consulta para registrar el candidato
        String sql = """
                INSERT INTO CANDIDATOS
                (CEDULA, NOMBRE, FECHA_NACIMIENTO)
                VALUES (?, ?, ?)
                """;

        // ORACLE CONEXION
        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, cedula);
            consulta.setString(2, nombre.trim());
            consulta.setDate(3, Date.valueOf(fechaNacimiento));

            int filas = consulta.executeUpdate();

            return filas == 1;
        }
    }
}
