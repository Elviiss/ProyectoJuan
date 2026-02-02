module org.example.proyectojuan {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;


    opens org.example.proyectojuan to javafx.fxml;
    exports org.example.proyectojuan;
}