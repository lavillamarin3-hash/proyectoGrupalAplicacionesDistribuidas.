
package org.example.axtividadgrupal.servidor.conexion;

import org.example.axtividadgrupal.servidor.autenticacion.AutenticacionNexo;
import org.example.axtividadgrupal.servidor.seleccion.CandidatoOracle;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServidorNexo {

    public static void main(String[] args) {

        try (ServerSocket servidor = new ServerSocket(
                5050, 50, InetAddress.getByName("127.0.0.1"))) {

            System.out.println("Servidor Nexo iniciado");
            System.out.println("Escuchando en puerto 5050");

            while (true) {

                try (Socket cliente = servidor.accept()) {

                    cliente.setSoTimeout(10000);

                    BufferedReader entrada = new BufferedReader(
                            new InputStreamReader(
                                    cliente.getInputStream(),
                                    StandardCharsets.UTF_8
                            )
                    );

                    PrintWriter salida = new PrintWriter(
                            new OutputStreamWriter(
                                    cliente.getOutputStream(),
                                    StandardCharsets.UTF_8
                            ), true
                    );

                    String solicitud = entrada.readLine();
                    System.out.println("Solicitud recibida: " + solicitud);

                    if ("PING".equals(solicitud)) {

                        salida.println("PONG");

                    } else if ("LOGIN".equals(solicitud)) {

                        String usuario = entrada.readLine();
                        String clave = entrada.readLine();

                        if (usuario == null || clave == null) {
                            salida.println("LOGIN_ERROR");
                            continue;
                        }

                        boolean valido =
                                AutenticacionNexo.validar(usuario, clave);

                        if (valido) {
                            salida.println("LOGIN_OK");
                            System.out.println("Inicio de sesión válido");
                        } else {
                            salida.println("LOGIN_ERROR");
                            System.out.println("Credenciales incorrectas");
                        }

                    } else if ("CANDIDATO_GUARDAR".equals(solicitud)) {

                        String cedula = entrada.readLine();
                        String nombre = entrada.readLine();
                        String fecha = entrada.readLine();

                        if (cedula == null || nombre == null || fecha == null) {
                            salida.println("CANDIDATO_DATOS_INVALIDOS");
                        } else {
                            try {

                                LocalDate fechaNacimiento = LocalDate.parse(fecha);

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

                            } catch (DateTimeParseException | IllegalArgumentException e) {

                                salida.println("CANDIDATO_DATOS_INVALIDOS");

                            } catch (Exception e) {

                                System.err.println("Error al registrar candidato:");
                                e.printStackTrace();
                                salida.println("CANDIDATO_ERROR");
                            }
                        }
                    } else {
                        salida.println("Solicitud no reconocida");
                    }


                } catch (Exception e) {
                    System.err.println(
                            "Error procesando solicitud: "
                                    + e.getClass().getSimpleName()
                    );
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
