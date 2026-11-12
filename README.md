# Nexo — Aplicaciones Distribuidas

Proyecto Java 17, JavaFX y Oracle. El cliente se comunica con un servidor Java; ese servidor realiza las consultas a Oracle.

## Base de datos compartida del equipo

Trabajamos sobre `NEXO_APP` en el servicio Oracle `freepdb1`, puerto TCP `1521`, alojado en la computadora del responsable del proyecto.

La conexión desde otras casas se realiza mediante una red privada Tailscale. Cada integrante usa su propia cuenta Oracle. La dirección privada, la invitación y las contraseñas se entregan por un canal privado.

**[Guía para conectar SQL Developer y Java](docs/ORACLE_COMPARTIDO.md)**

No subir contraseñas, configuraciones privadas de IntelliJ ni exportaciones de datos a este repositorio público.
