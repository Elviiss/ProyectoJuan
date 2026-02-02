module org.example.proyectojuan {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires google.cloud.firestore;
    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires com.google.api.apicommon;
    requires com.google.auth;
    requires google.cloud.core;


    opens org.example.proyectojuan to javafx.fxml, google.cloud.firestore;
    exports org.example.proyectojuan;
}