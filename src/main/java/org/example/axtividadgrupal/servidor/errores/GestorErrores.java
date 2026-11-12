package org.example.axtividadgrupal.servidor.errores;

public class GestorErrores {

    public static void registrar(String operacion, Exception error) {

        System.err.println("ERROR EN NEXO");
        System.err.println("operacion" + operacion);
        System.err.println("Tipo" + error.getClass().getSimpleName());

        error.printStackTrace();
    }
}
