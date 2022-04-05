module ProgettoProg3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.gluonhq.charm.glisten;
    requires java.desktop;


    opens Server;
    opens Server.Controller;
    opens Client;
    opens Client.Controller;
}