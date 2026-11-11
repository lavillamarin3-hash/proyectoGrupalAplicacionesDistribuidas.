
package org.example.axtividadgrupal.cliente.login;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtClave;

    @FXML
    private Button btnIngresar;

    @FXML
    private void iniciarSesion() {

        String usuario = txtUsuario.getText().trim();
        String clave = txtClave.getText();

        if (usuario.isEmpty() || clave.isEmpty()) {
            mostrarMensaje("Ingrese usuario y contraseña.");
            return;
        }

        btnIngresar.setDisable(true);

        // Comunicación con el servidor Java
        Task<String> tarea = new Task<>() {

            @Override
            protected String call() throws Exception {

                try (Socket socket = new Socket()) {

                    socket.connect(
                            new InetSocketAddress("127.0.0.1", 5050),
                            3000
                    );

                    socket.setSoTimeout(10000);

                    BufferedWriter salida =
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            socket.getOutputStream(),
                                            StandardCharsets.UTF_8
                                    )
                            );

                    BufferedReader entrada =
                            new BufferedReader(
                                    new InputStreamReader(
                                            socket.getInputStream(),
                                            StandardCharsets.UTF_8
                                    )
                            );

                    salida.write("LOGIN");
                    salida.newLine();

                    salida.write(usuario);
                    salida.newLine();

                    salida.write(clave);
                    salida.newLine();

                    salida.flush();

                    String respuesta = entrada.readLine();

                    if (respuesta == null) {
                        throw new IOException(
                                "El servidor no respondió."
                        );
                    }

                    return respuesta;
                }
            }
        };

        tarea.setOnSucceeded(evento -> {

            btnIngresar.setDisable(false);
            txtClave.clear();

            if ("LOGIN_OK".equals(tarea.getValue())) {

                mostrarMensaje(
                        "Bienvenido al sistema Nexo, " + usuario
                );

            } else {

                mostrarMensaje(
                        "Usuario o contraseña incorrectos."
                );
            }
        });


        tarea.setOnFailed(evento -> {

            btnIngresar.setDisable(false);
            txtClave.clear();

            Throwable error = tarea.getException();

            System.err.println("ERROR DEL LOGIN:");

            if (error != null) {
                error.printStackTrace();
            }

            mostrarMensaje(
                    "Error al comunicarse con el servidor. " +
                            "Revisa la consola de IntelliJ."
            );
        });


        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void mostrarMensaje(String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION
        );

        alerta.setTitle("Sistema Nexo");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
