
module org.example.axtividadgrupal {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    exports org.example.axtividadgrupal;
    exports org.example.axtividadgrupal.app;

    opens org.example.axtividadgrupal to javafx.fxml;
    opens org.example.axtividadgrupal.cliente.login to javafx.fxml;
    exports org.example.axtividadgrupal.servidor.conexion;
    opens org.example.axtividadgrupal.servidor.conexion to javafx.fxml;
    exports org.example.axtividadgrupal.servidor.autenticacion;
    opens org.example.axtividadgrupal.servidor.autenticacion to javafx.fxml;
    exports org.example.axtividadgrupal.servidor.usuarios;
    opens org.example.axtividadgrupal.servidor.usuarios to javafx.fxml;
    exports org.example.axtividadgrupal.cliente.conexion;
    opens org.example.axtividadgrupal.cliente.conexion to javafx.fxml;
    opens org.example.axtividadgrupal.app to javafx.fxml;
    opens org.example.axtividadgrupal.cliente.menu to javafx.fxml;
    opens org.example.axtividadgrupal.cliente.seleccion to javafx.fxml;
}
