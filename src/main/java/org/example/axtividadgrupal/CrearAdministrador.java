
package org.example.axtividadgrupal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

import javax.swing.JPasswordField;
import javax.swing.JOptionPane;

public class CrearAdministrador {

    public static void main(String[] args) {

        try (Connection conexion = ConexionOracle.conectar()) {

            // Verificar si ya existe un administrador
            String consulta = """
                SELECT COUNT(*)
                FROM USUARIOS
                WHERE ROL = 'ADMIN'
                """;

            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(consulta)) {

                rs.next();

                if (rs.getInt(1) > 0) {
                    System.out.println(
                            "Ya existe un administrador."
                    );
                    return;
                }
            }

            // Solicitar contraseña sin mostrarla
            JPasswordField campoClave = new JPasswordField();

            int opcion = JOptionPane.showConfirmDialog(
                    null,
                    campoClave,
                    "Contraseña para el administrador",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            char[] clave = campoClave.getPassword();

            try {
                if (clave.length < 8) {
                    System.out.println(
                            "La contraseña debe tener al menos 8 caracteres."
                    );
                    return;
                }

                // Generar hash seguro
                String hash = SeguridadClaves.generarHash(clave);

                // Registrar administrador en Oracle
                String sql = """
                    INSERT INTO USUARIOS
                    (NOMBRE_USUARIO, CLAVE_HASH, ROL, ACTIVO)
                    VALUES (?, ?, 'ADMIN', 'S')
                    """;

                try (PreparedStatement ps =
                             conexion.prepareStatement(sql)) {

                    ps.setString(1, "admin");
                    ps.setString(2, hash);

                    ps.executeUpdate();
                }

                System.out.println(
                        "Administrador creado correctamente."
                );

            } finally {
                Arrays.fill(clave, '\0');
            }

        } catch (Exception e) {
            System.out.println("Error al crear administrador.");
            e.printStackTrace();
        }
    }
}
