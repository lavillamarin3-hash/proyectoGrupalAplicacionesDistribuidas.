
package org.example.axtividadgrupal.servidor.autenticacion;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class SeguridadClaves {

    private static final int ITERACIONES = 210000;

    // Genera un hash protegido para guardar en Oracle
    public static String generarHash(char[] clave) throws Exception {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        byte[] hash = calcularHash(clave, salt, ITERACIONES);

        return "pbkdf2$" + ITERACIONES + "$"
                + Base64.getEncoder().encodeToString(salt)
                + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    // Comprueba la contraseña introducida durante el Login
    public static boolean verificarClave(
            char[] clave, String hashGuardado) throws Exception {

        if (clave == null || hashGuardado == null) {
            return false;
        }

        try {
            String[] partes = hashGuardado.split("\\$");

            if (partes.length != 4 ||
                    !partes[0].equals("pbkdf2")) {
                return false;
            }

            int iteraciones = Integer.parseInt(partes[1]);

            if (iteraciones < 100000 || iteraciones > 1000000) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);

            if (salt.length != 16 || esperado.length != 32) {
                return false;
            }

            byte[] calculado = calcularHash(
                    clave, salt, iteraciones
            );

            return MessageDigest.isEqual(esperado, calculado);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] calcularHash(
            char[] clave, byte[] salt, int iteraciones)
            throws Exception {

        PBEKeySpec especificacion =
                new PBEKeySpec(clave, salt, iteraciones, 256);

        try {
            SecretKeyFactory fabrica =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            return fabrica.generateSecret(
                    especificacion
            ).getEncoded();

        } finally {
            especificacion.clearPassword();
        }
    }
}
