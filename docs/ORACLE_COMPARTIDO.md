# Trabajar con la base Oracle compartida

## 1. Acceso a la red privada

1. Instala [Tailscale](https://tailscale.com/download) e inicia sesión con tu propia cuenta.
2. Acepta la invitación privada a la computadora que aloja Oracle.
3. Solicita al responsable la dirección privada Tailscale y tus credenciales Oracle. No se publican aquí.
4. Mantén Tailscale conectado. La computadora anfitriona debe permanecer encendida, conectada a Internet y sin suspenderse.

Comprueba desde PowerShell en tu computadora:

```powershell
Test-NetConnection HOST_PRIVADO_RECIBIDO -Port 1521
```

El resultado esperado es `TcpTestSucceeded: True`. Sustituye el marcador por el host recibido. La dirección `localhost` apunta a tu propio equipo; para esta base compartida debes usar la dirección del anfitrión.

## 2. SQL Developer

| Campo | Valor |
| --- | --- |
| Tipo de conexión | Básica |
| Rol | Predeterminado |
| Host | Dirección Tailscale recibida en privado |
| Puerto | `1521` |
| Nombre de servicio (no SID) | `freepdb1` |
| Usuario | Tu cuenta individual; para Samantha: `NEXO_SAMANTHA` |
| Contraseña | La recibida en privado |

Pulsa **Probar** y luego **Conectar**. En la hoja SQL ejecuta:

```sql
ALTER SESSION SET CURRENT_SCHEMA = NEXO_APP;
SELECT USER AS usuario,
       SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') AS esquema,
       SYS_CONTEXT('USERENV', 'SERVICE_NAME') AS servicio
FROM dual;
SELECT COUNT(*) AS total_usuarios FROM USUARIOS;
```

El esquema esperado es `NEXO_APP`. En el navegador de SQL Developer las tablas compartidas se encuentran en **Otros usuarios → NEXO_APP → Tablas**. Cambiar el esquema de la sesión no cambia el propietario de tu cuenta ni concede nuevos permisos.

## 3. Servidor Java de cada integrante

Actualiza tu copia del repositorio. En IntelliJ configura estas variables de entorno en la ejecución de `ServidorNexo`, `PruebaConexionOracle` y cualquier utilidad que acceda a Oracle:

```text
NEXO_DB_URL=jdbc:oracle:thin:@//HOST_PRIVADO_RECIBIDO:1521/freepdb1
NEXO_DB_USER=NEXO_SAMANTHA
NEXO_DB_PASSWORD=CONTRASENA_RECIBIDA_EN_PRIVADO
NEXO_DB_SCHEMA=NEXO_APP
```

Sustituye los marcadores y usa la cuenta asignada a cada persona. No copies estos valores reales a Java, README, commits o capturas públicas. `ConexionOracle` lee las variables; no lee archivos `.env` automáticamente. Si `NEXO_DB_URL` está ausente o vacía, conserva la conexión local original. `NEXO_DB_SCHEMA` es opcional y solo admite un nombre simple de esquema.

Cada integrante ejecuta su **servidor Java local** y su **cliente JavaFX local**. El servidor local conecta con el Oracle compartido por Tailscale. La comunicación cliente-servidor existente continúa en `127.0.0.1:5050`; no requiere publicar ese puerto.

## 4. Permisos y coordinación

- La cuenta de Samantha tiene acceso de lectura, inserción, actualización y eliminación a la tabla compartida `NEXO_APP.USUARIOS` mediante el rol `NEXO_DESARROLLO`.
- También puede crear tablas, secuencias y vistas en su propio esquema `NEXO_SAMANTHA`, con una cuota de 100 MB. Para experimentar allí, usa otra conexión y `ALTER SESSION SET CURRENT_SCHEMA = NEXO_SAMANTHA`.
- Las tablas del esquema personal no aparecen automáticamente en `NEXO_APP`. Para integrar un módulo, entrega el script de estructura al responsable, quien lo ejecutará como `NEXO_APP` y revisará las dependencias.
- Después de crear nuevas tablas o secuencias comunes, el propietario ejecuta [actualizar_permisos.sql](../database/actualizar_permisos.sql) para extender el acceso del equipo a esos objetos.
- Los cambios confirmados con `COMMIT` son visibles para todos. Coordina actualizaciones y eliminaciones; evita borrar datos de otros módulos. `ROLLBACK` solo revierte cambios todavía no confirmados; el DDL de Oracle hace confirmaciones implícitas.

## 5. Diagnóstico

| Síntoma | Revisar |
| --- | --- |
| Falló la prueba TCP / ORA-12170 | Ambos Tailscale conectados, invitación aceptada, permiso de red a TCP 1521, anfitrión despierto y firewall |
| ORA-12514 | Usar servicio `freepdb1`, no SID; comprobar listener y apertura de la PDB |
| ORA-01017 | Usuario y contraseña Oracle asignados, distintos de GitHub/Tailscale |
| ORA-00942 | `CURRENT_SCHEMA=NEXO_APP` y permisos sobre la tabla; pedir al propietario actualizar los permisos |
| Java indica variables faltantes | Configurarlas en la ejecución concreta del servidor/utilidad en IntelliJ |

La conexión remota solo se considera validada cuando un integrante externo completa tanto la prueba TCP como la consulta SQL. Una prueba desde la computadora anfitriona no verifica el trayecto desde otra casa.

Referencias: [Oracle: conexión por servicio](https://docs.oracle.com/en/database/oracle/oracle-database/26/netag/connecting-database-service.html), [Tailscale: compartir una máquina](https://tailscale.com/docs/features/sharing).
