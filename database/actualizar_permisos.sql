-- Ejecutar como NEXO_APP en FREEPDB1 despues de integrar nuevas tablas/secuencias.
-- El rol NEXO_DESARROLLO ya debe existir y estar asignado a los integrantes.
-- No crea cuentas, no incluye claves y no concede privilegios administrativos.
WHENEVER SQLERROR EXIT FAILURE ROLLBACK
SET SERVEROUTPUT ON
DECLARE
BEGIN
  IF USER <> 'NEXO_APP' OR SYS_CONTEXT('USERENV', 'CON_NAME') <> 'FREEPDB1' THEN
    RAISE_APPLICATION_ERROR(-20001, 'Conectarse como NEXO_APP al servicio freepdb1.');
  END IF;
  FOR t IN (SELECT table_name FROM user_tables WHERE nested = 'NO' AND secondary = 'N') LOOP
    EXECUTE IMMEDIATE 'GRANT SELECT, INSERT, UPDATE, DELETE ON '
      || DBMS_ASSERT.ENQUOTE_NAME(t.table_name, FALSE) || ' TO NEXO_DESARROLLO';
  END LOOP;
  FOR s IN (SELECT sequence_name FROM user_sequences WHERE sequence_name NOT LIKE 'ISEQ$$_%') LOOP
    EXECUTE IMMEDIATE 'GRANT SELECT ON '
      || DBMS_ASSERT.ENQUOTE_NAME(s.sequence_name, FALSE) || ' TO NEXO_DESARROLLO';
  END LOOP;
  DBMS_OUTPUT.PUT_LINE('Permisos del equipo actualizados.');
END;
/
