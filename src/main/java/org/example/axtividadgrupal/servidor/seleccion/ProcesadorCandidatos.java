
package org.example.axtividadgrupal.servidor.seleccion;

import org.example.axtividadgrupal.servidor.errores.GestorErrores;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;

public class ProcesadorCandidatos {

    public void procesar(BufferedReader entrada, PrintWriter salida) {

        try {

            //ingreso de datos del candidato
            String cedula = entrada.readLine();
            String nombre = entrada.readLine();
            String fecha = entrada.readLine();

            if (cedula == null || nombre == null || fecha == null) {
                salida.println("CANDIDATO_DATOS_INVALIDOS");
                return;
            }

            //validacion de la fecha
            LocalDate fechaNacimiento = LocalDate.parse(fecha);

            //guardar los datos en oracle
            CandidatoOracle candidatoOracle = new CandidatoOracle();

            boolean registrado = candidatoOracle.registrar(
                    cedula,
                    nombre,
                    fechaNacimiento
            );

            if (registrado) {
                salida.println("CANDIDATO_OK");
                System.out.println("Candidato registrado correctamente");
            } else {
                salida.println("CANDIDATO_ERROR");
            }

        } catch (IllegalArgumentException e) {

            //error si los datos no son validos
            salida.println("CANDIDATO_DATOS_INVALIDOS");

        } catch (SQLException e) {

            //verificar si la cedula ya esta registrada
            if (e.getErrorCode() == 1) {
                salida.println("CANDIDATO_DUPLICADO");
            } else {
                GestorErrores.registrar("REGISTRO_CANDIDATO", e);
                salida.println("CANDIDATO_ERROR");
            }

        } catch (Exception e) {

            //otros errores del registro
            GestorErrores.registrar("REGISTRO_CANDIDATO", e);
            salida.println("CANDIDATO_ERROR");
        }
    }
}
