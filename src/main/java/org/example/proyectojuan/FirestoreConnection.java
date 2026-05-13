package org.example.proyectojuan;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class FirestoreConnection {
    static Firestore db;
    static HashMap<String, Object> dataList;
    private static volatile FirestoreConnection INSTANCE;

    private FirestoreConnection() throws IOException {
        if(FirebaseApp.getApps().isEmpty()){
            try (java.io.InputStream in = getClass().getResourceAsStream("/serviceAccountKey.json")) {
                if (in == null) {
                    throw new IOException("No se encontró el archivo serviceAccountKey.json en resources");
                }
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(in))
                        .build();
                FirebaseApp.initializeApp(options);
            }
        }
        db = FirestoreClient.getFirestore();
        dataList = new java.util.HashMap<String, Object>();
    }

    public static FirestoreConnection getInstance() {
        if (INSTANCE == null){
            synchronized (FirestoreConnection.class){
                if(INSTANCE == null) {
                    try { INSTANCE = new FirestoreConnection(); }
                    catch (IOException e) { throw new RuntimeException(e);}
                }
            }
        }
        return INSTANCE;
    }

    public Firestore db() { return db; }

    public HashMap<String, Object> getDataList() { return dataList; }


    public void registrarActividad(Auditoria log) {
        try {
            Firestore db = getInstance().db();
            // Usamos un Map para asegurar que los nombres de los campos sean exactos
            java.util.Map<String, Object> datos = new java.util.HashMap<>();
            datos.put("usuario", log.getUsuario());
            datos.put("accion", log.getAccion());
            datos.put("fecha", com.google.cloud.Timestamp.now());

            // El .get() al final es CRUCIAL: obliga a Java a esperar a que Firebase responda
            db.collection("consultoria").add(datos).get();

            System.out.println("Log guardado en Firestore: " + log.getAccion());
        } catch (Exception e) {
            System.err.println("Error al guardar en Firestore: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

