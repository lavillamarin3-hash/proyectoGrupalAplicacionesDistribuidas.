package org.example.axtividadgrupal.servidor.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class ConexionOracle {

    public static Connection conectar() throws SQLException {
        String url = System.getenv("NEXO_DB_URL");
        String usuario = System.getenv("NEXO_DB_USER");
        String clave = System.getenv("NEXO_DB_PASSWORD");
        String esquema = System.getenv("NEXO_DB_SCHEMA");

        if (usuario == null || usuario.isBlank()
                || clave == null || clave.isBlank()) {
            throw new SQLException("Faltan NEXO_DB_USER o NEXO_DB_PASSWORD");
        }

        if (url == null || url.isBlank()) {
            url = "jdbc:oracle:thin:@//localhost:1521/freepdb1";
        }

        if (esquema != null && !esquema.isBlank()
                && !esquema.matches("^[A-Za-z][A-Za-z0-9_]{0,127}$")) {
            throw new SQLException("NEXO_DB_SCHEMA debe ser un identificador Oracle simple");
        }

        Connection conexion = DriverManager.getConnection(url, usuario, clave);
        try {
            if (esquema != null && !esquema.isBlank()) {
                try (Statement sentencia = conexion.createStatement()) {
                    sentencia.execute("ALTER SESSION SET CURRENT_SCHEMA = "
                            + esquema.toUpperCase(Locale.ROOT));
                }
            }
            return conexion;
        } catch (SQLException | RuntimeException error) {
            try {
                conexion.close();
            } catch (SQLException cierre) {
                error.addSuppressed(cierre);
            }
            throw error;
        }
    }
}
