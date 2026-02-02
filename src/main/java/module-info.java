module org.example.proyectojuan {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires google.cloud.firestore;
    requires firebase.admin;
    requires com.google.auth.oauth2;


    opens org.example.proyectojuan to javafx.fxml;
    exports org.example.proyectojuan;
}