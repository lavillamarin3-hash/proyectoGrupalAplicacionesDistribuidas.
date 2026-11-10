package org.example.axtividadgrupal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PruebaConexionOracle {

    public static void main(String[] args) {

        try {
            Connection conexion = ConexionOracle.conectar();

            System.out.println("Conectado a oracle");

            Statement consulta = conexion.createStatement();

            ResultSet resultado = consulta.executeQuery( //CONSULTA ORACLE
                    "SELECT USER FROM DUAL"
            );

            if (resultado.next()) {
                System.out.println(
                        "usuario conectado: " + resultado.getString(1)
                );
        }
            resultado.close();
            consulta.close();
            conexion.close();

    } catch (Exception e) {
          System.out.println("Error de conexion: ");
          e.printStackTrace();

        }
    }
}

