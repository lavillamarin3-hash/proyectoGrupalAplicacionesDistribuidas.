package org.example.axtividadgrupal.cliente.conexion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientePruebaNexo {

    public static void main(String[] args) throws IOException {

        try (Socket cliente = new Socket("127.0.0.1", 5050)){

            PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true);

            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

            System.out.println("Conectado al servidor Nexo");

            salida.println("PING");

            String respuesta = entrada.readLine();

            System.out.println("Respuesta: " + respuesta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
