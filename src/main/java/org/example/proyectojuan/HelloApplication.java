package org.example.proyectojuan;

import com.google.cloud.firestore.Firestore;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.sql.PreparedStatement;

import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Gestión de Directorios");

        ImageView logoView = new ImageView();
        try {
            Image image = new Image("https://cdn-icons-png.flaticon.com/512/12040/12040831.png");
            logoView.setImage(image);
            logoView.setFitWidth(100);
            logoView.setFitHeight(100);
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen: " + e.getMessage());
        }

        Label userNameLabel = new Label("Usuario:");
        TextField userTextField = new TextField();
        userTextField.setMaxWidth(200);

        Label pwLabel = new Label("Contraseña:");
        PasswordField pwBox = new PasswordField();
        pwBox.setMaxWidth(200);

        Button btn = new Button("Iniciar Sesión");
        Text actiontarget = new Text();

        btn.setOnAction(e -> {
            String user = userTextField.getText();
            String pass = pwBox.getText();

            String rolRecuperado = obtenerRolUsuario(user, pass);

            if (rolRecuperado != null) {
                mostrarVentanaProyectos( stage, user, rolRecuperado);
            } else {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Acceso denegado");
            }
        });

        VBox loginLayout = new VBox(15);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(25));
        loginLayout.getChildren().addAll(logoView, userNameLabel, userTextField, pwLabel, pwBox, btn, actiontarget);

        Scene loginScene = new Scene(loginLayout, 800, 800);

        try {
            loginScene.getStylesheets().add(getClass().getResource("HelloApplication.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS no encontrado, continuando sin estilos externos.");
        }

        stage.setScene(loginScene);
        stage.show();
    }


    private void mostrarVentanaProyectos(Stage stage, String nombreUsuario, String rol) {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(10, 20, 20, 20));

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 0, 10, 0));

        Label lblBienvenida = new Label("Bienvenido, " + nombreUsuario);
        lblBienvenida.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Cerrar Sesión");
        btnLogout.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnLogout.setOnAction(e -> {
            try {
                start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        topBar.getChildren().addAll(lblBienvenida, spacer, btnLogout);

        VBox contenidoCentral = new VBox(20);
        contenidoCentral.setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("PANEL DE CONTROL: DIRECTORIOS");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Button btnCrear = new Button("Nuevo Directorio");
        btnCrear.setMinWidth(200);
        btnCrear.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCrear.setOnAction(e -> mostrarFormularioNuevoProyecto(stage, nombreUsuario, rol));

        if (rol.equals("lector")) {
            btnCrear.setVisible(false);
            btnCrear.setManaged(false);
        }

        Button btnVerProyectos = new Button("Ver Directorio");
        btnVerProyectos.setMinWidth(200);
        btnVerProyectos.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");

        btnVerProyectos.setOnAction(e -> mostrarVentanaListaProyectos(stage, nombreUsuario, rol));

        Button btnConsultoria = new Button("Consultoría");
        btnConsultoria.setMinWidth(200);
        btnConsultoria.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");

        btnConsultoria.setOnAction(e -> mostrarVentanaConsultoria(stage, nombreUsuario, rol));

        Button btnGestionar = new Button("Gestionar Usuarios");
        btnGestionar.setMinWidth(200);
        btnGestionar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");

        btnGestionar.setOnAction(e -> mostrarVentanaGestionUsuarios(stage, nombreUsuario, rol));

        if (!rol.equals("admin")) {
            btnGestionar.setVisible(false);
            btnGestionar.setManaged(false);
        }

        Button btnInforme = new Button("Informe de Estadísticas");
        btnInforme.setMinWidth(200);
        btnInforme.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");

        btnInforme.setOnAction(e -> mostrarVentanaEstadisticas(stage, nombreUsuario, rol));

        contenidoCentral.getChildren().addAll(titulo, btnCrear, btnVerProyectos, btnGestionar, btnInforme, btnConsultoria);
        mainLayout.getChildren().addAll(topBar, contenidoCentral);

        Scene proyectosScene = new Scene(mainLayout, 800, 800);
        stage.setScene(proyectosScene);

        try {
            proyectosScene.getStylesheets().add(getClass().getResource("HelloApplication.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS no encontrado, continuando sin estilos externos.");
        }
    }

    private void mostrarVentanaConsultoria(Stage stage, String nombreUsuario, String rol) {
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(20));

        Label titulo = new Label("CONSULTORÍA: REGISTRO DE ACTIVIDAD");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        VBox listaLogs = new VBox(10);
        listaLogs.setPadding(new Insets(10));

        try {
            Firestore db = FirestoreConnection.getInstance().db();

            var querySnapshot = db.collection("consultoria").get().get();

            for (var doc : querySnapshot.getDocuments()) {
                String usuarioLog = doc.getString("usuario");
                String accionLog = doc.getString("accion");

                HBox fila = new HBox(15);
                fila.setStyle("-fx-background-color: #f44336; -fx-padding: 10; -fx-border-color: #ccc;");
                fila.getChildren().addAll(
                        new Text(usuarioLog + ":"),
                        new Text(accionLog)
                );
                listaLogs.getChildren().add(fila);
            }

        }  catch (Exception e) {
            System.out.println("CAUSA DEL ERROR: " + e.getCause());
            e.printStackTrace();
            listaLogs.getChildren().add(new Text("Error: " + e.getMessage()));
        }

        ScrollPane scroll = new ScrollPane(listaLogs);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(400);

        Button btnVolver = new Button("Volver al Panel");
        btnVolver.setOnAction(e -> mostrarVentanaProyectos(stage, nombreUsuario, rol));

        mainLayout.getChildren().addAll(titulo, scroll, btnVolver);

        Scene consultoria = new Scene(mainLayout, 800, 800);
        stage.setScene(consultoria);

        try {
            consultoria.getStylesheets().add(getClass().getResource("HelloApplication.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS no encontrado, continuando sin estilos externos.");
        }
    }

    private void mostrarVentanaGestionUsuarios(Stage stage, String nombreUsuario, String rol) {
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(20));

        Label titulo = new Label("GESTIÓN DE USUARIOS");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        VBox listaUsuarios = new VBox(10);
        listaUsuarios.setAlignment(Pos.CENTER);
        listaUsuarios.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10; -fx-border-color: #ccc;");

        HBox cabecera = new HBox(50);
        cabecera.setAlignment(Pos.CENTER);
        cabecera.getChildren().addAll(new Label("ID"), new Label("NOMBRE"), new Label("ROL"));
        listaUsuarios.getChildren().add(cabecera);

        String url = "jdbc:mysql://localhost:3306/aplicacion_usuarios_sge";
        String userBD = "root";
        String passBD = "root";
        String sql = "SELECT u.id_usuario, u.nombre, r.nombre_rol FROM usuarios u JOIN rol r ON u.id_rol = r.id_rol";

        try (Connection conexion = DriverManager.getConnection(url, userBD, passBD);
             Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                HBox fila = new HBox(55);
                fila.setAlignment(Pos.CENTER);
                fila.getChildren().addAll(
                        new Text(String.valueOf(rs.getInt("id_usuario"))),
                        new Text(rs.getString("nombre")),
                        new Text(rs.getString("nombre_rol"))
                );
                listaUsuarios.getChildren().add(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        HBox botonesAccion = new HBox(15);
        botonesAccion.setAlignment(Pos.CENTER);

        Button btnAnadir = new Button("Añadir Usuario");
        btnAnadir.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnAnadir.setOnAction(e -> {
            try {
                Auditoria log = new Auditoria("Nuevo usuario añadido ", nombreUsuario);
                FirestoreConnection.getInstance().registrarActividad(log);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            mostrarFormularioNuevoUsuario(stage, nombreUsuario, rol);
        });

        Button btnEditar = new Button("Editar Usuario");
        btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        TextField txtIdBorrar = new TextField();
        txtIdBorrar.setPromptText("ID a borrar");
        txtIdBorrar.setMaxWidth(80);

        btnEditar.setOnAction(e -> {
            String idTexto = txtIdBorrar.getText();
            if (!idTexto.isEmpty()) {
                try {
                    int id = Integer.parseInt(idTexto);
                    try {
                        Auditoria log = new Auditoria("Usuario editado (ID: " + id + ") ", nombreUsuario);
                        FirestoreConnection.getInstance().registrarActividad(log);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    mostrarFormularioEditarUsuario(stage, nombreUsuario, rol, id);
                } catch (NumberFormatException ex) {
                    System.out.println("ID no válido");
                }
            }
        });

        Button btnBorrar = new Button("Borrar Usuario");
        btnBorrar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");


        btnBorrar.setOnAction(e -> {
            String idTexto = txtIdBorrar.getText();
            if (!idTexto.isEmpty()) {
                try {
                    int id = Integer.parseInt(idTexto);
                    if (id == 1) return;

                    eliminarUsuarioBD(id);
                    try {
                        Auditoria log = new Auditoria("Usuario Borrado (ID: " + id + ")", nombreUsuario);
                        FirestoreConnection.getInstance().registrarActividad(log);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    mostrarVentanaGestionUsuarios(stage, nombreUsuario, rol);
                } catch (NumberFormatException ex) {
                    System.out.println("Por favor, introduce un ID numérico válido.");
                }
            }
        });

        botonesAccion.getChildren().add(0, txtIdBorrar);

        botonesAccion.getChildren().addAll(btnAnadir, btnEditar, btnBorrar);

        Button btnVolver = new Button("Volver al Panel");
        btnVolver.setOnAction(e -> mostrarVentanaProyectos(stage, nombreUsuario, rol));

        mainLayout.getChildren().addAll(titulo, listaUsuarios, botonesAccion, btnVolver);

        Scene scene = new Scene(mainLayout, 800, 800);
        stage.setScene(scene);

        try {
            scene.getStylesheets().add(getClass().getResource("HelloApplication.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS no encontrado, continuando sin estilos externos.");
        }
    }

    private void mostrarFormularioEditarUsuario(Stage stage, String nombreUsuario, String rolActual, int idAEditar) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Label titulo = new Label("EDITAR USUARIO (ID: " + idAEditar + ")");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TextField txtNombre = new TextField();
        TextField txtEmail = new TextField();
        javafx.scene.control.ComboBox<String> comboRoles = new javafx.scene.control.ComboBox<>();
        comboRoles.getItems().addAll("admin", "editor", "lector");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/aplicacion_usuarios_sge", "root", "root")) {
            String sql = "SELECT nombre, email, id_rol FROM usuarios WHERE id_usuario = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, idAEditar);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                txtNombre.setText(rs.getString("nombre"));
                txtEmail.setText(rs.getString("email"));
                int idRol = rs.getInt("id_rol");
                comboRoles.setValue(idRol == 1 ? "admin" : (idRol == 2 ? "editor" : "lector"));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        Button btnActualizar = new Button("Guardar Cambios");
        btnActualizar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        btnActualizar.setOnAction(e -> {
            int nuevoIdRol = comboRoles.getValue().equals("admin") ? 1 : (comboRoles.getValue().equals("editor") ? 2 : 3);
            actualizarUsuarioBD(idAEditar, txtNombre.getText(), txtEmail.getText(), nuevoIdRol);
            mostrarVentanaGestionUsuarios(stage, nombreUsuario, rolActual);
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> mostrarVentanaGestionUsuarios(stage, nombreUsuario, rolActual));

        layout.getChildren().addAll(titulo, new Label("Nombre:"), txtNombre, new Label("Email:"), txtEmail, new Label("Rol:"), comboRoles, btnActualizar, btnCancelar);
        stage.setScene(new Scene(layout, 800, 800));
    }

    private void actualizarUsuarioBD(int id, String nombre, String email, int idRol) {
        String url = "jdbc:mysql://localhost:3306/aplicacion_usuarios_sge";
        String sql = "UPDATE usuarios SET nombre = ?, email = ?, id_rol = ? WHERE id_usuario = ?";

        try (Connection conexion = DriverManager.getConnection(url, "root", "root");
             PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setString(1, nombre);
            pst.setString(2, email);
            pst.setInt(3, idRol);
            pst.setInt(4, id);

            pst.executeUpdate();
            System.out.println("Usuario actualizado con éxito.");

        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
        }
    }

    private void eliminarUsuarioBD(int idUsuario) {
        String url = "jdbc:mysql://localhost:3306/aplicacion_usuarios_sge";
        String userBD = "root";
        String passBD = "root";

        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";

        try (Connection conexion = DriverManager.getConnection(url, userBD, passBD);
             PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, idUsuario);

            int filasAfectadas = pst.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Usuario con ID " + idUsuario + " eliminado con éxito.");
            } else {
                System.out.println("No se encontró ningún usuario con ese ID.");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
    }

    private void mostrarFormularioNuevoUsuario(Stage stage, String nombreUsuario, String rolActual) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Label titulo = new Label("AÑADIR NUEVO USUARIO");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de usuario");
        txtNombre.setMaxWidth(250);

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Correo electrónico");
        txtEmail.setMaxWidth(250);

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Contraseña");
        txtPass.setMaxWidth(250);

        javafx.scene.control.ComboBox<String> comboRoles = new javafx.scene.control.ComboBox<>();
        comboRoles.getItems().addAll("admin", "editor", "lector");
        comboRoles.setPromptText("Selecciona un Rol");

        Button btnGuardar = new Button("Guardar Usuario");
        btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> mostrarVentanaGestionUsuarios(stage, nombreUsuario, rolActual));

        btnGuardar.setOnAction(e -> {
            String n = txtNombre.getText();
            String em = txtEmail.getText();
            String p = txtPass.getText();
            String r = comboRoles.getValue();

            if (!n.isEmpty() && r != null) {
                int idRol = r.equals("admin") ? 1 : (r.equals("editor") ? 2 : 3);
                insertarUsuarioBD(n, em, p, idRol);
                mostrarVentanaGestionUsuarios(stage, nombreUsuario, rolActual);
            }
        });

        layout.getChildren().addAll(titulo, txtNombre, txtEmail, txtPass, comboRoles, btnGuardar, btnCancelar);
        stage.setScene(new Scene(layout, 800, 800));
    }

    private void insertarUsuarioBD(String nombre, String email, String pass, int idRol) {
        String url = "jdbc:mysql://localhost:3306/aplicacion_usuarios_sge";
        String userBD = "root";
        String passBD = "root";

        String sql = "INSERT INTO usuarios (nombre, email, pass, id_rol) VALUES (?, ?, ?, ?)";

        try (Connection conexion = DriverManager.getConnection(url, userBD, passBD);
             PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setString(1, nombre);
            pst.setString(2, email);
            pst.setString(3, pass);
            pst.setInt(4, idRol);

            pst.executeUpdate();
            System.out.println("Usuario insertado correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
        }
    }

    private void mostrarVentanaListaProyectos(Stage stage, String nombreUsuario, String rol) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));

        Label titulo = new Label("BUSCADOR DE DIRECTORIOS");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TextField campoBusqueda = new TextField();
        campoBusqueda.setPromptText("Escribe el nombre del directorio...");
        campoBusqueda.setMaxWidth(300);

        Button btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        VBox listaResultados = new VBox(10);
        listaResultados.setPadding(new Insets(10));

        btnBuscar.setOnAction(e -> {
            listaResultados.getChildren().clear(); // Borramos lo anterior
            String textoBusqueda = campoBusqueda.getText();

            String sql = "SELECT nombre_proyecto, tipo FROM proyectos WHERE nombre_proyecto LIKE ?";

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/aplicacion_usuarios_sge", "root", "root");
                 PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setString(1, "%" + textoBusqueda + "%");
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    String nombre = rs.getString("nombre_proyecto");
                    String tipo = rs.getString("tipo");

                    HBox fila = new HBox(10);
                    fila.setAlignment(Pos.CENTER_LEFT);
                    fila.setStyle("-fx-background-color: #eeeeee; -fx-padding: 10; -fx-border-radius: 5;");

                    Label lblNombre = new Label(nombre + " [" + tipo + "]");
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    /*Button btnEditar = new Button("Editar");
                    Button btnDescargar = new Button("Descargar");

                    if (!"TXT".equalsIgnoreCase(tipo)) {
                        btnEditar.setDisable(true);
                    }

                    if (!"TXT".equalsIgnoreCase(tipo) && !"PDF".equalsIgnoreCase(tipo) && !"DOCX".equalsIgnoreCase(tipo)) {
                        btnDescargar.setDisable(true);
                    }

                    btnEditar.setOnAction(ev -> ventanaEditarContenidoTXT(stage, nombre, nombreUsuario, rol));
                    btnDescargar.setOnAction(ev -> descargarArchivo(nombre, tipo));

                    fila.getChildren().addAll(lblNombre, spacer, btnEditar, btnDescargar);
                     */

                    Button btnVerMas = new Button("Ver más");
                    btnVerMas.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

                    btnVerMas.setOnAction(ev -> mostrarDetallesProyecto(stage, nombre, nombreUsuario, rol));

                    fila.getChildren().addAll(lblNombre, spacer, btnVerMas);



                    listaResultados.getChildren().add(fila);
                }

                if (listaResultados.getChildren().isEmpty()) {
                    listaResultados.getChildren().add(new Label("No se encontraron directorios con: " + textoBusqueda));
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        Button btnVolver = new Button("Volver al Panel");
        btnVolver.setOnAction(e -> mostrarVentanaProyectos(stage, nombreUsuario, rol));

        layout.getChildren().addAll(titulo, campoBusqueda, btnBuscar, new ScrollPane(listaResultados), btnVolver);
        stage.setScene(new Scene(layout, 800, 800));

        try {
            layout.getStylesheets().add(getClass().getResource("HelloApplication.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS no encontrado, continuando sin estilos externos.");
        }
    }

    private void mostrarDetallesProyecto(Stage stage, String nombreP, String user, String rol) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Label titulo = new Label("DETALLES DEL DIRECTORIO");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 20; -fx-border-color: #ccc;");

        String sql = "SELECT * FROM proyectos WHERE nombre_proyecto = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/aplicacion_usuarios_sge", "root", "root");
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, nombreP);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                infoBox.getChildren().addAll(
                        new Label("Nombre: " + rs.getString("nombre_proyecto")),
                        new Label("Fecha Inicio: " + rs.getDate("fecha_inicio")),
                        new Label("Fecha Final: " + rs.getDate("fecha_final")),
                        new Label("Tipo: " + rs.getString("tipo")),
                        new Label("Estado: " + rs.getString("estado")),
                        new Label("Calificación: " + rs.getInt("calificacion")),
                        new Label("Jefe de Proyecto: " + rs.getString("jefe"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button btnVolver = new Button("Volver a la lista");
        btnVolver.setOnAction(e -> mostrarVentanaListaProyectos(stage, user, rol));

        layout.getChildren().addAll(titulo, infoBox, btnVolver);
        stage.setScene(new Scene(layout, 600, 600));
    }

    private void ventanaEditarContenidoTXT(Stage stage, String nombreProyecto, String user, String rol) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label lbl = new Label("Editando contenido de: " + nombreProyecto);
        TextArea areaTexto = new TextArea();

        String nombreArchivo = "proyectos_archivos/" + nombreProyecto.replaceAll("[^a-zA-Z0-9.-]", "_") + ".txt";

        try {
            areaTexto.setText(java.nio.file.Files.readString(java.nio.file.Paths.get(nombreArchivo)));
        } catch (IOException e) { areaTexto.setText("Error al cargar o archivo no encontrado."); }

        Button btnGuardar = new Button("Guardar Cambios");
        btnGuardar.setOnAction(e -> {
            try {
                java.nio.file.Files.writeString(java.nio.file.Paths.get(nombreArchivo), areaTexto.getText());
                mostrarVentanaListaProyectos(stage, user, rol);
            } catch (IOException ex) { ex.printStackTrace(); }
        });

        Button btnVolver = new Button("Volver");
        btnVolver.setOnAction(e -> mostrarVentanaListaProyectos(stage, user, rol));

        layout.getChildren().addAll(lbl, areaTexto, btnGuardar, btnVolver);
        stage.setScene(new Scene(layout, 500, 400));
    }

    private void descargarArchivo(String nombreProyecto, String tipo) {
        String extension = "." + tipo.toLowerCase();

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Descargar Proyecto " + tipo);
        fileChooser.setInitialFileName(nombreProyecto + extension);

        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter(tipo + " files", "*" + extension)
        );

        java.io.File destino = fileChooser.showSaveDialog(null);
        if (destino != null) {
            try {
                String origenPath = "proyectos_archivos/" + nombreProyecto.replaceAll("[^a-zA-Z0-9.-]", "_") + ".txt";
                java.nio.file.Files.copy(
                        java.nio.file.Paths.get(origenPath),
                        destino.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void mostrarFormularioNuevoProyecto(Stage stage, String nombreUsuario, String rol) {
        VBox formularioLayout = new VBox(15);
        formularioLayout.setAlignment(Pos.CENTER);
        formularioLayout.setPadding(new Insets(30));

        Label titulo = new Label("NUEVO DIRECTORIO");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del directorio");
        txtNombre.setMaxWidth(300);

        DatePicker pickerFechaInicio = new DatePicker();
        pickerFechaInicio.setPromptText("Fecha de inicio");
        pickerFechaInicio.setMaxWidth(300);

        DatePicker pickerFechaFinal = new DatePicker();
        pickerFechaFinal.setPromptText("Fecha final");
        pickerFechaFinal.setMaxWidth(300);

        ComboBox<String> comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll("Interno", "Externo", "Especial");
        comboTipo.setPromptText("Tipo de Proyecto");
        comboTipo.setMaxWidth(300);

        ComboBox<String> comboEstado = new ComboBox<>();
        comboEstado.getItems().addAll("Borrador", "En Curso", "Finalizado");
        comboEstado.setPromptText("Estado");
        comboEstado.setMaxWidth(300);

        ComboBox<Integer> comboCalificacion = new ComboBox<>();
        comboCalificacion.getItems().addAll(1, 2, 3, 4, 5);
        comboCalificacion.setPromptText("Calificación");
        comboCalificacion.setMaxWidth(300);

        TextField txtJefe = new TextField();
        txtJefe.setPromptText("Jefe de Proyecto");
        txtJefe.setMaxWidth(300);

        Button btnFinalizar = new Button("Confirmar y Crear");
        btnFinalizar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnFinalizar.setPadding(new Insets(10, 20, 10, 20));

        Button btnVolver = new Button("Cancelar");
        btnVolver.setOnAction(e -> mostrarVentanaProyectos(stage, nombreUsuario, rol));

        btnFinalizar.setOnAction(e -> {
            String nombre = txtNombre.getText();
            java.sql.Date fIni = (pickerFechaInicio.getValue() != null) ? java.sql.Date.valueOf(pickerFechaInicio.getValue()) : null;
            java.sql.Date fFin = (pickerFechaFinal.getValue() != null) ? java.sql.Date.valueOf(pickerFechaFinal.getValue()) : null;
            String tipo = (comboTipo.getValue() != null) ? comboTipo.getValue() : "";
            String estado = (comboEstado.getValue() != null) ? comboEstado.getValue() : "";
            int calif = (comboCalificacion.getValue() != null) ? comboCalificacion.getValue() : 0;
            String jefe = txtJefe.getText();

            if (!nombre.isEmpty()) {
                guardarProyecto(nombre, fIni, fFin, tipo, estado, calif, jefe);

                try {
                    Auditoria log = new Auditoria("Directorio Creado: " + nombre, nombreUsuario);
                    FirestoreConnection.getInstance().registrarActividad(log);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                mostrarVentanaProyectos(stage, nombreUsuario, rol);
            } else {
                System.out.println("El nombre del directorio es obligatorio");
            }
        });

        formularioLayout.getChildren().addAll(
                titulo,
                new Label("Nombre:"), txtNombre,
                new Label("Fecha Inicio:"), pickerFechaInicio,
                new Label("Fecha Final:"), pickerFechaFinal,
                new Label("Tipo:"), comboTipo,
                new Label("Estado:"), comboEstado,
                new Label("Calificación:"), comboCalificacion,
                new Label("Jefe de Proyecto:"), txtJefe,
                btnFinalizar, btnVolver
        );

        Scene sceneForm = new Scene(formularioLayout, 800, 800);
        stage.setScene(sceneForm);

        try {
            sceneForm.getStylesheets().add(getClass().getResource("HelloApplication.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS no encontrado, continuando sin estilos externos.");
        }
    }

    private String obtenerRolUsuario(String usuario, String contra) {
        String url = "jdbc:mysql://localhost:3306/aplicacion_usuarios_sge";
        String userBD = "root";
        String passBD = "root";

        String sql = "SELECT r.nombre_rol FROM usuarios u " +
                "JOIN rol r ON u.id_rol = r.id_rol " +
                "WHERE u.nombre = ? AND u.pass = ?";

        try (Connection conexion = DriverManager.getConnection(url, userBD, passBD);
             PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setString(1, usuario);
            pst.setString(2, contra);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("nombre_rol");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void guardarProyecto(String nombre, java.sql.Date fechaInicio, java.sql.Date fechaFinal, String tipo, String estado, int calificacion, String jefe) {
        String url = "jdbc:mysql://localhost:3306/aplicacion_usuarios_sge";
        String userBD = "root";
        String passBD = "root";

        String sql = "INSERT INTO proyectos (nombre_proyecto, fecha_inicio, fecha_final, tipo, estado, calificacion, jefe) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = DriverManager.getConnection(url, userBD, passBD);
             PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setString(1, nombre);
            pst.setDate(2, fechaInicio);
            pst.setDate(3, fechaFinal);
            pst.setString(4, tipo);
            pst.setString(5, estado);
            pst.setInt(6, calificacion);
            pst.setString(7, jefe);

            pst.executeUpdate();
            System.out.println("Directorio guardado con éxito en la BD");

        } catch (SQLException e) {
            System.err.println("Error al guardar directorio: " + e.getMessage());
        }
    }

    private void crearPDF(String ruta, String titulo, String contenido) {
        try {
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(ruta);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document documento = new com.itextpdf.layout.Document(pdf);

            documento.add(new com.itextpdf.layout.element.Paragraph(titulo)
                    .setBold().setFontSize(18));
            documento.add(new com.itextpdf.layout.element.Paragraph(contenido));

            documento.close();
            System.out.println("PDF creado con éxito.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void crearDocx(String ruta, String titulo, String contenido) {
        try (org.apache.poi.xwpf.usermodel.XWPFDocument document = new org.apache.poi.xwpf.usermodel.XWPFDocument()) {
            // Título
            org.apache.poi.xwpf.usermodel.XWPFParagraph title = document.createParagraph();
            org.apache.poi.xwpf.usermodel.XWPFRun titleRun = title.createRun();
            titleRun.setText(titulo);
            titleRun.setBold(true);
            titleRun.setFontSize(20);

            // Contenido
            org.apache.poi.xwpf.usermodel.XWPFParagraph body = document.createParagraph();
            org.apache.poi.xwpf.usermodel.XWPFRun bodyRun = body.createRun();
            bodyRun.setText(contenido);

            try (java.io.FileOutputStream out = new java.io.FileOutputStream(ruta)) {
                document.write(out);
            }
            System.out.println("DOCX creado con éxito.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void mostrarVentanaEstadisticas(Stage stage, String nombreUsuario, String rol) {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Label titulo = new Label("ESTADÍSTICAS DEL SISTEMA");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        int totalProyectos = obtenerConteo("SELECT COUNT(*) FROM proyectos");
        int totalUsuarios = obtenerConteo("SELECT COUNT(*) FROM usuarios");

        PieChart pieChart = new PieChart();
        pieChart.getData().add(new PieChart.Data("Proyectos (" + totalProyectos + ")", totalProyectos));
        pieChart.getData().add(new PieChart.Data("Usuarios (" + totalUsuarios + ")", totalUsuarios));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        xAxis.setLabel("Categoría");
        yAxis.setLabel("Cantidad");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Proyectos", totalProyectos));
        series.getData().add(new XYChart.Data<>("Usuarios", totalUsuarios));
        barChart.getData().add(series);

        HBox chartsContainer = new HBox(30, pieChart, barChart);
        chartsContainer.setAlignment(Pos.CENTER);

        Button btnVolver = new Button("Volver al Panel");
        btnVolver.setStyle("-fx-background-color: #757575; -fx-text-fill: white;");
        btnVolver.setOnAction(e -> mostrarVentanaProyectos(stage, nombreUsuario, rol));

        layout.getChildren().addAll(titulo, chartsContainer, btnVolver);

        Scene scene = new Scene(layout, 800, 800);
        stage.setScene(scene);

        try {
            scene.getStylesheets().add(getClass().getResource("HelloApplication.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS no encontrado, continuando sin estilos externos.");
        }
    }

    private int obtenerConteo(String sql) {
        String url = "jdbc:mysql://localhost:3306/aplicacion_usuarios_sge";
        try (Connection conexion = DriverManager.getConnection(url, "root", "root");
             Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        launch();
    }
}