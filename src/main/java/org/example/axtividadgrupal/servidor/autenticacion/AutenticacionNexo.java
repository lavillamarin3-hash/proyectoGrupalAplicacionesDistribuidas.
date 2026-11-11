
package org.example.axtividadgrupal.servidor.autenticacion;

import org.example.axtividadgrupal.servidor.conexion.ConexionOracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

public class AutenticacionNexo {

    public static boolean validar(
            String usuario, String contrasena) throws Exception {

        if (usuario == null || contrasena == null ||
                usuario.isBlank() || contrasena.isBlank()) {
            return false;
        }

        String sql = """
            SELECT CLAVE_HASH
            FROM USUARIOS
            WHERE NOMBRE_USUARIO = ?
            AND ACTIVO = 'S'
            """;

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setString(1, usuario);

            try (ResultSet resultado = consulta.executeQuery()) {

                if (!resultado.next()) {
                    return false;
                }

                String hashGuardado =
                        resultado.getString("CLAVE_HASH");

                char[] clave = contrasena.toCharArray();

                try {
                    return SeguridadClaves.verificarClave(
                            clave, hashGuardado
                    );
                } finally {
                    Arrays.fill(clave, '\0');
                }
            }
        }
    }
}
