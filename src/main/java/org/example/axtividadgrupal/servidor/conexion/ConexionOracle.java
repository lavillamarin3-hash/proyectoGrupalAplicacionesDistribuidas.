package org.example.axtividadgrupal.servidor.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionOracle {

    public static Connection conectar() throws SQLException {

       String url = "jdbc:oracle:thin:@//localhost:1521/freepdb1";
       String usuario = System.getenv("NEXO_DB_USER");
       String clave = System.getenv("NEXO_DB_PASSWORD");

       if (usuario == null || clave == null) {
           throw new SQLException(
                   "Faltan las variables de conexión a Oracle"
           );
       }

       return DriverManager.getConnection(
               url, usuario, clave
       );
    }
}
